package org.algo.translator.service.impl;

import org.algo.translator.controller.Bot;
import org.algo.translator.entity.Follower;
import org.algo.translator.entity.UserLanguage;
import org.algo.translator.enums.Statics;
import org.algo.translator.helpers.MessageMaker;
import org.algo.translator.model.UpdateDto;
import org.algo.translator.repo.FollowerRepo;
import org.algo.translator.repo.UserLanguageRepo;
import org.algo.translator.service.TextMessageService;
import org.algo.translator.service.TranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
public class TextMessageServiceImpl implements TextMessageService {

    @Value("${bot.bug_msg}")
    private String bugMsg;

    @Autowired
    private FollowerRepo followerRepo;

    @Autowired
    private CommandsCheckersService commandsCheckers;

    @Autowired
    private UserLanguageRepo userLanguageRepo;

    @Autowired
    private CallBackServiceImpl callBackServiceImpl;

    @Autowired
    private BoardServiceImpl boardServiceImpl;

    @Autowired
    private Bot bot;

    @Autowired
    private TranslateService translateService;


    @Override
    public void map(UpdateDto updateDto) {

        if (commandsCheckers.isStart(updateDto.getText())) {
            gettingStartCommand(updateDto);
            return;
        } else if (commandsCheckers.isStatics(updateDto.getText())) {
            gettingStaticsCommand(updateDto);
            return;
        } else if (commandsCheckers.isLanguage(updateDto.getText())) {
            gettingLanguageCommand(updateDto);
            return;
        }

        Optional<UserLanguage> userLanguageOptional = userLanguageRepo.findByFollower_ChatId(updateDto.getChatId());

        userLanguageOptional.ifPresent(userLanguage -> {
            if (userLanguage.getToLang() != null && userLanguage.getFromLang() != null) {
                translateText(updateDto.getText(), updateDto.getFollower(), userLanguage);
            }
        });

        if (userLanguageOptional.isEmpty()) {
            //todo if user languages not found then me send new select language board
            Message message = bot.sendMessage(
                    MessageMaker.make(updateDto.getChatId(), Statics.WELCOME.getValue(), boardServiceImpl.languagesBoard("", true))
            );
            //todo create new userLanguage from and to languages default type AUTO
            callBackServiceImpl.finishChooseLanguage(updateDto.getChatId(), message.getMessageId(), null, updateDto.getFollower());
        }
    }

    private void translateText(String text, Follower follower, UserLanguage language) {

        if (language.getFromLang().getCode().equals(language.getToLang().getCode())) {
            bot.sendMessage(MessageMaker.make(follower.getChatId(), text));
            return;
        }
        String translate = translateService.getTranslate(text, language.getFromLang(), language.getToLang());

        bot.sendMessage(MessageMaker.make(follower.getChatId(), translate != null ? translate : bugMsg));
    }

    private void gettingLanguageCommand(UpdateDto updateDto) {
        Message message = bot.sendMessage(
                MessageMaker.make(
                        updateDto.getChatId(),
                        Statics.WELCOME.getValue(),
                        boardServiceImpl.languagesBoard("", true)
                )
        );
        callBackServiceImpl.finishChooseLanguage(updateDto.getChatId(), message.getMessageId(), null, updateDto.getFollower());

    }

    private void gettingStartCommand(UpdateDto updateDto) {
        Message message = bot.sendMessage(
                MessageMaker.make(updateDto.getChatId(),
                        Statics.WELCOME.getValue(),
                        boardServiceImpl.languagesBoard("", true))
        );

        //todo create new userLanguage froum and to languages deafault type AUTO//
        callBackServiceImpl.finishChooseLanguage(updateDto.getChatId(), message.getMessageId(), null, updateDto.getFollower());
    }

    private void gettingStaticsCommand(UpdateDto updateDto) {
        String staticsTemplate =
                "<b>Statistics</b>\n\n" +
                        "<b>Total number of members : </b> " + followerRepo.count() + "\n" +
                        "<b>Members in the last 24 hours: : </b> " + followerRepo.countAllByStartedDateAfter((Instant.now(Clock.systemUTC()).getEpochSecond() * 1000) - 86_400_000) + "\n" +
                        "<b>Members in the last 17 days : </b>" + followerRepo.countAllByStartedDateAfter((Instant.now(Clock.systemUTC()).getEpochSecond() * 1000) - 86_400_000L * 17) + "\n\n" +
                        "For advertising service, contact @web_algo.";

        SendMessage message = MessageMaker.make(updateDto.getChatId(), staticsTemplate);
        bot.sendMessage(message);
    }
}
