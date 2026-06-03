# Task 1 실행 기록 — 입고 엔드포인트

## 무엇을 했는가

`ProductController`에 입고 엔드포인트(`POST /api/products/receipts`)를 추가했다. `ReceiveRequest` DTO로 요청을 받아 `ReceiveService`를 태우고, 입고 후 수량을 `ReceiveResponse`로 응답한다.

## 근거·결정 사항

- **응답 코드**: 신규·기존 모두 200 — `ReceiveService`가 신규/기존을 구분해 반환하지 않으므로 변경 최소화. 필요하면 이후 `ReceiveResult`에 필드 추가로 분리 가능
- **DTO**: `name` 항상 필수(`@NotBlank`) — API 경계에서 단순하게 막음. 기존 상품 요청 시 name은 redundant하지만 서비스에서 무시됨
- **경로**: `/api/products/receipts` — `/shipments`와 명사 복수형으로 일관
- `ApiExceptionHandler` 변경 없음 — `MethodArgumentNotValidException`(400)·`ProductNotFoundException`(404) 매핑이 이미 입고 흐름도 덮음

## 결과

신규 파일:
- `web/ReceiveRequest.java`
- `web/ReceiveResponse.java`
- `web/ProductReceiveControllerTest.java` (ADR-011 테스트 규약)

변경 파일:
- `web/ProductController.java` — `ReceiveService` 주입·`/receipts` 엔드포인트 추가
- `web/ProductShipControllerTest.java` — `ReceiveService` 목 추가

전체 테스트 통과.

## 다음 행동

Task 2(앱 띄워 실제 HTTP 확인)로 진입한다.
