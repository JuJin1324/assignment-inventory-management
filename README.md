# 재고 관리 시스템

## 실행 방법

### 환경 요건

- Java 21
- Docker
- Make

### Makefile 명령어

| 명령어                       | 설명 |
|-----------------------------|------|
| `make build`                | Gradle 빌드 + Docker 이미지 생성 |
| `make up`                   | DB + 앱 함께 시작 (포트 18080) |
| `make down`                 | DB + 앱 함께 중지 및 데이터 초기화 |
| `make db-desc`              | `product` 테이블 컬럼 정보 확인 |
| `make db-desc TABLE=stock`  | 지정 테이블 컬럼 정보 확인 |

### 실행 순서

```bash
make build  # 빌드
make up     # DB + 앱 실행
```

### API 호출 예시

앱이 뜨면 `localhost:18080`으로 요청을 보낸다.

**입고** — 상품이 없으면 신규 등록 후 재고를 늘린다.

```bash
curl -s -X POST http://localhost:18080/api/products/receipts \
  -H "Content-Type: application/json" \
  -d '{"productId": "P001", "name": "노트북", "quantity": 10}'
```

**출고** — 재고가 충분하면 차감하고 남은 수량을 응답한다. 부족하면 409로 거부한다.

```bash
curl -s -X POST http://localhost:18080/api/products/shipments \
  -H "Content-Type: application/json" \
  -d '{"productId": "P001", "quantity": 3}'
```

**재고 조회** — 상품의 현재 재고를 확인한다.

```bash
curl -s http://localhost:18080/api/products/P001
```
