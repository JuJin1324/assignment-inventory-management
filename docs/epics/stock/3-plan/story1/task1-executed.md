# Task 1 실행 기록 — service DTO → service.dto

## 무엇을 했나

`service` 패키지에 있던 Command·Result DTO 4개를 `service.dto` 서브패키지로 이동하고 영향받는 파일의 import를 갱신했다.

이동한 파일:
- `ReceiveCommand`, `ReceiveResult`, `ShipCommand`, `ShipResult` → `service/dto/`

import 갱신한 파일:
- `ReceiveService`, `ShipService` — 같은 패키지라 import 없이 쓰던 것을 명시 추가
- `ShipmentRequest`, `ReceiveRequest` — `toCommand()` 변환에서 사용
- `ProductController` — `ReceiveResult`, `ShipResult` 반환 타입
- `ProductFixtures` — `ReceiveCommand`, `ShipCommand` 빌더
- `ReceiveServiceTest`, `ShipServiceTest` — 같은 패키지에서 암묵적으로 쓰던 것을 명시 추가
- `ProductReceiveControllerTest`, `ProductShipControllerTest` — `ReceiveResult`, `ShipResult` 직접 생성

## 결과

전체 테스트 통과 (`BUILD SUCCESSFUL`).

## 다음 행동

Task 2 — web DTO → web.dto 이동.
