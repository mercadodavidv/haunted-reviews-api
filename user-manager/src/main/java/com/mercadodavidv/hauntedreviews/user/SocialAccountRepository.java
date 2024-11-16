package com.mercadodavidv.hauntedreviews.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

  Optional<SocialAccount> findByPrincipalNameAndProviderId(String principalName, String providerId);

  boolean existsByPrincipalNameAndProviderId(String principalName, String providerId);

}
