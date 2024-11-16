package com.mercadodavidv.hauntedreviews.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.reactive.server.WebTestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;

@TestConfiguration(proxyBeanMethods = false)
public class WebTestClientBuilderCustomizerConfig {

  @Autowired
  private HypermediaWebTestClientConfigurer configurer;

  /**
   * Exposes a WebTestClientBuilderCustomizer bean that applies a configurer to work with
   * hypermedia-enabled representations (Spring HATEOAS).
   */
  @Bean
  public WebTestClientBuilderCustomizer webTestClientBuilderCustomizer() {
    return builder -> builder.apply(configurer);
  }
}
