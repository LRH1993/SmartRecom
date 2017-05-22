package com.lvr.threerecom.ui.home.presenter.impl;

import com.lvr.threerecom.bean.MovieInfo;
import com.lvr.threerecom.client.RxDisposeManager;
import com.lvr.threerecom.ui.home.model.impl.RecomMovieModelImpl;
import com.lvr.threerecom.ui.home.presenter.RecomMoviePresenter;
import com.lvr.threerecom.ui.home.view.RecomMovieView;
import com.lvr.threerecom.widget.LoadingDialog;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lvr on 2017/5/22.
 */

public class RecomMoviePresenterImpl implements RecomMoviePresenter {
    private RecomMovieView mView;
    private RecomMovieModelImpl mModel;


    public RecomMoviePresenterImpl(RecomMovieView view) {
        this.mView = view;
        this.mModel = new RecomMovieModelImpl();

    }

    @Override
    public void requestMovieByType(int pageSize, int startPage, String type, String genres) {
        Observable<List<MovieInfo>> observable = mModel.loadMovieByType(pageSize, startPage, type, genres);
        observable.subscribe(new Observer<List<MovieInfo>>() {
            @Override
            public void onSubscribe(Disposable d) {
                RxDisposeManager.get().add("recommovie", d);

            }

            @Override
            public void onNext(List<MovieInfo> infos) {
                mView.returnMovieList(infos);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }
}
