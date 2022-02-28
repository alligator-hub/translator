package org.algo.translator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.algo.translator.entity.Follower;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDto {
    private Long chatId;

    private boolean isTextMessage;
    private boolean isCallBackQuery;
    private String firstName;
    private String lastName;
    private String username;
    private int messageId;

    private Follower follower;

    private String text;
    private String queryData;
}
