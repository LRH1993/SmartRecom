package com.lvr.threerecom.ui.music.fragment;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MusicMyAdapter;
import com.lvr.threerecom.base.BaseFragment;
import com.lvr.threerecom.bean.MusicMyItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lvr on 2017/4/25.
 */

public class MyFragment extends BaseFragment implements OnRefreshListener {
    @BindView(R.id.irv_music_my)
    IRecyclerView mIrvMusicMy;
    @BindView(R.id.iv_broadcast)
    ImageView mIvBroadcast;
    private List<MusicMyItem> mList;
    private MusicMyAdapter mMusicMyAdapter;
    private Context mContext;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_music_my;
    }

    @Override
    protected void initView() {
        mContext = getActivity();
        initData();
        setRecyclerView();
    }


    private void setRecyclerView() {
        mMusicMyAdapter = new MusicMyAdapter(mContext,mList);
        mIrvMusicMy.setLayoutManager(new LinearLayoutManager(mContext));
        // TODO: 2017/4/26  先默认不能刷新
//        mIrvMusicMy.setOnRefreshListener(this);
        mIrvMusicMy.setItemAnimator(new DefaultItemAnimator());
        mIrvMusicMy.setIAdapter(mMusicMyAdapter);
        mIrvMusicMy.addItemDecoration(new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL));



    }

    private void initData() {
        //// TODO: 2017/4/25  后期应该成从外存缓存中取出响应信息 该信息存储在本地数据库中
        mList = new ArrayList<>();
        MusicMyItem item1 = new MusicMyItem();
        item1.setTitle("本地音乐");
        item1.setCount(0);
        item1.setImageRes(getResources().getDrawable(R.drawable.music_local));
        mList.add(item1);
        MusicMyItem item2 = new MusicMyItem();
        item2.setTitle("最近播放");
        item2.setCount(0);
        item2.setImageRes(getResources().getDrawable(R.drawable.music_recent));
        mList.add(item2);
        MusicMyItem item3 = new MusicMyItem();
        item3.setTitle("下载管理");
        item3.setCount(0);
        item3.setImageRes(getResources().getDrawable(R.drawable.music_download));
        mList.add(item3);
        MusicMyItem item4 = new MusicMyItem();
        item4.setTitle("我的歌手");
        item4.setCount(0);
        item4.setImageRes(getResources().getDrawable(R.drawable.music_singer));
        mList.add(item4);
        MusicMyItem item5 = new MusicMyItem();
        item5.setTitle("我喜欢的音乐");
        item5.setCount(0);
        item5.setImageRes(getResources().getDrawable(R.drawable.music_list_default));
        mList.add(item5);

    }

    @Override
    public void onRefresh() {

    }
}
