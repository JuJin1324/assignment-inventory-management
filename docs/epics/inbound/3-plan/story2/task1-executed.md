# Task 1 실행 기록 — ADR-011 작성: 컨트롤러·서비스 입자 규약

## 무엇을 했는가

백로그 결정 메모(컨트롤러·서비스 입자 규약)를 ADR-011로 승급했다. 코드 변경 없이 문서만.

## 근거·결정 사항

- 백로그 메모에서 "두 번째 기능이 합류할 때"가 ADR 트리거였다. 입고(receive)가 합류하는 이 시점이 정확히 그 조건.
- 컨트롤러: 리소스 묶기 채택 (`ProductController`가 `/api/products/*` 전체 담당)
- 서비스: 유스케이스별 분리 채택 (`ShipService`, `ReceiveService` 등)
- 두 결정은 역방향 쌍 — 컨트롤러는 묶고, 서비스는 쪼갠다.

## 결과

- `docs/adr/adr-011-controller-service-granularity.md` 작성

## 다음 행동

Task 2(컨트롤러 리소스 묶기 코드 변경)로 진입한다.
