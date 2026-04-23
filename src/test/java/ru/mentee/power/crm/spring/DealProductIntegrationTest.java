package ru.mentee.power.crm.spring;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.entity.DealProduct;
import ru.mentee.power.crm.model.Product;
import ru.mentee.power.crm.repository.DealJpaRepository;
import ru.mentee.power.crm.spring.repository.ProductJpaRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DealProductIntegrationTest {

	@Autowired
	private DealJpaRepository dealRepository;

	@Autowired
	private ProductJpaRepository productRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void testSaveDealWithProducts() {
		UUID fakeLeadId = UUID.randomUUID();

		Deal deal = new Deal(fakeLeadId, new BigDecimal("150000"));
		Product product1 = new Product();
		product1.setName("Ноутбук Dell");
		product1.setSku("LAPTOP-001");
		product1.setPrice(new BigDecimal("90000"));

		Product product2 = new Product();
		product2.setName("Монитор LG");
		product2.setSku("MONITOR-001");
		product2.setPrice(new BigDecimal("25000"));

		productRepository.save(product1);
		productRepository.save(product2);

		DealProduct dealProduct1 = new DealProduct();
		dealProduct1.setProduct(product1);
		dealProduct1.setQuantity(2);
		dealProduct1.setUnitPrice(new BigDecimal(81000));

		DealProduct dealProduct2 = new DealProduct();
		dealProduct2.setProduct(product2);
		dealProduct2.setQuantity(1);
		dealProduct2.setUnitPrice(new BigDecimal(25000));

		deal.addDealProduct(dealProduct1);
		deal.addDealProduct(dealProduct2);

		dealRepository.save(deal);

		Deal loadedDeal = dealRepository.findById(deal.getId()).orElseThrow();

		assertThat(loadedDeal.getDealProducts()).hasSize(2);
		DealProduct loadedDp1 = loadedDeal.getDealProducts().get(0);
		DealProduct loadedDp2 = loadedDeal.getDealProducts().get(1);

		assertThat(loadedDp1.getQuantity()).isEqualTo(2);
		assertThat(loadedDp1.getUnitPrice()).isEqualByComparingTo(new BigDecimal("81000"));

		assertThat(loadedDp2.getUnitPrice()).isEqualByComparingTo(new BigDecimal("25000"));
		assertThat(loadedDp2.getProduct().getId()).isEqualTo(product2.getId());
	}

	@Test
	void testEntityGraphSolvesNPlusOne() {
		UUID companyId = UUID.randomUUID();
		try {
			entityManager.createNativeQuery("INSERT INTO companies (id, name) VALUES (?, 'Test Company')")
					.setParameter(1, companyId).executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException("Не удалось создать компанию. Проверьте структуру таблицы companies", e);
		}

		UUID leadId = UUID.randomUUID();
		try {
			entityManager.createNativeQuery("INSERT INTO leads (id, email, company_id) VALUES (?, 'test@test.com', ?)")
					.setParameter(1, leadId).setParameter(2, companyId).executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException("Не удалось создать лид. Проверьте структуру таблицы leads", e);
		}

		Deal deal = new Deal(leadId, new BigDecimal("205000"));

		Product p1 = createAndSaveProduct("Ноутбук", "SKU-1", new BigDecimal("90000"));
		Product p2 = createAndSaveProduct("Монитор", "SKU-2", new BigDecimal("25000"));
		Product p3 = createAndSaveProduct("Мышь", "SKU-3", new BigDecimal("5000"));

		DealProduct dp1 = new DealProduct();
		dp1.setProduct(p1);
		dp1.setQuantity(1);
		dp1.setUnitPrice(new BigDecimal("90000"));

		DealProduct dp2 = new DealProduct();
		dp2.setProduct(p2);
		dp2.setQuantity(2);
		dp2.setUnitPrice(new BigDecimal("25000"));

		DealProduct dp3 = new DealProduct();
		dp3.setProduct(p3);
		dp3.setQuantity(3);
		dp3.setUnitPrice(new BigDecimal("5000"));

		deal.addDealProduct(dp1);
		deal.addDealProduct(dp2);
		deal.addDealProduct(dp3);

		Deal savedDeal = dealRepository.save(deal);
		UUID dealId = savedDeal.getId();

		entityManager.flush();

		entityManager.clear();

		Deal dealWithoutGraph = dealRepository.findById(dealId).orElseThrow();

		int countWithoutGraph = 0;
		for (DealProduct dp : dealWithoutGraph.getDealProducts()) {
			if (dp.getProduct() != null) {
				countWithoutGraph++;
			}
		}
		assertThat(countWithoutGraph).isEqualTo(3);

		entityManager.clear();

		Deal dealWithGraph = dealRepository.findDealWithProducts(dealId).orElseThrow();

		int countWithGraph = 0;
		for (DealProduct dp : dealWithGraph.getDealProducts()) {
			if (dp.getProduct() != null) {
				countWithGraph++;
			}
		}
		assertThat(countWithGraph).isEqualTo(3);

		assertThat(dealWithoutGraph.getDealProducts().size()).isEqualTo(dealWithGraph.getDealProducts().size());
	}

	private Product createAndSaveProduct(String name, String sku, BigDecimal price) {
		Product p = new Product();
		p.setName(name);
		p.setSku(sku);
		p.setPrice(price);
		return productRepository.save(p);
	}
}
