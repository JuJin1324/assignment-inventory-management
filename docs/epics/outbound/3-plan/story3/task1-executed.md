# Task 1 — 출고 서비스 (실행 기록)

입력: [tasks.md](tasks.md) §Task 1

## 무엇을 했는가

도메인과 영속 사이를 잇는 출고 서비스를 세웠다 — productId·수량을 받아 재고를 조회하고 도메인 `ship`을 태운 뒤, 성공이면 저장한다. repository를 목으로 둔 단위테스트로 성공·부족·상품 없음을 검증한다(ADR-007).

- `StockRepository.findByProductId(String)` 추가
- `Stock`에 `product_id` 인덱스(`@Table(indexes = @Index(...))`)
- `ShipCommand`(서비스 입력)·`ShipResult`(서비스 결과)·`StockNotFoundException`
- `ShipService`(`@Service`·`@Transactional`) — 조회 → ship → 성공 시 저장
- `ShipServiceTest` — Mockito 목 단위테스트(성공·부족·상품 없음)
- 테스트 픽스처를 `StockFixtures`(중첩 빌더)로 재구성

## 근거·결정 사항

- **서비스 입력 = `ShipCommand` record** — `ship(String, int)` 대신 입력을 객체로 묶었다. 입력이 늘어도 시그니처가 흔들리지 않고, 테스트 픽스처(빌더)로 묶기 좋다. 검증은 싣지 않고 단순 운반체로(도메인이 검증).
- **서비스 결과 = `ShipResult(ShipmentResult outcome, int remainingQuantity)`** — 도메인 결과(성공/부족)와 남은 재고를 함께 돌려준다. `ShipmentResult`가 enum(단순 값)이라 record 필드로 두는 건 무거운 중첩이 아니다 — boolean으로 평탄화하지 않고 도메인 의미를 살렸다. productId는 안 싣는다(호출자가 이미 쥐고 있어 중복).
- **상품 없음 = `StockNotFoundException`** — `findByProductId`가 비면 던진다. 입력 오류(0·음수 → 400)와 "대상 없음(404)"은 Story 4에서 다른 HTTP 상태로 갈리므로 타입으로 구분. productId를 보관하고 static 메시지 템플릿으로 메시지를 만든다.
- **성공 시 명시적 `save()`** — `@Transactional` 더티체킹에 맡길 수도 있으나, 목 단위테스트가 "부족이면 save 호출 안 됨"을 단언하려면 명시적 호출이 있어야 한다(ADR-007). 의미도 또렷.
- **product_id 인덱스 = `@Index`(ddl-auto 유지)** — `findByProductId`가 풀 스캔 안 되게 인덱스를 둔다. schema.sql로 전환하면 H2 테스트 경로가 얽혀(ADR-006 재검토로 번짐) 비용이 크므로, `@Table(indexes = @Index(...))`로 선언해 ddl-auto가 만들게 했다. 운영 수준 스키마 관리(Flyway 등)가 필요해지면 별도 결정으로 올린다.
- **테스트 픽스처 = `StockFixtures`(중첩 빌더)** — Story 1에서 플랫 `StockFixture`로 두며 "둘째 빌더가 생기면 중첩으로 승급"하기로 했는데, `ShipCommand` 빌더가 생기는 게 그 트리거였다. `StockTestBuilder`·`ShipCommandTestBuilder`를 내장으로 묶고, 공유 기본값 `DEFAULT_PRODUCT_ID`를 바깥 상수로 올려 두 빌더가 같은 상품을 가리키게 했다(테스트 stub도 이 상수를 참조해 매직스트링 제거). 의도를 드러내는 무인자 메서드(`negativeQuantity()`·`zeroQuantity()`·`notFoundProductId()`)를 두고, productId를 임의로 받는 setter는 제거 — 테스트가 productId를 일일이 지정하지 않아 의도가 또렷.

## 결과

생성·변경된 파일:

```
src/main/java/com/deepfine/inventory/domain/Stock.java            (product_id @Index)
src/main/java/com/deepfine/inventory/domain/StockRepository.java  (findByProductId)
src/main/java/com/deepfine/inventory/service/ShipCommand.java     (신규 — 입력)
src/main/java/com/deepfine/inventory/service/ShipResult.java      (신규 — 결과)
src/main/java/com/deepfine/inventory/service/StockNotFoundException.java  (신규)
src/main/java/com/deepfine/inventory/service/ShipService.java     (신규 — 오케스트레이션)
src/test/java/com/deepfine/inventory/service/ShipServiceTest.java (신규 — 목 단위테스트)
src/test/java/com/deepfine/inventory/StockFixtures.java           (신규 — 중첩 빌더)
src/test/java/com/deepfine/inventory/domain/StockFixture.java     (삭제 — StockFixtures로 통합)
src/test/.../ShipTest.java, StockCreationTest.java, StockPersistenceTest.java  (Fixtures 마이그레이션)
Makefile                                                         (db-desc — 테이블 컬럼 확인 타깃)
```

검증:

- `./gradlew test` → 10건 통과(ShipServiceTest 3: 성공·부족·상품 없음 / ShipTest 4 / StockCreationTest 2 / StockPersistenceTest 1)
- PostgreSQL 인덱스 — `bootRun`에서 `create index idx_stock_product_id` 로그, psql `\d stock`에 `idx_stock_product_id btree (product_id)` 확인

## 완료 기준 점검

- [x] 서비스가 productId·수량으로 재고를 조회 → 차감 → 저장하고 결과(남은 재고/부족)를 돌려주는 상태
- [x] 재고 부족 시 저장하지 않고 부족을 알리는 상태
- [x] 조회부터 저장까지가 한 트랜잭션 경계(`@Transactional`) 안에서 도는 상태
- [x] repository 목 단위테스트로 성공·부족 흐름이 통과하는 상태

## 남은 일 / 다음 행동

- **Story 3 완료.** 출고 서비스가 도메인·영속을 엮어 출고를 오케스트레이션한다.
- 다음은 **Story 4 — API 노출**. plan-tasks는 그 Story 진입 직전에 별도 사이클로. ADR(API 계층 테스트 방식)을 먼저 정한다. `StockNotFoundException`(404)·입력 오류(400)·부족(거부 응답)의 HTTP 매핑이 거기서 붙는다.
- 커밋은 사용자 확인 후 (PR 단위).
