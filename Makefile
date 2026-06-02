.PHONY: build db-up db-down run db-desc

# 조회할 테이블 (기본 stock) — make db-desc TABLE=... 로 바꿀 수 있음
TABLE ?= stock

# 빌드
build:
	./gradlew build

# PostgreSQL 띄우기 (백그라운드, 준비될 때까지 대기)
db-up:
	docker compose up -d --wait

# PostgreSQL 내리기 (테스트용이라 데이터까지 비움 — 익명 볼륨 포함)
db-down:
	docker compose down -v

# 앱 실행
run:
	./gradlew bootRun

# 테이블 컬럼 정보 확인 (기본 stock, make db-desc TABLE=... 로 지정)
db-desc:
	docker exec inventory-postgres psql -U inventory -d inventory -c "\d $(TABLE)"
