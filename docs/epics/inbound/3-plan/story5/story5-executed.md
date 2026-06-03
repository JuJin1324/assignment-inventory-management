# Story 5 실행 기록 — 모델링 동기화

## 무엇을 했는가

입고 에픽(S1~S4) 구현으로 굳은 상품·입고를 모델링 문서에 되먹였다.

## 근거·결정 사항

- `structure.md` — 상품·재고 분리 구조에서 Product 단일 애그리거트로 갱신 (ADR-009 반영). "상품과 재고를 나눈 이유" 섹션을 "상품이 수량을 직접 드는 이유"로 교체
- `structure-ship.md` — 어휘 정합 (StockRepository → ProductRepository, Stock → Product). 출고 전용 문서로 범위 유지
- `structure-receive.md` — 신규 작성. 입고 계층 구조를 structure-ship과 같은 패턴으로 별도 문서로 분리
- `scenarios.md` — 입고 처리 시나리오 추가 (출고와 동일한 추상 수준). 출고 시나리오 어휘 정합 (재고→상품 수량)

## 결과

변경 파일:
- `docs/modeling/structure.md`
- `docs/modeling/structure-ship.md`
- `docs/modeling/scenarios.md`

신규 파일:
- `docs/modeling/structure-receive.md`

## 다음 행동

입고 에픽(S1~S5) 완료. 백로그에서 다음 에픽을 선택한다.
