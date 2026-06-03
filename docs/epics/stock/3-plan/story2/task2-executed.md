# Task 2 실행 기록 — StockResponse + GET 엔드포인트 + 테스트

## 무엇을 했나

- `web/dto/StockResponse.java` 추가 — `quantity` 하나를 싣는 record
- `ProductController`에 `GET /api/products/{productId}` 추가 — `StockService.query()` 호출 후 `StockResponse` 반환
- `ProductStockControllerTest` 슬라이스 테스트 추가 — 정상 조회(200), 미등록(404)
- `ProductControllerTestBase` 기반 클래스 추출 — `@WebMvcTest` + `MockMvc` + `ObjectMapper` + `MockitoBean` 3개를 한 곳으로 통합. 세 컨트롤러 테스트가 상속으로 공유하며, 서비스 추가 시 기반 클래스만 수정하면 되는 구조

## 결과

전체 테스트 통과 (`BUILD SUCCESSFUL`).

## 다음 행동

Story 2 완료. Story 3(시나리오 동기화)은 비코딩 — 바로 execute-task로.
