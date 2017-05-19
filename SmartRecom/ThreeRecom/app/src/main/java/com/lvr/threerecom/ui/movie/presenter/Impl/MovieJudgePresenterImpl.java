package com.lvr.threerecom.ui.movie.presenter.Impl;

import com.lvr.threerecom.client.RxDisposeManager;
import com.lvr.threerecom.ui.movie.model.MovieModelImpl;
import com.lvr.threerecom.ui.movie.presenter.MovieJudgePresenter;
import com.lvr.threerecom.ui.movie.view.MovieJudgeView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lvr on 2017/5/19.
 */

public class MovieJudgePresenterImpl implements MovieJudgePresenter {
    private MovieJudgeView mView;
    private MovieModelImpl mMovieModel;

    public MovieJudgePresenterImpl(MovieJudgeView view) {
        this.mView = view;
        mMovieModel = new MovieModelImpl();
    }

    @Override
    public void requestRatingResult(String userId, int movie_id, int rating) {
        mMovieModel.loadRatingResult(userId,movie_id,rating).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                RxDisposeManager.get().add("rating",d);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                mView.returnJudegeResult(aBoolean);
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
