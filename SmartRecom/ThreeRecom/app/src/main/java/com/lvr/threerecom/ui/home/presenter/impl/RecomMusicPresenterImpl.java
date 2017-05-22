package com.lvr.threerecom.ui.home.presenter.impl;

import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.bean.SongListDetail;
import com.lvr.threerecom.client.RxDisposeManager;
import com.lvr.threerecom.ui.home.model.impl.RecomMusicModelImpl;
import com.lvr.threerecom.ui.home.presenter.RecomMusicPresenter;
import com.lvr.threerecom.ui.home.view.RecomMusicView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lvr on 2017/5/22.
 */

public class RecomMusicPresenterImpl implements RecomMusicPresenter {
    private RecomMusicView mView;
    private RecomMusicModelImpl mModel;

    public RecomMusicPresenterImpl(RecomMusicView view) {
        this.mView = view;
        mModel= new RecomMusicModelImpl();
    }

    @Override
    public void requestRecomMusic(String from, String version, String format, String method, int num) {
        Observable<List<SongListDetail.SongDetail>> observable = mModel.loadRecomMusic(from, version, format, method, num);
        observable.subscribe(new Observer<List<SongListDetail.SongDetail>>() {
            @Override
            public void onSubscribe(Disposable d) {
                RxDisposeManager.get().add("recommusic",d);
            }

            @Override
            public void onNext(List<SongListDetail.SongDetail> songDetails) {
                mView.returnRecomMusicList(songDetails);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void requestSongDetail(String from, String version, String format, String method, String songid) {
        Observable<SongDetailInfo> observable = mModel.loadSongDetail(from, version, format, method, songid);
        observable.subscribe(new Observer<SongDetailInfo>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(SongDetailInfo info) {
                mView.returnSongDetail(info);
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
