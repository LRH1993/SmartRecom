package com.lvr.threerecom.ui.music;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import com.aspsine.irecyclerview.IRecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.lvr.threerecom.R;
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
import com.lvr.threerecom.utils.AESTools;
import com.lvr.threerecom.utils.ImageLoaderUtils;
import com.lvr.threerecom.utils.StatusBarSetting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;

/**
 * Created by lvr on 2017/4/27.
 */

public class MusicSongListDetailActivity extends BaseActivityWithoutStatus implements MusicSongListDetailView, MusicSongListDetailAdapter.onItemClickListener {

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
    }

    private void setRecyclerView() {
        mDetailAdapter = new MusicSongListDetailAdapter(mContext, mList);
        mIrvSongDetail.setLayoutManager(new LinearLayoutManager(mContext));
        mIrvSongDetail.setItemAnimator(new LandingAnimator());
        mIrvSongDetail.setIAdapter(new ScaleInAnimationAdapter(mDetailAdapter));
        mIrvSongDetail.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mPresenter.requestSongListDetail(AppConstantValue.MUSIC_URL_FORMAT, AppConstantValue.MUSIC_URL_FROM, AppConstantValue.MUSIC_URL_METHOD_SONGLIST_DETAIL, songListid);

    }

    @Override
    public void returnSongListDetailInfos(List<SongListDetail.SongDetail> songDetails) {
        System.out.println("接收到歌单中歌曲信息：" + songDetails.size());
        mList.addAll(songDetails);
        mDetailAdapter.notifyDataSetChanged();
    }

    @Override
    public void returnSongDetail(SongDetailInfo info) {
        System.out.println("接收到歌曲详细信息");
        Toast.makeText(mContext, "还不能播放，很快就会实现！", Toast.LENGTH_SHORT).show();
        
    }

    @Override
    public void onItemClick(String songid) {
        long millis = System.currentTimeMillis();
        String str = "songid=" + songid + "&ts=" + millis;
        String e = AESTools.encrpty(str);
        mPresenter.requestSongDetail(AppConstantValue.MUSIC_URL_FORMAT, AppConstantValue.MUSIC_URL_FROM, AppConstantValue.MUSIC_URL_METHOD_SONG_DETAIL
                , songid, millis + "", e);
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


}
