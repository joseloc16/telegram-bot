package org.joseloc.telegrambot.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Service
public class BotInitializer {

    private final BotService botService;

    public BotInitializer(BotService botService) {
        this.botService = botService;
    }

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(botService);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
