package org.joseloc.telegrambot.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
@Service
public class BotService extends TelegramLongPollingBot {

    private final MessageProcessor messageProcessor;

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
    public String getBotToken() {
        return "7056650078:AAENgEaGmyenNMnYoyLOSf56bZsm9sXm3F4";
    }

    @Override
    public void onUpdateReceived(Update update) {
        sendMessage(messageProcessor.processMessage(update));
    }

    private void sendMessage(SendMessage response) {
        try {
            execute(response);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }
}
