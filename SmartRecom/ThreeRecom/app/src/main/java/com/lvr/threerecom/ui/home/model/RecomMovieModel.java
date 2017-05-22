package com.lvr.threerecom.ui.home.model;

import com.lvr.threerecom.bean.MovieInfo;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by lvr on 2017/5/22.
 */

public interface RecomMovieModel {
    Observable<List<MovieInfo>> loadMovieByType(int pageSize, int startPage, String tyep, String genres);
}
