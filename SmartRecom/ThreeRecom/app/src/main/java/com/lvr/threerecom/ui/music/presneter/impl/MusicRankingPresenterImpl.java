package com.lvr.threerecom.ui.music.presneter.impl;

import android.content.Context;

import com.lvr.threerecom.bean.RankingListItem;
import com.lvr.threerecom.ui.music.model.MusicModel;
import com.lvr.threerecom.ui.music.model.impl.MusicModelImpl;
import com.lvr.threerecom.ui.music.presneter.MusicRankingPresenter;
import com.lvr.threerecom.ui.music.view.MusicRankingListView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lvr on 2017/4/27.
 */

public class MusicRankingPresenterImpl implements MusicRankingPresenter {
    private MusicRankingListView mView;
    private MusicModel mMusicModel;
    private Context mContext;

    public MusicRankingPresenterImpl(MusicRankingListView view, Context context) {
        this.mView = view;
        this.mMusicModel = new MusicModelImpl();
        this.mContext = context;
    }
    @Override
    public void requestRankingListAll(String format, String from, String method, int kflag) {
        Observable<List<RankingListItem.RangkingDetail>> observable = mMusicModel.loadRankingListAll(format, from, method, kflag);
        observable.subscribe(new Observer<List<RankingListItem.RangkingDetail>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<RankingListItem.RangkingDetail> details) {
                mView.returnRankingListInfos(details);
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
