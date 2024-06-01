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
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            SendMessage response;
            if (update.hasMessage() && update.getMessage().hasText()) {
                response = messageProcessor.processMessage(update);
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
}
