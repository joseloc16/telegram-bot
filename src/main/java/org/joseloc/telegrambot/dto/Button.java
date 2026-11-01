package org.joseloc.telegrambot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Button {
    String label;
    String callback;
}
