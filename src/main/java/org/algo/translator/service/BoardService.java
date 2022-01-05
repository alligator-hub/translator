package org.algo.translator.service;

import org.algo.translator.enums.LanguageType;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class BoardService {
    public InlineKeyboardMarkup languagesBoard(String from, boolean first) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        from = from.replaceAll(" - ", "");

        for (int i = 0; i < LanguageType.values().length-1; i += 2) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton btn1 = new InlineKeyboardButton();
            btn1.setText(LanguageType.values()[i].getFlag() + " " + LanguageType.values()[i].getLanguage());
            btn1.setCallbackData(from + " - " + LanguageType.values()[i].getCode());

            InlineKeyboardButton btn2 = new InlineKeyboardButton();
            btn2.setText(LanguageType.values()[i + 1].getFlag() + " " + LanguageType.values()[i + 1].getLanguage());
            btn2.setCallbackData(from + " - " + LanguageType.values()[i + 1].getCode());


            row.add(btn1);
            row.add(btn2);
            rowList.add(row);
        }

        if (!first) {
            InlineKeyboardButton btnBack = new InlineKeyboardButton();
            btnBack.setText("Back");
            btnBack.setCallbackData("back");
            List<InlineKeyboardButton> backRow = new ArrayList<>();
            backRow.add(btnBack);
            rowList.add(backRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup backBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        btn1.setText("Back");
        btn1.setCallbackData("back");


        row.add(btn1);
        rowList.add(row);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
