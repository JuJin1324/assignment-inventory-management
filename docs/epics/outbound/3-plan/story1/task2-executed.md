# Task 2 — 재고 + 정상 출고 (실행 기록)

입력: [tasks.md](tasks.md) §Task 2

## 무엇을 했는가

출고 도메인의 가장 작은 알맹이를 세웠다 — 재고를 순수 객체로 만들고, 재고가 충분할 때 출고 수량만큼 차감되게 했다. 정상 출고 하나를 단위 테스트로 증명한다.

- `Stock` 도메인 객체 — 상품 식별(`productId`) + 현재 수량(`quantity`), `ship(int)`로 차감
- `StockFixture` — 재고 테스트 데이터 빌더 (`aStock().quantity(100).build()`, ADR-003)
- `StockTest` — 재고 충분 시 정상 출고 1건 (Given/When/Then)
- Task 1의 임시 골격물 2개 제거 (`domain/package-info.java`, `SmokeTest.java`)
- Lombok 도입 (build.gradle)

## 근거·결정 사항

- **상품 식별 = `String productId`** — 과제 MVP 규모라 별도 `Product` 타입 없이 상품 코드 문자열로 최소화. 모델링·ADR에 `Product` 엔티티 결정이 없어 단순 값으로 둠. 식별 표현이 풍부해지면 그때 타입으로 승급.
- **`ship`은 void + 차감, 결과 표현 미확정** — 출고 결과(성공/부족)를 어떻게 돌려줄지는 부족 분기가 생기는 Task 3에서 정한다. 지금은 차감 후 `getQuantity()`로 남은 재고를 확인하는 방식으로 두어 결과 타입 확정을 미룸. 부족 가드·생성 불변식은 넣지 않음 (Task 3·4 몫) — 동작 증분을 정직하게 유지.
- **단순 수량 값, ADT 없음** — ADR-002 그대로. `Stock`은 수량 하나를 든 단순 객체.
- **Lombok `@Getter`로 접근자 가림** — getter는 도메인 행위가 아니라 접근 보일러플레이트. 가리면 클래스 본문에 생성자와 `ship`만 남아 "이 객체가 무엇을 하는가"가 또렷해진다. 생성자는 도메인 관심사라 명시적으로 둠(롬복으로 안 숨김) — Task 4의 생성 불변식이 여기 들어온다. `quantity`가 차감되어 `final`이 아니라 `@Value` 부적합, `@Getter`만 적용.
- **테스트 픽스처 = 플랫 `StockFixture` (Test Data Builder)** — `StockTestBuilder` 대신 역할 이름 `StockFixture`로. "재고 테스트 데이터를 공급한다"는 역할은 안정적이고, fluent 빌더는 그 메커니즘. 한 타입·한 빌더뿐인 지금은 중첩(`StockFixture` + 내부 `StockBuilder`) 계층이 일을 안 해 플랫로 둠 — 프리셋·둘째 빌더가 생기면 그때 중첩으로 승급(초기 구조 최소화).
- **`StockFixture` 기본 생성자 private (`@NoArgsConstructor(access = PRIVATE)`)** — 생성 진입점을 `aStock()` 하나로 강제. `new StockFixture()`를 컴파일 단계에서 차단.
- **임시 골격물 제거** — `package-info.java`는 `Stock`이 패키지를 물리화하면서, `SmokeTest`는 `StockTest`가 같은 JUnit5+AssertJ 파이프라인을 증명하면서 역할이 끝나 제거 (Task 1 실행 기록의 합의대로).

## 결과

생성·변경·삭제된 파일 (build/·.gradle/ 제외):

```
src/main/java/com/deepfine/inventory/domain/Stock.java        (신규 — @Getter, ship)
src/test/java/com/deepfine/inventory/domain/StockFixture.java (신규 — Test Data Builder)
src/test/java/com/deepfine/inventory/domain/StockTest.java    (신규 — 정상 출고 1건)
build.gradle                                                  (Lombok 의존성 추가)
src/main/java/com/deepfine/inventory/domain/package-info.java (삭제 — 임시 골격물)
src/test/java/com/deepfine/inventory/SmokeTest.java           (삭제 — 임시 골격물)
```

빌드·테스트 결과:

- `./gradlew test` → `BUILD SUCCESSFUL`
- `StockTest` → 정상 출고 1건 통과

## 완료 기준 점검

- [x] 재고가 충분할 때 출고로 수량이 차감되고 남은 재고가 확인됨
- [x] 정상 출고가 단위 테스트로 통과
- [x] DB·웹 의존 없음 (in-memory 순수 도메인)

## 남은 일 / 다음 행동

- **Task 3 — 재고 부족 거부** (execute-task): 재고보다 많이 요청하면 차감하지 않고 거부. 이 조각에서 **출고 결과 표현(성공/부족)을 확정**한다 — 단순 값, ADT 없이 (ADR-002).
- 사용자 검토 포인트: 상품 식별을 `String`으로 둔 것, `ship`의 void + getter 확인 방식 (Task 3에서 결과 표현이 정해지면 재검토 여지).
- 커밋은 사용자 확인 후 (PR 단위).
