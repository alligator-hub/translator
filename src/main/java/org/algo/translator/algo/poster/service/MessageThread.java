package org.algo.translator.algo.poster.service;

import com.google.gson.Gson;
import org.algo.translator.algo.poster.payload.PostingConfigurationDto;
import org.algo.translator.algo.poster.payload.SendResult;
import org.algo.translator.algo.poster.payload.SendResultWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * @author Yormamatov Davronbek
 * @since 02.02.2022
 */
@Component
class MessageThread implements Runnable {

    private String jsonFilePath="/apps/";

    private TelegramLongPollingBot bot;
    private Thread thread;
    private String threadName;
    private PostingConfigurationDto configDto;
    private Map<Integer, Long> chatIdMap;


    @Override
    public void run() {
        System.out.println(jsonFilePath);
        jsonFilePath = jsonFilePath + "/" + threadName + "-" + bot.getBotUsername() + "-" + configDto.getChannelUsername() + "-" + configDto.getPostId() + "-" + (Instant.now(Clock.systemUTC()).getEpochSecond() * 1000) + ".json";
        System.out.println(jsonFilePath);

        try {
            boolean file = new File(jsonFilePath).createNewFile();
            if (!file) return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        SendResultWrapper wrapper = new SendResultWrapper(new ArrayList<>(), 0, chatIdMap.size(), 0, chatIdMap.size());

        chatIdMap.forEach((id, chatId) -> wrapper.getResults().add(new SendResult(id, chatId, configDto.getPostId(), configDto.getChannelUsername(), configDto.isNotification(), false, 0L)));

        try (FileWriter writer = new FileWriter(jsonFilePath)) {
            gson.toJson(wrapper, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }


        final long[] counter = {0};
        chatIdMap.forEach((id, chatId) -> {
            //send message and change send status
            if (counter[0] < configDto.getSendFollowerCount()) {
                try {
                    CopyMessage copyMessage = copyPostWithCustomData(configDto, chatId);
                    bot.execute(copyMessage);
                    editeJson(chatId, true);
                } catch (TelegramApiException e) {
                    e.getMessage();
                    editeJson(chatId, false);
                }
                counter[0]++;
            }
        });

        File file = new File(jsonFilePath);

        if (configDto.getAdminChatId() != 0) {
            try {
                bot.execute(new SendDocument(configDto.getAdminChatId() + "", new InputFile(file)));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }



        clearProps();
    }

    private CopyMessage copyPostWithCustomData(PostingConfigurationDto boardDto, Long chatId) {
        CopyMessage copyMessage = new CopyMessage(
                chatId + "",
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

    private void editeJson(Long chatId, boolean send) {
        Gson gson = new Gson();
        try {

            FileReader reader = new FileReader(jsonFilePath);
            SendResultWrapper fullWrapper = gson.fromJson(reader, SendResultWrapper.class);
            fullWrapper.getResults().stream().filter(result -> Objects.equals(result.getChatId(), chatId)).forEach(result -> {
                result.setSend(send);
                result.setSendDateTimeMilliseconds(Instant.now(Clock.systemUTC()).getEpochSecond() * 1000);
            });
            if (send) fullWrapper.setSendCount(fullWrapper.getSendCount() + 1);
            else fullWrapper.setConflictCount(fullWrapper.getConflictCount() + 1);
            fullWrapper.setWaitCount(fullWrapper.getWaitCount() - 1);
            reader.close();

            FileWriter writer = new FileWriter(jsonFilePath);
            writer.write(gson.toJson(fullWrapper, SendResultWrapper.class));
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProps(String threadName, Map<Integer, Long> chatIdMap, TelegramLongPollingBot bot, PostingConfigurationDto boardDto) {
        this.threadName = threadName;
        this.chatIdMap = chatIdMap;
        this.bot = bot;
        this.configDto = boardDto;
    }

    public void clearProps() {
        bot = null;
        thread = null;
        threadName = null;
        configDto = null;
        chatIdMap = null;
    }

    public void start() {
        System.out.println("Thread started");
        thread = new Thread(this, this.threadName);
        thread.start();
    }
}

