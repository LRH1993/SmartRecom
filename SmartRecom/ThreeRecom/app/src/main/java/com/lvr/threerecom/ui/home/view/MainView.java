package com.lvr.threerecom.ui.home.view;

import com.lvr.threerecom.bean.MovieInfo;

import java.util.List;

/**
 * Created by lvr on 2017/4/24.
 */

public interface MainView {
    void returnMovieInfos(List<MovieInfo> movieInfoList);
}
