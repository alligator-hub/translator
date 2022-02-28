package org.algo.translator.service;

import org.algo.translator.entity.Follower;
import org.algo.translator.model.UpdateDto;

/**
 * @author Yormamatov Davronbek
 * @since 28.02.2022
 */

public interface FollowerService {
    Follower receiveAction(UpdateDto request);

    /**
     * add new follower method
     *
     * @param request custom update dto
     * @return follower which available in db
     */
    Follower addNewFollower(UpdateDto request);
}
