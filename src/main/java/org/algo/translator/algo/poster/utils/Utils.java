package org.algo.translator.algo.poster.utils;

import org.algo.translator.algo.poster.payload.CustomUpdate;
import org.algo.translator.algo.poster.payload.PostingConfigurationDto;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * @author Yormamatov Davronbek
 * @since 20.02.2022
 */
public class Utils {
    public static CopyMessage copyPostWithCustomData(PostingConfigurationDto boardDto, CustomUpdate customUpdate) {
        CopyMessage copyMessage = new CopyMessage(
                customUpdate.getChatId() + "",
                boardDto.getChannelUsername(),
                boardDto.getPostId(),
                boardDto.getHtmText(),
                ParseMode.HTML,
                null,
                boardDto.isNotification(),
                null,
                true,
                boardDto.getInlineKeyboardMarkup());
        copyMessage.setParseMode(ParseMode.HTML);
        return copyMessage;
    }

    public static boolean checkPostIdAndChannelUsername(PostingConfigurationDto boardDto, TelegramLongPollingBot bot, CustomUpdate customUpdate) {
        if (boardDto.getChannelUsername() == null && boardDto.getPostId() == 0) {
            try {
                bot.execute(new SendMessage(customUpdate.getChatId() + "", "Enter the CHANNEL USERNAME and POST ID correctly!"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public static boolean checkPostId(PostingConfigurationDto boardDto, TelegramLongPollingBot bot, CustomUpdate customUpdate) {
        if (boardDto.getPostId() == 0) {
            try {
                bot.execute(new SendMessage(customUpdate.getChatId() + "", "Enter POST ID correctly!"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public static boolean checkChannelUsername(PostingConfigurationDto boardDto, TelegramLongPollingBot bot, CustomUpdate customUpdate) {
        if (boardDto.getChannelUsername() == null && boardDto.getPostId() == 0) {
            try {
                bot.execute(new SendMessage(customUpdate.getChatId() + "", "Enter the CHANNEL USERNAME correctly!"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public static boolean checkFollowerCount(PostingConfigurationDto boardDto, TelegramLongPollingBot bot, CustomUpdate customUpdate) {
        if (boardDto.getAllFollowerCount() < boardDto.getSendFollowerCount()) {
            try {
                bot.execute(new SendMessage(customUpdate.getChatId() + "", "The SENDERS count greater than FOLLOWERS count!"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return true;
        } else if (boardDto.getSendFollowerCount() == 0) {
            try {
                bot.execute(new SendMessage(customUpdate.getChatId() + "", "Enter count of the subscribers to be sent!"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public static boolean isAllDigit(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) return false;
        }
        return true;
    }

}
