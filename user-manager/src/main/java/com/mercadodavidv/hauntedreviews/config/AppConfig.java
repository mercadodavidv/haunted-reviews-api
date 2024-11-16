package com.mercadodavidv.hauntedreviews.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@EnableSpringDataWebSupport
@EnableJpaAuditing
@EnableConfigurationProperties(DelegatingPasswordEncoderConfigProperties.class)
public class AppConfig {

  @Bean
  ForwardedHeaderFilter forwardedHeaderFilter() {
    return new ForwardedHeaderFilter();
  }

  @Bean
  HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
      final LocalValidatorFactoryBean localValidatorFactoryBean) {
    return properties -> properties.put("jakarta.persistence.validation.factory",
        localValidatorFactoryBean);
  }
}
