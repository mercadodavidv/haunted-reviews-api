package com.mercadodavidv.hauntedreviews.config;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "user-manager.delegating-password-encoder")
@AllArgsConstructor
@Getter
public class DelegatingPasswordEncoderConfigProperties {

  private final List<String> acceptedIds;

}
