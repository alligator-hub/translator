package org.algo.translator.service.impl;

import org.algo.translator.controller.Bot;
import org.algo.translator.entity.Follower;
import org.algo.translator.entity.UserLanguage;
import org.algo.translator.enums.QueryButtons;
import org.algo.translator.enums.Statics;
import org.algo.translator.model.UpdateDto;
import org.algo.translator.repo.UserLanguageRepo;
import org.algo.translator.helpers.MessageMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CallBackServiceImpl {

    @Autowired
    @Lazy
    Bot bot;

    @Autowired
    @Lazy
    BoardServiceImpl boardServiceImpl;

    @Autowired
    UserLanguageRepo userLanguageRepo;

    public void map(UpdateDto request) {

        if (!request.getQueryData().startsWith(" - ") & !request.getQueryData().equals(QueryButtons.BACK.getCode())) {
            // todo send finish wait text
            finishChooseLanguage(request.getChatId(), request.getMessageId(), request.getQueryData(), request.getFollower());
        } else if (request.getQueryData().equals(QueryButtons.BACK.getCode())) {
            // todo send first selection ( source language )
            selectLanguage(request.getChatId(), request.getMessageId(), request.getQueryData(), true);
        } else if (request.getQueryData().startsWith(" - ")) {
            // todo send second selection  ( translation language )
            selectLanguage(request.getChatId(), request.getMessageId(), request.getQueryData(), false);
        }
    }

    private void selectLanguage(Long chatId, Integer messageId, String data, boolean first) {

        String text;
        if (first) {
            data = "";
            text = Statics.WELCOME.getValue();
        } else {
            text = Statics.TRANSLATE_LANG.getValue();
        }

        bot.editMessage(MessageMaker.editMessage(text, chatId, messageId, null, boardServiceImpl.languagesBoard(data, first)));

    }

    public void finishChooseLanguage(Long chatId, Integer messageId, String data, Follower follower) {
        QueryButtons fromLang;
        QueryButtons toLang;
        if (data == null) {
            fromLang = QueryButtons.ENGLISH;
            toLang = QueryButtons.ENGLISH;
        } else {
            fromLang = QueryButtons.getLanguage(data.substring(0, data.indexOf(" ")));
            toLang = QueryButtons.getLanguage(data.substring(data.lastIndexOf(" ") + 1));
        }

        UserLanguage userLanguage;
        Optional<UserLanguage> userLanguageOptional = userLanguageRepo.findByFollower_ChatId(chatId);
        userLanguage = userLanguageOptional.orElseGet(() -> new UserLanguage(follower));

        userLanguage.setFromLang(fromLang);
        userLanguage.setToLang(toLang);

        userLanguageRepo.save(userLanguage);
        if (data == null) return;
        bot.editMessage(MessageMaker.editMessage(Statics.ENTER_TEXT.getValue(), chatId, messageId, null, boardServiceImpl.backBoard()));
    }
}
