package ru.mentee.power.crm.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadJpaRepository;
import ru.mentee.power.crm.spring.repository.CompanyRepository;
import ru.mentee.power.crm.spring.repository.DealRepository;
import ru.mentee.power.crm.spring.service.LeadProcessor;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadServiceUnitTest {

    @Mock
    private LeadJpaRepository leadJpaRepository;

    @Mock
    private DealRepository dealRepository;

    @Mock
    private LeadProcessor leadProcessor;

    @Mock
    private CompanyRepository companyRepository;

    private LeadService leadService;

    @BeforeEach
    void setUp() {
        leadService = new LeadService(leadJpaRepository, dealRepository, leadProcessor, companyRepository);
    }

    @Test
    void addLead_WhenCompanyExists_ShouldUseExistingCompany() {
        String email = "test@example.com";
        String companyName = "Test Company";
        Company existingCompany = new Company();
        existingCompany.setId(UUID.randomUUID());
        existingCompany.setName(companyName);
        existingCompany.setIndustry("Industry");
        LeadStatus status = LeadStatus.NEW;

        when(companyRepository.findByName(companyName)).thenReturn(Optional.of(existingCompany));
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadService.addLead(email, companyName, status);

        verify(companyRepository).findByName(companyName);
        verify(companyRepository, never()).save(any(Company.class));
        verify(leadJpaRepository).save(any(Lead.class));

        ArgumentCaptor<Lead> leadCaptor = ArgumentCaptor.forClass(Lead.class);
        verify(leadJpaRepository).save(leadCaptor.capture());
        Lead savedLead = leadCaptor.getValue();
        assertThat(savedLead.getEmail()).isEqualTo(email);
        assertThat(savedLead.getCompany()).isEqualTo(existingCompany);
        assertThat(savedLead.getStatus()).isEqualTo(status);
    }

    @Test
    void addLead_WhenCompanyDoesNotExist_ShouldCreateNewCompany() {
        String email = "test@example.com";
        String companyName = "New Company";
        LeadStatus status = LeadStatus.NEW;
        Company newCompany = new Company();
        newCompany.setId(UUID.randomUUID());
        newCompany.setName(companyName);

        when(companyRepository.findByName(companyName)).thenReturn(Optional.empty());
        when(companyRepository.save(any(Company.class))).thenReturn(newCompany);
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadService.addLead(email, companyName, status);

        verify(companyRepository).findByName(companyName);
        verify(companyRepository).save(any(Company.class));
        verify(leadJpaRepository).save(any(Lead.class));

        ArgumentCaptor<Lead> leadCaptor = ArgumentCaptor.forClass(Lead.class);
        verify(leadJpaRepository).save(leadCaptor.capture());
        Lead savedLead = leadCaptor.getValue();
        assertThat(savedLead.getEmail()).isEqualTo(email);
        assertThat(savedLead.getCompany()).isEqualTo(newCompany);
    }

    @Test
    void findAll_ShouldReturnAllLeads() {
        Company company1 = new Company();
        company1.setName("Company1");
        Lead lead1 = new Lead("lead1@example.com", company1, LeadStatus.NEW);
        lead1.setId(UUID.randomUUID());

        Company company2 = new Company();
        company2.setName("Company2");
        Lead lead2 = new Lead("lead2@example.com", company2, LeadStatus.CONTACTED);
        lead2.setId(UUID.randomUUID());

        List<Lead> leads = Arrays.asList(lead1, lead2);

        when(leadJpaRepository.findAll()).thenReturn(leads);

        List<Lead> result = leadService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(lead1, lead2);
        verify(leadJpaRepository).findAll();
    }

    @Test
    void findByStatus_ShouldFilterLeadsByStatus() {
        Company company1 = new Company();
        company1.setName("Company1");
        Lead lead1 = new Lead("lead1@example.com", company1, LeadStatus.NEW);
        lead1.setId(UUID.randomUUID());

        Company company2 = new Company();
        company2.setName("Company2");
        Lead lead2 = new Lead("lead2@example.com", company2, LeadStatus.NEW);
        lead2.setId(UUID.randomUUID());

        Company company3 = new Company();
        company3.setName("Company3");
        Lead lead3 = new Lead("lead3@example.com", company3, LeadStatus.CONTACTED);
        lead3.setId(UUID.randomUUID());

        List<Lead> allLeads = Arrays.asList(lead1, lead2, lead3);

        when(leadJpaRepository.findAll()).thenReturn(allLeads);

        List<Lead> result = leadService.findByStatus(LeadStatus.NEW);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(lead1, lead2);
        verify(leadJpaRepository).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnLead() {
        UUID id = UUID.randomUUID();
        Company company = new Company();
        company.setName("Company");
        Lead lead = new Lead("test@example.com", company, LeadStatus.NEW);
        lead.setId(id);

        when(leadJpaRepository.findById(id)).thenReturn(Optional.of(lead));

        Optional<Lead> result = leadService.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(lead);
        verify(leadJpaRepository).findById(id);
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        UUID id = UUID.randomUUID();

        when(leadJpaRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Lead> result = leadService.findById(id);

        assertThat(result).isEmpty();
        verify(leadJpaRepository).findById(id);
    }

    @Test
    void update_WhenLeadExists_ShouldUpdateAndSave() {
        UUID id = UUID.randomUUID();
        Company oldCompany = new Company();
        oldCompany.setName("Old Company");
        Lead existingLead = new Lead("old@example.com", oldCompany, LeadStatus.NEW);
        existingLead.setId(id);

        Company newCompany = new Company();
        newCompany.setName("New Company");
        Lead updatedLead = new Lead("new@example.com", newCompany, LeadStatus.CONTACTED);

        when(leadJpaRepository.findById(id)).thenReturn(Optional.of(existingLead));
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Lead result = leadService.update(id, updatedLead);

        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getCompany().getName()).isEqualTo("New Company");
        assertThat(result.getStatus()).isEqualTo(LeadStatus.CONTACTED);
        verify(leadJpaRepository).findById(id);
        verify(leadJpaRepository).save(existingLead);
    }

    @Test
    void update_WhenLeadNotExists_ShouldThrowResponseStatusException() {
        UUID id = UUID.randomUUID();
        Company newCompany = new Company();
        newCompany.setName("New Company");
        Lead updatedLead = new Lead("new@example.com", newCompany, LeadStatus.CONTACTED);

        when(leadJpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leadService.update(id, updatedLead))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);
        verify(leadJpaRepository).findById(id);
        verify(leadJpaRepository, never()).save(any(Lead.class));
    }

    @Test
    void delete_WhenLeadExists_ShouldDelete() {
        UUID id = UUID.randomUUID();
        Company company = new Company();
        company.setName("Company");
        Lead lead = new Lead("test@example.com", company, LeadStatus.NEW);
        lead.setId(id);

        when(leadJpaRepository.findById(id)).thenReturn(Optional.of(lead));

        leadService.delete(id);

        verify(leadJpaRepository).findById(id);
        verify(leadJpaRepository).deleteById(id);
    }

    @Test
    void delete_WhenLeadNotExists_ShouldThrowResponseStatusException() {
        UUID id = UUID.randomUUID();

        when(leadJpaRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leadService.delete(id))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);
        verify(leadJpaRepository).findById(id);
        verify(leadJpaRepository, never()).deleteById(any());
    }

    @Test
    void findLeads_WithNoFilters_ShouldReturnAllLeads() {
        Company company1 = new Company();
        company1.setName("Company1");
        Lead lead1 = new Lead("lead1@example.com", company1, LeadStatus.NEW);
        lead1.setId(UUID.randomUUID());

        Company company2 = new Company();
        company2.setName("Company2");
        Lead lead2 = new Lead("lead2@example.com", company2, LeadStatus.CONTACTED);
        lead2.setId(UUID.randomUUID());

        List<Lead> allLeads = Arrays.asList(lead1, lead2);

        when(leadJpaRepository.findAll()).thenReturn(allLeads);

        List<Lead> result = leadService.findLeads(null, null);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(lead1, lead2);
    }

    @Test
    void findLeads_WithBlankSearchAndBlankStatus_ShouldReturnAllLeads() {
        Company company = new Company();
        company.setName("Company1");
        Lead lead1 = new Lead("lead1@example.com", company, LeadStatus.NEW);
        lead1.setId(UUID.randomUUID());

        List<Lead> allLeads = Arrays.asList(lead1);

        when(leadJpaRepository.findAll()).thenReturn(allLeads);

        List<Lead> result = leadService.findLeads("", "");

        assertThat(result).hasSize(1);
    }

    @Test
    void findLeads_WithSearchFilter_ShouldFilterByEmail() {
        Company company1 = new Company();
        company1.setName("Company1");
        Lead lead1 = new Lead("john@example.com", company1, LeadStatus.NEW);
        lead1.setId(UUID.randomUUID());

        Company company2 = new Company();
        company2.setName("Company2");
        Lead lead2 = new Lead("jane@example.com", company2, LeadStatus.NEW);
        lead2.setId(UUID.randomUUID());

        Company company3 = new Company();
        company3.setName("Company3");
        Lead lead3 = new Lead("bob@example.com", company3, LeadStatus.NEW);
        lead3.setId(UUID.randomUUID());

        List<Lead> allLeads = Arrays.asList(lead1, lead2, lead3);

        when(leadJpaRepository.findAll()).thenReturn(allLeads);

        List<Lead> result = leadService.findLeads("john", null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findLeads_WithSearchFilterCaseInsensitive_ShouldFilterByEmail() {
        Company company1 = new Company();
        company1.setName("Company1");
        Lead lead1 = new Lead("JOHN@example.com", company1, LeadStatus.NEW);
        lead1.setId(UUID.randomUUID());

        Company company2 = new Company();
        company2.setName("Company2");
        Lead lead2 = new Lead("jane@example.com", company2, LeadStatus.NEW);
        lead2.setId(UUID.randomUUID());

        List<Lead> allLeads = Arrays.asList(lead1, lead2);

        when(leadJpaRepository.findAll()).thenReturn(allLeads);

        List<Lead> result = leadService.findLeads("john", null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("JOHN@example.com");
    }

    @Test
    void findLeads_WithStatusFilter_ShouldFilterByStatus() {
        Company company1 = new Company();
        company1.setName("Company1");
        Lead lead1 = new Lead("lead1@example.com", company1, LeadStatus.NEW);
        lead1.setId(UUID.randomUUID());

        Company company2 = new Company();
        company2.setName("Company2");
        Lead lead2 = new Lead("lead2@example.com", company2, LeadStatus.CONTACTED);
        lead2.setId(UUID.randomUUID());

        Company company3 = new Company();
        company3.setName("Company3");
        Lead lead3 = new Lead("lead3@example.com", company3, LeadStatus.NEW);
        lead3.setId(UUID.randomUUID());

        List<Lead> allLeads = Arrays.asList(lead1, lead2, lead3);

        when(leadJpaRepository.findAll()).thenReturn(allLeads);

        List<Lead> result = leadService.findLeads(null, "NEW");

        assertThat(result).hasSize(2);
        assertThat(result).extracting("status").containsOnly(LeadStatus.NEW);
    }

    @Test
    void findLeads_WithStatusFilterCaseInsensitive_ShouldFilterByStatus() {
        Company company1 = new Company();
        company1.setName("Company1");
        Lead lead1 = new Lead("lead1@example.com", company1, LeadStatus.NEW);
        lead1.setId(UUID.randomUUID());

        Company company2 = new Company();
        company2.setName("Company2");
        Lead lead2 = new Lead("lead2@example.com", company2, LeadStatus.CONTACTED);
        lead2.setId(UUID.randomUUID());

        List<Lead> allLeads = Arrays.asList(lead1, lead2);

        when(leadJpaRepository.findAll()).thenReturn(allLeads);

        List<Lead> result = leadService.findLeads(null, "new");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(LeadStatus.NEW);
    }

    @Test
    void findLeads_WithBothFilters_ShouldFilterByBoth() {
        Company company1 = new Company();
        company1.setName("Company1");
        Lead lead1 = new Lead("john@example.com", company1, LeadStatus.NEW);
        lead1.setId(UUID.randomUUID());

        Company company2 = new Company();
        company2.setName("Company2");
        Lead lead2 = new Lead("john.doe@example.com", company2, LeadStatus.CONTACTED);
        lead2.setId(UUID.randomUUID());

        Company company3 = new Company();
        company3.setName("Company3");
        Lead lead3 = new Lead("jane@example.com", company3, LeadStatus.NEW);
        lead3.setId(UUID.randomUUID());

        List<Lead> allLeads = Arrays.asList(lead1, lead2, lead3);

        when(leadJpaRepository.findAll()).thenReturn(allLeads);

        List<Lead> result = leadService.findLeads("john", "NEW");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
        assertThat(result.get(0).getStatus()).isEqualTo(LeadStatus.NEW);
    }

    @Test
    void save_WhenCompanyIsNullAndCompanyNameIsNotNull_ShouldCreateOrFindCompany() {
        String companyName = "Test Company";
        Lead lead = new Lead("test@example.com", null, LeadStatus.NEW);
        lead.setCompanyName(companyName);

        Company existingCompany = new Company();
        existingCompany.setId(UUID.randomUUID());
        existingCompany.setName(companyName);

        when(companyRepository.findByName(companyName)).thenReturn(Optional.of(existingCompany));
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadService.save(lead);

        verify(companyRepository).findByName(companyName);
        verify(companyRepository, never()).save(any(Company.class));
        assertThat(lead.getCompany()).isEqualTo(existingCompany);
        verify(leadJpaRepository).save(lead);
    }

    @Test
    void save_WhenCompanyIsNullAndCompanyNameIsNotNull_AndCompanyDoesNotExist_ShouldCreateCompany() {
        String companyName = "New Company";
        Lead lead = new Lead("test@example.com", null, LeadStatus.NEW);
        lead.setCompanyName(companyName);

        Company newCompany = new Company();
        newCompany.setId(UUID.randomUUID());
        newCompany.setName(companyName);

        when(companyRepository.findByName(companyName)).thenReturn(Optional.empty());
        when(companyRepository.save(any(Company.class))).thenReturn(newCompany);
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadService.save(lead);

        verify(companyRepository).findByName(companyName);
        verify(companyRepository).save(any(Company.class));
        assertThat(lead.getCompany()).isEqualTo(newCompany);
        verify(leadJpaRepository).save(lead);
    }

    @Test
    void save_WhenCompanyHasNullId_ShouldFindOrCreateCompany() {
        Company companyWithoutId = new Company();
        companyWithoutId.setName("Existing Company");
        companyWithoutId.setId(null);
        Lead lead = new Lead("test@example.com", null, LeadStatus.NEW);
        lead.setCompany(companyWithoutId);

        Company existingCompany = new Company();
        existingCompany.setId(UUID.randomUUID());
        existingCompany.setName("Existing Company");

        when(companyRepository.findByName("Existing Company")).thenReturn(Optional.of(existingCompany));
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadService.save(lead);

        verify(companyRepository).findByName("Existing Company");
        verify(companyRepository, never()).save(any(Company.class));
        assertThat(lead.getCompany()).isEqualTo(existingCompany);
        verify(leadJpaRepository).save(lead);
    }

    @Test
    void save_WhenCompanyHasNullId_AndCompanyDoesNotExist_ShouldSaveCompany() {
        Company companyWithoutId = new Company();
        companyWithoutId.setName("New Company");
        companyWithoutId.setId(null);

        Lead lead = new Lead("test@example.com", null, LeadStatus.NEW);
        lead.setCompany(companyWithoutId);

        Company savedCompany = new Company();
        savedCompany.setId(UUID.randomUUID());
        savedCompany.setName("New Company");

        when(companyRepository.findByName("New Company")).thenReturn(Optional.empty());
        when(companyRepository.save(companyWithoutId)).thenReturn(savedCompany);
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadService.save(lead);

        verify(companyRepository).findByName("New Company");
        verify(companyRepository).save(companyWithoutId);
        assertThat(lead.getCompany()).isEqualTo(savedCompany);
        verify(leadJpaRepository).save(lead);
    }

    @Test
    void save_WhenCompanyHasId_ShouldNotModifyCompany() {
        Company companyWithId = new Company();
        companyWithId.setId(UUID.randomUUID());
        companyWithId.setName("Existing Company");

        Lead lead = new Lead("test@example.com", companyWithId, LeadStatus.NEW);

        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadService.save(lead);

        verify(companyRepository, never()).findByName(any());
        verify(companyRepository, never()).save(any(Company.class));
        assertThat(lead.getCompany()).isEqualTo(companyWithId);
        verify(leadJpaRepository).save(lead);
    }

    @Test
    void save_WithTrimmedCompanyName_ShouldTrimBeforeLookup() {
        String companyNameWithSpaces = "  Test Company  ";
        Lead lead = new Lead("test@example.com", null, LeadStatus.NEW);
        lead.setCompanyName(companyNameWithSpaces);

        Company existingCompany = new Company();
        existingCompany.setId(UUID.randomUUID());
        existingCompany.setName("Test Company");

        when(companyRepository.findByName("Test Company")).thenReturn(Optional.of(existingCompany));
        when(leadJpaRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadService.save(lead);

        verify(companyRepository).findByName("Test Company");
        assertThat(lead.getCompany()).isEqualTo(existingCompany);
    }

    @Test
    void findByEmail_ShouldReturnLead() {
        String email = "test@example.com";
        Company company = new Company();
        company.setName("Company");
        Lead lead = new Lead(email, company, LeadStatus.NEW);
        lead.setId(UUID.randomUUID());

        when(leadJpaRepository.findByEmail(email)).thenReturn(Optional.of(lead));

        Optional<Lead> result = leadService.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(lead);
        verify(leadJpaRepository).findByEmail(email);
    }

    @Test
    void findByEmail_WhenNotExists_ShouldReturnEmpty() {
        String email = "nonexistent@example.com";

        when(leadJpaRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<Lead> result = leadService.findByEmail(email);

        assertThat(result).isEmpty();
        verify(leadJpaRepository).findByEmail(email);
    }

    @Test
    void findByStatuses_ShouldReturnLeadsByStatuses() {
        Company company1 = new Company();
        company1.setName("Company1");
        Lead lead1 = new Lead("lead1@example.com", company1, LeadStatus.NEW);
        lead1.setId(UUID.randomUUID());

        Company company2 = new Company();
        company2.setName("Company2");
        Lead lead2 = new Lead("lead2@example.com", company2, LeadStatus.CONTACTED);
        lead2.setId(UUID.randomUUID());

        List<Lead> leads = Arrays.asList(lead1, lead2);

        when(leadJpaRepository.findByStatusIn(Arrays.asList(LeadStatus.NEW, LeadStatus.CONTACTED))).thenReturn(leads);

        List<Lead> result = leadService.findByStatuses(LeadStatus.NEW, LeadStatus.CONTACTED);

        assertThat(result).hasSize(2);
        verify(leadJpaRepository).findByStatusIn(Arrays.asList(LeadStatus.NEW, LeadStatus.CONTACTED));
    }

    @Test
    void getFirstPage_ShouldReturnFirstPageSortedByCreatedAtDesc() {
        Company company1 = new Company();
        company1.setName("Company1");
        Lead lead1 = new Lead("lead1@example.com", company1, LeadStatus.NEW);
        lead1.setId(UUID.randomUUID());

        Company company2 = new Company();
        company2.setName("Company2");
        Lead lead2 = new Lead("lead2@example.com", company2, LeadStatus.CONTACTED);
        lead2.setId(UUID.randomUUID());

        Page<Lead> page = new PageImpl<>(Arrays.asList(lead1, lead2));
        int pageSize = 10;

        PageRequest expectedPageRequest = PageRequest.of(0, pageSize, Sort.by("createdAt").descending());
        when(leadJpaRepository.findAll(expectedPageRequest)).thenReturn(page);

        Page<Lead> result = leadService.getFirstPage(pageSize);

        assertThat(result.getContent()).hasSize(2);
        verify(leadJpaRepository).findAll(expectedPageRequest);
    }
}