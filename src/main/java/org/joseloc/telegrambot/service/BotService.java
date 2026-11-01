package org.joseloc.telegrambot.service;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.joseloc.telegrambot.enums.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Service
public class BotService extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotService.class);

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

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        SendMessage response;

        MessageType messageType = MessageType.fromUpdate(update);
        LOGGER.info("Received messageType: {}", messageType);

        try {
            response = switch (messageType) {
                case TEXT -> messageProcessor.processMessage(update);
                case PHOTO -> messageProcessor.processPhoto(update);
                case AUDIO -> messageProcessor.processAudio(update);
                case VOICE -> messageProcessor.processVoice(update);
                case VIDEO -> messageProcessor.processVideo(update);
                case CALLBACK -> messageProcessor.processCallback(update.getCallbackQuery());
                case UNKNOWN -> {
                    String chatId = update.hasMessage() ? update.getMessage().getChatId().toString() : "unknown";
                    yield new SendMessage(chatId, "Disculpa. No puedo procesar este mensaje.");
                }
            };
            LOGGER.info(response.getText());
            execute(response);
        } catch (TelegramApiException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

}
