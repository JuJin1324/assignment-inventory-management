# Task 4 — 경계·불변식 방어 (실행 기록)

입력: [tasks.md](tasks.md) §Task 4

## 무엇을 했는가

출고의 경계와 재고 불변식을 못박았다 — 정확히 0까지 출고, 재고 수량 0 미만 불가, 비정상 출고 수량 거부. 이로써 Story 1 닫힘 기준(정상 출고·부족 거부·경계)이 모두 충족된다.

- `Stock` 생성자 가드 — `quantity < 0`이면 `IllegalArgumentException` (불변식: 수량 ≥ 0)
- `Stock.ship` 가드 — `quantity <= 0`이면 `IllegalArgumentException` (잘못된 출고 수량 방어)
- `ShipTest`에 경계·입력 검증 테스트 추가
- `StockCreationTest` 신규 — 생성 불변식 테스트

## 근거·결정 사항

- **경계는 테스트만 추가** — `요청 > 재고`만 부족(Task 3)이라 같은 수량 요청은 이미 `SUCCESS`로 0까지 차감된다. 로직은 그대로 두고 경계 *동작*을 증명하는 테스트(`ship(100)` → 0)만 보강.
- **방어 = 예외, 부족 = 반환값** — Task 3에서 갈라둔 경계를 지킨다. 부족은 예상되는 정상 분기라 `ShipmentResult`로, 잘못된 입력(0·음수 출고 수량)·불변식 위반(음수 재고)은 일어나면 안 되는 비정상이라 `IllegalArgumentException`으로. `ship`에서 입력 검증을 부족 판정보다 *앞에* 둔다 — 잘못된 요청은 부족 여부를 따지기 전에 걸러야 의미가 맞음.
- **예외 타입 = `IllegalArgumentException`** — 잘못된 인자에 대한 Java 표준 관용. 커스텀 도메인 예외는 방어 케이스가 단순해 클래스를 늘릴 이득이 약해 보류(초기 구조 최소화).
- **0 허용 / 음수 거부** — 재고 0은 유효한 상태(품절), 음수만 불변식 위반. 출고 수량은 반대로 0도 거부(무의미한 요청).
- **테스트 조직** — 경계·입력 검증은 출고 동작이라 `ShipTest`, 생성 불변식은 재고 생성 관심사라 `StockCreationTest`로 분리 (Task 3에서 예고한 동작별 분리).

### 보류한 결정 (Story 2로 이월)

- **생성자 가드 vs static 팩토리(`Stock.of`)** — 지금은 생성자 가드 유지(단일 길목·최소). static 팩토리는 if를 제거하지 않고 팩토리로 옮길 뿐이고, 불변식 보장을 위해 생성자 private화가 한 세트로 따라온다. 이 패턴이 값을 하는 시점은 Story 2(JPA): JPA용 protected no-arg 생성자 + 앱 생성·검증용 팩토리로 구성이 갈릴 때. 그 자리에서 팩토리·생성자 접근 제한·검증 위치를 함께 정리한다.

## 결과

생성·변경된 파일 (build/·.gradle/ 제외):

```
src/main/java/com/deepfine/inventory/domain/Stock.java          (생성자·ship 가드 추가)
src/test/java/com/deepfine/inventory/domain/ShipTest.java       (경계·입력 검증 테스트 추가)
src/test/java/com/deepfine/inventory/domain/StockCreationTest.java  (신규 — 생성 불변식)
```

빌드·테스트 결과:

- `./gradlew test` → `BUILD SUCCESSFUL`
- `ShipTest` 4건 (정상 출고 / 부족 거부 / 경계 0까지 / 0·음수 거부), `StockCreationTest` 2건 (음수 거부 / 0 허용) — 전부 통과

## 완료 기준 점검

- [x] 경계(정확히 0까지)·불변식(수량 ≥ 0)·입력 검증이 단위 테스트로 통과
- [x] Story 1 닫힘 기준(정상 출고·부족 거부·경계)이 모두 충족

## 남은 일 / 다음 행동

- **Story 1 완료.** 출고 도메인이 순수 객체로 정상 출고·부족 거부·경계·불변식·입력 검증을 갖췄다.
- 다음은 **Story 2 — RDBMS 도입**. plan-tasks는 *그 Story 진입 직전*에 별도 사이클로 짠다(여기서 미리 짜지 않음). 그 사이클에서 위 보류 결정(static 팩토리·JPA 생성자 구성)을 함께 처리.
- 커밋은 사용자 확인 후 (PR 단위).
