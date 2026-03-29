package ru.mentee.power.crm.spring;

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
        dealProduct1.setUnitPrice(new BigDecimal (81000));

        DealProduct dealProduct2 = new DealProduct();
        dealProduct2.setProduct(product2);
        dealProduct2.setQuantity(1);
        dealProduct2.setUnitPrice(new BigDecimal (25000));

        deal.addDealProduct(dealProduct1);
        deal.addDealProduct(dealProduct2);

        dealRepository.save(deal);

        Deal loadedDeal = dealRepository.findById(deal.getId()).orElseThrow();

        assertThat(loadedDeal.getDealProducts()).hasSize(2);

    }
}