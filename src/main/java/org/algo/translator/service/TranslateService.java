package org.algo.translator.service;

import org.algo.translator.enums.QueryButtons;

/**
 * @author Yormamatov Davronbek
 * @since 28.02.2022
 */

public interface TranslateService {
    String getTranslate(String text, QueryButtons from, QueryButtons to);
}
