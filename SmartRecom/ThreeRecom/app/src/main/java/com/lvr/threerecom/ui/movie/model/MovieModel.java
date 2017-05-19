package com.lvr.threerecom.ui.movie.model;

import com.lvr.threerecom.bean.MovieInfo;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by lvr on 2017/4/22.
 */

public interface MovieModel {
    Observable<List<MovieInfo>> loadMovieByType(int pageSize, int startPage, String tyep, String genres);
    Observable<Boolean> loadRatingResult(String userId, int movie_id, int rating);
}
