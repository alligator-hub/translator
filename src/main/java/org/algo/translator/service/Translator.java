package org.algo.translator.service;


import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.algo.translator.enums.LanguageType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Translator {
    @Value("${translate.key}")
    private String translateApiKey;


    public String getTranslate(String text, LanguageType from, LanguageType to) {

        try {
            System.setProperty("GOOGLE_API_KEY", "AIzaSyBvopR1NnpGOiZVl6UaeovNOMyRYpvLdMA");
            Translate translate = TranslateOptions.getDefaultInstance().getService();

            Translation translation = translate.translate(
                    text,
                    Translate.TranslateOption.sourceLanguage(from.getCode()),
                    Translate.TranslateOption.targetLanguage(to.getCode())
            );
            return translation.getTranslatedText();
        } catch (Exception ignore) {

        }
        return null;
    }

}
