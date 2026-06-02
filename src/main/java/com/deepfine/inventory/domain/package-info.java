/**
 * 출고 도메인의 순수 객체(상품·재고·출고 로직)가 사는 패키지.
 *
 * <p>영속·웹에 의존하지 않는다 — 영속 경계는 의존 역전(repository 인터페이스)으로
 * 분리한다 (ADR-001). Task 2에서 상품(Product)·재고(Stock)와 출고 로직이 들어오고,
 * 영속 어댑터는 Story 2, 웹 어댑터는 Story 3에서 이 도메인 바깥에 붙는다.
 */
package com.deepfine.inventory.domain;
