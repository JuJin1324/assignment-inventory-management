# Task 2 — 예외 → HTTP 상태 매핑 (실행 기록)

입력: [tasks.md](tasks.md) §Task 2

## 무엇을 했는가

출고 흐름이 던지는 예외를 일관된 HTTP 에러 응답으로 매핑했다 — 대상 재고 없음은 404, 잘못된 입력(빈 productId·0·음수 수량)은 400. 잘못된 입력은 경계에서 bean validation으로 끊고, 매핑은 `@RestControllerAdvice` 한곳에 모은다. `@WebMvcTest` 슬라이스로 404·400을 검증하고, 앱을 띄워 전 구간(성공·부족·404·400)을 실제 HTTP로 확인했다.

- `spring-boot-starter-validation` 추가
- `ShipmentRequest` — `@NotBlank String productId`, `@Min(1) int quantity`
- `ShipmentController` — `@Valid @RequestBody`
- `ErrorResponse`(신규) — `{status, message}` 최소 모양
- `ApiExceptionHandler`(신규, `@RestControllerAdvice`) — `StockNotFoundException` → 404, `MethodArgumentNotValidException` → 400(필드 오류를 메시지로 합침)
- `ShipmentControllerTest`에 404·400(수량)·400(productId) 3건 추가
- `StockFixtures.ShipmentRequestTestBuilder`에 `notFoundProductId()`·`zeroQuantity()`·`blankProductId()` 추가, `"NO-SUCH-PRODUCT"`를 `NOT_FOUND_PRODUCT_ID` 상수로 승급

## 근거·결정 사항

- **입력 오류 = 경계 bean validation(`@Valid` → 400)** — 0·음수·빈 productId를 도메인까지 보내지 않고 웹 경계에서 끊는다. 슬라이스 테스트가 서비스 목 없이 깔끔하고, 잘못된 요청이 오케스트레이션을 태우지 않는다. 도메인 예외(`IllegalArgumentException`) 방식과의 갈림길에서 경계 검증을 택했다.
- **검증 이중화는 의도** — 경계(`@Min(1)`)와 도메인(`Stock.ship`의 `IllegalArgumentException`) 양쪽이 수량을 막는다. API 경로에선 경계가 먼저 끊어 도메인 IAE는 도달하지 않지만, 도메인 방어는 불변식 보호로 그대로 둔다(계층 방어). 그래서 advice에 `IllegalArgumentException` 핸들러는 두지 않았다 — 현재 API로는 도달 불가라 죽은 매핑이 된다.
- **부족(409)은 advice 밖** — 부족은 예외가 아닌 정상 분기(반환값)라 컨트롤러 인라인 처리를 유지하고, advice는 예외 두 건(404·400)만 다룬다. 전역 예외 처리로 과설계하지 않는다.
- **`ErrorResponse(int status, String message)`** — 두 매핑 공통의 최소 에러 바디. 표준화는 필요한 두 매핑에만 맞춘다.
- **테스트 픽스처 정리** — `"NO-SUCH-PRODUCT"` 리터럴을 테스트에서 빼 빌더(`notFoundProductId()`) 안에 감추고, 테스트는 `request.productId()`로 되읽어 mock 예외에 넘긴다(ShipServiceTest의 `command.productId()` 관례와 동일). 0·빈값도 `zeroQuantity()`·`blankProductId()`로 의미 메서드를 쓴다(StockTestBuilder 결). 공유 리터럴은 `NOT_FOUND_PRODUCT_ID` 상수로 승급.

## 결과

생성·변경된 파일:

```
build.gradle                                                      (starter-validation 추가)
src/main/java/com/deepfine/inventory/web/ShipmentRequest.java     (@NotBlank·@Min)
src/main/java/com/deepfine/inventory/web/ShipmentController.java  (@Valid)
src/main/java/com/deepfine/inventory/web/ErrorResponse.java       (신규)
src/main/java/com/deepfine/inventory/web/ApiExceptionHandler.java (신규 — @RestControllerAdvice)
src/test/java/com/deepfine/inventory/web/ShipmentControllerTest.java (404·400 3건 추가)
src/test/java/com/deepfine/inventory/StockFixtures.java           (빌더 메서드·상수)
```

검증:

- `./gradlew test` → 전체 15건 통과(ShipmentControllerTest 5: 성공 200·부족 409·404·400 수량·400 productId)
- 앱 + Docker PostgreSQL 기동, 실제 HTTP로 전 구간 확인:
  - 성공 `POST PROD-001 ×30` → `200 {"remainingQuantity":70}`
  - 부족 `POST PROD-001 ×1000` → `409 {"remainingQuantity":70}`
  - 404 `POST NO-SUCH-PRODUCT ×10` → `404 {"status":404,"message":"재고를 찾을 수 없습니다: productId=NO-SUCH-PRODUCT"}`
  - 400 `POST PROD-001 ×0` → `400 {"status":400,"message":"quantity 1 이상이어야 합니다"}`
  - 400 `POST 빈 productId ×10` → `400 {"status":400,"message":"productId 공백일 수 없습니다"}`

## 완료 기준 점검

- [x] 상품 없음에 404, 잘못된 입력에 400이 일관된 에러 바디로 돌아오는 상태
- [x] `@WebMvcTest` 슬라이스 테스트로 404·400이 통과하는 상태
- [x] 앱을 띄워 실제 HTTP 요청으로 출고 흐름(성공·부족·404·400)이 동작함을 확인한 상태

## 남은 일 / 다음 행동

- Story 4가 닫혔다 — 출고 처리 에픽의 happy-path 흐름(도메인 → 영속 → 서비스 → API)이 완성됐다.
- 동시성(동시 재고 변경 충돌)은 이 흐름 위에 별도 에픽에서 얹는다.
- 커밋은 사용자 확인 후 (PR 단위).
