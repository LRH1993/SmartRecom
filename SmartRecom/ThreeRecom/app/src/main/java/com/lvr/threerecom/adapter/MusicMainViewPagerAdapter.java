package com.lvr.threerecom.adapter;

import android.support.v4.app.FragmentManager;

import com.lvr.threerecom.base.BaseFragment;
import com.lvr.threerecom.base.BaseFragmentAdapter;

import java.util.List;

/**
 * Created by lvr on 2017/4/25.
 */

public class MusicMainViewPagerAdapter extends BaseFragmentAdapter {
    public MusicMainViewPagerAdapter(FragmentManager fm, List<BaseFragment> fragmentList, List<String> mTitles) {
        super(fm, fragmentList, mTitles);
    }
    public MusicMainViewPagerAdapter(FragmentManager fm, List<BaseFragment> fragmentList) {
        super(fm, fragmentList);
    }
}
