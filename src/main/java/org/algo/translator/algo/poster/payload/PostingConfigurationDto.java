package org.algo.translator.algo.poster.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * @author Yormamatov Davronbek
 * @since 30.01.2022
 */


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostingConfigurationDto {

    private boolean sendByDesc;
    private boolean notification;
    private long sendFollowerCount;
    private long allFollowerCount;
    private String channelUsername;
    private int postId;
    private InlineKeyboardMarkup inlineKeyboardMarkup;
    private String inlineKeyboardText;
    private String htmText;
    private long adminChatId;


}
