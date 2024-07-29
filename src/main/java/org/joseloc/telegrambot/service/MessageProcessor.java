package org.joseloc.telegrambot.service;

import org.apache.commons.io.IOUtils;
import org.joseloc.telegrambot.dto.Button;
import org.joseloc.telegrambot.dto.MensajesTest;
import org.joseloc.telegrambot.util.CustomMultipartFile;
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

    private final WordFinder wordFinder;
    private final BotService botService;
    private final ProcessingService processingService;

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
        String messageReceived = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        if (messageReceived.startsWith("/")) {
            return handleCommand(messageReceived, chatId);
        } else {
            System.out.println("Message Received: " + messageReceived);
            List<String> foundWords = wordFinder.findWords(messageReceived);
            return generateSendMessage(chatId, foundWords);
        }
    }

    public SendMessage processAudio(Update update) throws TelegramApiException {
        String fileId = update.getMessage().getAudio().getFileId();
        return processMediaFile(update, fileId, "audio/mpeg");
    }

    public SendMessage processVoice(Update update) throws TelegramApiException {
        String fileId = update.getMessage().getVoice().getFileId();
        return processMediaFile(update, fileId, "audio/ogg");
    }

    public SendMessage processVideo(Update update) throws TelegramApiException {
        String fileId = update.getMessage().getVideo().getFileId();
        return processMediaFile(update, fileId, "video/mp4");
    }

    public SendMessage processPhoto(Update update) throws TelegramApiException {
        List<PhotoSize> photos = update.getMessage().getPhoto();
        String fileId = photos.get(photos.size() - 1).getFileId();
        return processMediaFile(update, fileId, "image/jpeg");
    }

    private SendMessage processMediaFile(Update update, String fileId, String contentType) throws TelegramApiException {
        String chatId = update.getMessage().getChatId().toString();
        File file = botService.execute(new GetFile(fileId));
        String fileUrl = URL_BASE_TELEGRAM_BOT + botToken + "/" + file.getFilePath();
        String transcriptionText  = "";

        try {
            MultipartFile multipartFile = downloadAndConvertToMultipartFile(fileUrl, contentType);

            if (contentType.equals("image/jpeg")) {
                transcriptionText  = processingService.processImage(multipartFile);
                return new SendMessage(chatId, transcriptionText );
            }

            transcriptionText  = processingService.processAudio(multipartFile);
            return new SendMessage(chatId, "Texto del audio: " + transcriptionText );
        } catch (Exception e) {
            e.printStackTrace();
            return new SendMessage(chatId, "Error al procesar el archivo");
        }
    }

    private MultipartFile downloadAndConvertToMultipartFile(String fileUrl, String contentType) throws Exception {
        URL url = new URL(fileUrl);
        InputStream inputStream = new BufferedInputStream(url.openStream());
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        MultipartFile multipartFile = new CustomMultipartFile(fileName, fileName, contentType, bytes);
        inputStream.close();
        return multipartFile;
    }

    private SendMessage handleCommand(String messageReceived, String chatId) {
        return switch (messageReceived) {
            case "/start" -> initMenu(chatId);
            case "/info" ->  new SendMessage(chatId,"Soy VerifyBot, mi función principal es detectar mensajes fraudulentos ;)" +
                    "\nPuedo responder a comandos como: /start, /info, y más.");
            case "/help" ->  new SendMessage(chatId,"Lista de comandos:\n/start - Iniciar\n/info - Información\n/help - Ayuda");
            default ->  new SendMessage(chatId,"Lo siento, no reconozco ese comando. Usa /help para ver la lista de comandos disponibles.");
        };
    }

    private SendMessage initMenu(String chatId) {
        String welcomeMessage = "¡Bienvenido! Soy VerifyBot, tu bot de confianza";
        List<Button> buttons = Arrays.asList(
                new Button("Realizar Test", REALIZAR_TEST.getDisplayName()),
                new Button("Verificar Mensaje", VERIFICAR_NOTICIA.getDisplayName()));
        return sendWithKeyboard(chatId, welcomeMessage, buttons);
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

    public SendMessage processCallback(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String data = callbackQuery.getData();
        SendMessage message;

        System.out.println(data);

        if("Realizar Test".equals(data)) {
            if(callbackQuery.getData().equals(REALIZAR_TEST.getDisplayName())) {
                contadorMensajes = 0;
                contadorAciertos = 0;
            } else {
                validarAciertos(callbackQuery);
                contadorMensajes++;
            }

            if(contadorMensajes >= 5) {
                message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Su porcentaje de aciertos fue: " + calcularPorcentaje() + "%");
            } else {
                List<String> mensajes = mensajesTests.stream()
                        .map(MensajesTest::getTexto)
                        .collect(Collectors.toList());
                message = sendWithKeyboard(chatId, mensajes.get(contadorMensajes), createButtons());
            }

            System.out.println("contador mensajes-test: " + contadorMensajes);
            System.out.println("contador aciertos: " + contadorAciertos);
            return message;
        } else {
            return new SendMessage(chatId, "Ingrese mensaje a verificar :D");
        }
    }

    private void validarAciertos(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        MensajesTest mensajeActual = mensajesTests.get(contadorMensajes);
        if (callbackData.equals(VERDADERO.getDisplayName()) && mensajeActual.esEstafa()
                || callbackData.equals(FALSO.getDisplayName()) && !mensajeActual.esEstafa()) {
            contadorAciertos++;
        }
    }

    private double calcularPorcentaje() {
        return ((double) contadorAciertos / 5) * 100;
    }

    private List<Button> createButtons() {
        return Arrays.asList(
                new Button("Verdadero", VERDADERO.getDisplayName()),
                new Button("Falso", FALSO.getDisplayName()));
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
