package com.lvr.threerecom.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.FavorMovieAdapter;
import com.lvr.threerecom.base.BaseActivityWithoutStatus;
import com.lvr.threerecom.bean.FavorListBean;
import com.lvr.threerecom.utils.StatusBarSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;

/**
 * Created by lvr on 2017/5/16.
 */

public class FavorMusicActivity extends BaseActivityWithoutStatus implements View.OnClickListener {


    @BindView(R.id.iv_wrong)
    ImageView mIvWrong;
    @BindView(R.id.iv_right)
    ImageView mIvRight;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_favor)
    RecyclerView mRvFavor;
    private String[] mTypes = {"流行","摇滚","民谣","华语","韩语","欧美","电子","舞曲","浪漫","兴奋","感动","孤独","放松","快乐","思念","影视原声","校园","80后","90后","网络歌曲","00后","70后","说唱","日语","粤语","轻音乐","爵士","乡村","古典","民族","英伦","清晨","夜晚","学习","运动","怀旧","清新","治愈","另类/独立","安静"};
    private FavorMovieAdapter mAdapter;
    private List<String> mSelectList;
    private Context mContext;
    @Override
    public int getLayoutId() {
        return R.layout.activity_favor_movie;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        mContext = this;
        StatusBarSetting.setTranslucent(this);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            mSelectList = (List<String>) extras.get("select");
        }
        setRecyclerView();
        mIvRight.setOnClickListener(this);
        mIvWrong.setOnClickListener(this);
    }
    private void setRecyclerView() {
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(mTypes));

        mAdapter = new FavorMovieAdapter(list,mContext,mSelectList);
        FlexboxLayoutManager manager = new FlexboxLayoutManager();
        //设置主轴排列方式
        manager.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        manager.setFlexWrap(FlexWrap.WRAP);

        mRvFavor.setLayoutManager(manager);
        mRvFavor.setItemAnimator(new DefaultItemAnimator());
        mRvFavor.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_wrong:{
                finish();
                break;
            }
            case R.id.iv_right:{
                List<String> list = mAdapter.getSelectList();
                FavorListBean bean = new FavorListBean();
                bean.setMovie(false);
                bean.setList(list);
                EventBus.getDefault().post(bean);
                finish();
                break;
            }
        }
    }


}
