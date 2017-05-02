package com.lvr.threerecom.ui.movie.view;

import com.lvr.threerecom.bean.MovieInfo;

import java.util.List;

/**
 * Created by lvr on 2017/4/22.
 */

public interface MovieView {
    void returnMovieInfos(List<MovieInfo> movieInfoList);
}
