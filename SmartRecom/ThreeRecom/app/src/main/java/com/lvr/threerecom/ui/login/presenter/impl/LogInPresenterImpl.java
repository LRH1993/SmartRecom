package com.lvr.threerecom.ui.login.presenter.impl;

import com.lvr.threerecom.client.RxDisposeManager;
import com.lvr.threerecom.ui.login.model.impl.LogInModelImpl;
import com.lvr.threerecom.ui.login.presenter.LogInPresenter;
import com.lvr.threerecom.ui.login.view.LogInView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lvr on 2017/5/19.
 */

public class LogInPresenterImpl implements LogInPresenter {
    private LogInView mView;
    private LogInModelImpl mModel;

    public LogInPresenterImpl(LogInView view) {
        this.mView = view;
        mModel = new LogInModelImpl();
    }
    @Override
    public void requestLogIn(String username, String password) {
        mModel.loadLogIn(username,password).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                RxDisposeManager.get().add("logIn",d);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                mView.returnLogInResult(aBoolean);
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
