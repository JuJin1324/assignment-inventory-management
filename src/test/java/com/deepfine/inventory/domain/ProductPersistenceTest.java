package com.deepfine.inventory.domain;

import static com.deepfine.inventory.ProductFixtures.aProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.deepfine.inventory.config.JpaAuditingConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

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
		Product product = aProduct().build();

		// when
		Product saved = productRepository.save(product);
		em.flush();
		em.clear();
		Product found = productRepository.findById(saved.getId()).orElseThrow();

		// then
		assertThat(found.getProductId()).isEqualTo(product.getProductId());
		assertThat(found.getName()).isEqualTo(product.getName());
		assertThat(found.getQuantity()).isEqualTo(product.getQuantity());
		assertThat(found.getCreatedAt()).isNotNull();
		assertThat(found.getUpdatedAt()).isNotNull();
		assertThat(found.getVersion()).isEqualTo(0L);
	}

	@Test
	@DisplayName("버전 불일치 저장 시도 → ObjectOptimisticLockingFailureException")
	void save_withStaleVersion_throwsOptimisticLockException() {
		// given: 저장 후 두 참조가 같은 엔티티를 같은 버전으로 로드
		Product saved = productRepository.save(aProduct().build());
		em.flush();
		em.clear();

		Product first = productRepository.findById(saved.getId()).orElseThrow();
		Product second = productRepository.findById(saved.getId()).orElseThrow();
		em.getEntityManager().detach(second); // second는 version 0으로 분리

		// first가 먼저 수정·저장 → DB version 1로 증가
		first.receive(10);
		productRepository.save(first);
		em.flush();
		em.clear();

		// second는 version 0 상태로 저장 시도 → 충돌
		second.receive(5);
		assertThatThrownBy(() -> {
			productRepository.save(second);
			em.flush();
		}).isInstanceOf(ObjectOptimisticLockingFailureException.class);
	}
}
