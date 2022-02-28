package org.algo.translator.service.impl;


import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.algo.translator.enums.QueryButtons;
import org.algo.translator.service.TranslateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TranslateServiceImpl implements TranslateService {
    @Value("${translate.key}")
    private String translateApiKey;

@Override
    public String getTranslate(String text, QueryButtons from, QueryButtons to) {

        System.setProperty("GOOGLE_API_KEY", translateApiKey);
        Translate translate = TranslateOptions.getDefaultInstance().getService();

        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage(from.getCode()),
                Translate.TranslateOption.targetLanguage(to.getCode())
        );
        return translation.getTranslatedText();
    }

}
