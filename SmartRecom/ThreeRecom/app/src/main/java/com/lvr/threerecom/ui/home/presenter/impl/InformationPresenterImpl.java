package com.lvr.threerecom.ui.home.presenter.impl;

import com.lvr.threerecom.bean.InformationBean;
import com.lvr.threerecom.ui.home.model.impl.InformationModelImpl;
import com.lvr.threerecom.ui.home.presenter.InformationPresenter;
import com.lvr.threerecom.ui.home.view.InformationView;

import java.util.ArrayList;
import java.util.List;

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
        //暂时先自己设置  不请求网络数据
        initData();

    }

    private void initData() {
        List<InformationBean> list = new ArrayList<>();
        InformationBean touxiang = new InformationBean();
        touxiang.setTitle("头像");
        //默认头像 其他是网络url
        touxiang.setContent("default");
        touxiang.setSet(false);
        list.add(touxiang);
        InformationBean yonghumiing = new InformationBean();
        yonghumiing.setTitle("用户名");
        yonghumiing.setContent("未设置");
        yonghumiing.setSet(false);
        list.add(yonghumiing);
        InformationBean age = new InformationBean();
        age.setTitle("年龄");
        age.setContent("未设置");
        age.setSet(false);
        list.add(age);
        InformationBean gender = new InformationBean();
        gender.setTitle("性别");
        gender.setContent("未设置");
        gender.setSet(false);
        list.add(gender);
        InformationBean movie = new InformationBean();
        movie.setTitle("喜欢的电影");
        movie.setContent("未设置");
        movie.setSet(false);
        list.add(movie);
        InformationBean music = new InformationBean();
        music.setTitle("喜欢的音乐");
        music.setContent("未设置");
        movie.setSet(false);
        list.add(music);
        mView.returnInformation(list);
    }
}
