package org.algo.translator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.algo.translator.entity.Follower;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private String text;
    private Long chatId;
    private String data;
    private Boolean isText;
    private Boolean isCallBackQuery;
    private String firstName;
    private String lastName;
    private String username;
    private Integer messageId;

    private Follower follower;
}
