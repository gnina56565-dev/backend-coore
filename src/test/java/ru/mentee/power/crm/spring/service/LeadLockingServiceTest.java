package ru.mentee.power.crm.spring.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadJpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LeadLockingServiceTest {

    @Autowired
    private LeadLockingService leadLockingService;

    @Autowired
    private LeadJpaRepository leadRepository;

    @Test
    void shouldPreventLostUpdate_whenPessimisticLockUsed() throws Exception {
        // Given: Lead с начальным статусом
        Lead lead = new Lead("concurrent@test.com", LeadStatus.NEW);
        lead = leadRepository.save(lead);
        UUID leadId = lead.getId();

        // When: Два потока одновременно обновляют Lead с pessimistic lock
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        Future<LeadStatus> task1 = executor.submit(() -> {
            startLatch.await(); // Синхронизируем старт
            Lead updated = leadLockingService.convertLeadToDealWithLock(leadId, LeadStatus.CONTACTED);
            doneLatch.countDown();
            return updated.getStatus();
        });

        Future<LeadStatus> task2 = executor.submit(() -> {
            startLatch.await();
            Lead updated = leadLockingService.convertLeadToDealWithLock(leadId, LeadStatus.QUALIFIED);
            doneLatch.countDown();
            return updated.getStatus();
        });

        startLatch.countDown(); // Запускаем оба потока одновременно
        doneLatch.await(10, TimeUnit.SECONDS); // Ждём завершения

        // Then: Оба обновления успешны, вторая транзакция ждала первую
        LeadStatus status1 = task1.get();
        LeadStatus status2 = task2.get();

        assertThat(status1).isIn(LeadStatus.CONTACTED, LeadStatus.QUALIFIED);
        assertThat(status2).isIn(LeadStatus.CONTACTED, LeadStatus.QUALIFIED);
        assertThat(status1).isNotEqualTo(status2);

        Lead finalLead = leadRepository.findById(leadId).orElseThrow();
        assertThat(finalLead.getStatus()).isIn(LeadStatus.CONTACTED, LeadStatus.QUALIFIED);

        executor.shutdown();
    }

    @Test
    void shouldThrowOptimisticLockException_whenConcurrentUpdateWithoutLock() throws Exception {
        // Given: Lead с optimistic locking через @Version
        Lead lead = new Lead("optimistic@test.com", LeadStatus.NEW);
        lead = leadRepository.save(lead);
        UUID leadId = lead.getId();

        // When: Два потока одновременно обновляют БЕЗ pessimistic lock
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch startLatch = new CountDownLatch(1);

        Future<?> task1 = executor.submit(() -> {
            startLatch.await();
            leadLockingService.updateLeadStatusOptimistic(leadId, LeadStatus.CONTACTED);
            return null;
        });

        Future<?> task2 = executor.submit(() -> {
            startLatch.await();
            Thread.sleep(50); // Небольшая задержка чтобы первая транзакция стартовала
            leadLockingService.updateLeadStatusOptimistic(leadId, LeadStatus.QUALIFIED);
            return null;
        });

        startLatch.countDown();

        // Then: Одна транзакция успешна, вторая выбрасывает OptimisticLockException
        boolean exceptionThrown = false;
        try {
            task1.get(5, TimeUnit.SECONDS);
            task2.get(5, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            // Одна из транзакций должна выбросить OptimisticLockException
            assertThat(e.getCause())
                    .isInstanceOfAny(ObjectOptimisticLockingFailureException.class);
            exceptionThrown = true;
        }

        assertThat(exceptionThrown).isTrue();
        executor.shutdown();
    }

    @Test
    void shouldDeadLock() throws Exception {
        Lead leadA = leadRepository.save(new Lead("a@test.com", "CompanyA", LeadStatus.NEW));
        Lead leadB = leadRepository.save(new Lead("b@test.com", "CompanyB", LeadStatus.NEW));

        List<UUID> order1 = List.of(leadA.getId(), leadB.getId());
        List<UUID> order2 = List.of(leadB.getId(), leadA.getId()); // Обратный порядок

        // When: Два потока одновременно обновляют Lead с pessimistic lock
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Future<?> task1 = executor.submit(() -> {
            try {
                startLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            leadLockingService.processLeadsInOrder(order1);
        });
        Future<?> task2 = executor.submit(() -> {
            try {
                startLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            leadLockingService.processLeadsInOrder(order2);
        });

        startLatch.countDown();
        boolean deadlockDetected = false;
        try {
            task1.get(10, TimeUnit.SECONDS);
            task2.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {

            if (e.getCause() instanceof org.springframework.dao.CannotAcquireLockException) {
                deadlockDetected = true;
            }
        }

        assertThat(deadlockDetected).isTrue();
        executor.shutdown();
    }
}