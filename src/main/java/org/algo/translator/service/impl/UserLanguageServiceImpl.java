package org.algo.translator.service.impl;

import org.algo.translator.entity.Follower;
import org.algo.translator.entity.UserLanguage;
import org.algo.translator.enums.QueryButtons;
import org.algo.translator.repo.UserLanguageRepo;
import org.algo.translator.service.UserLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Yormamatov Davronbek
 * @since 28.02.2022
 */

@Service
public class UserLanguageServiceImpl implements UserLanguageService {

    @Autowired
    private UserLanguageRepo userLanguageRepo;

    @Override
    public UserLanguage createUserLanguage(Follower follower) {
        UserLanguage userLanguage = new UserLanguage();
        userLanguage.setFromLang(QueryButtons.ENGLISH);
        userLanguage.setToLang(QueryButtons.ENGLISH);
        userLanguage.setFollower(follower);
        return userLanguageRepo.save(userLanguage);
    }

    @Override
    public Optional<UserLanguage> getUserLanguage(Long chatId) {
         return userLanguageRepo.findByFollower_ChatId(chatId);
    }
}
