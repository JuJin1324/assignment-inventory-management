.PHONY: build db-up db-down run

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
