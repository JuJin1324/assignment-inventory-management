package com.deepfine.inventory.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 재고 영속 — Spring Data JPA. 기본 저장·조회(save/findById 등)에 더해 productId로 조회한다.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

	Optional<Product> findByProductId(String productId);
}
