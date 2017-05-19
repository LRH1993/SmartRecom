package com.lvr.threerecom.ui.login.model.impl;

import com.lvr.threerecom.api.ApiService;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.bean.LogInAndSignUpBean;
import com.lvr.threerecom.client.RetrofitClient;
import com.lvr.threerecom.ui.login.model.LogInModel;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by lvr on 2017/5/19.
 */

public class LogInModelImpl implements LogInModel {
    @Override
    public Observable<Boolean> loadLogIn(String username, String password) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MOVIE_BASE_URL_TYPE);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.getLogInResult(username,password).map(new Function<LogInAndSignUpBean, Boolean>() {
            @Override
            public Boolean apply(LogInAndSignUpBean bean) throws Exception {
                String info = bean.getLogin_state();
                if(info.equals("loginFailure")){
                    return false;
                }else{
                    return true;
                }
            }
        }).compose(RetrofitClient.schedulersTransformer);//线程调度;
    }
}
