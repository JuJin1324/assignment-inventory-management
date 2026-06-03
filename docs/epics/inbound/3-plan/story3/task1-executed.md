# Task 1 실행 기록 — 도메인: Product.receive()

## 무엇을 했는가

`Product`에 `receive(int quantity)` 메서드를 추가했다. 수량을 증가시키고, 0·음수 입력은 불변식으로 막는다.

## 근거·결정 사항

- `ship()`과 대칭 구조로 작성 — 0·음수 거부는 같은 `IllegalArgumentException` 패턴
- 반환값 없음 — 입고는 항상 성공(실패 분기 없음). 출고의 `ShipmentResult` 같은 결과 타입이 불필요

## 결과

변경 파일:
- `domain/Product.java` — `receive()` 메서드 추가
- `domain/ReceiveTest.java` — 수량 증가·0 기점 입고·0·음수 거부 케이스

전체 테스트 통과.

## 다음 행동

Task 2(ReceiveService find-or-create)로 진입한다.
