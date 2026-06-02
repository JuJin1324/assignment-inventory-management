# explore-solutions 인박스 — 출고 처리

define 단계에서 떠오른 해결·설계 결정 후보를 모아 둔다. AS-IS가 아니라 TO-BE에서 다룰 재료다. explore-solutions에서 정리·결정한다.

- **구현 아키텍처** — 레이어드 vs 헥사고날
- **영속 기술** — JPA vs MyBatis
- **단위테스트 전략** — Given/When/Then 구조, Fixture 패턴 도입
- **구동·개발 도구 구성** — Docker(DB·앱 구동), Make 자동화 스크립트, psql CLI

참조 — structure.md의 *열린 설계 결정*도 출고 explore에서 함께 본다:

- **재고 저장 모델** — 수량 컬럼 vs 변동 이력 합산
- **동시성 제어 기법** — 비관적 락 / 낙관적 버전 / 격리 수준 (PostgreSQL 기준)
