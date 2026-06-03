# Story 1 실행 기록 — README 실행 섹션

## 무엇을 했는가

README.md를 신규 생성하고 실행 섹션을 작성했다. 앱을 로컬이 아닌 Docker로 실행하는 방향으로 변경하면서 관련 파일도 함께 정비했다.

## 결정 사항

- **앱 Docker화** — `make run`(로컬 bootRun) 대신 Docker Compose로 앱을 띄운다. DB와 앱을 `make up` 하나로 시작하고 `make down` 하나로 닫을 수 있게 단순화.
- **포트 충돌 방지** — 호스트 포트를 앱 18080, DB 15432로 설정. 컨테이너 내부는 앱 8080, DB 5432 그대로 유지.
- **Makefile 4개로 단순화** — `build / up / down / db-desc`. 기존 `db-up / run / db-down`을 `up / down`으로 통합.

## 결과

생성·수정한 파일:

- `README.md` — 실행 섹션(환경 요건·Makefile 명령어·실행 순서·API 예시) 작성
- `Dockerfile` — eclipse-temurin:21-jre-alpine 기반, `build/libs/` jar 실행
- `.dockerignore` — 불필요한 빌드 컨텍스트 제외
- `docker-compose.yml` — app 서비스 추가, postgres 헬스체크 후 앱 기동
- `Makefile` — build/up/down/db-desc 4개로 재정비
- `src/main/resources/application.yml` — DB URL 포트 15432로 수정
