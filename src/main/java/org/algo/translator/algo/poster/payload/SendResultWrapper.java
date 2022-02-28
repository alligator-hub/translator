package org.algo.translator.algo.poster.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yormamatov Davronbek
 * @since 20.02.2022
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendResultWrapper {
    private List<SendResult> results = new ArrayList<>();
    private Integer sendCount;
    private Integer waitCount;
    private Integer conflictCount;
    private Integer size;

}
