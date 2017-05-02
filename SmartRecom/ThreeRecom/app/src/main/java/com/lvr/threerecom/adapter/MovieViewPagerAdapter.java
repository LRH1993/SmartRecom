package com.lvr.threerecom.adapter;

import android.support.v4.app.FragmentManager;

import com.lvr.threerecom.base.BaseFragment;
import com.lvr.threerecom.base.BaseFragmentAdapter;

import java.util.List;

/**
 * Created by lvr on 2017/2/6.
 */

public class MovieViewPagerAdapter extends BaseFragmentAdapter {
    public MovieViewPagerAdapter(FragmentManager fm, List<BaseFragment> fragmentList, List<String> mTitles) {
        super(fm, fragmentList, mTitles);
    }

    public MovieViewPagerAdapter(FragmentManager fm, List<BaseFragment> fragmentList) {
        super(fm, fragmentList);
    }
}
