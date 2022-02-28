package org.algo.translator.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * @author Yormamatov Davronbek
 * @since 28.02.2022
 */

public interface BoardService {
    InlineKeyboardMarkup languagesBoard(String from, boolean first);
    InlineKeyboardMarkup backBoard();
}
