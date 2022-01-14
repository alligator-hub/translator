package org.algo.translator.service;

import org.algo.translator.controller.Bot;
import org.algo.translator.entity.Follower;
import org.algo.translator.entity.UserLanguage;
import org.algo.translator.enums.LanguageType;
import org.algo.translator.enums.Statics;
import org.algo.translator.model.RequestDto;
import org.algo.translator.repo.FollowerRepo;
import org.algo.translator.repo.UserLanguageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CallBackService {

    @Autowired
    @Lazy
    Bot bot;

    @Autowired
    @Lazy
    BoardService boardService;

    @Autowired
    MessageMaker messageMaker;

    @Autowired
    UserLanguageRepo userLanguageRepo;

    @Autowired
    @Lazy
    DefaultService defaultService;

    @Autowired
    FollowerRepo followerRepo;

    public void map(RequestDto request) {

        Optional<UserLanguage> userLanguageOptional = userLanguageRepo.findByFollower_ChatId(request.getChatId());
        if (userLanguageOptional.isPresent()) {
            try {
                UserLanguage userLanguage = userLanguageOptional.get();
                if (!Objects.equals(userLanguage.getLastBoardId(), request.getMessageId())) {
                    bot.deleteMessage(request.getChatId(), userLanguage.getLastBoardId());
                }
                userLanguage.setLastBoardId(request.getMessageId());
                userLanguageRepo.save(userLanguage);
            } catch (Exception ignored) {
            }
        }


        if (!request.getData().startsWith(" - ") & !request.getData().equals("back")) {
            // todo send finish wait text
            finishChooseLanguage(request.getChatId(), request.getMessageId(), request.getData(), request.getFollower());
        } else if (request.getData().equals("back")) {
            // todo send first selection ( source language )
            selectLanguage(request.getChatId(), request.getMessageId(), request.getData(), true);
        } else if (request.getData().startsWith(" - ")) {
            // todo send second selection  ( translation language )
            selectLanguage(request.getChatId(), request.getMessageId(), request.getData(), false);
        }
    }

    public void selectLanguage(Long chatId, Integer messageId, String data, boolean first) {

        try {
            String text;
            if (first) {
                data = "";
                text = Statics.WELCOME.getValue();
            } else {
                text = Statics.TRANSLATE_LANG.getValue();
            }

            bot.editMessage(messageMaker.editMessage(text, chatId, messageId, null, boardService.languagesBoard(data, first)));
        } catch (Exception ignored) {
        }
    }


    public void finishChooseLanguage(Long chatId, Integer messageId, String data, Follower follower) {
        try {
            LanguageType fromLang;
            LanguageType toLang;
            if (data == null) {
                fromLang = LanguageType.AUTO;
                toLang = LanguageType.AUTO;
            } else {
                fromLang = LanguageType.getLanguage(data.substring(0, data.indexOf(" ")));
                toLang = LanguageType.getLanguage(data.substring(data.lastIndexOf(" ") + 1));
            }

            UserLanguage userLanguage;
            Optional<UserLanguage> userLanguageOptional = userLanguageRepo.findByFollower_ChatId(chatId);
            userLanguage = userLanguageOptional.orElseGet(() -> new UserLanguage(follower));

            userLanguage.setFromLang(fromLang);
            userLanguage.setToLang(toLang);
            userLanguage.setLastBoardId(messageId);

            userLanguageRepo.save(userLanguage);
            if (data == null) return;
            bot.editMessage(messageMaker.editMessage(Statics.ENTER_TEXT.getValue(), chatId, messageId, null, boardService.backBoard()));

        } catch (Exception ignored) {
        }
    }
}
