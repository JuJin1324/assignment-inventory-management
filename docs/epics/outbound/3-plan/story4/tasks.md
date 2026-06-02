# Story 4 — Task 목록

선행: [../stories.md](../stories.md) §Story 4

## 전체 흐름

```mermaid
graph LR
    T1["Task 1<br/>출고 엔드포인트 (정상 흐름 — 성공·부족 응답) (완료)"]
    T2["Task 2<br/>예외 → HTTP 상태 매핑 (404·400) (완료)"]
    T1 --> T2
```

에픽 내내 지켜온 "부족 = 정상 분기(반환값) / 잘못된 입력 = 예외" 경계가 그대로 Task를 가른다. Task 1은 엔드포인트의 정상 흐름 — 컨트롤러가 서비스를 태우고 성공이면 남은 재고를, 부족이면(예외가 아니라 반환값이라) 인라인으로 거부 응답을 돌려준다. Task 2는 예외 → HTTP 매핑 — 상품 없음(404)·잘못된 입력(400)을 한곳(`@RestControllerAdvice`)에 모은다. 성공·부족은 한 PR, 예외 매핑은 다른 PR로 검토 이야기가 갈린다.

---

## Task 1 — 출고 엔드포인트 (정상 흐름) ✅ 완료

실행 기록: [task1-executed.md](task1-executed.md)

### 목표

출고 REST 엔드포인트가 요청을 받아 `ShipService`를 태우고, 성공이면 남은 재고를, 부족이면 거부 응답을 돌려준다. `@WebMvcTest` 슬라이스로 성공·부족을 검증한다(ADR-008).

### 핵심 작업

- `spring-boot-starter-web` 의존성 추가
- 컨트롤러 — POST 엔드포인트가 요청을 받아 `ShipCommand`로 `ShipService` 호출
- 요청 DTO(productId·quantity), 응답 DTO(남은 재고)
- 부족(`ShipResult.INSUFFICIENT`)을 컨트롤러가 인라인으로 거부 응답에 매핑
- `@WebMvcTest` 슬라이스 테스트 — `ShipService` 목, 성공·부족

### Task 안에서 정할 것

- 엔드포인트 URL·메서드(POST)
- 부족의 HTTP 상태(409 Conflict vs 422 Unprocessable Entity)
- 응답 DTO 모양(남은 재고 + 결과 표시 방식)

### 이 Task에서 하지 않을 것

- 예외 → HTTP 매핑(상품 없음 404·잘못된 입력 400) — Task 2
- 인증·인가, 페이지네이션, 목록 조회 등 다른 엔드포인트

### 완료 기준

- 엔드포인트가 성공 시 남은 재고를 응답하는 상태
- 부족 시 거부 응답으로 돌려주는 상태(예외 아닌 정상 분기)
- `@WebMvcTest` 슬라이스 테스트로 성공·부족이 통과하는 상태

---

## Task 2 — 예외 → HTTP 상태 매핑 (404·400) ✅ 완료

실행 기록: [task2-executed.md](task2-executed.md)

### 목표

서비스·도메인이 던지는 예외를 일관된 HTTP 에러 응답으로 매핑한다 — 상품 없음은 404, 잘못된 입력(0·음수)은 400. `@WebMvcTest` 슬라이스로 검증하고, 앱을 띄워 전 구간을 실제 HTTP로 확인한다.

### 핵심 작업

- `@RestControllerAdvice` — `StockNotFoundException` → 404, 입력 오류 → 400
- 에러 응답 바디 모양(상태·메시지)
- `@WebMvcTest` 슬라이스 테스트 — 404·400
- 앱 띄워 실제 HTTP 요청으로 출고 흐름(성공·부족·404·400) 확인

### Task 안에서 정할 것

- 입력 오류(0·음수)를 **도메인 예외(IllegalArgumentException → 400)** 로 받을지, **요청 DTO bean validation(`@Valid`) → 400** 으로 받을지
- 에러 바디 포맷(필드 구성)

### 이 Task에서 하지 않을 것

- 정상 흐름(성공·부족 응답) — Task 1
- 전역 예외 처리 표준화 과설계(필요한 두 매핑만)

### 완료 기준

- 상품 없음에 404, 잘못된 입력에 400이 일관된 에러 바디로 돌아오는 상태
- `@WebMvcTest` 슬라이스 테스트로 404·400이 통과하는 상태
- 앱을 띄워 실제 HTTP 요청으로 출고 흐름(성공·부족·404·400)이 동작함을 확인한 상태

---

## 다음 사이클

Task 1 → 2 순서로 execute-task로 실행한다. Story 4가 닫히면 출고 처리 에픽의 happy-path 흐름(도메인 → 영속 → 서비스 → API)이 완성된다. 동시성(동시 재고 변경 충돌)은 그 뒤 별도 에픽에서 이 흐름 위에 얹는다.
