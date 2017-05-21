package com.lvr.threerecom.ui.login.model.impl;

import com.lvr.threerecom.api.ApiService;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.bean.SignupBean;
import com.lvr.threerecom.client.RetrofitClient;
import com.lvr.threerecom.ui.login.model.SignUpModel;
import com.lvr.threerecom.utils.SPUtils;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by lvr on 2017/5/19.
 */

public class SignUpModelImpl implements SignUpModel {
    @Override
    public Observable<Boolean> loadSignUp(final String username, String password) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MOVIE_BASE_URL_TYPE);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.getSignUpResult(username,password).map(new Function<SignupBean, Boolean>() {
            @Override
            public Boolean apply(SignupBean bean) throws Exception {
                String info = bean.getRegister_prompt_info();
                if (info.equals("嘻嘻，注册成功！")) {
                    SPUtils.setSharedBooleanData(AppApplication.getAppContext(),"isLogin",true);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(),"userid",username);
                    SPUtils.setSharedlongData(AppApplication.getAppContext(), "loginDate", System.currentTimeMillis());
                    SPUtils.setSharedStringData(AppApplication.getAppContext(),"nickname",null);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(), "age", null);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(),"gender",null);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(),"movie_preference",null);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(),"music_preference",null);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(),"photoUrl",null);
                    return true;
                } else {
                    return false;
                }
            }
        }).compose(RetrofitClient.schedulersTransformer);//线程调度;
    }
}
