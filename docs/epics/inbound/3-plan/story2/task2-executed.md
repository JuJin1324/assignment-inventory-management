# Task 2 실행 기록 — 컨트롤러 리소스 묶기

## 무엇을 했는가

`ShipmentController`를 `ProductController`로 재편하고 엔드포인트 경로를 상품 리소스 기준으로 정리했다. 테스트도 ADR-011 테스트 규약에 따라 `ProductShipControllerTest`로 분리했다.

## 근거·결정 사항

- `@RequestMapping("/api/products")`를 클래스에 두고 `@PostMapping("/shipments")`로 출고 엔드포인트를 구성 — 이후 입고(`/api/products/receive` 등)가 같은 컨트롤러에 합류할 자리를 열어둠
- `ShipmentRequest`·`ShipmentResponse` DTO 이름은 유지 — 출고(shipment) 의미가 명확하고 S4에서 입고 DTO가 별도로 추가될 예정
- `ShipService`는 기존 그대로 유스케이스 단위로 서 있음 — 코드 변경 불필요

## 결과

변경 파일:
- `web/ProductController.java` (신규, `ShipmentController` 대체)
- `web/ProductShipControllerTest.java` (신규, `ShipmentControllerTest` 대체)
- `web/ShipmentController.java` (삭제)
- `web/ShipmentControllerTest.java` (삭제)

엔드포인트: `/api/stock/shipment` → `/api/products/shipments`

전체 테스트 통과.

## 다음 행동

S2 완료. S3(입고 도메인 + 서비스) plan-tasks로 진입한다.
