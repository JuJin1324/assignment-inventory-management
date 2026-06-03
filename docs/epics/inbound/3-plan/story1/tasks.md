# Story 1 — Task 목록

선행: [../stories.md](../stories.md) §Story 1

## 전체 흐름

```mermaid
graph LR
    T1["Task 1<br/>전 계층 Stock→Product 리네임 (동작 불변) (완료)"]
    T2["Task 2<br/>Product에 name 추가"]
    T1 --> T2
```

리네임은 한 PR로 원자적으로 간다 — 도메인·영속·서비스·웹·테스트에 흩어진 `Stock` 어휘를 `Product`로 한 번에 옮긴다(반쯤 바꾸면 빌드가 깨진다). `productId`·출고 동작은 건드리지 않아 검토 이야기는 "이름만 바뀜, 동작 불변" 하나다. 그 위에 `name`을 더하는 건 *모델 확장*이라는 다른 의도이고 additive라, Task 2로 분리해 검토를 가른다.

---

## Task 1 — 전 계층 Stock → Product 리네임 ✅ 완료

실행 기록: [task1-executed.md](task1-executed.md)

### 목표

`Stock` 도메인 어휘를 `Product`로 일괄 리네임한다. `productId`(비즈니스 키)·출고 동작·API 노출 방식은 그대로 두고, 빌드·테스트를 green으로 유지한다.

### 핵심 작업

- **도메인** — `Stock`→`Product`, `StockRepository`→`ProductRepository`(`findByProductId` 유지), `Stock.ship`→`Product.ship`, `StockNotFoundException`→`ProductNotFoundException`. (`ShipmentResult`는 출고 결과 enum이라 유지)
- **서비스** — `ShipService`·`ShipCommand`·`ShipResult`의 `Stock` 참조를 `Product`로 (`productId` 그대로)
- **웹** — `ShipmentController`·`ShipmentRequest`/`Response`·`ApiExceptionHandler`의 참조·예외 매핑을 `Product`로
- **영속** — 테이블·인덱스 매핑을 `product`로
- **테스트** — `StockFixtures`→`ProductFixtures`, 전 테스트의 어휘

### 이 Task에서 하지 않을 것

- `name` 추가 — Task 2
- 입고 기능·계층 재편(컨트롤러 리소스 묶기) — S2 이후
- API 식별자 노출 방식 변경 — 이미 `productId`만 노출(대리 `id`는 내부 PK)이라 그대로 둔다

### 완료 기준

- `Stock` 어휘가 `Product`로 바뀐 상태 (도메인·영속·서비스·웹·테스트)
- `productId` 비즈니스 키·출고 동작·API 노출이 불변인 상태
- 테이블이 `product`로 매핑되어 저장·조회되는 상태
- 전체 테스트가 그대로 통과하는 상태

---

## Task 2 — Product에 name 추가

### 목표

`Product`에 `name`을 더해 과제 구조 `{id, productId, name, quantity}`를 완성한다. 출고는 `name`을 쓰지 않고, 입고(S3)가 채울 자리를 마련한다.

### 핵심 작업

- `Product`에 `name` 필드·컬럼 추가
- 생성자·JPA 매핑에 반영
- 저장·조회에 `name`이 실리는지 확인

### 이 Task에서 하지 않을 것

- 입고에서 `name`을 받아 등록 — S3·S4
- `name` 필수화로 출고 경로를 강제 — 출고는 `name`이 필요 없다(필수/nullable 여부는 실행 시 판단)

### 완료 기준

- `Product`가 `name`을 들고 영속되는 상태
- 출고 동작·테스트가 그대로 통과하는 상태
- `name`이 입고가 채울 수 있게 열려 있는 상태

---

## 다음 사이클

T1 → T2 순서로 execute-task로 실행한다. 두 ADR(재고→상품 통합·식별자 모델)은 굳었으니, 실행 중 ADR 문서로 기록한다. S1이 닫히면 바닥이 상품으로 굳어 S2(계층 재편)의 plan-tasks로 진입한다.
