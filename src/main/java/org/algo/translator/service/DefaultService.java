package org.algo.translator.service;

import org.algo.translator.controller.Bot;
import org.algo.translator.entity.Follower;
import org.algo.translator.enums.Statics;
import org.algo.translator.model.RequestDto;
import org.algo.translator.repo.FollowerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class DefaultService {

    @Autowired
    FollowerRepo followerRepo;

    @Autowired
    @Lazy
    BoardService boardService;

    @Autowired
    @Lazy
    MessageMaker maker;

    @Autowired
    @Lazy
    CallBackService callBackService;

    @Autowired
    @Lazy
    Bot bot;

    public Follower receiveAction(RequestDto request) {

        Optional<Follower> followerOptional = followerRepo.findByChatId(request.getChatId());

        if (followerOptional.isPresent()) {
            Follower follower = followerOptional.get();
            follower.setFirstName(request.getFirstName());
            follower.setLastName(request.getLastName());
            follower.setUsername(request.getUsername());
            follower.setLastConnectionDate((LocalDateTime.now().toInstant(ZoneOffset.UTC)).getEpochSecond());
            return followerRepo.save(follower);
        } else {
            return addNewFollower(request);
        }
    }


    public Follower addNewFollower(RequestDto request) {

        Long startedDate = (LocalDateTime.now().toInstant(ZoneOffset.UTC)).getEpochSecond();

        Follower follower = new Follower();
        follower.setFirstName(request.getFirstName());
        follower.setLastName(request.getLastName());
        follower.setUsername(request.getUsername());
        follower.setChatId(request.getChatId());
        follower.setStartedDate(startedDate);
        follower.setLastConnectionDate(startedDate);
        return followerRepo.save(follower);
    }

    public Boolean isStart(String text) {
        return text.equals(Statics.CMD_START.getValue());
    }
}
