package com.lvr.threerecom.ui.music;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.lvr.threerecom.R;
import com.lvr.threerecom.adapter.MusicPlayingViewPagerAdapter;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.base.BaseFragment;
import com.lvr.threerecom.broadcastreceiver.ProgressReceiver;
import com.lvr.threerecom.service.MediaPlayService;
import com.lvr.threerecom.service.MediaServiceConnection;
import com.lvr.threerecom.ui.music.fragment.RoundFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;

/**
 * Created by lvr on 2017/5/2.
 */

public class PlayingActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.iv_albumart)
    ImageView mIvAlbumart;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.iv_needle)
    ImageView mIvNeedle;
    @BindView(R.id.fl_headerView)
    FrameLayout mFlHeaderView;
    @BindView(R.id.iv_playing_fav)
    ImageView mIvPlayingFav;
    @BindView(R.id.iv_playing_down)
    ImageView mIvPlayingDown;
    @BindView(R.id.iv_playing_cmt)
    ImageView mIvPlayingCmt;
    @BindView(R.id.iv_playing_more)
    ImageView mIvPlayingMore;
    @BindView(R.id.tv_music_duration_played)
    TextView mTvMusicDurationPlayed;
    @BindView(R.id.sb_play_seek)
    SeekBar mSbPlaySeek;
    @BindView(R.id.tv_music_duration)
    TextView mTvMusicDuration;
    @BindView(R.id.iv_playing_mode)
    ImageView mIvPlayingMode;
    @BindView(R.id.iv_playing_pre)
    ImageView mIvPlayingPre;
    @BindView(R.id.iv_playing_play)
    ImageView mIvPlayingPlay;
    @BindView(R.id.iv_playing_next)
    ImageView mIvPlayingNext;
    @BindView(R.id.iv_playing_playlist)
    ImageView mIvPlayingPlaylist;
    private ActionBar mActionBar;
    private List<BaseFragment> mFragmentList = new ArrayList<>();
    private int mDuration;
    private int mCurPostion;
    private String mTitle;
    private String mAuthor;
    private String mPicUrl;
    private static Context mContext;
    private static int radius = 15;
    private MusicPlayingViewPagerAdapter mPagerAdapter;
    private Intent mIntent;
    private MediaServiceConnection mConnection;
    private boolean isLocal = false;
    private ProgressReceiver mReceiver;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int position = msg.arg1;
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            String cur = format.format(position);
            mTvMusicDurationPlayed.setText(cur);
            mSbPlaySeek.setProgress(position);
        }
    };


    @Override
    public int getLayoutId() {
        return R.layout.activity_music_play;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        mContext = this;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mDuration = (int) extras.get("duration");
            mCurPostion = (int) extras.get("curPostion");
            mTitle = (String) extras.get("title");
            mAuthor = (String) extras.get("author");
            mPicUrl = (String) extras.get("picUrl");
        }
        setToolBar();
        setUI();
        setViewPager();
        //动态注册更新播放进度的广播
        mReceiver = new ProgressReceiver(mHandler);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.lvr.progress");
        registerReceiver(mReceiver,filter);
    }

    private void setViewPager() {
        RoundFragment roundFragment = new RoundFragment();
        Bundle bundle = new Bundle();
        bundle.putString("picUrl", mPicUrl);
        roundFragment.setArguments(bundle);
        mFragmentList.add(roundFragment);
        mPagerAdapter = new MusicPlayingViewPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void setUI() {
        if (mIntent == null) {
            mIntent = new Intent(this, MediaPlayService.class);
            mConnection = new MediaServiceConnection(mIvPlayingPlay);
            bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        }
        mActionBar.setTitle(mTitle + "---" + mAuthor);
        mSbPlaySeek.setMax(mDuration);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        String total = format.format(mDuration);
        String cur = format.format(mCurPostion);
        mTvMusicDuration.setText(total);
        mTvMusicDurationPlayed.setText(cur);
        if (!TextUtils.isEmpty(mPicUrl)) {
            new PathAsyncTask(mIvAlbumart).execute(mPicUrl);
        }
        mIvPlayingPlay.setOnClickListener(this);
        mIvPlayingNext.setOnClickListener(this);
        mIvPlayingPre.setOnClickListener(this);


    }

    private void setToolBar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_playing_next: {
                mConnection.mMediaPlayService.next(isLocal);
                break;
            }
            case R.id.iv_playing_play: {

                if (mConnection.mMediaPlayService.isPlaying()) {
                    //正在播放 转变为暂停
                    mIvPlayingPlay.setImageResource(R.drawable.play_rdi_btn_play);
                    mConnection.mMediaPlayService.pause();
                } else {
                    //正在暂停 转变为播放
                    mIvPlayingPlay.setImageResource(R.drawable.play_rdi_btn_pause);
                    mConnection.mMediaPlayService.resume();
                }

                break;
            }
            case R.id.iv_playing_pre: {
                mConnection.mMediaPlayService.pre(isLocal);
            }
        }

    }



    private static class PathAsyncTask extends AsyncTask<String, Void, String> {
        private ImageView mImageView;

        public PathAsyncTask(ImageView view) {
            this.mImageView = view;
        }

        private String mPath;
        private FileInputStream mIs;

        @Override
        protected String doInBackground(String... params) {
            FutureTarget<File> future = Glide.with(AppApplication.getAppContext())
                    .load(params[0])
                    .downloadOnly(100, 100);
            try {
                File cacheFile = future.get();
                mPath = cacheFile.getAbsolutePath();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            return mPath;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                mIs = new FileInputStream(s);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(mIs);
            applyBlur(bitmap, mImageView);

        }
    }


    private static void applyBlur(Bitmap bgBitmap, ImageView view) {
        //处理得到模糊效果的图
        RenderScript renderScript = RenderScript.create(AppApplication.getAppContext());
        // Allocate memory for Renderscript to work with
        final Allocation input = Allocation.createFromBitmap(renderScript, bgBitmap);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());
        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(input);
        // Set the blur radius
        scriptIntrinsicBlur.setRadius(radius);
        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output);
        // Copy the output to the blurred bitmap
        output.copyTo(bgBitmap);
        renderScript.destroy();
        BitmapDrawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bgBitmap);
        view.setBackground(bitmapDrawable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        unregisterReceiver(mReceiver);
    }
}
