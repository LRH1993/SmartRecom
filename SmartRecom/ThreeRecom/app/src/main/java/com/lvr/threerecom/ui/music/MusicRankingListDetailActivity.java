package com.lvr.threerecom.ui.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MusicRankingListDetailAdapter;
import com.lvr.threerecom.anims.LandingAnimator;
import com.lvr.threerecom.anims.ScaleInAnimationAdapter;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.app.BaiDuMusicApi;
import com.lvr.threerecom.base.BaseActivityWithoutStatus;
import com.lvr.threerecom.bean.RankingListDetail;
import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.service.MediaPlayService;
import com.lvr.threerecom.ui.music.presneter.impl.MusicRankingListDetailPresenterImpl;
import com.lvr.threerecom.ui.music.view.MusicRankingListDetailView;
import com.lvr.threerecom.utils.ImageLoaderUtils;
import com.lvr.threerecom.utils.StatusBarSetting;
import com.lvr.threerecom.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;

/**
 * Created by lvr on 2017/5/12.
 */

public class MusicRankingListDetailActivity extends BaseActivityWithoutStatus implements MusicRankingListDetailView, MusicRankingListDetailAdapter.onItemClickListener, MusicRankingListDetailAdapter.onPlayAllClickListener {
    @BindView(R.id.iv_album_art)
    ImageView mIvAlbumArt;
    @BindView(R.id.rl_toobar)
    RelativeLayout mRlToobar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.irv_song_detail)
    IRecyclerView mIrvSongDetail;
    @BindView(R.id.bottom_container)
    FrameLayout mBottomContainer;
    private static Context mContext;
    @BindView(R.id.tv_name)
    TextView mTvName;
    private int mType;
    private MusicRankingListDetailPresenterImpl mPresenter;
    private MusicRankingListDetailAdapter mDetailAdapter;
    private List<RankingListDetail.SongListBean> mList = new ArrayList<>();
    private String mFields;
    //请求返回的SongDetailInfo先存放在数组中，对应下标索引是其在集合中所处位置
    private SongDetailInfo[] mInfos;
    //song_id 对应的在集合中的位置
    private HashMap<String, Integer> positionMap = new HashMap<>();
    private static MediaPlayService.MediaBinder mMediaBinder;
    private MediaPlayService mService;
    private MediaServiceConnection mConnection;
    //指示现在加入musicList集合中的元素下标应该是多少
    private AtomicInteger index = new AtomicInteger(0);
    private Intent mIntent;
    private boolean isPlayAll = false;
    private boolean isLocal;

    @Override
    public int getLayoutId() {
        return R.layout.activity_rankinglist_detail;
    }

    @Override
    public void initPresenter() {
        mPresenter = new MusicRankingListDetailPresenterImpl(this);
    }

    @Override
    public void initView() {

        StatusBarSetting.setTranslucent(this);
        mContext = this;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mType = (int) extras.get("type");
        }
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mFields = new BaiDuMusicApi().encode("song_id,title,author,album_title,pic_big,pic_small,havehigh,all_rate,charge,has_mv_mobile,learn,song_source,korean_bb_song");
        setRecyclerView();
        //初始化服务连接
        mConnection = new MediaServiceConnection();
        if (mIntent == null) {
            mIntent = new Intent(this, MediaPlayService.class);
            startService(mIntent);
            bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        }
    }

    private void setRecyclerView() {
        mDetailAdapter = new MusicRankingListDetailAdapter(mContext, mList);
        mDetailAdapter.setOnItemClickListener(this);
        mDetailAdapter.setOnPlayAllClickListener(this);
        mIrvSongDetail.setLayoutManager(new LinearLayoutManager(mContext));
        mIrvSongDetail.setItemAnimator(new LandingAnimator());
        mIrvSongDetail.setIAdapter(new ScaleInAnimationAdapter(mDetailAdapter));
        mIrvSongDetail.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        LoadingDialog.showDialogForLoading(this).show();
        mPresenter.requestRankListDetail(AppConstantValue.MUSIC_URL_FORMAT, AppConstantValue.MUSIC_URL_FROM, AppConstantValue.MUSIC_URL_METHOD_RANKING_DETAIL, mType, 0, 100, mFields);
    }


    @Override
    public void returnRankingListDetail(RankingListDetail detail) {
        List<RankingListDetail.SongListBean> list = detail.getSong_list();
        setUI(detail.getBillboard());
        mList.addAll(list);
        //初始化数组集合
        mInfos = new SongDetailInfo[mList.size()];
        mDetailAdapter.notifyDataSetChanged();
        initMusicList();
    }

    /**
     * 设置UI信息
     *
     * @param billboard
     */
    private void setUI(RankingListDetail.BillboardBean billboard) {
        mTvName.setText(billboard.getName());
        ImageLoaderUtils.display(this, mIvAlbumArt, billboard.getPic_s260());

    }

    private void initMusicList() {
        for (int i = 0; i < mList.size(); i++) {
            RankingListDetail.SongListBean songDetail = mList.get(i);
            String song_id = songDetail.getSong_id();
            positionMap.put(song_id, i);
            mPresenter.requestSongDetail(AppConstantValue.MUSIC_URL_FROM_2, AppConstantValue.MUSIC_URL_VERSION, AppConstantValue.MUSIC_URL_FORMAT, AppConstantValue.MUSIC_URL_METHOD_SONG_DETAIL
                    , song_id);
        }
    }

    @Override
    public void returnSongDetail(SongDetailInfo info) {
        System.out.println("回调次数：" + index);
        if (mMediaBinder != null) {
            if (mService == null) {
                mService = mMediaBinder.getMediaPlayService();
            }

            if (info.getSonginfo() == null) {
                // TODO: 2017/5/10 为空 不能播放 后续需要处理
            } else {
                String song_id = info.getSonginfo().getSong_id();
                Integer position = positionMap.get(song_id);
                mInfos[position] = info;
            }
            int currentNumber = index.addAndGet(1);
            if (currentNumber == mInfos.length) {
                for (int i = 0; i < mInfos.length; i++) {
                    if (i == 0) {
                        //先清除之前的播放集合
                        mService.clearMusicList();
                    }
                    mService.addMusicList(mInfos[i]);
                }
                LoadingDialog.cancelDialogForLoading();
            }


        }
    }

    @Override
    public void onItemClick(int position) {
        //播放单个
        isPlayAll = false;
        if (mService != null) {
            mService.setPlayAll(false);
            mService.playSong(position, isLocal);
        }
    }

    @Override
    public void onItemClick() {
        //播放全部
        isPlayAll = true;
        if (mService != null) {
            mService.playAll(isLocal);
        }
    }


    private static class MediaServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMediaBinder = (MediaPlayService.MediaBinder) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }
}
