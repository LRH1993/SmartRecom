package com.lvr.threerecom.ui.home.view;

import com.lvr.threerecom.bean.MovieInfo;

import java.util.List;

/**
 * Created by lvr on 2017/5/22.
 */

public interface RecomMovieView {
    void returnMovieList(List<MovieInfo> movieInfoList);
}
