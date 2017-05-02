package com.lvr.threerecom.ui.home.model;

import com.lvr.threerecom.bean.MovieInfo;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by lvr on 2017/4/24.
 */

public interface MainModel {
    Observable<List<MovieInfo>> loadHotMovie();
}
