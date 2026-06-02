# Task 1 — Gradle 프로젝트 골격 (실행 기록)

입력: [tasks.md](tasks.md) §Task 1

## 무엇을 했는가

도메인 코드와 단위 테스트가 컴파일·실행되는 Spring Boot 빌드 골격을 세웠다. 빈(스모크) 테스트가 `./gradlew test`로 실제로 돈다.

- Spring Initializr로 스캐폴드 생성 (Gradle·Java·Spring Boot 버전 정합 보장)
- repo 루트로 풀되 기존 `.gitignore`(과제 본문 제외 규칙) 보존, Initializr의 `.gitignore`·`HELP.md`는 제외
- 생성된 `@SpringBootTest contextLoads` 테스트를 순수 JUnit5+AssertJ 스모크 테스트로 교체
- 도메인 패키지(`com.deepfine.inventory.domain`)를 `package-info.java`로 앵커

## 근거·결정 사항

- **빌드 베이스 = Java 21 + Spring Boot 3.5.3** — 메이저 라인은 plan-tasks에서 합의(가장 널리 깔린 LTS, 제출물 호환성). 패치는 Maven Central 최신 안정 3.x로 3.5.3 확정. 로컬 활성 JDK가 Corretto 21이라 툴체인과 일치.
- **테스트 의존성 = `spring-boot-starter-test`** — ADR-003이 지정한 JUnit5+AssertJ를 그대로 품고 Story 2/3에서도 재사용하므로 build.gradle을 다시 안 건드린다. 웹·DB 스타터는 추가 안 함.
- **스모크 테스트를 `@SpringBootTest` → 순수 JUnit5+AssertJ로 교체** — Story 1은 "DB·웹 없이 순수 도메인"이라 Spring 컨텍스트를 띄울 이유가 없다. 샘플 테스트의 역할은 Task 2가 쓸 *JUnit5+AssertJ 파이프라인이 도는 것*을 증명하는 것. `contextLoads`는 띄울 컨텍스트가 실질적으로 생기는 Story 2/3에서 복귀.
- **도메인 패키지 앵커** — `domain/package-info.java`로 도메인 패키지를 물리적으로 세우고 영속·웹과 분리되는 경계(ADR-001)를 문서화. 영속/웹 패키지는 코드가 들어오는 Story 2/3에서 생성(빈 패키지 미리 만들지 않음).
- **설정 파일 = yaml** — Initializr 기본 `application.properties`를 `application.yml`로 교체. 설정 포맷은 골격 컨벤션이라 Task 1에서 확정. Story 2의 datasource 설정이 이 위에 얹힌다.

## 결과

생성·변경된 파일 (build/·.gradle/ 제외):

```
build.gradle                  (boot 3.5.3, java 21 toolchain, starter + starter-test)
settings.gradle               (rootProject.name = 'inventory')
gradlew, gradlew.bat, gradle/wrapper/*   (Gradle Wrapper 8.14.5)
.gitattributes
src/main/java/com/deepfine/inventory/InventoryApplication.java       (@SpringBootApplication)
src/main/java/com/deepfine/inventory/domain/package-info.java        (도메인 패키지 앵커)
src/main/resources/application.yml                                  (properties → yaml)
src/test/java/com/deepfine/inventory/SmokeTest.java                  (JUnit5+AssertJ 스모크)
```

빌드·테스트 결과:

- `./gradlew build` → `BUILD SUCCESSFUL` (compileJava·test·check 포함 7개 태스크)
- `SmokeTest` → `tests=1, failures=0` 통과

## 완료 기준 점검

- [x] `./gradlew build` 성공
- [x] 샘플 테스트가 `./gradlew test`로 통과
- [x] 도메인 코드가 들어갈 패키지가 영속·웹과 분리돼 잡힘 (`com.deepfine.inventory.domain`)

## 남은 일 / 다음 행동

- **Task 2에서 제거할 임시 골격물 2개** (사용자 합의):
  - `domain/package-info.java` — 빈 패키지 앵커. Product·Stock이 패키지를 물리화하면 제거 (경계는 ADR-001·도메인 클래스가 이미 말함).
  - `SmokeTest.java` — 테스트 파이프라인 증명용. 도메인 단위 테스트가 같은 파이프라인을 증명하면 제거.
- 사용자 검토 포인트: 버전 핀(3.5.3), 빌드 베이스 구성
- 다음 단위: **Task 2 — 출고 도메인 + 단위 테스트** (execute-task). 상품·재고·출고 로직이 `com.deepfine.inventory.domain`에 들어오고, 위 임시 골격물 2개를 함께 제거한다.
- 커밋은 사용자 확인 후 (PR 단위).
