package com.lvr.threerecom.ui.home.model.impl;

import com.lvr.threerecom.api.ApiService;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.bean.InformationBean;
import com.lvr.threerecom.bean.LoginBean;
import com.lvr.threerecom.client.RetrofitClient;
import com.lvr.threerecom.ui.home.model.InformationModel;
import com.lvr.threerecom.utils.SPUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Created by lvr on 2017/5/15.
 */

public class InformationModelImpl implements InformationModel {
    @Override
    public Observable<List<InformationBean>> loadInformation() {
        Observable<List<InformationBean>> observable = Observable.create(new ObservableOnSubscribe<List<InformationBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<InformationBean>> e) throws Exception {
                List<InformationBean> list = initData();
                e.onNext(list);
            }
        }).compose(RetrofitClient.schedulersTransformer);
        return observable;
    }

    private List<InformationBean> initData() {
        List<InformationBean> list = new ArrayList<>();
        InformationBean touxiang = new InformationBean();
        touxiang.setTitle("头像");
        //默认头像 其他是网络url
        String url = SPUtils.getSharedStringData(AppApplication.getAppContext(), "photoUrl");
        if (url != null && !url.isEmpty()) {
            touxiang.setContent(url);
            touxiang.setSet(true);
        } else {
            touxiang.setContent("default");
            touxiang.setSet(false);
        }
        list.add(touxiang);
        InformationBean yonghumiing = new InformationBean();
        yonghumiing.setTitle("用户名");
        String nickname = SPUtils.getSharedStringData(AppApplication.getAppContext(), "nickname");
        if (nickname != null && !nickname.isEmpty()) {
            yonghumiing.setContent(nickname);
            yonghumiing.setSet(true);
        } else {
            yonghumiing.setContent("未设置");
            yonghumiing.setSet(false);
        }
        list.add(yonghumiing);
        InformationBean age = new InformationBean();
        age.setTitle("年龄");
        String age1 = SPUtils.getSharedStringData(AppApplication.getAppContext(), "age");
        if (age1 != null && !age1.isEmpty()) {
            age.setContent(age1);
            age.setSet(true);
        } else {
            age.setContent("未设置");
            age.setSet(false);
        }
        list.add(age);
        InformationBean gender = new InformationBean();
        gender.setTitle("性别");
        String gender1 = SPUtils.getSharedStringData(AppApplication.getAppContext(), "gender");
        if (gender1 != null && !gender1.isEmpty()) {
            gender.setContent(gender1);
            gender.setSet(true);
        } else {
            gender.setContent("未设置");
            gender.setSet(false);
        }
        list.add(gender);
        InformationBean movie = new InformationBean();
        movie.setTitle("喜欢的电影");
        String movie_preference = SPUtils.getSharedStringData(AppApplication.getAppContext(), "movie_preference");
        if (movie_preference != null && !movie_preference.isEmpty()) {
            movie.setContent(movie_preference);
            movie.setSet(true);
        } else {
            movie.setContent("未设置");
            movie.setSet(false);
        }
        list.add(movie);
        InformationBean music = new InformationBean();
        music.setTitle("喜欢的音乐");
        String music_preference = SPUtils.getSharedStringData(AppApplication.getAppContext(), "music_preference");
        if (music_preference != null && !music_preference.isEmpty()) {
            music.setContent(music_preference);
            music.setSet(true);
        } else {
            music.setContent("未设置");
            music.setSet(false);
        }
        list.add(music);
        return list;
    }

    @Override
    public Observable<Boolean> updateInformation(String userid, String nickname, String age, String sex, String movie_preference, String music_preference) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MOVIE_BASE_URL_HOT);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.uploadUserInformation(userid, nickname, age, sex, movie_preference, music_preference).map(new Function<LoginBean, Boolean>() {
            @Override
            public Boolean apply(LoginBean bean) throws Exception {
                boolean state = bean.isState();
                if (state) {
                    return true;
                } else {
                    return false;
                }

            }
        }).compose(RetrofitClient.schedulersTransformer);
    }

    @Override
    public Observable<Boolean> updatePhoto(String userid,String path) {
        File file = new File(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData(userid, file.getName(), requestBody);
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.PHOTO_BASE_URL);
        ApiService api = retrofitClient.create(ApiService.class);

        return api.uploadImageFile(part).map(new Function<Response<Object>, Boolean>() {
            @Override
            public Boolean apply(Response<Object> response) throws Exception {
                int code = response.code();
                System.out.println("获得返回状态码："+code);
                if(code==200){
                    return true;
                }else{
                    return false;
                }
            }
        }).compose(RetrofitClient.schedulersTransformer);
    }
}
