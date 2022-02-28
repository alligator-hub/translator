package org.algo.translator.repo;

import org.algo.translator.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerRepo extends JpaRepository<Follower, Long> {
    Optional<Follower> findByChatId(Long chatId);

    @Query(value = "select chat_id from follower",
            nativeQuery = true)
    HashSet<Long> getAllChatId();

    @Query(value = "select * from follower limit :limit",
            nativeQuery = true)
    List<Follower> getFollowersLimit(long limit);

    Integer countAllByStartedDateAfter(Long startedDate);
}
