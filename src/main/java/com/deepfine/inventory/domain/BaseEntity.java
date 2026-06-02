package com.deepfine.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 모든 영속 엔티티가 공통으로 갖는 컬럼 — 생성·수정 시각과 낙관적 락 버전 (ADR-004).
 *
 * <p>생성·수정 시각은 JPA Auditing이 채운다. {@code version}은 동시 수정 충돌을 잡는
 * 낙관적 락 컬럼으로, JPA가 자동으로 증가·검사한다 — 충돌을 어떻게 다룰지(재시도·에러
 * 응답)는 별도 에픽 몫이고 여기선 컬럼과 기본 강제까지만 둔다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	@Version
	private Long version;
}
