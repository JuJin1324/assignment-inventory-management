package com.deepfine.inventory.domain;

import static com.deepfine.inventory.ProductFixtures.aProduct;
import static org.assertj.core.api.Assertions.assertThat;

import com.deepfine.inventory.config.JpaAuditingConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

/**
 * Product 영속 검증 — 임베디드 H2로 저장·조회와 공통 컬럼·낙관락 매핑을 확인한다.
 * Auditing은 @DataJpaTest가 자동으로 안 켜므로 JpaAuditingConfig을 import한다.
 */
@DataJpaTest
@Import(JpaAuditingConfig.class)
class ProductPersistenceTest {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("저장 → 조회 → 값·공통 컬럼·version 채워짐")
	void save_then_find() {
		// given
		Product product = aProduct().quantity(100).build();

		// when
		Product saved = productRepository.save(product);
		em.flush();
		em.clear();
		Product found = productRepository.findById(saved.getId()).orElseThrow();

		// then
		assertThat(found.getProductId()).isEqualTo("PROD-001");
		assertThat(found.getQuantity()).isEqualTo(100);
		assertThat(found.getCreatedAt()).isNotNull();
		assertThat(found.getUpdatedAt()).isNotNull();
		assertThat(found.getVersion()).isEqualTo(0L);
	}
}
