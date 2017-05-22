package com.lvr.threerecom.ui.home.presenter;

/**
 * Created by lvr on 2017/5/22.
 */

public interface RecomMoviePresenter {
    void requestMovieByType(int pageSize,int startPage,String type,String genres);
}
