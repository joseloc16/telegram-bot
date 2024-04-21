package org.joseloc.telegrambot.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BotService extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "josmig_bot";
    }

    @Override
    public String getBotToken() {
        return "token_generado_por_telegram";
    }

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        String messageReceived = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        System.out.println("Message Received: " + messageReceived);
        senMessage(generateSendMessage(chatId, messageReceived));
    }

    private SendMessage generateSendMessage(Long chatId, String messageReceived) {
        StringBuilder stringBuilder = listToStringBuilder(encontrarPalabras(messageReceived));
        return new SendMessage(chatId.toString(), "Las palabras encontradas fueron: " + "\n" + stringBuilder);
    }

    private List<String> encontrarPalabras(String mensaje) {
        List<String> palabrasEncontradas = new ArrayList<>();

        List<String> listadoPalabras = new ArrayList<>();
        listadoPalabras.add("cliente");
        listadoPalabras.add("detectado");
        listadoPalabras.add("actividad");
        listadoPalabras.add("cuenta bancaria");
        listadoPalabras.add("sospechosa");
        listadoPalabras.add("premio");
        listadoPalabras.add("ganado");
        listadoPalabras.add("reclamar");
        listadoPalabras.add("responda");
        listadoPalabras.add("nombre");
        listadoPalabras.add("direccion");

        String[] palabrasArray = mensaje.split("\\s+");
        List<String> strs = Arrays.asList(palabrasArray);

        for (String str : strs) {
            for (String listadoPalabra : listadoPalabras) {
                if (str.equals(listadoPalabra)) {
                    palabrasEncontradas.add(str);
                }
            }
        }
        return palabrasEncontradas;
    }

    private StringBuilder listToStringBuilder(List<String> listadoPalabras) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : listadoPalabras) {
            stringBuilder.append(str);
            stringBuilder.append("\n");
        }
        return stringBuilder;
    }

    private void senMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }
}
