package org.algo.translator.algo.poster.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Yormamatov Davronbek
 * @since 19.02.2022
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostButtonDto {
    private String url;
    private String text;
    private int row;
    private int position;
}
