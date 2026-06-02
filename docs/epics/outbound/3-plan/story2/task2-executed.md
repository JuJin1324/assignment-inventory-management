# Task 2 — Stock 영속 + 저장·조회 검증 (실행 기록)

입력: [tasks.md](tasks.md) §Task 2

## 무엇을 했는가

Stock을 PostgreSQL에 저장하고 다시 읽을 수 있게 했다. 모든 엔티티가 공통으로 가질 컬럼(createdAt·updatedAt·version)을 BaseEntity로 묶어 Stock에 입히고, 저장·조회와 매핑을 테스트로 확인했다. 이로써 Story 2가 닫힌다.

- `BaseEntity`(`@MappedSuperclass`) — `createdAt`·`updatedAt`(JPA Auditing), `version`(`@Version`)
- `JpaAuditingConfig` — `@EnableJpaAuditing` 분리(테스트에서 `@Import`)
- `Stock`에 JPA 매핑 — `@Entity`, `@Id @GeneratedValue(IDENTITY)`, `@NoArgsConstructor(PROTECTED)`, BaseEntity 상속
- `StockRepository` — `JpaRepository<Stock, Long>`
- `StockPersistenceTest` — `@DataJpaTest`로 저장·조회 + 공통 컬럼·version 검증
- H2 의존성(테스트 런타임)

## 근거·결정 사항

- **식별자 = 자동 증가 대리키(`@GeneratedValue IDENTITY`)** — productId를 자연키로 쓰면 "상품당 재고 한 줄"을 가정하게 되는데 그 제약은 모델에서 정한 바 없다. 대리키가 그 가정에 안 묶여 유연하다.
- **JPA용 기본 생성자 = `@NoArgsConstructor(access = PROTECTED)`** — JPA는 no-arg 생성자가 필요하다. 앱 코드는 검증 생성자(`Stock(productId, quantity)`)만 쓰게 protected로 닫았다. (Story 1 Task 4에서 미뤄둔 결정 처리)
- **`productId`의 `final` 제거** — JPA가 리플렉션으로 필드를 채우려면 `final`이면 안 된다. ADR-004에서 받아들인 "도메인이 JPA에 묶임"의 구체적 대가. 도메인 *로직*(검증 생성자·ship)은 그대로라 Story 1 단위 테스트는 통과한다.
- **BaseEntity의 `@Getter`는 JPA용이 아님** — JPA는 필드 접근(리플렉션)이라 getter가 필요 없다. 다만 `@DataJpaTest`가 createdAt·version이 채워졌는지 읽어 단언하고, Story 3에서 감사 컬럼을 응답에 실으면 거기서도 읽힌다 — 그 소비자들을 위해 둔다.
- **Auditing은 yml로 못 켬** — `@CreatedDate`/`@LastModifiedDate`는 `@EnableJpaAuditing`이 `AuditingHandler` 빈을 등록해야 작동하고, 이를 켜는 yml 프로퍼티는 없다. `@DataJpaTest`가 메인 설정을 안 끌어오므로 별도 `@Configuration`으로 떼어 테스트에서 `@Import`한다.
- **테스트 DB = H2(임베디드)** — 과제 시간 제약상의 선택. 지금 테스트엔 PostgreSQL 충실도가 결정적이지 않다고 판단. `@DataJpaTest`가 클래스패스의 H2를 자동으로 임베디드로 띄우고 메인의 PostgreSQL datasource는 무시한다. PostgreSQL 충실 검증이 필요해지면 Testcontainers로 올린다.

## 결과

생성·변경된 파일:

```
src/main/java/com/deepfine/inventory/domain/BaseEntity.java            (신규 — 공통 컬럼)
src/main/java/com/deepfine/inventory/config/JpaAuditingConfig.java     (신규 — @EnableJpaAuditing)
src/main/java/com/deepfine/inventory/domain/Stock.java                 (JPA 매핑 + final 제거)
src/main/java/com/deepfine/inventory/domain/StockRepository.java       (신규 — JpaRepository)
src/test/java/com/deepfine/inventory/domain/StockPersistenceTest.java  (신규 — @DataJpaTest)
build.gradle                                                           (H2 테스트 런타임)
```

검증:

- `./gradlew test` → ShipTest 4 / StockCreationTest 2 / **StockPersistenceTest 1** 전부 통과. 영속 테스트는 H2에서 저장 → 조회 → productId·quantity 값, createdAt·updatedAt·version 채워짐을 확인.
- PostgreSQL 스키마 — `bootRun`으로 `ddl-auto`가 `stock` 테이블을 만들고, psql `\d stock`으로 컬럼(id 대리키·created_at·updated_at·version·product_id·quantity·PK)이 엔티티대로 생성됨을 확인. 매핑이 실제 PostgreSQL에서 유효함.

## 완료 기준 점검

- [x] Stock이 PostgreSQL에 저장되고 다시 읽히는 상태 (매핑·스키마 PostgreSQL에서 유효, 저장·조회 로직은 `@DataJpaTest`로 검증)
- [x] 공통 컬럼(createdAt·updatedAt)과 version이 채워지는 상태
- [x] Story 1 도메인 단위 테스트가 그대로 통과하는 상태(동작 불변)
- [x] `@DataJpaTest` 저장·조회 검증이 통과하는 상태

## 남은 일 / 다음 행동

- **Story 2 완료.** 출고 도메인이 PostgreSQL에 영속된다.
- 자동 테스트는 H2라 PostgreSQL 자체에 대한 저장·조회 회귀는 자동으로 잡지 않는다(스키마는 수동 확인). PostgreSQL 충실 회귀가 필요해지면 Testcontainers로 올린다.
- 다음은 **Story 3 — API 노출**. plan-tasks는 그 Story 진입 직전에 별도 사이클로.
- 커밋은 사용자 확인 후 (PR 단위).
