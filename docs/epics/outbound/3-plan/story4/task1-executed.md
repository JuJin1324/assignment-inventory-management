# Task 1 — 출고 엔드포인트 (실행 기록)

입력: [tasks.md](tasks.md) §Task 1

## 무엇을 했는가

출고 REST 엔드포인트를 세웠다 — 요청을 받아 `ShipService`를 태우고, 성공이면 200, 부족이면 409로 남은(현재) 재고를 돌려준다. `@WebMvcTest` 슬라이스로 성공·부족을 검증한다(ADR-008).

- `spring-boot-starter-web` 추가
- `ShipmentController` — `POST /api/stock/shipment`
- `ShipmentRequest`(`toCommand()`)·`ShipmentResponse`(남은 재고) DTO
- `ShipmentControllerTest` — `@WebMvcTest` 슬라이스, `@MockitoBean ShipService`, 성공·부족
- `StockFixtures`에 `ShipmentRequestTestBuilder` 추가, 테스트 JSON 직렬화 헬퍼

## 근거·결정 사항

- **엔드포인트 = `POST /api/stock/shipment`** — 출고는 독립 리소스가 아니라 재고에 귀속된 행위다. CRUD로 안 떨어지는 행위는 POST + 행위명으로 고정하는 컨벤션을 따른다. (`/api/shipments`에서 변경)
- **부족 = 409 Conflict 인라인** — 부족(`ShipResult.INSUFFICIENT`)은 예외가 아닌 정상 분기(반환값)라 `@RestControllerAdvice`가 아니라 컨트롤러가 직접 매핑한다. 현재 재고 상태와 충돌이라 409.
- **응답 = `ShipmentResponse(int remainingQuantity)`** — 성공 200·부족 409 둘 다 남은(현재) 재고만 싣고, 성공/부족은 HTTP 상태로 전한다. 내부 `ShipmentResult` enum은 응답에 노출하지 않는다.
- **`ShipmentRequest.toCommand()`** — 웹 DTO를 서비스 입력(`ShipCommand`)과 분리하고 변환을 요청 DTO가 맡는다.
- **`spring-boot-starter` 제거** — `spring-boot-starter-web`·`data-jpa`가 코어 스타터를 전이 의존으로 포함해 명시 의존이 중복이었다.
- **`@MockitoBean`** — `@MockBean`(Boot 3.4+ deprecated) 대신.
- **테스트 정리** — URL을 `SHIP_URL` 상수로(컨트롤러 경로와 어긋나면 잡힘). 요청 본문은 수기 JSON 문자열 대신 `ShipmentRequestTestBuilder`로 객체를 만들고 `asJson` 헬퍼로 직렬화 — 빌더는 객체 생성만, 직렬화는 테스트 인프라에 두고 앱이 설정한 autowired `ObjectMapper`를 쓴다(빌더 안에 ObjectMapper를 넣으면 관심사가 섞이고 앱과 다른 매퍼로 divergence 위험).

### 보류 (백로그로)

- **컨트롤러·서비스 입자 규약** — 기능이 늘 때 액션별 다중 vs 리소스/도메인 단위로 묶기. 액션이 `ship` 하나뿐인 지금 정하면 추측 설계라, 두 번째 기능(입고·조회) 진입 시 ADR로 결정하기로 백로그에 메모([../../backlog.md](../../backlog.md) 결정 메모).

## 결과

생성·변경된 파일:

```
build.gradle                                                      (web starter 추가, 코어 starter 제거)
src/main/java/com/deepfine/inventory/web/ShipmentController.java  (신규)
src/main/java/com/deepfine/inventory/web/ShipmentRequest.java     (신규)
src/main/java/com/deepfine/inventory/web/ShipmentResponse.java    (신규)
src/test/java/com/deepfine/inventory/web/ShipmentControllerTest.java  (신규 — 슬라이스)
src/test/java/com/deepfine/inventory/StockFixtures.java           (ShipmentRequestTestBuilder)
```

검증:

- `./gradlew test` → 12건 통과(ShipmentControllerTest 2: 성공 200·부족 409 포함)

## 완료 기준 점검

- [x] 엔드포인트가 성공 시 남은 재고를 응답하는 상태
- [x] 부족 시 거부 응답(409)으로 돌려주는 상태(예외 아닌 정상 분기)
- [x] `@WebMvcTest` 슬라이스 테스트로 성공·부족이 통과하는 상태

## 남은 일 / 다음 행동

- **Task 2 — 예외 → HTTP 상태 매핑** (execute-task): `StockNotFoundException` → 404, 입력 오류(0·음수) → 400을 `@RestControllerAdvice`로. 지금은 이 예외들이 Spring 기본 500으로 떨어진다. 입력 오류를 도메인 예외로 받을지 요청 DTO `@Valid`로 받을지 정한다. 앱 띄워 전 구간(성공·부족·404·400) 실제 HTTP 확인.
- 커밋은 사용자 확인 후 (PR 단위).
