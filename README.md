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

## 문서 지도

### 1. [docs/modeling/](docs/modeling/)

과제의 비즈니스 모델링을 담는다.

- **[stakeholders.md](docs/modeling/stakeholders.md)** — 시스템을 사용하는 이해관계자와 그들의 기대 정의
- **[scenarios.md](docs/modeling/scenarios.md)** — 입고·출고·재고 조회 대표 사용 사례를 시퀀스로 표현
- **[structure.md](docs/modeling/structure.md)** — 도메인 개념 구조 (상품 애그리거트 중심)
- **structure-*.md** — 입고·출고·재고 조회·동시성 제어 각 기능의 계층 구조
- **[infrastructure.md](docs/modeling/infrastructure.md)** — 앱·DB Docker Compose 구조

### 2. [docs/epics/](docs/epics/)

구현 맥락을 담는다. [backlog.md](docs/epics/backlog.md)에서 시작해 에픽별로 문제 정의 → 해결 방안 탐색 → stories.md → tasks.md → executed.md 흐름으로 내려가면 어떤 판단으로 무엇을 만들었는지 확인할 수 있다.

#### 요구사항 → 구현 연결

| 요구사항 | API | 구조 | 에픽 |
|---------|-----|------|------|
| 입고 | `POST /api/products/receipts` | [structure-receive.md](docs/modeling/structure-receive.md) | [docs/epics/inbound/](docs/epics/inbound/) |
| 출고 | `POST /api/products/shipments` | [structure-ship.md](docs/modeling/structure-ship.md) | [docs/epics/outbound/](docs/epics/outbound/) |
| 재고 조회 | `GET /api/products/{productId}` | [structure-stock.md](docs/modeling/structure-stock.md) | [docs/epics/stock/](docs/epics/stock/) |
| 동시성 제어 | 낙관 락 + 재시도 (입고·출고 공통) | [structure-concurrent.md](docs/modeling/structure-concurrent.md) | [docs/epics/concurrent/](docs/epics/concurrent/) |

### 3. [docs/adr/](docs/adr/)

구조·기술과 관련한 설계 결정을 담는다. 선택지와 결정 근거가 정리되어 있다.

#### 핵심 ADR

| ADR | 결정 |
|-----|------|
| [ADR-001](docs/adr/adr-001-layered-with-hexagonal.md) | 레이어드 아키텍처 기반, 헥사고날 요소 일부 적용 |
| [ADR-009](docs/adr/adr-009-product-stock-merged.md) | 상품이 재고 수량을 직접 보유 — 단일 애그리거트 |
| [ADR-012](docs/adr/adr-012-optimistic-locking.md) | 동시성 제어 — 낙관 락(@Version) |
| [ADR-003](docs/adr/adr-003-test-strategy.md) | 테스트 전략 — 단위·슬라이스·영속 계층 분리 |


## 코드 구조

### main

```
src/main/java/com/deepfine/inventory
├── config/
├── domain/
├── service/
└── web/
```

레이어드 아키텍처를 기반으로 하되 web → service → domain 방향 의존성만 허용한다 ([ADR-001](docs/adr/adr-001-layered-with-hexagonal.md)).

- **config/** — JPA Auditing 설정
- **domain/** — Product 애그리거트·ProductRepository. 비즈니스 규칙
- **service/** — 유스케이스 단위 서비스: ReceiveService/ShipService/StockService
- **web/** — HTTP 어댑터: ProductController, ApiExceptionHandler

### test

```
src/test/java/com/deepfine/inventory
├── domain/
├── service/
├── web/
└── ProductFixtures
```

main 패키지 구조를 그대로 따르며 계층별로 테스트를 분리한다 ([ADR-003](docs/adr/adr-003-test-strategy.md)).

- **domain/** — 도메인 단위 테스트 + 영속성 단위 테스트
- **service/** — 서비스 단위 테스트 
- **web/** — 컨트롤러 단위 테스트
- **ProductFixtures** — 계층 전반에서 공유하는 테스트 데이터 빌더

