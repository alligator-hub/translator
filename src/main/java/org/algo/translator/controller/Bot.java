package org.algo.translator.controller;

import lombok.extern.slf4j.Slf4j;
import org.algo.translator.entity.Follower;
import org.algo.translator.enums.Statics;
import org.algo.translator.model.RequestDto;
import org.algo.translator.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

@Component
@Slf4j
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    @Value("${admin.username}")
    private String adminUsername;


    @Autowired
    DefaultService defaultService;

    @Autowired
    @Lazy
    TextService textService;

    @Autowired
    CallBackService callBackService;

    @Autowired
    MessageMaker maker;

    @Autowired
    BoardService boardService;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    @Override
    public void onUpdateReceived(Update update) {

        RequestDto request = getRequest(update);

        if (request.getIsText()) {

            Follower follower = defaultService.receiveAction(request);
            request.setFollower(follower);

            if (defaultService.isStart(request.getText())) {
                Message message = sendMessage(
                        maker.make(request.getChatId(),
                                Statics.WELCOME.getValue(),
                                boardService.languagesBoard("",true))
                );

                //todo create new userLanguage froum and to languages deafault type AUTO//
                callBackService.finishChooseLanguage(request.getChatId(), message.getMessageId(), null, follower);

            } else if (request.getText().equals("/statistics") && request.getUsername().equals(adminUsername)) {
                textService.statistics(request.getChatId());

            } else if (request.getText().equals("/language")) {
                Message message = sendMessage(
                        maker.make(
                                request.getChatId(),
                                Statics.WELCOME.getValue(),
                                boardService.languagesBoard("",true)
                        )
                );
                callBackService.finishChooseLanguage(request.getChatId(), message.getMessageId(), null, follower);
            } else {
                textService.map(request);
            }


        } else if (request.getIsCallBackQuery()) {
            Follower follower = defaultService.receiveAction(request);
            request.setFollower(follower);
            callBackService.map(request);
        }
    }


    private RequestDto getRequest(Update update) {
        RequestDto requestDto = new RequestDto();
        if (update.hasCallbackQuery()) {
            requestDto.setIsText(false);
            requestDto.setIsCallBackQuery(true);

            requestDto.setChatId(update.getCallbackQuery().getMessage().getChatId());
            requestDto.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

            requestDto.setFirstName(update.getCallbackQuery().getMessage().getChat().getFirstName());
            requestDto.setLastName(update.getCallbackQuery().getMessage().getChat().getLastName());
            requestDto.setUsername(update.getCallbackQuery().getMessage().getChat().getUserName());

            requestDto.setText(update.getCallbackQuery().getMessage().getText());
            requestDto.setData(update.getCallbackQuery().getData());

        } else if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                requestDto.setIsText(true);
                requestDto.setIsCallBackQuery(false);

                requestDto.setChatId(update.getMessage().getChatId());
                requestDto.setMessageId(update.getMessage().getMessageId());

                requestDto.setFirstName(update.getMessage().getChat().getFirstName());
                requestDto.setLastName(update.getMessage().getChat().getLastName());
                requestDto.setUsername(update.getMessage().getChat().getUserName());

                requestDto.setData(null);
                requestDto.setText(update.getMessage().getText());
            }
        }
        return requestDto;
    }


    public Message sendMessage(SendMessage sendMessage) {
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Serializable editMessage(EditMessageText editMessageText) {
        try {
            return execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteMessage(Long chatId, Integer messageId) {
        try {
            execute(new DeleteMessage(String.valueOf(chatId), messageId));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
