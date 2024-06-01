package org.joseloc.telegrambot.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MensajesTest {
    private String texto;
    private boolean esEstafa;

    public String getTexto() {
        return texto;
    }

    public boolean esEstafa() {
        return esEstafa;
    }
}
