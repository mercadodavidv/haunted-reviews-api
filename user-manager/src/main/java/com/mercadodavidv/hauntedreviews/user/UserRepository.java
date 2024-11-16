package com.mercadodavidv.hauntedreviews.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<User, Long> {

  <T> Optional<T> findByUsernameIgnoreCase(String username, Class<T> type);

  <T> Optional<T> findById(Long id, Class<T> type);

  Optional<User> findByEmailIgnoreCaseAndEmailNotNull(String email);

  long countByUsernameIgnoreCase(String username);

  long countByEmailIgnoreCase(String email);

}
