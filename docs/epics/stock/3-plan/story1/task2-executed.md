# Task 2 실행 기록 — web DTO → web.dto

## 무엇을 했나

`web` 패키지에 있던 Request·Response DTO 4개를 `web.dto` 서브패키지로 이동하고 영향받는 파일의 import를 갱신했다.

이동한 파일:
- `ReceiveRequest`, `ReceiveResponse`, `ShipmentRequest`, `ShipmentResponse` → `web/dto/`

import 갱신한 파일:
- `ProductController` — 4개 DTO 모두 명시 import 추가
- `ProductFixtures` — `ReceiveRequest`, `ShipmentRequest` import 갱신

## 결과

전체 테스트 통과 (`BUILD SUCCESSFUL`).

## 다음 행동

Story 1 완료. Story 2(재고 조회 엔드포인트) plan-tasks로.
