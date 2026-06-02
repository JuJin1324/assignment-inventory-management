# Task 3 — 재고 부족 거부 (실행 기록)

입력: [tasks.md](tasks.md) §Task 3

## 무엇을 했는가

재고가 부족하면 출고를 거부하고 재고를 건드리지 않게 했다. 부족은 예외가 아니라 정상 분기로, 출고 결과를 일급 값으로 돌려주도록 `ship`의 반환을 바꿨다.

- `ShipmentResult` enum 신규 — `SUCCESS` / `INSUFFICIENT`
- `Stock.ship` — `void` → `ShipmentResult` 반환. 부족(`요청 > 재고`)이면 차감하지 않고 `INSUFFICIENT`, 충분하면 차감 후 `SUCCESS`
- 테스트를 동작 중심 `ShipTest`로 리네임 — 정상 출고 테스트를 `SUCCESS` 검증으로 보강, 부족 거부 테스트 추가

## 근거·결정 사항

- **출고 결과 표현 = 무페이로드 enum `ShipmentResult`** (이 Task에서 합의한 핵심 결정). 후보였던 boolean(불투명), Vavr/FP(합성할 연산이 없어 지렛대 미실현 + ADR-002가 접은 길)를 제치고 enum으로. 부족은 *예상되는 정상 흐름*이라 예외(통제 흐름을 끊는 비정상 신호)로 다루면 의미가 어긋나고 호출부가 `try/catch`로 정상 분기를 받는 이상한 모양이 된다. enum은 변형 데이터를 안 싣는 단순 상수 집합이라 ADR-002가 막은 sealed/Result(ADT)가 아니라 "단순 값" 범위 안. 패러다임은 그대로 OOP이고, "결과를 예외가 아니라 반환값으로 싣는" 변화일 뿐. 성공 시 남은 재고는 `getQuantity()`로 확인(Task 2에서 세운 방식 유지).
  - *기존 OOP의 애매함 교정:* 종전 방식은 부족 시 예외를 던져 `ship`을 void로 둘 수 있었으나, 정상 분기를 예외로 뭉갠 게 애매함의 뿌리였다. 이번에 부족(정상 분기, 반환값)과 잘못된 입력(방어, 예외 — Task 4)을 갈라 푼다.
- **부족 판정 = `요청 > 재고`** — 같은 수량 요청(정확히 0까지)은 `SUCCESS`로 통과. 그 경계 *동작*은 로직으로 맞춰뒀고, 경계를 증명하는 *테스트*는 Task 4 몫이라 여기선 넣지 않음. 비정상 입력(0·음수) 방어도 Task 4.
- **테스트 조직 = 동작 중심 `ShipTest`** — 클래스 거울(`StockTest`)이 아니라 동작 명세로. 지금 테스트가 전부 출고 동작이라 "출고는 이렇게 동작한다"는 스펙으로 읽힌다. Task 4에서 경계·입력 검증은 출고 동작이라 `ShipTest`에 들어가고, 생성 불변식(수량≥0)은 *재고 생성* 관심사라 별도 테스트(예: `StockCreationTest`)로 갈린다 — 동작별 분리가 자연스럽게 드러남.

## 결과

생성·변경된 파일 (build/·.gradle/ 제외):

```
src/main/java/com/deepfine/inventory/domain/ShipmentResult.java  (신규 — SUCCESS/INSUFFICIENT)
src/main/java/com/deepfine/inventory/domain/Stock.java           (ship: void → ShipmentResult)
src/test/java/com/deepfine/inventory/domain/ShipTest.java        (StockTest → ShipTest, 부족 거부 테스트 추가)
```

빌드·테스트 결과:

- `./gradlew test` → `BUILD SUCCESSFUL`
- `ShipTest` → 정상 출고 / 부족 거부 2건 통과

## 완료 기준 점검

- [x] 재고 부족 시 출고가 거부되고 재고가 그대로
- [x] 부족 거부가 단위 테스트로 통과
- [x] 출고 결과를 어떻게 돌려줄지 표현 방식이 정해짐 (`ShipmentResult` enum)

## 남은 일 / 다음 행동

- **Task 4 — 경계·불변식 방어** (execute-task): 경계(재고와 같은 수량 출고 → 정확히 0까지 + 테스트), 불변식(생성 시 수량 ≥ 0, 음수 거부), 입력 검증(0·음수 출고 수량 거부 — 부족과 구분되는 방어, 예외). 생성 불변식 테스트는 `StockCreationTest`로, 경계·입력 검증은 `ShipTest`로.
- 커밋은 사용자 확인 후 (PR 단위).
