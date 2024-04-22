package org.joseloc.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
@Service
public class BotService extends TelegramLongPollingBot {

    private final MessageProcessor messageProcessor;

    @Override
    public String getBotUsername() {
        return "josmig_bot";
    }

    @Override
    public String getBotToken() {
        return "7056650078:AAENgEaGmyenNMnYoyLOSf56bZsm9sXm3F4";
    }

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage response = messageProcessor.processMessage(update);
        sendResponse(response);
    }

    private void sendResponse(SendMessage response) {
        try {
            execute(response);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }
}
