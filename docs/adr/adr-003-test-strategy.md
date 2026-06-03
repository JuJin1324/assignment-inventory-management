# ADR-003: 단위테스트 전략 — Given/When/Then + Test Data Builder

## 날짜

2026-06-02

## 상태

채택 (Accepted)

## 맥락

도메인-퍼스트로 Story 1부터 순수 도메인 단위 테스트를 짠다. 프레임워크·본문 구조·테스트 데이터 준비 방식을 정해 일관된 컨벤션을 세워야 한다.

## 결정

- **프레임워크** — JUnit 5 + AssertJ
- **본문 구조** — Given/When/Then 주석 섹션 (`// given` / `// when` / `// then`)으로 준비·실행·검증을 나눈다
- **테스트 데이터** — Test Data Builder (fluent 빌더, 예: `aStock().quantity(100).build()`)

### Fixture 후보군

- **Object Mother** — 정적 팩토리(예: `재고_100개()`). 가볍지만 조합이 늘면 팩토리가 비대해진다.
- **Test Data Builder** — fluent 빌더. 테스트별로 필요한 필드만 지정하고 나머지는 기본값. 조합이 유연하고 도메인이 커져도 확장 부담이 적다. → **채택**

## 근거

- **Given/When/Then** — 본문을 준비·실행·검증으로 나눠 읽기 쉽고, scenarios의 시퀀스 사고와 한 결이다.
- **Test Data Builder (처음부터)** — 빌더로 시작하면 테스트마다 관심 있는 필드만 드러내고 나머지는 기본값으로 숨길 수 있어, 의도가 또렷해지고 도메인 확장에도 한 패턴으로 버틴다.
- **JUnit 5 + AssertJ** — Java/Spring 표준이고 assertion이 읽기 좋다.
