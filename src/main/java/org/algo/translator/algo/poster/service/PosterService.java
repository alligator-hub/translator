package org.algo.translator.algo.poster.service;

import org.algo.translator.algo.poster.enums.ButtonData;
import org.algo.translator.algo.poster.payload.CustomUpdate;
import org.algo.translator.algo.poster.payload.PostButtonDto;
import org.algo.translator.algo.poster.utils.Boards;
import org.algo.translator.algo.poster.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Yormamatov Davronbek
 * @since 20.02.2022
 */

@Component
public class PosterService {

    @Autowired
    private DeepService deepService;

    public boolean work(CustomUpdate customUpdate, TelegramLongPollingBot bot) {
        if (customUpdate.isTextMessage()) {
            deepService.setBot(bot);
            return textMessage(customUpdate);
        } else if (customUpdate.isCallBackQuery()) {
            deepService.setBot(bot);
            return query(customUpdate);
        }
        return false;
    }

    public void  weCatchStart(CustomUpdate customUpdate){
        deepService.deleteMessage(customUpdate.getChatId(),customUpdate.getMessageId());
    }

    private boolean query(CustomUpdate customUpdate) {

        boolean anyMatch = Arrays.stream(ButtonData.values()).anyMatch(buttonData -> buttonData.getData().equals(customUpdate.getQueryData()));
        if (!anyMatch) return false;

        if (ButtonData.SWITCH_NOTIFY.getData().equals(customUpdate.getQueryData())) {
            deepService.getBoardDto().setNotification(!deepService.getBoardDto().isNotification());
            deepService.setLastBoardId(customUpdate.getMessageId());
            deepService.updateBoard(customUpdate);
            return true;
        } else if (ButtonData.SWITCH_DESC_SENDING.getData().equals(customUpdate.getQueryData())) {
            deepService.getBoardDto().setSendByDesc(!deepService.getBoardDto().isSendByDesc());
            deepService.setLastBoardId(customUpdate.getMessageId());
            deepService.updateBoard(customUpdate);
            return true;
        } else if (ButtonData.HOW_MANY_SEND.getData().equals(customUpdate.getQueryData())) {
            deepService.setLastBoardId(customUpdate.getMessageId());
            deepService.notWait();
            deepService.isWaitCountFromAdmin(true);
            deepService.requestFollowerCount(customUpdate);
            return true;
        } else if (ButtonData.SET_CHANNEL_USERNAME.getData().equals(customUpdate.getQueryData())) {
            deepService.setLastBoardId(customUpdate.getMessageId());
            deepService.notWait();
            deepService.isWaitUsernameFromAdmin(true);
            deepService.requestChannelUsername(customUpdate);
            return true;
        } else if (ButtonData.SET_POST_ID.getData().equals(customUpdate.getQueryData())) {
            deepService.setLastBoardId(customUpdate.getMessageId());
            deepService.notWait();
            deepService.isWaitPostIDFromAdmin(true);
            deepService.requestPostId(customUpdate);
            return true;
        } else if (ButtonData.SET_CUSTOM_BOARD.getData().equals(customUpdate.getQueryData())) {
            deepService.setLastBoardId(customUpdate.getMessageId());
            deepService.notWait();
            deepService.isWaitInlineKeyboardTextFromAdmin(true);
            deepService.requestPostBoard(customUpdate);
            return true;
        } else if (ButtonData.SET_HTML_TEXT.getData().equals(customUpdate.getQueryData())) {
            deepService.setLastBoardId(customUpdate.getMessageId());
            deepService.notWait();
            deepService.isWaitHtmlTextFromAdmin(true);
            deepService.requestHtmlText(customUpdate);
            return true;
        } else if (ButtonData.TEST_POST.getData().equals(customUpdate.getQueryData())) {
            deepService.notWait();
            deepService.setLastBoardId(customUpdate.getMessageId());
            deepService.testPost(customUpdate);
            return true;
        } else if (ButtonData.RESUME_EDIT.getData().equals(customUpdate.getQueryData())) {
            deepService.resumeEdit(customUpdate);
            return true;
        } else if (ButtonData.KILL_PROCESS.getData().equals(customUpdate.getQueryData())) {
            deepService.clear(customUpdate);
            return true;
        }
        return false;
    }

    private boolean textMessage(CustomUpdate customUpdate) {
        if (deepService.isWaitCountFromAdmin()) {
            if (Utils.isAllDigit(customUpdate.getText())) {
                deepService.getBoardDto().setSendFollowerCount(Long.parseLong(customUpdate.getText()));
                deepService.notWait();
                deepService.clearThanDelete();
                deepService.deleteMessage(customUpdate.getChatId(), customUpdate.getMessageId());
                deepService.updateBoard(customUpdate);
            } else deepService.requestFollowerCount(customUpdate);
            return true;
        } else if (deepService.isWaitPostIDFromAdmin()) {
            if (Utils.isAllDigit(customUpdate.getText())) {
                deepService.getBoardDto().setPostId(Integer.parseInt(customUpdate.getText()));
                deepService.notWait();
                deepService.clearThanDelete();
                deepService.deleteMessage(customUpdate.getChatId(), customUpdate.getMessageId());
                deepService.updateBoard(customUpdate);
            } else deepService.requestPostId(customUpdate);
            return true;
        } else if (deepService.isWaitUsernameFromAdmin()) {
            if (customUpdate.getText().startsWith("@")) {
                deepService.getBoardDto().setChannelUsername(customUpdate.getText());
                deepService.notWait();
                deepService.clearThanDelete();
                deepService.deleteMessage(customUpdate.getChatId(), customUpdate.getMessageId());
                deepService.updateBoard(customUpdate);
            } else deepService.requestChannelUsername(customUpdate);
            return true;
        } else if (deepService.isWaitHtmlTextFromAdmin()) {
            deepService.getBoardDto().setHtmText(customUpdate.getText());
            deepService.notWait();
            deepService.clearThanDelete();
            deepService.deleteMessage(customUpdate.getChatId(), customUpdate.getMessageId());
            deepService.updateBoard(customUpdate);
            return true;
        } else if (deepService.isWaitInlineKeyboardTextFromAdmin()) {
            List<PostButtonDto> postButtonDtoList = Boards.parseTextToButtonDtoList(customUpdate.getText());
            if (postButtonDtoList != null) {
                deepService.getBoardDto().setInlineKeyboardText(customUpdate.getText());
                deepService.getBoardDto().setInlineKeyboardMarkup(Boards.makeBoardFromDtoList(postButtonDtoList));
                deepService.notWait();
                deepService.clearThanDelete();
                deepService.deleteMessage(customUpdate.getChatId(), customUpdate.getMessageId());
                deepService.updateBoard(customUpdate);
            } else deepService.requestPostBoard(customUpdate);
            return true;
        }
        return false;
    }

    public void asyncMessage(CustomUpdate customUpdate, long followerCount) {
        deepService.newPostingConfigure(customUpdate, followerCount);
    }

    public void startAsyncSending(CustomUpdate customUpdate, Map<Integer, Long> chatIdMap) {
        deepService.startPosting(customUpdate, chatIdMap);
    }

    public long getLimit() {
        return deepService.getBoardDto().getSendFollowerCount();
    }
}
