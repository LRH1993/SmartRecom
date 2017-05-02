package com.lvr.threerecom.ui.music;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MusicMainViewPagerAdapter;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.base.BaseFragment;
import com.lvr.threerecom.ui.music.fragment.MusicDisplayFragment;
import com.lvr.threerecom.ui.music.fragment.MyFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lvr on 2017/4/24.
 */

public class MusicActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.iv_bar_back)
    ImageView mIvBarBack;
    @BindView(R.id.iv_bar_music)
    ImageView mIvBarMusic;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.vp_music_main)
    ViewPager mVpMusicMain;
    @BindView(R.id.iv_bar_my)
    ImageView mIvBarMy;
    private MusicMainViewPagerAdapter mPagerAdapter;
    private List<BaseFragment> mFragments = new ArrayList<>();
    private MyFragment mMyFragment;
    private MusicDisplayFragment mMusicDisplayFragment;
    private List<ImageView> tabs;

    @Override
    public int getLayoutId() {
        return R.layout.activity_music;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        mIvBarBack.setOnClickListener(this);
        mIvBarMusic.setOnClickListener(this);
        mIvBarMy.setOnClickListener(this);
        tabs = new ArrayList<>();
        tabs.add(mIvBarMy);
        tabs.add(mIvBarMusic);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化Fragment
        initFragment(savedInstanceState);

    }

    private void initFragment(Bundle state) {
        if (state != null) {
            //异常情况下
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof MyFragment) {
                    mMyFragment = (MyFragment) fragment;
                }
                if (fragment instanceof MusicDisplayFragment) {
                    mMusicDisplayFragment = (MusicDisplayFragment) fragment;
                }
            }
        } else {
            //添加到FragmentManger，异常时，自动保存Fragment状态
            mMyFragment = new MyFragment();
            mMusicDisplayFragment = new MusicDisplayFragment();
        }
        setViewPager();
    }

    /**
     * 设置ViewPager
     */
    private void setViewPager() {
        mFragments.add(mMyFragment);
        mFragments.add(mMusicDisplayFragment);
        mPagerAdapter = new MusicMainViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mVpMusicMain.setAdapter(mPagerAdapter);
        mVpMusicMain.setCurrentItem(0);
        mIvBarMy.setSelected(true);
        mVpMusicMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * viewPager与Toolbar中的标签联动
     * @param position
     */
    private void switchTabs(int position) {
        for (int i = 0; i < tabs.size(); i++) {
            if (position == i) {
                tabs.get(i).setSelected(true);
            } else {
                tabs.get(i).setSelected(false);
            }
        }
    }

    /**
     * 入口
     *
     * @param activity
     */
    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, MusicActivity.class);
        activity.startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bar_back: {
                finish();
                break;
            }
            case R.id.iv_bar_music: {
                mIvBarBack.setSelected(true);
                mVpMusicMain.setCurrentItem(1);
                break;
            }
            case R.id.iv_bar_my: {
                mIvBarMy.setSelected(true);
                mVpMusicMain.setCurrentItem(0);
                break;
            }
        }

    }


}
