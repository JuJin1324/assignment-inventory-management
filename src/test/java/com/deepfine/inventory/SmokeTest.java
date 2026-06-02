package com.deepfine.inventory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 빌드·테스트 골격이 도는지 확인하는 스모크 테스트.
 * JUnit 5 + AssertJ 파이프라인이 동작함을 증명한다 (ADR-003).
 * 도메인 로직 검증은 Task 2부터 이 자리 옆에서 시작한다.
 */
class SmokeTest {

	@Test
	@DisplayName("JUnit5 + AssertJ 테스트 파이프라인이 동작한다")
	void smoke() {
		// given
		int sum = 1 + 1;

		// when / then
		assertThat(sum).isEqualTo(2);
	}
}
