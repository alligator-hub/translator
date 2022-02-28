package org.algo.translator.service;

import org.algo.translator.model.UpdateDto;

/**
 * @author Yormamatov Davronbek
 * @since 28.02.2022
 */

public interface CallBackService {
    void map(UpdateDto request);
}
