package org.joseloc.telegrambot.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageProcessor {

    private static final Logger logger = LogManager.getLogger(MessageProcessor.class);

    private final WordFinder wordFinder;

    public SendMessage processMessage(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageReceived = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if(messageReceived.startsWith("/")) {
                return handleCommand(messageReceived, chatId);
            } else {
                logger.info("Mensaje recibido: {}", messageReceived);
                List<String> foundWords = wordFinder.findWords(messageReceived);
                return generateSendMessage(chatId, foundWords);
            }
        } else {
            String chatId = update.hasMessage() ? update.getMessage().getChatId().toString() : "unknown";
            String errorMessage = "Update recibido sin mensaje o texto válido.";
            logger.error(errorMessage);
            return new SendMessage(chatId, "Disculpa. No puedo procesar este mensaje.");
        }
    }

    private SendMessage handleCommand(String messageReceived, String chatId) {
        String responseMessage = switch (messageReceived) {
            case "/start" -> "¡Bienvenido! ¿En qué puedo ayudarte hoy?";
            case "/info" -> "Soy VerifyBot, mi función principal es detectar mensajes fraudulentos ;)" +
                    "\nPuedo responder a comandos como: /start, /info, y más.";
            case "/help" -> "Lista de comandos:\n/start - Iniciar\n/info - Información\n/help - Ayuda";
            default -> "Lo siento, no reconozco ese comando. Usa /help para ver la lista de comandos disponibles.";
        };
        return new SendMessage(chatId, responseMessage);
    }

    private SendMessage generateSendMessage(String chatId, List<String> foundWords) {
        StringBuilder stringBuilder = listToStringBuilder(foundWords);
        return new SendMessage(chatId, "Las palabras encontradas fueron: " + "\n" + stringBuilder);
    }

    private StringBuilder listToStringBuilder(List<String> foundWords) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String word : foundWords) {
            stringBuilder.append(word);
            stringBuilder.append("\n");
        }
        return stringBuilder;
    }
}
