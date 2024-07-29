package org.joseloc.telegrambot.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class BotService extends TelegramLongPollingBot {

    @Setter
    private MessageProcessor messageProcessor;

    @Value("${verifyBot.botName}")
    private String botName;

    @Getter
    @Value("${verifyBot.botToken}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            SendMessage response;
            if (update.hasMessage() && update.getMessage().hasText()) {
                response = messageProcessor.processMessage(update);
            } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
                response = messageProcessor.processPhoto(update);
            } else if (update.hasMessage() && update.getMessage().hasAudio()) {
                response = messageProcessor.processAudio(update);
            } else if (update.hasMessage() && update.getMessage().hasVoice()) {
                response = messageProcessor.processVoice(update);
            } else if (update.hasMessage() && update.getMessage().hasVideo()) {
                response = messageProcessor.processVideo(update);
            } else if (update.hasCallbackQuery()) {
                response = messageProcessor.processCallback(update.getCallbackQuery());
            } else {
                String chatId = update.hasMessage() ? update.getMessage().getChatId().toString() : "unknown";
                response = new SendMessage(chatId, "Disculpa. No puedo procesar este mensaje.");
            }
            execute(response);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    public String getFilePath(String fileId) throws TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        return execute(getFile).getFilePath();
    }

}
