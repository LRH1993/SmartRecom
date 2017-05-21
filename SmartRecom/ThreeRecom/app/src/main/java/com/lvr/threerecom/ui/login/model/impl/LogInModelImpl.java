package com.lvr.threerecom.ui.login.model.impl;

import com.lvr.threerecom.api.ApiService;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.bean.LoginBean;
import com.lvr.threerecom.client.RetrofitClient;
import com.lvr.threerecom.ui.login.model.LogInModel;
import com.lvr.threerecom.utils.SPUtils;

import de.greenrobot.event.EventBus;
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
        return api.getLogInResult(username,password).map(new Function<LoginBean, Boolean>() {
            @Override
            public Boolean apply(LoginBean bean) throws Exception {
                boolean state = bean.isState();
                if(!state){
                    return false;
                }else{
                    //存储记录登录状态
                    SPUtils.setSharedBooleanData(AppApplication.getAppContext(),"isLogin",true);
                    SPUtils.setSharedStringData(AppApplication.getAppContext(),"nickname",bean.getNickname());
                    SPUtils.setSharedStringData(AppApplication.getAppContext(),"userid",bean.getUserid()+"");
                    SPUtils.setSharedStringData(AppApplication.getAppContext(),"age",bean.getAge()+"");
                    if(bean.getSex().equals("M")){
                        SPUtils.setSharedStringData(AppApplication.getAppContext(),"gender","男");
                    }else{
                        SPUtils.setSharedStringData(AppApplication.getAppContext(),"gender","女");
                    }
                    String movie_preference = bean.getMovie_preference();
                    if(movie_preference==null){
                        SPUtils.setSharedStringData(AppApplication.getAppContext(),"movie_preference",null);
                    }else{
                        String[] movie = movie_preference.split(" ");
                        StringBuffer movie_like = new StringBuffer();
                        for(int i=0;i<movie.length;i++){
                            if(i==movie.length-1){
                                movie_like.append(movie[i]);
                            }else{
                                movie_like.append(movie[i]+"、");
                            }
                        }
                        SPUtils.setSharedStringData(AppApplication.getAppContext(),"movie_preference",movie_like.toString());
                    }
                    String music_preference = bean.getMusic_preference();
                    if(music_preference==null){
                        SPUtils.setSharedStringData(AppApplication.getAppContext(),"music_preference",null);
                    }else{
                        String[] music = music_preference.split(" ");
                        StringBuffer music_like = new StringBuffer();
                        for(int i=0;i<music.length;i++){
                            if(i==music.length-1){
                                music_like.append(music[i]);
                            }else{
                                music_like.append(music[i]+"、");
                            }
                        }
                        SPUtils.setSharedStringData(AppApplication.getAppContext(),"music_preference",music_like.toString());
                    }
                    SPUtils.setSharedStringData(AppApplication.getAppContext(),"photoUrl",bean.getUser_photo_url());
                    SPUtils.setSharedlongData(AppApplication.getAppContext(), "loginDate", System.currentTimeMillis());
                    EventBus.getDefault().post(bean);
                    return true;
                }
            }
        }).compose(RetrofitClient.schedulersTransformer);//线程调度;
    }
}
