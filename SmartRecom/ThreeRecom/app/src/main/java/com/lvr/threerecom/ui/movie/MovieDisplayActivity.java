package com.lvr.threerecom.ui.movie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MovieViewPagerAdapter;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.base.BaseFragment;
import com.lvr.threerecom.bean.FabSrcollBean;
import com.lvr.threerecom.utils.MyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;

/**
 * Created by lvr on 2017/4/22.
 */

public class MovieDisplayActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabs)
    TabLayout mTabs;

    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.vp_content)
    ViewPager mVpContent;

    private String[] mTypesChinese = {"犯罪", "科幻", "战争", "喜剧", "动作", "动画", "纪录片", "爱情", "恐怖"};
    private String[] mTypesEnglish = {"crime", "fantasy", "war", "comedy", "action", "animation", "documentary", "romance", "horror"};
    private MovieViewPagerAdapter mFragmentAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_movie;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        mToolbar.setTitle("电影");
        setSupportActionBar(mToolbar);
        loadMovieChannel();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FabSrcollBean bean = new FabSrcollBean();
                bean.setTop(true);
                EventBus.getDefault().post(bean);
            }
        });
    }

    /**
     * 加载电影频道
     */
    private void loadMovieChannel() {
        List<BaseFragment> mNewsFragmentList = new ArrayList<>();
        for (int i = 0; i < mTypesChinese.length; i++) {
            mNewsFragmentList.add(createListFragments(i));
        }
        if (mFragmentAdapter == null) {
            mFragmentAdapter = new MovieViewPagerAdapter(getSupportFragmentManager(), mNewsFragmentList, Arrays.asList(mTypesChinese));
        } else {
            //刷新fragment
            mFragmentAdapter.setFragments(getSupportFragmentManager(), mNewsFragmentList, Arrays.asList(mTypesChinese));
        }
        mVpContent.setAdapter(mFragmentAdapter);
        mTabs.setupWithViewPager(mVpContent);
        MyUtils.dynamicSetTabLayoutMode(mTabs);
        mTabs.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.black));

    }

    private BaseFragment createListFragments(int index) {
        MovieFragment fragment = new MovieFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", mTypesEnglish[index]);
        fragment.setArguments(bundle);
        return fragment;
    }


    /**
     * 设置
     */
    private void setTabs() {
        mTabs.setupWithViewPager(mVpContent);
        mTabs.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.black));
    }

    /**
     * 入口
     *
     * @param activity
     */
    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, MovieDisplayActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_toolbar, menu);
        return true;
    }

}
