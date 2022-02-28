package org.algo.translator.algo.poster.service;

import org.algo.translator.algo.poster.enums.ButtonData;
import org.algo.translator.algo.poster.payload.CustomUpdate;
import org.algo.translator.algo.poster.payload.PostingConfigurationDto;
import org.algo.translator.algo.poster.utils.Boards;
import org.algo.translator.algo.poster.utils.Utils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yormamatov Davronbek
 * @since 29.01.2022
 */


@Component
public class DeepService {

    private TelegramLongPollingBot bot;

    private PostingConfigurationDto boardDto = new PostingConfigurationDto();

    private Map<Long, Integer> thanDelete = new HashMap<>();

    private boolean waitCountFromAdmin = false;
    private boolean waitPostIDFromAdmin = false;
    private boolean waitUsernameFromAdmin = false;
    private boolean waitHtmlTextFromAdmin = false;
    private boolean waitInlineKeyboardTextFromAdmin = false;

    private int lastBoardId = 0;

    protected void newPostingConfigure(CustomUpdate customUpdate, long countOfFollowers) {
        boardDto = new PostingConfigurationDto();
        boardDto.setAllFollowerCount(countOfFollowers);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(customUpdate.getChatId() + "");
        sendMessage.setReplyToMessageId(customUpdate.getMessageId());
        sendMessage.setText("USERNAME - POST ID");
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setReplyMarkup(Boards.asyncMessageToMembersBoard(boardDto));
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    protected void updateBoard(CustomUpdate customUpdate) {
        if (boardDto.getChannelUsername() == null && boardDto.getPostId() == 0) {
            try {
                bot.execute(
                        new EditMessageText(
                                customUpdate.getChatId() + "",
                                getLastBoardId(),
                                null,
                                "USERNAME - POST ID",
                                ParseMode.HTML,
                                true,
                                Boards.asyncMessageToMembersBoard(boardDto),
                                null
                        )
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (boardDto.getChannelUsername() != null && boardDto.getPostId() == 0) {
            try {
                bot.execute(
                        new EditMessageText(
                                customUpdate.getChatId() + "",
                                getLastBoardId(),
                                null,
                                "POST ID",
                                ParseMode.HTML,
                                true,
                                Boards.asyncMessageToMembersBoard(boardDto),
                                null
                        )
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (boardDto.getChannelUsername() == null && boardDto.getPostId() != 0) {
            try {
                bot.execute(
                        new EditMessageText(
                                customUpdate.getChatId() + "",
                                getLastBoardId(),
                                null,
                                "USERNAME",
                                ParseMode.HTML,
                                true,
                                Boards.asyncMessageToMembersBoard(boardDto),
                                null
                        )
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (boardDto.getChannelUsername() != null && boardDto.getPostId() != 0) {
            try {
                bot.execute(new DeleteMessage(customUpdate.getChatId() + "", getLastBoardId()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            try {
                bot.execute(
                        new CopyMessage(
                                customUpdate.getChatId() + "",
                                boardDto.getChannelUsername(),
                                boardDto.getPostId(),
                                boardDto.getHtmText(),
                                ParseMode.HTML,
                                null,
                                false,
                                null,
                                true,
                                Boards.asyncMessageToMembersBoard(boardDto)
                        )
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
                wrong(customUpdate);
            }
        }
    }

    protected void startPosting(CustomUpdate customUpdate, Map<Integer, Long> chatIdMap) {
        boardDto.setAdminChatId(customUpdate.getChatId() == null ? 0 : customUpdate.getChatId());
        if (Utils.checkChannelUsername(boardDto, bot, customUpdate)) return;
        if (Utils.checkPostId(boardDto, bot, customUpdate)) return;
        if (Utils.checkPostIdAndChannelUsername(boardDto, bot, customUpdate)) return;
        if (Utils.checkFollowerCount(boardDto, bot, customUpdate)) return;


        try {
            CopyMessage copyMessage = Utils.copyPostWithCustomData(boardDto, customUpdate);
            bot.execute(copyMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            wrong(customUpdate);
            return;
        }

        MessageThread messageThread = new MessageThread();
        messageThread.setProps("threadPosting", chatIdMap, bot, boardDto);
        messageThread.start();


    }

    protected void testPost(CustomUpdate customUpdate) {

        if (Utils.checkChannelUsername(boardDto, bot, customUpdate)) return;
        if (Utils.checkPostId(boardDto, bot, customUpdate)) return;
        if (Utils.checkPostIdAndChannelUsername(boardDto, bot, customUpdate)) return;

        CopyMessage copyMessage = Utils.copyPostWithCustomData(boardDto, customUpdate);
        try {
            bot.execute(new DeleteMessage(customUpdate.getChatId() + "", getLastBoardId()));
            bot.execute(copyMessage);
            bot.execute(new SendMessage(
                    customUpdate.getChatId() + "",
                    "<b>If Post are ready</b> -> click the [ " + ButtonData.START_POSTING.getText() + " ] button and the message will start sending.\n\n" +
                            "<b>If Post do not like it</b> -> click the [ " + ButtonData.KILL_PROCESS.getText() + " ] button and then everything will be canceled.\n\n" +
                            "<b>If Post need to make changes</b> -> click the [ " + ButtonData.RESUME_EDIT.getText() + " ] button, and then you can continue editing.",
                    ParseMode.HTML, true, false, null, Boards.lastQuestionBoard(), null, true));
        } catch (TelegramApiException e) {
            e.printStackTrace();
            wrong(customUpdate);
        }
    }

    protected void resumeEdit(CustomUpdate customUpdate) {
        try {
            bot.execute(new DeleteMessage(customUpdate.getChatId() + "", customUpdate.getMessageId()));
            bot.execute(
                    new CopyMessage(
                            customUpdate.getChatId() + "",
                            boardDto.getChannelUsername(),
                            boardDto.getPostId(),
                            boardDto.getHtmText(),
                            ParseMode.HTML,
                            null,
                            false,
                            null,
                            true,
                            Boards.asyncMessageToMembersBoard(boardDto)
                    )
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    protected void requestFollowerCount(CustomUpdate customUpdate) {
        try {
            Message execute = bot.execute(new SendMessage(customUpdate.getChatId() + "", "Send to how many subscribers you need to send:"));
            addThanDelete(customUpdate.getChatId(), execute.getMessageId());
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    protected void requestChannelUsername(CustomUpdate customUpdate) {
        try {
            Message execute = bot.execute(new SendMessage(customUpdate.getChatId() + "", "Send channel username with @ character:"));
            addThanDelete(customUpdate.getChatId(), execute.getMessageId());
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    protected void requestPostId(CustomUpdate customUpdate) {
        try {
            Message execute = bot.execute(new SendMessage(customUpdate.getChatId() + "", "Send the Post ID to be copied:"));
            addThanDelete(customUpdate.getChatId(), execute.getMessageId());
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    protected void requestPostBoard(CustomUpdate customUpdate) {
        try {
            Message execute = bot.execute(new SendMessage(customUpdate.getChatId() + "", "Send the pattern to the post buttons.\nFor example pattern :\n{\n\t[\n\t\t[btn_1](url_for_btn_1),\n\t\t[button2](urlForButton2)\n\t],\n\t[\n\t\t[button3](urlForButton3)\n\t]\n} "));
            addThanDelete(customUpdate.getChatId(), execute.getMessageId());
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    protected void requestHtmlText(CustomUpdate customUpdate) {
        try {
            Message execute = bot.execute(new SendMessage(customUpdate.getChatId() + "", "Send post text.\nHtml tags are available."));
            addThanDelete(customUpdate.getChatId(), execute.getMessageId());
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    protected PostingConfigurationDto getBoardDto() {
        return boardDto;
    }

    protected void notWait() {
        waitCountFromAdmin = false;
        waitPostIDFromAdmin = false;
        waitUsernameFromAdmin = false;
        waitHtmlTextFromAdmin = false;
        waitInlineKeyboardTextFromAdmin = false;
    }

    protected void wrong(CustomUpdate customUpdate) {
        try {
            bot.execute(new SendMessage(customUpdate.getChatId() + "", "WRONG!!!\nCHANNEL USERNAME or\nPOST ID or\nCURRENT BOT NOT ADMIN IN THIS CHANNEL: https://t.me/" + (boardDto.getChannelUsername() != null ? boardDto.getChannelUsername().substring(1) : null) + "\nPOST URL: https://t.me/" + (boardDto.getChannelUsername() != null ? boardDto.getChannelUsername().substring(1) : null) + "/" + boardDto.getPostId()));
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    protected void clearThanDelete() {
        thanDelete.forEach(this::deleteMessage);
        thanDelete = new HashMap<>();
    }

    protected void deleteMessage(Long chatId, Integer messageId) {
        try {
            bot.execute(new DeleteMessage(chatId + "", messageId));
        } catch (TelegramApiException ignored) {
        }
    }

    protected void addThanDelete(Long chatId, Integer messageId) {
        thanDelete.put(chatId, messageId);
    }

    protected boolean isWaitCountFromAdmin() {
        return waitCountFromAdmin;
    }

    protected void isWaitCountFromAdmin(boolean waitCountFromAdmin) {
        this.waitCountFromAdmin = waitCountFromAdmin;
    }

    protected boolean isWaitPostIDFromAdmin() {
        return waitPostIDFromAdmin;
    }

    protected void isWaitPostIDFromAdmin(boolean waitPostIDFromAdmin) {
        this.waitPostIDFromAdmin = waitPostIDFromAdmin;
    }

    protected boolean isWaitUsernameFromAdmin() {
        return waitUsernameFromAdmin;
    }

    protected void isWaitUsernameFromAdmin(boolean waitUsernameFromAdmin) {
        this.waitUsernameFromAdmin = waitUsernameFromAdmin;
    }

    protected boolean isWaitHtmlTextFromAdmin() {
        return waitHtmlTextFromAdmin;
    }

    protected void isWaitHtmlTextFromAdmin(boolean waitHtmlTextFromAdmin) {
        this.waitHtmlTextFromAdmin = waitHtmlTextFromAdmin;
    }

    protected boolean isWaitInlineKeyboardTextFromAdmin() {
        return waitInlineKeyboardTextFromAdmin;
    }

    protected void isWaitInlineKeyboardTextFromAdmin(boolean waitInlineKeyboardTextFromAdmin) {
        this.waitInlineKeyboardTextFromAdmin = waitInlineKeyboardTextFromAdmin;
    }

    protected int getLastBoardId() {
        return lastBoardId;
    }

    protected void setLastBoardId(int lastBoardId) {
        this.lastBoardId = lastBoardId;
    }

    protected void setBot(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    protected void clear(CustomUpdate customUpdate) {
        try {
            bot.execute(new DeleteMessage(customUpdate.getChatId() + "", customUpdate.getMessageId()));
            bot.execute(new SendMessage(customUpdate.getChatId() + "", "PROCESS KILLED"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        bot = null;
        boardDto = new PostingConfigurationDto();
        waitCountFromAdmin = false;
        waitPostIDFromAdmin = false;
        waitUsernameFromAdmin = false;
        waitHtmlTextFromAdmin = false;
        waitInlineKeyboardTextFromAdmin = false;
        lastBoardId = 0;
    }

}

