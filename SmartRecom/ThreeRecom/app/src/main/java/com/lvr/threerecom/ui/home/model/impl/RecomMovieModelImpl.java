package com.lvr.threerecom.ui.home.model.impl;

import com.lvr.threerecom.api.ApiService;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.bean.MovieInfo;
import com.lvr.threerecom.client.RetrofitClient;
import com.lvr.threerecom.ui.home.model.RecomMovieModel;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by lvr on 2017/5/22.
 */

public class RecomMovieModelImpl implements RecomMovieModel {
    @Override
    public Observable<List<MovieInfo>> loadMovieByType(int pageSize, int startPage, String tyep, String genres) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MOVIE_BASE_URL_TYPE);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.getMoiveByType(pageSize,startPage,tyep,genres).map(new Function<Map<String, List<MovieInfo>>, List<MovieInfo>>() {
            @Override
            public List<MovieInfo> apply(Map<String, List<MovieInfo>> map) throws Exception {
                return map.get("getMoviesByPage");
            }
        }).compose(RetrofitClient.schedulersTransformer);//线程调度
    }
}
