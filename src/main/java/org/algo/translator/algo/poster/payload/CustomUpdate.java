package org.algo.translator.algo.poster.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Yormamatov Davronbek
 * @since 20.02.2022
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomUpdate {

    private boolean textMessage;
    private boolean callBackQuery;

    private Long chatId;
    private String firstName;
    private String lastName;
    private String username;

    private int messageId;
    private String text;
    private String queryData;

    public static CustomUpdate makeCustomUpdate(Update update) {
        CustomUpdate customUpdate = null;
        try {
            if (update.hasCallbackQuery()) {
                customUpdate = new CustomUpdate();
                customUpdate.setTextMessage(false);
                customUpdate.setCallBackQuery(true);

                customUpdate.setChatId(update.getCallbackQuery().getMessage().getChatId());
                customUpdate.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

                customUpdate.setFirstName(update.getCallbackQuery().getMessage().getChat().getFirstName());
                customUpdate.setLastName(update.getCallbackQuery().getMessage().getChat().getLastName());
                customUpdate.setUsername(update.getCallbackQuery().getMessage().getChat().getUserName());

                customUpdate.setText(update.getCallbackQuery().getMessage().getText());
                customUpdate.setQueryData(update.getCallbackQuery().getData());

            } else if (update.hasMessage()) {
                if (update.getMessage().hasText()) {
                    customUpdate = new CustomUpdate();
                    customUpdate.setTextMessage(true);
                    customUpdate.setCallBackQuery(false);

                    customUpdate.setChatId(update.getMessage().getChatId());
                    customUpdate.setMessageId(update.getMessage().getMessageId());

                    customUpdate.setFirstName(update.getMessage().getChat().getFirstName());
                    customUpdate.setLastName(update.getMessage().getChat().getLastName());
                    customUpdate.setUsername(update.getMessage().getChat().getUserName());

                    customUpdate.setQueryData(null);
                    customUpdate.setText(update.getMessage().getText());
                }
            }
        } catch (Exception ignore) {
        }
        return customUpdate;
    }

}
