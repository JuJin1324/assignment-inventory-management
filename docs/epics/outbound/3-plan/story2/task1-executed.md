# Task 1 — 실행 환경 (실행 기록)

입력: [tasks.md](tasks.md) §Task 1

## 무엇을 했는가

PostgreSQL을 Docker로 띄우고, Make 한 명령으로 빌드·실행할 수 있게 했다. 앱에 JPA·PostgreSQL 의존성과 datasource 설정을 더해, 앱이 떠서 DB에 붙는 바닥을 세웠다.

- `docker-compose.yml` — PostgreSQL 16(alpine), 계정·DB 이름 `inventory`, 포트 5432, healthcheck(pg_isready). 데이터 볼륨은 두지 않음(테스트용·매번 클린 슬레이트).
- `Makefile` — `build` / `db-up` / `db-down`(`down -v`) / `run`
- `build.gradle` — `spring-boot-starter-data-jpa`, PostgreSQL 드라이버 추가
- `application.yml` — datasource(url·계정), JPA(`ddl-auto: update`, show-sql)

## 근거·결정 사항

- **PostgreSQL 16-alpine** — 널리 쓰는 안정 버전, alpine으로 이미지 가볍게. 계정·비밀번호·DB 모두 `inventory`로 둠(로컬 개발용 — 운영 비밀은 이 단계 관심사 아님).
- **`ddl-auto: update`** — 개발 단계라 엔티티가 바뀌면 스키마가 따라오게. 스키마 파일을 따로 두지 않고 앱이 뜰 때 엔티티에서 테이블이 만들어진다(plan-tasks에서 정한 ddl-auto 방식). 만들 테이블은 Task 2의 Stock 엔티티에서 나온다 — 지금은 엔티티가 없어 만들어지는 테이블도 없다.
- **`db-up`에 `--wait`** — `docker compose up -d --wait`로 healthcheck가 healthy가 될 때까지 기다린다. 이래야 곧장 `make run` 했을 때 DB가 준비된 상태다.
- **Make 타깃은 Task 1에 모음** — ddl-auto라 별도 스키마 초기화 타깃(`db-init`)이 필요 없다. Task 2는 코드만 더하고 Make는 안 건드린다.
- **데이터 볼륨 없음 + `db-down -v`** — 지금은 테스트용이라 세션 간 데이터 보존이 필요 없고, 오히려 옛 데이터·스키마가 남아 헷갈릴 여지를 없앤다. 명명 볼륨을 빼도 PostgreSQL 이미지가 익명 볼륨을 자동 생성하므로, `db-down`을 `down -v`로 두어 내릴 때 그 볼륨까지 비워 매번 클린 슬레이트가 되게 했다. 시드 데이터·오래 살릴 개발 DB가 필요해지면 그때 영속 볼륨을 다시 넣는다.

## 결과

생성·변경된 파일:

```
docker-compose.yml                 (신규 — PostgreSQL 16)
Makefile                           (신규 — build/db-up/db-down/run)
build.gradle                       (data-jpa + postgresql 드라이버)
src/main/resources/application.yml (datasource·JPA 설정)
```

검증:

- `make build` → `BUILD SUCCESSFUL` (의존성 풀림, 컴파일·도메인 단위 테스트 통과)
- `make db-up` → 이미지 pull → 컨테이너 기동 → `Healthy`
- `make run`(bootRun) → `HikariPool-1 - Added connection ... PgConnection`, `Database version: 16.14`, `Started InventoryApplication` 후 클린 종료 — 앱이 PostgreSQL에 실제로 붙음을 확인

## 완료 기준 점검

- [x] PostgreSQL이 Docker로 떠 있는 상태
- [x] Make 한 명령으로 빌드·실행이 되는 상태
- [x] 앱이 떠서 PostgreSQL에 붙는 상태(datasource 정상 연결)

## 남은 일 / 다음 행동

- **Task 2 — Stock 영속 + 저장·조회 검증** (execute-task): BaseEntity(createdAt·updatedAt·version) + Stock JPA 매핑 + StockRepository + `@DataJpaTest`. Task 안에서 식별자 전략·JPA용 기본 생성자 접근 제한·테스트 DB(Testcontainers vs H2)를 정한다.
- 커밋은 사용자 확인 후 (PR 단위).
