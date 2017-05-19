package com.lvr.threerecom.ui.login.presenter.impl;

import com.lvr.threerecom.client.RxDisposeManager;
import com.lvr.threerecom.ui.login.model.impl.SignUpModelImpl;
import com.lvr.threerecom.ui.login.presenter.SignUpPresenter;
import com.lvr.threerecom.ui.login.view.SignUpView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lvr on 2017/5/19.
 */

public class SignUpPresenterImpl implements SignUpPresenter {
    private SignUpView mView;
    private SignUpModelImpl mModel;

    public SignUpPresenterImpl(SignUpView view) {
        this.mView = view;
        mModel = new SignUpModelImpl();
    }

    @Override
    public void requestSignUp(String username, String password) {
        mModel.loadSignUp(username,password).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                RxDisposeManager.get().add("signUp",d);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                mView.returnSignUpResult(aBoolean);
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
