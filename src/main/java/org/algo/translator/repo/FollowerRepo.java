package org.algo.translator.repo;

import org.algo.translator.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowerRepo extends JpaRepository<Follower, Long> {
    Optional<Follower> findByChatId(Long chatId);
}
