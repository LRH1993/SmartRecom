package com.lvr.threerecom.ui.home.presenter.impl;

import com.lvr.threerecom.bean.InformationBean;
import com.lvr.threerecom.client.RxDisposeManager;
import com.lvr.threerecom.ui.home.model.impl.InformationModelImpl;
import com.lvr.threerecom.ui.home.presenter.InformationPresenter;
import com.lvr.threerecom.ui.home.view.InformationView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lvr on 2017/5/15.
 */

public class InformationPresenterImpl implements InformationPresenter{
    private InformationModelImpl mModel;
    private InformationView mView;

    public InformationPresenterImpl(InformationView view) {
        this.mView = view;
        mModel = new InformationModelImpl();
    }

    @Override
    public void requestInformation() {
        Observable<List<InformationBean>> observable = mModel.loadInformation();
        observable.subscribe(new Observer<List<InformationBean>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<InformationBean> been) {
                mView.returnInformation(been);
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
    public void requestUpdateInformation(String userid, String nickname, String age, String sex, String movie_preference, String music_preference) {
        Observable<Boolean> observable = mModel.updateInformation(userid, nickname, age, sex, movie_preference, music_preference);
        observable.subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                RxDisposeManager.get().add("updateinformation",d);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                mView.returnUpdateInformationResult(aBoolean);
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
    public void requestUpdatePhoto(String userid, String path) {
        Observable<Boolean> observable = mModel.updatePhoto(userid, path);
        observable.subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                RxDisposeManager.get().add("updatephoto",d);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                mView.returnUpdatePhotoResult(aBoolean);
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
