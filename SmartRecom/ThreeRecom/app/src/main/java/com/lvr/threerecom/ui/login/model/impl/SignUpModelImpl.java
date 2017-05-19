package com.lvr.threerecom.ui.login.model.impl;

import com.lvr.threerecom.api.ApiService;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.bean.LogInAndSignUpBean;
import com.lvr.threerecom.client.RetrofitClient;
import com.lvr.threerecom.ui.login.model.SignUpModel;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by lvr on 2017/5/19.
 */

public class SignUpModelImpl implements SignUpModel {
    @Override
    public Observable<Boolean> loadSignUp(String username, String password) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MOVIE_BASE_URL_TYPE);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.getSignUpResult(username,password).map(new Function<LogInAndSignUpBean, Boolean>() {
            @Override
            public Boolean apply(LogInAndSignUpBean bean) throws Exception {
                String info = bean.getRegister_prompt_info();
                if(info.equals("嘻嘻，注册成功！")){
                    return true;
                }else{
                    return false;
                }
            }
        }).compose(RetrofitClient.schedulersTransformer);//线程调度;
    }
}
