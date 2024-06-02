package org.joseloc.telegrambot.enums;

public enum ButtonType {
    INICIO ("Inicio"),
    VERDADERO ("Verdaero"),
    FALSO ("Falso");

    private final String displayName;

    ButtonType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
