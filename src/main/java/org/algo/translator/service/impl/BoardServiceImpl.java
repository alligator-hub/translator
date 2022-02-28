package org.algo.translator.service.impl;

import org.algo.translator.enums.QueryButtons;
import org.algo.translator.service.BoardService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {
    @Override
    public InlineKeyboardMarkup languagesBoard(String from, boolean first) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        from = from.replaceAll(" - ", "");

        for (int i = 0; i < QueryButtons.values().length; i += 2) {
            if (QueryButtons.values()[i] == QueryButtons.BACK || QueryButtons.values()[i] == QueryButtons.AUTO)
                continue;
            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton btn1 = new InlineKeyboardButton();
            btn1.setText(QueryButtons.values()[i].getSticker() + " " + QueryButtons.values()[i].getText());
            btn1.setCallbackData(from + " - " + QueryButtons.values()[i].getCode());

            InlineKeyboardButton btn2 = new InlineKeyboardButton();
            btn2.setText(QueryButtons.values()[i + 1].getSticker() + " " + QueryButtons.values()[i + 1].getText());
            btn2.setCallbackData(from + " - " + QueryButtons.values()[i + 1].getCode());


            row.add(btn1);
            row.add(btn2);
            rowList.add(row);
        }

        if (!first) {
            InlineKeyboardButton btnBack = new InlineKeyboardButton();
            btnBack.setText(QueryButtons.BACK.getText());
            btnBack.setCallbackData(QueryButtons.BACK.getCode());
            rowList.add(List.of(btnBack));
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    @Override
    public InlineKeyboardMarkup backBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        btn1.setText(QueryButtons.BACK.getText());
        btn1.setCallbackData(QueryButtons.BACK.getCode());


        row.add(btn1);
        rowList.add(row);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}
