# Task 2 — Product에 name 추가 (실행 기록)

입력: [tasks.md](tasks.md) §Task 2

## 무엇을 했는가

`Product`에 `name`을 더해 과제 구조 `{id, productId, name, quantity}`를 완성했다. 생성자를 `Product(productId, name, quantity)`로 두고, 테스트 픽스처가 기본 이름을 공급하게 했다. 출고 동작은 그대로다.

- `Product`에 `name` 필드(`@Column(nullable = false)`) 추가, 생성자 `Product(productId, name, quantity)`
- `ProductFixtures` — `DEFAULT_PRODUCT_NAME`("상품 A")·`name(String)` 세터 추가, 3-인자 생성자로 빌드
- `ProductPersistenceTest` — `name`이 저장·조회되는지 검증 추가

## 근거·결정 사항

- **`name` = `@Column(nullable = false)` + 단일 생성자** — 상품은 이름을 가진다는 과제 모델을 그대로 따라 `{productId, name, quantity}`를 온전한 한 레코드로 둔다. 출고 경로(`ShipService`)는 상품을 *생성*하지 않고 조회·차감만 하므로 name 강제의 부담이 없다 — 강제는 *생성 경로*(픽스처·향후 입고)에만 걸린다. tasks.md의 "출고 경로를 name으로 강제하지 않는다"를 지키면서 모델은 완전하게.
- **2-인자 생성자 → 3-인자로 교체(오버로드 안 둠)** — 기존 `Product(productId, quantity)`의 유일한 호출자가 `ProductFixtures.build()`라, 오버로드를 남기지 않고 단일 생성자로 바꿔 *이름 없는 상품* 생성 경로를 아예 막았다(blast radius는 픽스처 한 곳).
- **기본 이름은 픽스처에** — 출고 테스트들은 name을 쓰지 않으므로 픽스처 기본값("상품 A")으로 흘려보내고, name 자체 검증은 영속 테스트에서 한다.

## 결과

변경된 파일:

```
src/main/java/com/deepfine/inventory/domain/Product.java            (name 필드·생성자)
src/test/java/com/deepfine/inventory/ProductFixtures.java           (DEFAULT_PRODUCT_NAME·name() 세터)
src/test/java/com/deepfine/inventory/domain/ProductPersistenceTest.java (name 검증)
```

검증:

- `./gradlew test` → 전체 **15건 통과**(실패·에러 0). `ProductPersistenceTest`가 name 저장·조회를 확인.

## 완료 기준 점검

- [x] `Product`가 `name`을 들고 영속되는 상태
- [x] 출고 동작·테스트가 그대로 통과하는 상태
- [x] `name`이 입고가 채울 수 있게 열려 있는 상태 (`name(String)` 세터·생성자 인자)

## 남은 일 / 다음 행동

- **ADR 문서화** — 굳은 두 ADR을 문서로: 재고→상품 통합(ADR-009 후보), 식별자 모델 = productId 비즈니스 키 유지 + name(ADR-010 후보). S1을 닫기 전에 기록.
- **Story 1 종료** → S2(계층 재편)의 plan-tasks로 진입. `/api/stock` → 상품 리소스 경로 정리도 S2에서.
- 커밋은 사용자 확인 후 (PR 단위).
