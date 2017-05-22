package com.lvr.threerecom.ui.home;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.aspsine.irecyclerview.IRecyclerView;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MusicSongListDetailAdapter;
import com.lvr.threerecom.anims.LandingAnimator;
import com.lvr.threerecom.anims.ScaleInAnimationAdapter;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.bean.SongListDetail;
import com.lvr.threerecom.service.MediaPlayService;
import com.lvr.threerecom.ui.MainActivity;
import com.lvr.threerecom.ui.home.presenter.impl.RecomMusicPresenterImpl;
import com.lvr.threerecom.ui.home.view.RecomMusicView;
import com.lvr.threerecom.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;

/**
 * Created by lvr on 2017/5/19.
 * 暂时并不是个性化推荐
 *
 */

public class RecomMusicActivity extends BaseActivity implements RecomMusicView,MusicSongListDetailAdapter.onItemClickListener, MusicSongListDetailAdapter.onPlayAllClickListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fl_image)
    FrameLayout mFlImage;
    @BindView(R.id.irv_song_detail)
    IRecyclerView mIrvSongDetail;
    @BindView(R.id.bottom_container)
    FrameLayout mBottomContainer;
    private int num = 100;
    private RecomMusicPresenterImpl mPresenter;
    private static Context mContext;
    private MediaServiceConnection mConnection;
    private MediaPlayService mService;
    private boolean isPlayAll = false;
    private Intent mIntent;
    private static MediaPlayService.MediaBinder mMediaBinder;
    //song_id 对应的在集合中的位置
    private HashMap<String, Integer> positionMap = new HashMap<>();
    //请求返回的SongDetailInfo先存放在数组中，对应下标索引是其在集合中所处位置
    private SongDetailInfo[] mInfos;
    //指示现在加入musicList集合中的元素下标应该是多少
    AtomicInteger index = new AtomicInteger(0);
    private List<SongListDetail.SongDetail> mList = new ArrayList<>();
    private MusicSongListDetailAdapter mDetailAdapter;
    private boolean isLocal =false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recom_music;
    }

    @Override
    public void initPresenter() {
        mPresenter = new RecomMusicPresenterImpl(this);
    }

    @Override
    public void initView() {


        mContext = this;
        mToolbar.setTitle("音乐推荐");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mConnection = new MediaServiceConnection();
        if (mIntent == null) {
            mIntent = new Intent(this, MediaPlayService.class);
            startService(mIntent);
            bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        }
        setRecyclerView();
        mDetailAdapter.setOnItemClickListener(this);
        mDetailAdapter.setOnPlayAllClickListener(this);
    }

    private void setRecyclerView() {
        mDetailAdapter = new MusicSongListDetailAdapter(mContext, mList);
        mIrvSongDetail.setLayoutManager(new LinearLayoutManager(mContext));
        mIrvSongDetail.setItemAnimator(new LandingAnimator());
        mIrvSongDetail.setIAdapter(new ScaleInAnimationAdapter(mDetailAdapter));
        mIrvSongDetail.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        LoadingDialog.showDialogForLoading(this).show();
        mPresenter.requestRecomMusic(AppConstantValue.MUSIC_URL_FROM_2, AppConstantValue.MUSIC_URL_VERSION, AppConstantValue.MUSIC_URL_FORMAT, AppConstantValue.MUSIC_URL_METHOD_RECOM, num);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void returnRecomMusicList(List<SongListDetail.SongDetail> songDetailList) {
        mList.addAll(songDetailList);
        //初始化数组集合
        mInfos = new SongDetailInfo[mList.size()];
        mDetailAdapter.notifyDataSetChanged();
        initMusicList();
    }

    @Override
    public void returnSongDetail(SongDetailInfo info) {
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

    private void initMusicList() {
        for (int i = 0; i < mList.size(); i++) {
            SongListDetail.SongDetail songDetail = mList.get(i);
            String song_id = songDetail.getSong_id();
            positionMap.put(song_id, i);
            mPresenter.requestSongDetail(AppConstantValue.MUSIC_URL_FROM_2, AppConstantValue.MUSIC_URL_VERSION, AppConstantValue.MUSIC_URL_FORMAT, AppConstantValue.MUSIC_URL_METHOD_SONG_DETAIL
                    , song_id);
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
    public void onItemClick(List<SongListDetail.SongDetail> songDetails) {
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

}
