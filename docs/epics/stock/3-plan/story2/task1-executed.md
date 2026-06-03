# Task 1 실행 기록 — StockService + StockResult

## 무엇을 했나

재고 조회 전용 서비스 레이어를 추가했다.

- `service/dto/StockResult.java` — `quantity` 하나를 싣는 record
- `service/StockService.java` — `query(String productId)` 구현. `findByProductId`로 조회해 `StockResult` 반환, 없으면 `ProductNotFoundException`

## 근거·결정

- `@Transactional(readOnly = true)` — 쓰기가 없는 조회 전용 유스케이스. flush 생략·읽기 최적화.
- 서비스 입력은 `String productId` 직접 수신 — 단일 파라미터라 Command 래퍼 없이도 충분.

## 결과

컴파일 통과 (`BUILD SUCCESSFUL`).

## 다음 행동

Task 2 — StockResponse + GET 엔드포인트 + 슬라이스 테스트.
