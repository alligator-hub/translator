package org.algo.translator.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Statics {
    WELCOME("From which language:"),
    TRANSLATE_LANG("Which language:"),
    ENTER_TEXT("Enter the text to be translated:"),
    CMD_START("/start"),
    ;

    private final String value;
}
