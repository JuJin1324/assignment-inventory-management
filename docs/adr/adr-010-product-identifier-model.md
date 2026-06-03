# ADR-010: 상품 식별자 모델 — productId 비즈니스 키 유지

## 날짜

2026-06-03

## 상태

채택 (Accepted)

## 맥락

통합된 `Product`(ADR-009)는 대리 키 `id`(Long, auto-gen)와 비즈니스 키 `productId`(String)를 갖는다 — 출고에서 이미 둘 다 존재한다. 과제 데이터는 `{id, name, quantity}`로 `id`를 유일 식별자로 준다. 입고는 "미등록 상품이면 신규 등록 후 입고"라 find-or-create의 "이미 있음"을 가를 키가 필요하다. 식별자를 어떻게 둘지 정해야 한다.

## 후보군

### 1. productId 비즈니스 키 유지 + name 추가

`Product {id(대리), productId(비즈니스), name, quantity}`. find-or-create는 `productId`로 판별.

- **장점:** 안정된 비즈니스 키로 신규 상품도 식별 가능(대리 `id`는 신규엔 아직 없다). 출고 코드·요청 불변(리네임 + name 추가). 대리 `id`(DB 시퀀스)를 API에 노출하지 않는다.
- **단점:** 과제 `{id, name, quantity}`에 없는 `productId`가 하나 더 붙어 키가 둘(대리 + 비즈니스).

### 2. 과제 id로 단일화

`Product {id, name, quantity}`만. `productId` 제거, 요청도 `id` 참조.

- **장점:** 과제 구조를 문자 그대로 따른다.
- **단점:** 대리 `id`(auto-gen)는 신규 상품 등록 전엔 없어, find-or-create 판별을 `name` 등으로 해야 한다(이름 충돌 위험). 출고 요청이 `id` 참조로 바뀌어 churn이 크다. DB 시퀀스를 API에 노출하게 된다.

## 결정

**`productId` 비즈니스 키를 유지하고 `name`을 더한다.** find-or-create는 `productId`로 "이미 있음"을 가른다. 대리 `id`는 내부 PK로만 쓰고 API에는 노출하지 않는다.

이 결정으로 입고 find-or-create의 판별 키(Story 3 ADR 후보 "미등록 상품 판별 키")도 함께 굳는다 — `productId`다.

## 근거

- **find-or-create엔 안정된 키가 필요** — 대리 `id`는 신규 상품에 아직 없으므로 판별 키가 될 수 없고, `name`은 충돌 위험이 있다. `productId`가 그 자리에 정확히 맞는다.
- **출고 골격 보존** — 출고가 이미 `productId`로 조회·요청을 처리하므로 churn이 최소다.
- **식별자 캡슐화** — DB 시퀀스(대리 `id`)를 외부 API에 그대로 노출하지 않는다. MVP라도 내부 키와 외부 키를 가른다.

## 재검토 트리거

외부 카탈로그의 식별자(예: 표준 SKU)를 비즈니스 키로 채택해야 하거나, `productId`의 의미·유일성 정책이 바뀌면 식별자 모델을 다시 본다.
