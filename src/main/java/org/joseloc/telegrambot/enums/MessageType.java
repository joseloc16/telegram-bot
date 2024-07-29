package org.joseloc.telegrambot.enums;

import org.telegram.telegrambots.meta.api.objects.Update;

public enum MessageType {
    TEXT, PHOTO, AUDIO, VOICE, VIDEO, CALLBACK, UNKNOWN;

    public static MessageType fromUpdate(Update update) {
        if (update.getMessage().hasText()) {
            return MessageType.TEXT;
        } else if (update.getMessage().hasPhoto()) {
            return MessageType.PHOTO;
        } else if (update.getMessage().hasAudio()) {
            return MessageType.AUDIO;
        } else if (update.getMessage().hasVoice()) {
            return MessageType.VOICE;
        } else if (update.getMessage().hasVideo()) {
            return MessageType.VIDEO;
        } else if (update.hasCallbackQuery()) {
            return MessageType.CALLBACK;
        } else {
            return MessageType.UNKNOWN;
        }
    }
}
