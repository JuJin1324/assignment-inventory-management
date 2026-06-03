# Story 1 실행 기록 — 낙관 락 충돌 → 409 매핑

## 무엇을 했는가

`BaseEntity`에 이미 있던 `@Version`을 활용해, 충돌 시 발생하는 `ObjectOptimisticLockingFailureException`을 `ApiExceptionHandler`에서 409 Conflict로 매핑했다. 재시도는 없다.

## 근거·결정 사항

- `ApiExceptionHandler` — `ObjectOptimisticLockingFailureException` + `OptimisticLockingFailureException` → 409 매핑 추가 (ADR-012)
- 슬라이스 테스트(`@WebMvcTest`) — 서비스가 낙관 락 예외를 던질 때 409 응답 검증
- 영속 테스트(`@DataJpaTest`) — 같은 version 0으로 로드된 두 엔티티 중 하나가 먼저 저장되면 나머지 저장 시 `ObjectOptimisticLockingFailureException` 발생 검증

## 결과

변경 파일:
- `src/main/java/com/deepfine/inventory/web/ApiExceptionHandler.java`
- `src/test/java/com/deepfine/inventory/web/ProductShipControllerTest.java`
- `src/test/java/com/deepfine/inventory/domain/ProductPersistenceTest.java`

## 다음 행동

S2(시나리오 동기화, 비코딩) execute-task로 진입한다.
