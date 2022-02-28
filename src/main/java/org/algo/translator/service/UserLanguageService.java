package org.algo.translator.service;

import org.algo.translator.entity.Follower;
import org.algo.translator.entity.UserLanguage;

import java.util.Optional;

/**
 * @author Yormamatov Davronbek
 * @since 28.02.2022
 */

public interface UserLanguageService {
    /**
     * add new follower language method
     *
     * @param follower follower object which available in db
     */
    UserLanguage createUserLanguage(Follower follower);

    /**
     * add new follower language method
     *
     * @param follower follower object which available in db
     */
    Optional<UserLanguage> getUserLanguage(Long chatId);
}
