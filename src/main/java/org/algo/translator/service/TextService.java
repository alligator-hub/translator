package org.algo.translator.service;

import org.algo.translator.controller.Bot;
import org.algo.translator.entity.Follower;
import org.algo.translator.entity.UserLanguage;
import org.algo.translator.enums.Statics;
import org.algo.translator.model.RequestDto;
import org.algo.translator.repo.FollowerRepo;
import org.algo.translator.repo.UserLanguageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Service
public class TextService {

    @Value("${admin.username}")
    private String adminUsername;

    @Autowired
    FollowerRepo followerRepo;

    @Autowired
    DefaultService defaultService;

    @Autowired
    UserLanguageRepo userLanguageRepo;

    @Autowired
    CallBackService callBackService;

    @Autowired
    BoardService boardService;

    @Autowired
    MessageMaker maker;

    @Autowired
    Bot bot;

    @Autowired
    Translator translator;

    public void map(RequestDto request) {


        Optional<UserLanguage> userLanguageOptional = userLanguageRepo.findByFollower_ChatId(request.getChatId());
        if (userLanguageOptional.isPresent()) {
            UserLanguage userLanguage = userLanguageOptional.get();
            if (userLanguage.getToLang() != null && userLanguage.getFromLang() != null) {
                translateText(request.getText(), request.getFollower(), userLanguage);
                return;
            }
        }
        //todo if useanguage not found then me send new select language board
        Message message = bot.sendMessage(
                maker.make(request.getChatId(), Statics.WELCOME.getValue(), boardService.languagesBoard("", true))
        );
        //todo create new userLanguage froum and to languages deafault type AUTO
        callBackService.finishChooseLanguage(request.getChatId(), message.getMessageId(), null, request.getFollower());
    }

    public void translateText(String text, Follower follower, UserLanguage language) {
        String translate = null;
        try {
            if (language.getFromLang().getCode().equals(language.getToLang().getCode())) {
                bot.sendMessage(maker.make(follower.getChatId(), text));
                return;
            }
            translate = translator.getTranslate(text, language.getFromLang(), language.getToLang());
        } catch (Exception ignore) {
        }

        bot.sendMessage(maker.make(follower.getChatId(), translate == null ? "[BUG] Let the admin know @web_algo" : translate));
    }

    public void statistics(Long chatId) {
        try {
            String staticsTemplate =
                    "<pre>---------------------------------</pre>\n" +
                            "<b>Followers count:</b> " + followerRepo.count() + "\n" +
                            "<pre>---------------------------------</pre>";

            SendMessage message = maker.make(chatId, staticsTemplate);
            bot.sendMessage(message);
        } catch (Exception ignore) {
        }
    }
}
