package org.joseloc.telegrambot.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lombok.RequiredArgsConstructor;
import org.joseloc.telegrambot.dto.Button;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageProcessor {

    private static final Logger logger = LogManager.getLogger(MessageProcessor.class);

    //private final WordFinder wordFinder;

    public SendMessage processMessage(Update update) {
        String messageReceived = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        if (messageReceived.startsWith("/")) {
            return handleCommand(messageReceived, chatId);
        } else {
            List<Button> buttons = Arrays.asList(
                    new Button("Button 1", "callback_1"),
                    new Button("Button 2", "callback_2"));
            return sendWithKeyboard(chatId, messageReceived, buttons);
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

    //private SendMessage generateSendMessage(String chatId, List<String> foundWords) {
    //    StringBuilder stringBuilder = listToStringBuilder(foundWords);
    //    return new SendMessage(chatId, "Las palabras encontradas fueron: " + "\n" + stringBuilder);
    //}

    //private StringBuilder listToStringBuilder(List<String> foundWords) {
    //    StringBuilder stringBuilder = new StringBuilder();
    //    for (String word : foundWords) {
    //        stringBuilder.append(word);
    //        stringBuilder.append("\n");
    //    }
    //    return stringBuilder;
    //}

    public SendMessage processCallback(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        String chatId = callbackQuery.getMessage().getChatId().toString();
        return new SendMessage(chatId, "Callback recibido: " + callbackData);
    }

    public SendMessage sendWithKeyboard(String chatId, String text, List<Button> buttons) {
        SendMessage send = new SendMessage();
        send.setChatId(chatId);
        send.setText(text);
        send.setReplyMarkup(inlineKeyboard(buttons));
        return send;
    }

    public InlineKeyboardMarkup inlineKeyboard(List<Button> buttons) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> totalList = new ArrayList<>();
        for (Button button : buttons) {
            List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();

            InlineKeyboardButton inlineButton = new InlineKeyboardButton(button.getLabel());
            inlineButton.setCallbackData(button.getCallback());
            keyboardButtonRow.add(inlineButton);
            totalList.add(keyboardButtonRow);
        }
        inlineKeyboardMarkup.setKeyboard(totalList);
        return inlineKeyboardMarkup;
    }
}
