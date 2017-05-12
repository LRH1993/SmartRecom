package com.lvr.threerecom.ui.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.lvr.threerecom.R;
import com.lvr.threerecom.service.MediaPlayService;
import com.lvr.threerecom.adapter.MusicSongListDetailAdapter;
import com.lvr.threerecom.anims.LandingAnimator;
import com.lvr.threerecom.anims.ScaleInAnimationAdapter;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.base.BaseActivityWithoutStatus;
import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.bean.SongListDetail;
import com.lvr.threerecom.ui.music.presneter.impl.MusicSongListDetailPresenterImpl;
import com.lvr.threerecom.ui.music.view.MusicSongListDetailView;
import com.lvr.threerecom.utils.ImageLoaderUtils;
import com.lvr.threerecom.utils.StatusBarSetting;
import com.lvr.threerecom.widget.LoadingDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;

/**
 * Created by lvr on 2017/4/27.
 */

public class MusicSongListDetailActivity extends BaseActivityWithoutStatus implements MusicSongListDetailView, MusicSongListDetailAdapter.onItemClickListener, MusicSongListDetailAdapter.onPlayAllClickListener {

    @BindView(R.id.overlay)
    View mOverlay;
    @BindView(R.id.iv_songlist_photo)
    ImageView mIvSonglistPhoto;
    @BindView(R.id.tv_songlist_count)
    TextView mTvSonglistCount;
    @BindView(R.id.tv_songlist_name)
    TextView mTvSonglistName;
    @BindView(R.id.ll_collect)
    LinearLayout mLlCollect;
    @BindView(R.id.iv_comment)
    ImageView mIvComment;
    @BindView(R.id.ll_comment)
    LinearLayout mLlComment;
    @BindView(R.id.iv_share)
    ImageView mIvShare;
    @BindView(R.id.ll_share)
    LinearLayout mLlShare;
    @BindView(R.id.ll_download)
    LinearLayout mLlDownload;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.irv_song_detail)
    IRecyclerView mIrvSongDetail;
    @BindView(R.id.tv_songlist_detail)
    TextView mTvSonglistDetail;
    @BindView(R.id.iv_collect)
    ImageView mIvCollect;
    @BindView(R.id.headerdetail)
    RelativeLayout mHeaderdetail;
    @BindView(R.id.headerview)
    FrameLayout mHeaderview;
    @BindView(R.id.bottom_container)
    FrameLayout mBottomContainer;
    @BindView(R.id.album_art)
    ImageView mAlbumArt;
    @BindView(R.id.fra)
    FrameLayout mFra;
    @BindView(R.id.rl_toobar)
    RelativeLayout mRlToobar;

    private String songListid;
    private boolean isLocal;
    private String photoUrl;
    private String listName;
    private String detail;
    private String count;
    private static Context mContext;
    private static int radius = 25;
    private MusicSongListDetailPresenterImpl mPresenter;
    private MusicSongListDetailAdapter mDetailAdapter;
    private List<SongListDetail.SongDetail> mList = new ArrayList<>();
    private Intent mIntent;
    private static MediaPlayService.MediaBinder mMediaBinder;
    private List<SongListDetail.SongDetail> mMReturnList;
    private MediaPlayService mService;
    private boolean isPlayAll = false;
    private MediaServiceConnection mConnection;
    //song_id 对应的在集合中的位置
    private HashMap<String, Integer> positionMap = new HashMap<>();
    //请求返回的SongDetailInfo先存放在数组中，对应下标索引是其在集合中所处位置
    private SongDetailInfo[] mInfos;
    //指示现在加入musicList集合中的元素下标应该是多少
    AtomicInteger index = new AtomicInteger(0);

    @Override
    public int getLayoutId() {
        return R.layout.activity_songlist_detail;
    }

    @Override
    public void initPresenter() {
        mPresenter = new MusicSongListDetailPresenterImpl(this, this);
    }

    @Override
    public void initView() {
        StatusBarSetting.setTranslucent(this);

        mContext = this;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            songListid = (String) extras.get("songListId");
            isLocal = (boolean) extras.get("islocal");
            photoUrl = (String) extras.get("songListPhoto");
            listName = (String) extras.get("songListname");
            detail = (String) extras.get("songListTag");
            count = (String) extras.get("songListCount");
        }
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setUI();
        setRecyclerView();
        mDetailAdapter.setOnItemClickListener(this);
        mDetailAdapter.setOnPlayAllClickListener(this);


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

    private void setUI() {
        ImageLoaderUtils.display(this, mIvSonglistPhoto, photoUrl);
        mTvSonglistCount.setText(count);
        mTvSonglistName.setText(listName);
        String[] split = detail.split(",");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("标签：");
        for (int i = 0; i < split.length; i++) {
            stringBuffer.append(split[i] + " ");
        }
        mTvSonglistDetail.setText(stringBuffer);
        new PathAsyncTask(mAlbumArt).execute(photoUrl);
        mConnection = new MediaServiceConnection();
        if (mIntent == null) {
            mIntent = new Intent(this, MediaPlayService.class);
            startService(mIntent);
            bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        }
    }

    private void setRecyclerView() {
        mDetailAdapter = new MusicSongListDetailAdapter(mContext, mList);
        mIrvSongDetail.setLayoutManager(new LinearLayoutManager(mContext));
        mIrvSongDetail.setItemAnimator(new LandingAnimator());
        mIrvSongDetail.setIAdapter(new ScaleInAnimationAdapter(mDetailAdapter));
        mIrvSongDetail.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        LoadingDialog.showDialogForLoading(this).show();
        mPresenter.requestSongListDetail(AppConstantValue.MUSIC_URL_FORMAT, AppConstantValue.MUSIC_URL_FROM, AppConstantValue.MUSIC_URL_METHOD_SONGLIST_DETAIL, songListid);

    }

    @Override
    public void returnSongListDetailInfos(List<SongListDetail.SongDetail> songDetails) {
        mList.addAll(songDetails);
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

            if(info.getSonginfo()==null){
                // TODO: 2017/5/10 为空 不能播放 后续需要处理 
            }else{
                String song_id = info.getSonginfo().getSong_id();
                Integer position = positionMap.get(song_id);
                mInfos[position] = info;
            }
            int currentNumber = index.addAndGet(1);
            if (currentNumber == mInfos.length) {
                for (int i = 0; i < mInfos.length; i++) {
                    if(i==0){
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
    }
}
