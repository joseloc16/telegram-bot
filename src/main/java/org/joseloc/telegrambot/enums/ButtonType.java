package org.joseloc.telegrambot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonType {
    REALIZAR_TEST ("Realizar test"),
    VERIFICAR_MENSAJE ("Verificar mensaje"),
    VERDADERO ("Verdaero"),
    FALSO ("Falso");

    private final String displayName;

}
