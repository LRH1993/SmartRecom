package com.lvr.threerecom.ui.movie.presenter.Impl;

import android.app.Activity;

import com.lvr.threerecom.bean.MovieInfo;
import com.lvr.threerecom.client.RxDisposeManager;
import com.lvr.threerecom.ui.movie.MovieFragment;
import com.lvr.threerecom.ui.movie.model.MovieModel;
import com.lvr.threerecom.ui.movie.model.MovieModelImpl;
import com.lvr.threerecom.ui.movie.presenter.MoviePresenter;
import com.lvr.threerecom.ui.movie.view.MovieView;
import com.lvr.threerecom.widget.LoadingDialog;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lvr on 2017/4/22.
 */

public class MoviePresenterImpl implements MoviePresenter {
    private MovieView mMovieView;
    private MovieModel mMovieModel;
    private Activity mContext;

    public MoviePresenterImpl(MovieFragment view, Activity context) {
        this.mMovieView = view;
        this.mMovieModel = new MovieModelImpl();
        this.mContext = context;
    }

    @Override
    public void requestMovieByType(int pageSize, int startPage, String type, String genres) {
        Observable<List<MovieInfo>> observable = mMovieModel.loadMovieByType(pageSize, startPage, type, genres);
        observable.subscribe(new Observer<List<MovieInfo>>() {
            @Override
            public void onSubscribe(Disposable d) {
                RxDisposeManager.get().add("MovieByType", d);

            }

            @Override
            public void onNext(List<MovieInfo> infos) {
                mMovieView.returnMovieInfos(infos);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("接收到错误信号");
                e.printStackTrace();
                LoadingDialog.cancelDialogForLoading();
            }

            @Override
            public void onComplete() {
                System.out.println("接收数据完成");
            }
        });

    }
}
