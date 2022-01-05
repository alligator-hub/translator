package org.algo.translator.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class MessageMaker {

    public SendMessage make(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.setReplyMarkup(removeBtn());
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    public SendMessage make(Long chatId, String message, InlineKeyboardMarkup board) {
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), message);
        sendMessage.setReplyMarkup(board);
        sendMessage.setParseMode(ParseMode.HTML);
        return sendMessage;
    }

    public EditMessageText editMessage(String newText, Long chatId, Integer messageId, String inlineMessageId, InlineKeyboardMarkup keyboardMarkup) {
        EditMessageText editMessageText = new EditMessageText(newText);
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setInlineMessageId(inlineMessageId);
        editMessageText.setMessageId(messageId);
        editMessageText.setReplyMarkup(keyboardMarkup);
        editMessageText.setParseMode(ParseMode.HTML);
        return editMessageText;
    }
    private ReplyKeyboardRemove removeBtn() {
        return new ReplyKeyboardRemove(true, false);
    }
}
