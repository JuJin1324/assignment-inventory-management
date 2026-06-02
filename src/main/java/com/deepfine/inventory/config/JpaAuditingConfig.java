package com.deepfine.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Auditing(@CreatedDate·@LastModifiedDate) 활성화.
 *
 * <p>@DataJpaTest는 메인 클래스의 설정을 자동으로 끌어오지 않으므로, Auditing을 별도
 * @Configuration으로 떼어 테스트에서 @Import해 쓴다.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
