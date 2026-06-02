package com.deepfine.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 재고 영속 — Spring Data JPA. 기본 저장·조회(save/findById 등)를 그대로 쓴다.
 */
public interface StockRepository extends JpaRepository<Stock, Long> {
}
