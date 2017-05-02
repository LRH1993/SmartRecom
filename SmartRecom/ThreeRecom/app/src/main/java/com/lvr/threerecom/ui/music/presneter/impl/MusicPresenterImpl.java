package com.lvr.threerecom.ui.music.presneter.impl;

import android.content.Context;

import com.lvr.threerecom.bean.WrapperSongListInfo;
import com.lvr.threerecom.ui.music.model.MusicModel;
import com.lvr.threerecom.ui.music.model.impl.MusicModelImpl;
import com.lvr.threerecom.ui.music.presneter.MusicPresenter;
import com.lvr.threerecom.ui.music.view.MusicSongListView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lvr on 2017/4/26.
 */

public class MusicPresenterImpl implements MusicPresenter {
    private MusicSongListView mView;
    private MusicModel mMusicModel;
    private Context mContext;

    public MusicPresenterImpl(MusicSongListView view, Context context) {
        this.mView = view;
        this.mMusicModel = new MusicModelImpl();
        this.mContext = context;
    }
    @Override
    public void requestSongListAll(String format,String from,String method, int page_size, int page_no) {
        Observable<List<WrapperSongListInfo.SongListInfo>> observable = mMusicModel.loadSongListAll(format,from, method, page_size, page_no);
        observable.subscribe(new Observer<List<WrapperSongListInfo.SongListInfo>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<WrapperSongListInfo.SongListInfo> infos) {
               mView.returnSongListInfos(infos);
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
