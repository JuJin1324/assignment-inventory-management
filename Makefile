.PHONY: build up down db-desc

# 조회할 테이블 (기본 product) — make db-desc TABLE=... 로 바꿀 수 있음
TABLE ?= product

# Gradle 빌드 + Docker 이미지 생성
build:
	./gradlew build
	docker compose build

# DB + 앱 함께 띄우기
up:
	docker compose up -d --wait

# DB + 앱 함께 내리기 (데이터 초기화)
down:
	docker compose down -v

# 테이블 컬럼 정보 확인 (기본 product, make db-desc TABLE=... 로 지정)
db-desc:
	docker exec inventory-postgres psql -U inventory -d inventory -c "\d $(TABLE)"
