package com.lvr.threerecom.ui.music.fragment;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.LinearLayout;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MusicSongListAdapter;
import com.lvr.threerecom.anims.LandingAnimator;
import com.lvr.threerecom.anims.ScaleInAnimationAdapter;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.base.BaseFragment;
import com.lvr.threerecom.bean.WrapperSongListInfo;
import com.lvr.threerecom.ui.music.presneter.impl.MusicPresenterImpl;
import com.lvr.threerecom.ui.music.view.MusicSongListView;
import com.lvr.threerecom.widget.LoadMoreFooterView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lvr on 2017/4/26.
 */

public class SongListFragment extends BaseFragment implements MusicSongListView,OnLoadMoreListener{
    @BindView(R.id.ll_selector)
    LinearLayout mLlSelector;
    @BindView(R.id.irv_song_list)
    IRecyclerView mIrvSongList;
    private MusicPresenterImpl mPresenter;
    private int pageSize =12;
    private int startPage =1;
    private MusicSongListAdapter mMusicSongListAdapter;
    private Context mContext;
    private List<WrapperSongListInfo.SongListInfo> mList = new ArrayList<>();
    private LoadMoreFooterView mFooterView;
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_song_list;
    }

    @Override
    protected void initView() {
        mContext = getActivity();
        mPresenter = new MusicPresenterImpl(this, getActivity());
        mMusicSongListAdapter = new MusicSongListAdapter(mContext, mList);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        manager.setItemPrefetchEnabled(false);
        mIrvSongList.setLayoutManager(manager);
        mIrvSongList.setOnLoadMoreListener(this);
        mIrvSongList.setItemAnimator(new LandingAnimator());
        mIrvSongList.setIAdapter(new ScaleInAnimationAdapter(mMusicSongListAdapter));
        mFooterView = (LoadMoreFooterView) mIrvSongList.getLoadMoreFooterView();
        mPresenter.requestSongListAll(AppConstantValue.MUSIC_URL_FORMAT,AppConstantValue.MUSIC_URL_FROM,AppConstantValue.MUSIC_URL_METHOD_GEDAN,pageSize,startPage);

    }

    @Override
    public void returnSongListInfos(List<WrapperSongListInfo.SongListInfo> songListInfos) {
        System.out.println("获得音乐歌单数据集："+songListInfos.size());
        if (songListInfos.size() != 0) {
            if (startPage == 1) {
                //第一次加载
                mList.clear();
                mList.addAll(songListInfos);
                System.out.println("初次加载数据集大小："+mList.size());
                mMusicSongListAdapter.notifyDataSetChanged();
            } else {
                //加载更多
                mList.addAll(songListInfos);
                System.out.println("数据集大小："+mList.size());
                mMusicSongListAdapter.notifyDataSetChanged();
            }
        } else {
            System.out.println("数据集中数据条目："+songListInfos.size());
            startPage--;
            mFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
        }
    }

    @Override
    public void onLoadMore() {
        ++startPage;
        System.out.println("当前加载的页面："+startPage);
        mPresenter.requestSongListAll(AppConstantValue.MUSIC_URL_FORMAT,AppConstantValue.MUSIC_URL_FROM,AppConstantValue.MUSIC_URL_METHOD_GEDAN,pageSize,startPage);
        mFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
    }
}
