# Task 2 실행 기록 — 서비스: ReceiveService (find-or-create)

## 무엇을 했는가

`ReceiveService`를 작성했다. `productId`로 상품을 조회해 있으면 수량을 증가시키고, 없으면 `name`과 함께 신규 등록해 입고한다. 한 트랜잭션 안에서 완결.

## 근거·결정 사항

- 신규 상품 생성 시 `new Product(productId, name, 0)` 후 `receive(quantity)` — 생성자에서 직접 수량을 넣지 않고 도메인 메서드를 태워 불변식 검증을 재사용
- `orElseGet()` 으로 find-or-create를 한 줄로 표현 — 조회·분기를 서비스에서 명시적으로 처리하지 않고 지연 생성으로 위임
- `ReceiveResult(quantity)` — 입고 후 현재 수량만 반환. 신규/기존 구분은 API 계층(S4)이 필요하면 그때 추가

## 결과

신규 파일:
- `service/ReceiveCommand.java`
- `service/ReceiveResult.java`
- `service/ReceiveService.java`
- `service/ReceiveServiceTest.java`

변경 파일:
- `ProductFixtures.java` — `ReceiveCommandTestBuilder` 추가

전체 테스트 통과.

## 다음 행동

S3 완료. S4(입고 API 노출) plan-tasks로 진입한다.
