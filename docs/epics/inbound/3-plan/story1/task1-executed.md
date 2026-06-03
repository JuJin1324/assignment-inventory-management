# Task 1 — 전 계층 Stock → Product 리네임 (실행 기록)

입력: [tasks.md](tasks.md) §Task 1

## 무엇을 했는가

`Stock` 도메인 어휘를 `Product`로 전 계층(도메인·영속·서비스·웹·테스트) 일괄 리네임했다. `productId`(비즈니스 키)·출고 동작·API 노출은 그대로 두고, 빌드·테스트를 green으로 유지했다.

- 파일 이동(6, git mv로 히스토리 보존): `Stock`→`Product`, `StockRepository`→`ProductRepository`, `StockNotFoundException`→`ProductNotFoundException`, `StockFixtures`→`ProductFixtures`, `StockCreationTest`→`ProductCreationTest`, `StockPersistenceTest`→`ProductPersistenceTest`
- 참조 갱신(6): `ShipService`, `ApiExceptionHandler`, `ShipmentRequest`(javadoc), `ShipTest`, `ShipServiceTest`, `ShipmentControllerTest`
- 테이블·인덱스 매핑 `stock`→`product`(`idx_product_product_id`), 변수·필드(`stock`→`product`, `stockRepository`→`productRepository`), 상수(`DEFAULT_STOCK_QUANTITY`→`DEFAULT_PRODUCT_QUANTITY`), 빌더(`aStock`→`aProduct`)

## 근거·결정 사항

- **`productId`·출고 동작·API URL 불변** — 이번 Task는 엔티티 어휘만 바꾼다. API 식별자는 이미 `productId`만 노출(대리 `id`는 내부 PK)이라 그대로 뒀고, 출고 엔드포인트 URL `/api/stock/shipment`의 `stock`도 *리소스 경로 재편*이라 S2 몫으로 남겼다.
- **`Ship*`/`Shipment*`는 유지** — `ShipService`·`ShipCommand`·`ShipResult`·`ShipmentController`·`ShipmentResult`는 *출고(ship) 행위* 이름이라 엔티티 리네임 대상이 아니다. 내부 `Stock` 참조만 `Product`로 바뀐다.
- **`ProductNotFoundException`도 함께 리네임** — 출고 도메인이 상품으로 바뀌니 예외명·메시지(`재고를 찾을 수 없습니다`→`상품을 찾을 수 없습니다`)도 따라간다. 404 테스트는 상태 코드만 검증해 메시지 변경 영향 없음.
- **테스트 메서드명 선별** — 엔티티를 가리키던 `ship_stockNotFound`·`ship_stock_not_found`는 `product`로 바꾸고, *재고 수량* 의미인 `ship_decrements_stock_when_sufficient` 등은 유지(상품이 든 재고 수량은 여전히 유효한 개념).
- **치환 안전성** — URL의 소문자 `stock`을 건드리지 않도록, PascalCase·camelCase·상수는 전역으로, 변수 `\bstock\b`는 URL 없는 파일에만 적용했다. 치환 후 `grep -i stock`으로 남은 것이 URL·재고수량 메서드명뿐임을 확인.

## 결과

생성·이동·변경된 파일: 위 "무엇을 했는가" 목록. 코드 동작 변경 없음(순수 리네임 + 한글 doc/메시지 정합).

검증:

- `./gradlew test` → 전체 **15건 통과**(실패·에러 0). 출고 도메인·영속·서비스·API 동작 불변.

## 완료 기준 점검

- [x] `Stock` 어휘가 `Product`로 바뀐 상태 (도메인·영속·서비스·웹·테스트)
- [x] `productId` 비즈니스 키·출고 동작·API 노출이 불변인 상태
- [x] 테이블이 `product`로 매핑되어 저장·조회되는 상태 (`ProductPersistenceTest` 통과)
- [x] 전체 테스트가 그대로 통과하는 상태

## 남은 일 / 다음 행동

- **Task 2 — Product에 `name` 추가** (execute-task): 과제 `{id, productId, name, quantity}` 완성. 출고는 안 쓰고 입고(S3)가 채울 자리.
- 두 ADR(재고→상품 통합·식별자 모델)은 굳었으니 ADR 문서로 기록(어느 Task/시점에 쓸지 후속 결정).
- 커밋은 사용자 확인 후 (PR 단위).
