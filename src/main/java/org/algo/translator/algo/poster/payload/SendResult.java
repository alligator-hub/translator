package org.algo.translator.algo.poster.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Yormamatov Davronbek
 * @since 20.02.2022
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendResult {
    private Integer id;

    private Long chatId;

    private Integer forwardPostId;

    private String forwardChannelUsername;

    private boolean notification;

    private boolean send;

    private Long sendDateTimeMilliseconds;

}
