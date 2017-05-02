package com.lvr.threerecom.ui.movie.presenter;

/**
 * Created by lvr on 2017/4/22.
 */

public interface MoviePresenter {
    void requestMovieByType(int pageSize,int startPage,String type,String genres);
}
