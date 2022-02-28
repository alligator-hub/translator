package org.algo.translator.service.impl;

import org.algo.translator.entity.Follower;
import org.algo.translator.model.UpdateDto;
import org.algo.translator.repo.FollowerRepo;
import org.algo.translator.service.FollowerService;
import org.algo.translator.service.UserLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * @author Yormamatov Davronbek
 * @since 28.02.2022
 */

@Service
public class FollowerServiceImpl implements FollowerService {

    @Autowired
    private FollowerRepo followerRepo;

    @Autowired
    private UserLanguageService userLanguageService;

    @Override
    public Follower receiveAction(UpdateDto request) {
        Optional<Follower> followerOptional = followerRepo.findByChatId(request.getChatId());

        followerOptional.ifPresent(follower -> {
            follower.setFirstName(request.getFirstName());
            follower.setLastName(request.getLastName());
            follower.setUsername(request.getUsername());
            follower.setLastConnectionDate(Instant.now(Clock.systemUTC()).getEpochSecond() * 1000);
            followerRepo.save(follower);
        });

        return followerOptional.orElseGet(() -> addNewFollower(request));
    }

    @Override
    public Follower addNewFollower(UpdateDto request) {
        Long startedDate = Instant.now(Clock.systemUTC()).getEpochSecond() * 1000;
        Follower follower = new Follower();
        follower.setFirstName(request.getFirstName());
        follower.setLastName(request.getLastName());
        follower.setUsername(request.getUsername());
        follower.setChatId(request.getChatId());
        follower.setStartedDate(startedDate);
        follower.setLastConnectionDate(startedDate);
        followerRepo.save(follower);

        if (userLanguageService.getUserLanguage(follower.getChatId()).isEmpty())
            userLanguageService.createUserLanguage(follower);
        return follower;
    }

}
