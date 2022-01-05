package org.algo.translator.repo;

import org.algo.translator.entity.UserLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLanguageRepo extends JpaRepository<UserLanguage, Long> {

    Optional<UserLanguage> findByFollower_ChatId(Long follower_chatId);
}
