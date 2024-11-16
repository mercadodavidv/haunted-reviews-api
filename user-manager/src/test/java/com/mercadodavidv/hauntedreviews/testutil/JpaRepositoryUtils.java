package com.mercadodavidv.hauntedreviews.testutil;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class JpaRepositoryUtils {

  private final EntityManager entityManager;

  public JpaRepositoryUtils(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void flush() {
    entityManager.flush();
  }

  public void clear() {
    entityManager.clear();
  }
}
