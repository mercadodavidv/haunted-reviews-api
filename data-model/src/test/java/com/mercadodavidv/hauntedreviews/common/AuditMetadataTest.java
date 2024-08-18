package com.mercadodavidv.hauntedreviews.common;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class AuditMetadataTest {

  @Test
  void testAudit_NoArgsConstructor() {

    AuditMetadata auditMetadata = new AuditMetadata();

    assertNull(auditMetadata.getCreatedDate());
    assertNull(auditMetadata.getLastModifiedDate());
  }

  @Test
  void testAudit_NowStaticMethod() {

    AuditMetadata auditMetadata = AuditMetadata.now();

    assertNotNull(auditMetadata.getCreatedDate());
    assertNotNull(auditMetadata.getLastModifiedDate());
    assertEquals(auditMetadata.getCreatedDate(), auditMetadata.getLastModifiedDate());
    assertThat(auditMetadata.getCreatedDate()).isCloseTo(Instant.now(),
        within(59, ChronoUnit.MINUTES));
    assertThat(auditMetadata.getLastModifiedDate()).isCloseTo(Instant.now(),
        within(59, ChronoUnit.MINUTES));
  }
}
