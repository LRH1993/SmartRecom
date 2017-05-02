package com.lvr.threerecom.ui.home.model.impl;

import com.lvr.threerecom.api.ApiService;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.bean.MovieInfo;
import com.lvr.threerecom.client.RetrofitClient;
import com.lvr.threerecom.ui.home.model.MainModel;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by lvr on 2017/4/24.
 */

public class MainModelImpl implements MainModel {
    @Override
    public Observable<List<MovieInfo>> loadHotMovie() {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MOVIE_BASE_URL_HOT);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.getHotMoive().map(new Function<Map<String, List<MovieInfo>>, List<MovieInfo>>() {
            @Override
            public List<MovieInfo> apply(Map<String, List<MovieInfo>> map) throws Exception {
                List<MovieInfo> movies = map.get("getHotMovies");
                return movies;
            }
        }).compose(RetrofitClient.schedulersTransformer);
    }
}
