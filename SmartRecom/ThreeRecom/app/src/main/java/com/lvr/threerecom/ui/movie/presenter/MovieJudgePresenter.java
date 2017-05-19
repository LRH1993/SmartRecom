package com.lvr.threerecom.ui.movie.presenter;

/**
 * Created by lvr on 2017/5/19.
 */

public interface MovieJudgePresenter {
    void requestRatingResult(String userId, int movie_id, int rating);
}
