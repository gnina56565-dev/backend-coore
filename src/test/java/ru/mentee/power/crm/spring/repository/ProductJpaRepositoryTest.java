package ru.mentee.power.crm.spring.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class ProductJpaRepositoryTest {

	@Autowired
	private ProductJpaRepository productRepository;

	@Test
	void shouldSaveAndFindProduct_whenValidData() {
		Product product = new Product();
		product.setName("Консультация по архитектуре");
		product.setSku("CONSULT-ARCH-001");
		product.setPrice(new BigDecimal("50000.00"));
		product.setActive(true);

		Product saved = productRepository.save(product);

		assertThat(saved.getId()).isNotNull();
		Optional<Product> found = productRepository.findById(saved.getId());
		assertThat(found).isPresent();
		assertThat(found.get().getSku()).isEqualTo("CONSULT-ARCH-001");
	}

	@Test
	void shouldfind_findBySku() {
		Product product = new Product();
		product.setName("Консультация по архитектуре");
		product.setSku("LAPTOP-001");
		product.setPrice(new BigDecimal("50000.00"));
		product.setActive(true);

		productRepository.saveAndFlush(product);

		Optional<Product> found = productRepository.findBySku("LAPTOP-001");

		assertThat(found).isPresent();
		assertThat(found.get().getSku()).isEqualTo("LAPTOP-001");
	}

	@Test
	void shouldFind_findByActiveTrue() {
		Product product1 = new Product();
		product1.setName("Продукт 1");
		product1.setSku("ACTIVE-001");
		product1.setPrice(new BigDecimal("50000.00"));
		product1.setActive(true);

		Product product2 = new Product();
		product2.setName("Продукт 2");
		product2.setSku("ACTIVE-002");
		product2.setPrice(new BigDecimal("100000.00"));
		product2.setActive(true);

		Product product3 = new Product();
		product3.setName("Продукт 3");
		product3.setSku("INACTIVE-001");
		product3.setPrice(new BigDecimal("150000.00"));
		product3.setActive(false);

		productRepository.save(product1);
		productRepository.save(product2);
		productRepository.save(product3);

		List<Product> result = productRepository.findByActiveTrue();

		assertThat(result).hasSize(2);
		assertThat(result).extracting(Product::getSku).containsExactlyInAnyOrder("ACTIVE-001", "ACTIVE-002");
	}

	@Test
	void shouldThrowException_whenSkuIsNotUnique() {
		Product product1 = new Product();
		product1.setName("Продукт 1");
		product1.setSku("TEST-001");
		product1.setPrice(new BigDecimal("50000.00"));
		product1.setActive(true);

		Product product2 = new Product();
		product2.setName("Продукт 2");
		product2.setSku("TEST-001");
		product2.setPrice(new BigDecimal("100000.00"));
		product2.setActive(true);

		productRepository.saveAndFlush(product1);

		assertThatThrownBy(() -> {
			productRepository.saveAndFlush(product2);
		}).isInstanceOf(DataIntegrityViolationException.class);
	}
}
