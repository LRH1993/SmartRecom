package com.lvr.threerecom.adapter;

import android.support.v4.app.FragmentManager;

import com.lvr.threerecom.base.BaseFragment;
import com.lvr.threerecom.base.BaseFragmentAdapter;

import java.util.List;

/**
 * Created by lvr on 2017/5/2.
 */

public class MusicPlayingViewPagerAdapter extends BaseFragmentAdapter {
    public MusicPlayingViewPagerAdapter(FragmentManager fm, List<BaseFragment> fragmentList) {
        super(fm, fragmentList);
    }
}
