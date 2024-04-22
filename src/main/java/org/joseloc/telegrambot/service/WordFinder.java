package org.joseloc.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.joseloc.telegrambot.entity.WordDetect;
import org.joseloc.telegrambot.repository.IBotRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WordFinder {

    private final IBotRepository botRepository;

    public List<String> findWords(String message) {
        List<String> fakeWords = getFakeWords();
        List<String> foundWords = new ArrayList<>();
        String[] wordsArray = message.split("\\s+");
        List<String> wordsList = Arrays.asList(wordsArray);
        for (String word : wordsList) {
            if (fakeWords.contains(word)) {
                foundWords.add(word);
            }
        }
        return foundWords;
    }

    private List<String> getFakeWords() {
        return  botRepository.findAll().stream()
                .map(WordDetect::getWord)
                .collect(Collectors.toList());
    }

}