package org.algo.translator.helpers;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

public class MessageMaker {

    public static SendMessage make(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.setReplyMarkup(removeBtn());
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    public static SendMessage make(Long chatId, String message, InlineKeyboardMarkup board) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.setReplyMarkup(board);
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    public static EditMessageText editMessage(String newText, Long chatId, Integer messageId, String inlineMessageId, InlineKeyboardMarkup keyboardMarkup) {
        EditMessageText editMessageText = new EditMessageText(newText);
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setInlineMessageId(inlineMessageId);
        editMessageText.setMessageId(messageId);
        editMessageText.setReplyMarkup(keyboardMarkup);
        editMessageText.setParseMode(ParseMode.HTML);
        return editMessageText;
    }

    public static ReplyKeyboardRemove removeBtn() {
        return new ReplyKeyboardRemove(true, false);
    }
}
