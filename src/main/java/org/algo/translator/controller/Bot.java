package org.algo.translator.controller;

import lombok.extern.slf4j.Slf4j;
import org.algo.translator.algo.poster.enums.ButtonData;
import org.algo.translator.algo.poster.payload.CustomUpdate;
import org.algo.translator.algo.poster.service.PosterService;
import org.algo.translator.entity.Follower;
import org.algo.translator.helpers.MessageMaker;
import org.algo.translator.model.UpdateDto;
import org.algo.translator.repo.FollowerRepo;
import org.algo.translator.service.FollowerService;
import org.algo.translator.service.impl.BoardServiceImpl;
import org.algo.translator.service.impl.CallBackServiceImpl;
import org.algo.translator.service.impl.TextMessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @Lazy
    private TextMessageServiceImpl textMessageServiceImpl;

    @Autowired
    private CallBackServiceImpl callBackServiceImpl;

    @Autowired
    private FollowerRepo followerRepo;

    @Autowired
    private PosterService posterService;

    @Autowired
    private FollowerService followerService;

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
        if (injectMethod(update, adminUsername)) return;

        UpdateDto updateDto = getRequest(update);
        if (updateDto == null) return;

        Follower follower = followerService.receiveAction(updateDto);
        updateDto.setFollower(follower);

        if (updateDto.isTextMessage()) {
            textMessageServiceImpl.map(updateDto);
        } else if (updateDto.isCallBackQuery()) {
            callBackServiceImpl.map(updateDto);
        }
    }

    private UpdateDto getRequest(Update update) {
        UpdateDto updateDto = new UpdateDto();

        Message message = null;

        if (update.hasCallbackQuery()) {
            updateDto.setCallBackQuery(true);
            message = update.getCallbackQuery().getMessage();
            updateDto.setQueryData(update.getCallbackQuery().getData());
        } else if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                updateDto.setTextMessage(true);
                message = update.getMessage();
                updateDto.setText(message.getText());
            }
        }
        if (message == null) return null;

        updateDto.setChatId(message.getChatId());
        updateDto.setMessageId(message.getMessageId());
        updateDto.setFirstName(message.getChat().getFirstName());
        updateDto.setLastName(message.getChat().getLastName());
        updateDto.setUsername(message.getChat().getUserName());

        return updateDto.getChatId() == null ? null : updateDto;
    }

    public Message sendMessage(SendMessage sendMessage) {
        if (sendMessage.getText().length() > 4096) {
            sendMessage.setText(sendMessage.getText().substring(0, 4096));
        }
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void editMessage(EditMessageText editMessageText) {
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean injectMethod(Update update, String adminUsername) {
        CustomUpdate dto = CustomUpdate.makeCustomUpdate(update);
        if (dto == null) return false;
        if (dto.getUsername() == null) return false;
        if (!Objects.equals(dto.getUsername(), adminUsername)) return false;
        boolean work = posterService.work(dto, this);
        if (dto.isTextMessage()) {
            if (dto.getText().equals("/async_message")) {
                //todo yourFollowerCount
                posterService.asyncMessage(dto, followerRepo.count());
                return true;
            }
        } else if (dto.isCallBackQuery()) {
            if (ButtonData.START_POSTING.getData().equals(dto.getQueryData())) {

                posterService.weCatchStart(dto);

                //todo followers map KEY any id,VALUE followerChatId
                Map<Integer, Long> yourFollowersMap = new HashMap<>();
                List<Follower> all = followerRepo.getFollowersLimit(posterService.getLimit());
                all.forEach(follower -> {
                    yourFollowersMap.put(follower.getId().intValue(), follower.getChatId());
                });
                posterService.startAsyncSending(dto, yourFollowersMap);
                return true;
            }
        }
        return work;
    }
}
