package com.lvr.threerecom.ui.music.presneter.impl;

import com.lvr.threerecom.bean.RankingListDetail;
import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.ui.music.model.MusicModel;
import com.lvr.threerecom.ui.music.model.impl.MusicModelImpl;
import com.lvr.threerecom.ui.music.presneter.MusicRankingListDetailPresenter;
import com.lvr.threerecom.ui.music.view.MusicRankingListDetailView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lvr on 2017/5/12.
 */

public class MusicRankingListDetailPresenterImpl implements MusicRankingListDetailPresenter {
    private MusicRankingListDetailView mView;
    private MusicModel mMusicModel;

    public MusicRankingListDetailPresenterImpl(MusicRankingListDetailView view) {
        mView = view;
        mMusicModel = new MusicModelImpl();
    }

    @Override
    public void requestRankListDetail(String format, String from, String method, int type, int offset, int size, String fields) {
        Observable<RankingListDetail> observable = mMusicModel.loadRankListDetail(format, from, method, type, offset, size, fields);
        observable.subscribe(new Observer<RankingListDetail>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(RankingListDetail detail) {
                mView.returnRankingListDetail(detail);
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
        Observable<SongDetailInfo> observable = mMusicModel.loadSongDetail(from, version, format, method, songid);
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
