package com.deepfine.inventory.domain;

import static com.deepfine.inventory.domain.StockFixture.aStock;
import static org.assertj.core.api.Assertions.assertThat;

import com.deepfine.inventory.config.JpaAuditingConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

/**
 * Stock 영속 검증 — 임베디드 H2로 저장·조회와 공통 컬럼·낙관락 매핑을 확인한다.
 * Auditing은 @DataJpaTest가 자동으로 안 켜므로 JpaAuditingConfig을 import한다.
 */
@DataJpaTest
@Import(JpaAuditingConfig.class)
class StockPersistenceTest {

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private TestEntityManager em;

	@Test
	@DisplayName("저장 → 조회 → 값·공통 컬럼·version 채워짐")
	void save_then_find() {
		// given
		Stock stock = aStock().productId("PROD-001").quantity(100).build();

		// when
		Stock saved = stockRepository.save(stock);
		em.flush();
		em.clear();
		Stock found = stockRepository.findById(saved.getId()).orElseThrow();

		// then
		assertThat(found.getProductId()).isEqualTo("PROD-001");
		assertThat(found.getQuantity()).isEqualTo(100);
		assertThat(found.getCreatedAt()).isNotNull();
		assertThat(found.getUpdatedAt()).isNotNull();
		assertThat(found.getVersion()).isEqualTo(0L);
	}
}
