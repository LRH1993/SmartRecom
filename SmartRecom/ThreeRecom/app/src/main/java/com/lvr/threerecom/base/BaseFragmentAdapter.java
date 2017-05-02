package com.lvr.threerecom.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.lvr.threerecom.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class BaseFragmentAdapter extends FragmentPagerAdapter {

    List<BaseFragment> fragmentList = new ArrayList<BaseFragment>();
    private List<String> mTitles;

    public BaseFragmentAdapter(FragmentManager fm, List<BaseFragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    public BaseFragmentAdapter(FragmentManager fm, List<BaseFragment> fragmentList, List<String> mTitles) {
        super(fm);
        this.fragmentList = fragmentList;
        this.mTitles = mTitles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return !CollectionUtils.isNullOrEmpty(mTitles) ? mTitles.get(position) : "";
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    //刷新fragment
    public void setFragments(FragmentManager fm, List<BaseFragment> fragments, List<String> mTitles) {
        this.mTitles = mTitles;
        if (this.fragmentList != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.fragmentList) {
                ft.remove(f);
            }
            ft.commitAllowingStateLoss();
            ft = null;
            fm.executePendingTransactions();
        }
        this.fragmentList = fragments;
        notifyDataSetChanged();
    }
}
