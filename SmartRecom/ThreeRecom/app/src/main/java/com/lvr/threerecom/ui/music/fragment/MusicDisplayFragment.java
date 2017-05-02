package com.lvr.threerecom.ui.music.fragment;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MusicViewPagerAdapter;
import com.lvr.threerecom.base.BaseFragment;
import com.lvr.threerecom.utils.MyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lvr on 2017/4/25.
 * 包含新曲，歌单和榜单两页
 */

public class MusicDisplayFragment extends BaseFragment {
    @BindView(R.id.tl_music_net)
    TabLayout mTlMusicNet;
    @BindView(R.id.vp_music_net)
    ViewPager mVpMusicNet;
    @BindView(R.id.main_content)
    LinearLayout mMainContent;
    private String[] title = {"歌单", "排行榜"};
    private MusicViewPagerAdapter mFragmentAdapter;
    private Context mContext;
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_music_songs;
    }

    @Override
    public void initView() {
        mContext = getActivity();
        loadMusicChannel();
    }

    private void loadMusicChannel() {
        List<BaseFragment> mMusicFragmentList;
        mMusicFragmentList = createListFragments();
        if (mFragmentAdapter == null) {
            mFragmentAdapter = new MusicViewPagerAdapter(getActivity().getSupportFragmentManager(), mMusicFragmentList, Arrays.asList(title));
        } else {
            //刷新fragment
            mFragmentAdapter.setFragments(getActivity().getSupportFragmentManager(), mMusicFragmentList, Arrays.asList(title));
        }
        mVpMusicNet.setAdapter(mFragmentAdapter);
        mTlMusicNet.setupWithViewPager(mVpMusicNet);
        MyUtils.dynamicSetTabLayoutMode(mTlMusicNet);
    }

    private  List<BaseFragment> createListFragments() {
        List<BaseFragment> list = new ArrayList<>();
        list.add(new SongListFragment());
        list.add(new RankingFragment());
        return list;
    }
}
