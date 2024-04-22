package org.joseloc.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageProcessor {

    private final WordFinder wordFinder;

    public SendMessage processMessage(Update update) {
        String messageReceived = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        System.out.println("Message Received: " + messageReceived);
        List<String> foundWords = wordFinder.findWords(messageReceived);
        return generateSendMessage(chatId, foundWords);
    }

    private SendMessage generateSendMessage(Long chatId, List<String> foundWords) {
        StringBuilder stringBuilder = listToStringBuilder(foundWords);
        return new SendMessage(chatId.toString(), "Las palabras encontradas fueron: " + "\n" + stringBuilder);
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
