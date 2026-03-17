    package ru.mentee.power.crm.service;

    import jakarta.persistence.EntityManager;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.transaction.PlatformTransactionManager;
    import org.springframework.transaction.TransactionDefinition;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.transaction.support.TransactionTemplate;
    import ru.mentee.power.crm.domain.Deal;
    import ru.mentee.power.crm.exception.IllegalLeadStateException;
    import ru.mentee.power.crm.model.CreateDealRequest;
    import ru.mentee.power.crm.model.Lead;
    import ru.mentee.power.crm.model.LeadStatus;
    import ru.mentee.power.crm.repository.LeadRepository;
    import ru.mentee.power.crm.spring.repository.DealRepository;
    import ru.mentee.power.crm.spring.service.LeadProcessor;
    import ru.mentee.power.crm.spring.service.LeadService;

    import java.math.BigDecimal;
    import java.util.List;
    import java.util.UUID;

    import static org.junit.jupiter.api.Assertions.assertAll;
    import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.junit.jupiter.api.Assertions.assertThrows;
    import static org.junit.jupiter.api.Assertions.assertTrue;

    @SpringBootTest
    class LeadServiceIntegrationTest {

        @Autowired
        private LeadService leadService;

        @Autowired
        private LeadRepository leadRepository;

        @Autowired
        private DealRepository dealRepository;

        @Autowired
        private LeadProcessor leadProcessor;

        @Autowired
        private PlatformTransactionManager transactionManager;

        @Autowired
        private EntityManager entityManager;

        private TransactionTemplate txTemplateDefault;
        private TransactionTemplate txTemplateReadCommitted;
        private TransactionTemplate txTemplateRepeatableRead;

        private Lead createValidLead(LeadStatus status) {
            Lead lead = new Lead("test-" + System.nanoTime() + "@email.com", "TestCorp", status);
            return leadRepository.save(lead);
        }

        private Lead createLeadWithEmail(LeadStatus status, String email) {
            Lead lead = new Lead(email, "TestCorp", status);
            return leadRepository.save(lead);
        }

        @BeforeEach
        void setUpTemplates() {
            txTemplateDefault = new TransactionTemplate(transactionManager);

            txTemplateReadCommitted = new TransactionTemplate(transactionManager);
            txTemplateReadCommitted.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

            txTemplateRepeatableRead = new TransactionTemplate(transactionManager);
            txTemplateRepeatableRead.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        }

        @Test
        void convertLeadToDeal_shouldRollbackOnConstraintViolation() {
            Lead lead = new Lead("test-" + System.nanoTime() + "@email.com", "TestCorp", LeadStatus.QUALIFIED);
            lead = leadRepository.save(lead);

            long initialDealsCount = dealRepository.findAll().size();

            CreateDealRequest request = new CreateDealRequest();
            request.setTitle("Deal with null amount");
            request.setAmount(null);
            request.setCompanyId(UUID.randomUUID());

            UUID leadId = lead.getId();

            assertThrows(
                    NullPointerException.class,
                    () -> leadService.convertLeadToDeal(leadId, request)
            );
            Lead persistedLead = leadRepository.findById(leadId).orElseThrow();
            assertEquals(LeadStatus.QUALIFIED, persistedLead.getStatus());
            assertEquals(initialDealsCount, dealRepository.findAll().size());
        }

        @Test
        void convertLeadToDeal_shouldCommitOnSuccess() {
            Lead lead = new Lead("commit-test+" + System.nanoTime() + "@test.com", "Corp", LeadStatus.QUALIFIED);
            lead = leadRepository.save(lead);
            UUID leadId = lead.getId();

            CreateDealRequest request = new CreateDealRequest();
            request.setTitle("Успешная сделка");
            request.setAmount(BigDecimal.valueOf(5000));
            request.setCompanyId(UUID.randomUUID());
            Deal deal = leadService.convertLeadToDeal(leadId, request);
            entityManager.clear();
            assertTrue(dealRepository.findById(deal.getId()).isPresent());
            Lead updatedLead = leadRepository.findById(leadId).orElseThrow();
            assertEquals(LeadStatus.CONTACTED, updatedLead.getStatus());
        }

        @Test
        @Transactional
        void convertLeadToDeal_shouldThrowExceptionWhenLeadNotQualified() {
            Lead lead = createValidLead(LeadStatus.NEW);
            UUID leadId = lead.getId();
            entityManager.flush();
            entityManager.clear();
            List<UUID> existingDealIds = dealRepository.findAll().stream()
                    .map(Deal::getId)
                    .toList();
            CreateDealRequest request = new CreateDealRequest();
            request.setTitle("Тестовая сделка");
            request.setAmount(BigDecimal.valueOf(10000));
            request.setCompanyId(UUID.randomUUID());
            IllegalLeadStateException exception = assertThrows(
                    IllegalLeadStateException.class,
                    () -> leadService.convertLeadToDeal(leadId, request)
            );
            assertTrue(exception.getMessage().contains(leadId.toString()));
            assertTrue(exception.getMessage().contains(LeadStatus.NEW.name()));
            entityManager.flush();
            entityManager.clear();
            List<Deal> allDeals = dealRepository.findAll();
            List<UUID> currentDealIds = allDeals.stream()
                    .map(Deal::getId)
                    .toList();
            assertEquals(existingDealIds.size(), allDeals.size(),
                    "Количество сделок не должно измениться");
            assertTrue(currentDealIds.containsAll(existingDealIds),
                    "Все старые сделки должны остаться");
            Lead unchangedLead = leadRepository.findById(leadId).orElseThrow();
            assertEquals(LeadStatus.NEW, unchangedLead.getStatus(),
                    "Статус лида не должен измениться");
        }

        @Test
        @Transactional
        void demonstrateSelfInvocationProblem() {
            Lead lead1 = createValidLead(LeadStatus.NEW);
            Lead lead2 = createLeadWithEmail(LeadStatus.NEW,
                    "throw-exception-" + System.nanoTime() + "@test.com");
            Lead lead3 = createValidLead(LeadStatus.NEW);

            entityManager.flush();

            final UUID id1 = lead1.getId();
            final UUID id2 = lead2.getId();
            final UUID id3 = lead3.getId();
            assertThrows(RuntimeException.class,
                    () -> leadService.processLeads(List.of(id1, id2, id3)));
            entityManager.flush();
            entityManager.clear();
            Lead checked1 = leadRepository.findById(id1).orElseThrow();
            Lead checked2 = leadRepository.findById(id2).orElseThrow();
            Lead checked3 = leadRepository.findById(id3).orElseThrow();

            assertAll(
                    () -> assertEquals(LeadStatus.NEW, checked1.getStatus(),
                            "Lead 1 должен быть NEW из-за self-invocation"),
                    () -> assertEquals(LeadStatus.NEW, checked2.getStatus(),
                            "Lead 2 должен быть NEW"),
                    () -> assertEquals(LeadStatus.NEW, checked3.getStatus(),
                            "Lead 3 должен быть NEW")
            );
        }

        @Test
        void processLeads_shouldIsolateTransactionsPerLead() {
            Lead lead1 = createValidLead(LeadStatus.NEW);
            Lead lead2 = createLeadWithEmail(LeadStatus.NEW,
                    "throw-exception-" + System.nanoTime() + "@test.com");
            Lead lead3 = createValidLead(LeadStatus.NEW);

            final UUID id1 = lead1.getId();
            final UUID id2 = lead2.getId();
            final UUID id3 = lead3.getId();
            RuntimeException exception = assertThrows(RuntimeException.class, () ->
                    leadService.processLeads(List.of(id1, id2, id3)));
            assertTrue(
                    exception.getMessage().contains(id2.toString()) ||
                            (exception.getCause() != null && exception.getCause()
                                    .getMessage().contains(id2.toString())),
                    "Исключение должно ссылаться на lead id2"
            );
            Lead result1 = leadRepository.findById(id1).orElseThrow();
            Lead result2 = leadRepository.findById(id2).orElseThrow();
            Lead result3 = leadRepository.findById(id3).orElseThrow();
            assertAll(
                    () -> assertEquals(LeadStatus.CONTACTED, result1.getStatus(),
                            "id1: REQUIRES_NEW-транзакция должна закоммититься до исключения"),
                    () -> assertEquals(LeadStatus.NEW, result2.getStatus(),
                            "id2: REQUIRES_NEW-транзакция должна откатиться из-за исключения"),
                    () -> assertEquals(LeadStatus.NEW, result3.getStatus(),
                            "id3: не должен обрабатываться — цикл прерван после исключения на id2")
            );
            leadRepository.deleteById(id1);
            leadRepository.deleteById(id2);
            leadRepository.deleteById(id3);
        }

        @Test
        void propagation_REQUIRED_shouldReuseTransaction() {
            System.setProperty("test.fail.required", "true");
            try {
                Lead outerLead = createValidLead(LeadStatus.NEW);
                UUID outerId = outerLead.getId();
                String originalCompany = outerLead.getCompany();
                entityManager.clear();
                long countBefore = leadRepository.count();
                assertThrows(RuntimeException.class, () ->
                        txTemplateDefault.execute(status -> {
                            Lead outer = leadRepository.findById(outerId).orElseThrow();
                            outer.setCompany("Changed-In-Tx");
                            leadRepository.save(outer);
                            leadProcessor.methodRequired();
                            return null;
                        })
                );
                entityManager.clear();
                long countAfter = leadRepository.count();
                Lead result = leadRepository.findById(outerId).orElseThrow();
                assertAll(
                        () -> assertEquals(countBefore, countAfter),
                        () -> assertEquals(originalCompany, result.getCompany()),
                        () -> assertEquals(LeadStatus.NEW, result.getStatus())
                );
                leadRepository.delete(result);
            } finally {
                System.clearProperty("test.fail.required");
                leadRepository.findAll().stream()
                        .filter(l -> l.getEmail().contains("required+"))
                        .forEach(leadRepository::delete);
            }
        }

        @Test
        void propagation_REQUIRES_NEW_shouldCreateNewTransaction() {
            System.setProperty("test.fail.required.new", "true");
            try {
                Lead outerLead = createValidLead(LeadStatus.NEW);
                UUID outerId = outerLead.getId();
                entityManager.clear();
                long countBefore = leadRepository.count();
                assertDoesNotThrow(() ->
                        txTemplateDefault.execute(status -> {
                            Lead outer = leadRepository.findById(outerId).orElseThrow();
                            outer.setCompany("Before-Inner");
                            leadRepository.save(outer);
                            try {
                                leadProcessor.methodRequiredNew();
                            } catch (RuntimeException e) {
                            }
                            outer.setCompany("After-Inner");
                            leadRepository.save(outer);

                            return null;
                        })
                );
                entityManager.clear();
                Lead resultOuter = leadRepository.findById(outerId).orElseThrow();
                long innerLeadsCount = leadRepository.findAll().stream()
                        .filter(l -> l.getEmail().contains("requires-new+"))
                        .count();
                assertAll(
                        () -> assertEquals("After-Inner", resultOuter.getCompany(),
                                "Внешние изменения (Метод А) должны закоммититься"),
                        () -> assertEquals(0, innerLeadsCount,
                                "Внутренняя транзакция (Метод Б с REQUIRES_NEW) должна откатиться"),
                        () -> assertEquals(LeadStatus.NEW, resultOuter.getStatus(),
                                "Статус внешнего лида не должен измениться (мы меняли только company)")
                );
                leadRepository.delete(resultOuter);
            } finally {
                System.clearProperty("test.fail.required.new");
                leadRepository.findAll().stream()
                        .filter(l -> l.getEmail().contains("requires-new+"))
                        .forEach(leadRepository::delete);
            }
        }

        @Test
        void isolation_READ_COMMITTED_allowsNonRepeatableRead() {
            Lead lead = createValidLead(LeadStatus.NEW);
            UUID leadId = lead.getId();
            String originalCompany = lead.getCompany();

            final String[] readValues = new String[2];

            txTemplateReadCommitted.execute(status -> {
                Lead firstRead = leadRepository.findById(leadId).orElseThrow();
                readValues[0] = firstRead.getCompany();

                entityManager.clear();

                txTemplateDefault.execute(status2 -> {
                    Lead toUpdate = leadRepository.findById(leadId).orElseThrow();
                    toUpdate.setCompany("Updated-In-Second-Tx");
                    leadRepository.save(toUpdate);
                    return null;
                });

                Lead secondRead = leadRepository.findById(leadId).orElseThrow();
                readValues[1] = secondRead.getCompany();

                return null;
            });

            assertEquals(originalCompany, readValues[0]);
            assertEquals("Updated-In-Second-Tx", readValues[1]);

            leadRepository.delete(lead);
        }
        @Test
        void isolation_REPEATABLE_READ_preventsNonRepeatableRead() {
            Lead lead = createValidLead(LeadStatus.NEW);
            UUID leadId = lead.getId();
            String originalCompany = lead.getCompany();

            final String[] readValues = new String[2];

            txTemplateRepeatableRead.execute(status -> {
                Lead firstRead = leadRepository.findById(leadId).orElseThrow();
                readValues[0] = firstRead.getCompany();
                entityManager.clear();
                TransactionTemplate txRequiresNew = new TransactionTemplate(transactionManager);
                txRequiresNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                txRequiresNew.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

                txRequiresNew.execute(status2 -> {
                    entityManager.clear();
                    Lead toUpdate = leadRepository.findById(leadId).orElseThrow();
                    toUpdate.setCompany("Updated-In-Second-Tx");
                    leadRepository.save(toUpdate);
                    return null;
                });
                Lead secondRead = leadRepository.findById(leadId).orElseThrow();
                readValues[1] = secondRead.getCompany();

                return null;
            });
            assertEquals(originalCompany, readValues[0]);
            assertEquals(originalCompany, readValues[1] + readValues[1]);

            leadRepository.delete(lead);
        }
    }

