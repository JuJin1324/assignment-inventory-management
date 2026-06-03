# Task 2 실행 기록 — 앱 띄워 실제 HTTP 확인

## 무엇을 했는가

Docker PostgreSQL을 기동하고 앱을 띄워 입고 엔드포인트 전 구간을 실제 HTTP로 확인했다.

## 결과

| 시나리오 | 요청 | 응답 |
|---|---|---|
| 신규 상품 입고 | `POST /api/products/receipts` PROD-001, qty 50 | 200 `{"quantity": 50}` |
| 기존 상품 입고 (수량 증가) | `POST /api/products/receipts` PROD-001, qty 30 | 200 `{"quantity": 80}` |
| 수량 0 | qty 0 | 400 |
| name blank | name "" | 400 |

## 다음 행동

S4 완료. S5(모델링 동기화, 비코딩) execute-task로 진입한다.
