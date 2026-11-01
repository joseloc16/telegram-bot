package org.joseloc.telegrambot.service;

import jakarta.websocket.SendHandler;
import org.apache.commons.io.IOUtils;
import org.joseloc.telegrambot.dto.Button;
import org.joseloc.telegrambot.dto.MensajesTest;
import org.joseloc.telegrambot.util.CustomMultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.joseloc.telegrambot.enums.ButtonType.*;

@Service
public class MessageProcessor {

    private String className = SendHandler.class.getSimpleName();
    String method = "";

    private final WordFinder wordFinder;
    private final BotService botService;
    private final ProcessingService processingService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);
    private static final String URL_BASE_TELEGRAM_BOT = "https://api.telegram.org/file/bot";

    @Value("${verifyBot.botToken}")
    private String botToken;

    @Value("${contador.mensajes-test}")
    private int contadorMensajes;

    @Value("${contador.aciertos}")
    private int contadorAciertos;

    public MessageProcessor(BotService botService, ProcessingService processingService, WordFinder wordFinder) {
        this.botService = botService;
        this.processingService = processingService;
        this.wordFinder = wordFinder;
        this.botService.setMessageProcessor(this); //asegura que BotService tenga acceso a los métodos del MessageProcessor
    }

    MensajesTest mensajesTest_1 = new MensajesTest("Noticia 1", true);
    MensajesTest mensajesTest_2 = new MensajesTest("Noticia 2", true);
    MensajesTest mensajesTest_3 = new MensajesTest("Noticia 3", true);
    MensajesTest mensajesTest_4 = new MensajesTest("Noticia 4", false);
    MensajesTest mensajesTest_5 = new MensajesTest("Noticia 5", false);

    List<MensajesTest> mensajesTests = Arrays.asList(mensajesTest_1, mensajesTest_2, mensajesTest_3, mensajesTest_4, mensajesTest_5);

    public SendMessage processMessage(Update update) {
        method = "processMessage";
        LOGGER.info("{}: {}: {}", className, method, "Mensaje: " + update.getMessage().getText());
        LOGGER.info("{}: {}: {}", className, method, "ChatId: " + update.getMessage().getChatId());

        String messageReceived = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        if (messageReceived.startsWith("/")) {
            return handleCommand(messageReceived, chatId);
        } else {
            LOGGER.info("{}: {}", "Message Received", messageReceived);
            List<String> foundWords = wordFinder.findWords(messageReceived);
            return generateSendMessage(chatId, foundWords);
        }
    }
    private SendMessage handleCommand(String messageReceived, String chatId) {
        String method = "handleCommand";
        LOGGER.info("{}: {}: Received command: {}", className, method, messageReceived);

        return switch (messageReceived) {
            case "/start" -> {
                LOGGER.info("{}: {}: Handling /start command", className, method);
                yield initMenu(chatId);
            }
            case "/info" -> {
                LOGGER.info("{}: {}: Handling /info command", className, method);
                yield new SendMessage(chatId, "Soy VerifyBot, mi función principal es detectar mensajes fraudulentos ;)" +
                        "\nPuedo responder a comandos como: /start, /info, y más.");
            }
            case "/help" -> {
                LOGGER.info("{}: {}: Handling /help command", className, method);
                yield new SendMessage(chatId, "Lista de comandos:\n/start - Iniciar\n/info - Información\n/help - Ayuda");
            }
            default -> {
                LOGGER.warn("{}: {}: Unknown command received: {}", className, method, messageReceived);
                yield new SendMessage(chatId, "Lo siento, no reconozco ese comando. Usa /help para ver la lista de comandos disponibles.");
            }
        };
    }

    private SendMessage initMenu(String chatId) {
        method = "initMenu";
        LOGGER.info("{}: {}: Initializing menu for chat ID: {}", className, method, chatId);

        String welcomeMessage = "¡Bienvenido! Soy VerifyBot, tu bot de confianza";
        List<Button> buttons = Arrays.asList(
                new Button("Realizar Test", REALIZAR_TEST.getDisplayName()),
                new Button("Verificar Mensaje", VERIFICAR_NOTICIA.getDisplayName()));

        LOGGER.info("{}: {}: Menu initialized with buttons: {}", className, method, buttons);
        return sendWithKeyboard(chatId, welcomeMessage, buttons);
    }

    private SendMessage generateSendMessage(String chatId, List<String> foundWords) {
        String method = "generateSendMessage";
        LOGGER.info("{}: {}: Generating message for chat ID: {}", className, method, chatId);
        LOGGER.info("{}: {}: Found words: {}", className, method, foundWords);

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
