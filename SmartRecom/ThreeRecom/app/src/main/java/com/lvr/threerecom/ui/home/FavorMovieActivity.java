package com.lvr.threerecom.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.android.flexbox.AlignItems;
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

public class FavorMovieActivity extends BaseActivityWithoutStatus implements View.OnClickListener {
    @BindView(R.id.iv_wrong)
    ImageView mIvWrong;
    @BindView(R.id.iv_right)
    ImageView mIvRight;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_favor)
    RecyclerView mRvFavor;
    private String[] mTypes = {"爱情", "科幻", "青春", "喜剧", "动作", "犯罪", "纪录片", "黑色幽默", "动画", "悬疑", "文艺", "恐怖", "战争", "家庭", "浪漫", "励志", "剧情"};
    private String[] mAreas = {"美国", "日本", "香港", "韩国", "内地", "台湾", "欧洲", "印度", "巴西", "澳大利亚", "爱尔兰", "比利时", "墨西哥", "波兰", "土耳其"};
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
        list.addAll(Arrays.asList(mAreas));

        mAdapter = new FavorMovieAdapter(list,mContext,mSelectList);
        FlexboxLayoutManager manager = new FlexboxLayoutManager();
        //设置主轴排列方式
        manager.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        manager.setFlexWrap(FlexWrap.WRAP);
        manager.setAlignItems(AlignItems.STRETCH);
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
                bean.setMovie(true);
                bean.setList(list);
                EventBus.getDefault().post(bean);
                finish();
                break;
            }
        }
    }
}
