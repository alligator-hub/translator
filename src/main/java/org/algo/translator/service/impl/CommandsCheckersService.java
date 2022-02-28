package org.algo.translator.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CommandsCheckersService {
    @Value("${bot.commands.start}")
    private String cmdStart;
    @Value("${bot.commands.statics}")
    private String cmdStatics;
    @Value("${bot.commands.language}")
    private String cmdLanguage;


    /*
     * todo checkers
     */
    public boolean isStart(String text) {
        return text.equals(cmdStart);
    }

    public boolean isStatics(String text) {
        return text.equals(cmdStatics);
    }

    public boolean isLanguage(String text) {
        return text.equals(cmdLanguage);
    }
}
