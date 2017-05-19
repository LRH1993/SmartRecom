package com.lvr.threerecom.ui.movie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lvr.threerecom.R;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.bean.MovieInfo;
import com.lvr.threerecom.utils.ImageLoaderUtils;
import com.lvr.threerecom.utils.StatusBarSetting;
import com.lvr.threerecom.widget.SuperTextView;

import butterknife.BindView;

/**
 * Created by lvr on 2017/4/23.
 */

public class MovieDetailActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "MovieDetailActivity";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_rating)
    TextView mTvRating;
    @BindView(R.id.tv_genres)
    TextView mTvGenres;
    @BindView(R.id.tv_release)
    TextView mTvRelease;
    @BindView(R.id.ll_intro)
    LinearLayout mLlIntro;
    @BindView(R.id.tv_watch)
    SuperTextView mTvWatch;
    @BindView(R.id.tv_judge)
    SuperTextView mTvJudge;
    @BindView(R.id.ll_function)
    LinearLayout mLlFunction;
    @BindView(R.id.iv_photo)
    ImageView mIvPhoto;
    private Context mContext;
    private int radius = 25;
    private MovieInfo mInfo;
    private String mImageUrl;

    @Override
    public int getLayoutId() {
        return R.layout.activity_movie_detail;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView()  {
        StatusBarSetting.setTranslucent(MovieDetailActivity.this);
        mContext = this;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mInfo = (MovieInfo) extras.get("movie");
            if (mInfo != null) {
                setUIData(mInfo);
            }
        }
        mTvJudge.setOnClickListener(this);
        mTvWatch.setOnClickListener(this);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setUIData(MovieInfo info)  {
        mImageUrl = info.getMovie_picture_url();
        final String name = info.getMovie_name();
        double score = info.getMovie_average_score();
        String date = info.getMovie_release_date();
        String genres = info.getMovie_genres();
        ImageLoaderUtils.displayBigPhoto(mContext,mIvPhoto, mImageUrl);
        mTvName.setText(name);
        mTvRating.setText(score+"");
        String[] split = genres.split(" ");

        String types = "";
        for(int i=0;i<split.length;i++){
            if(i==split.length-1){
                types+=split[i];
                System.out.println("影片类型："+split[i]);
            }else{
                types+=split[i]+"/";
                System.out.println("影片类型："+split[i]);
            }
        }
        mTvGenres.setText(types);
        mTvRelease.setText("上映时间："+date);
        applyBlur();

    }
    private void applyBlur()  {
        Drawable db = getResources().getDrawable(R.drawable.login_bg);
        BitmapDrawable drawable = (BitmapDrawable) db;
        Bitmap bgBitmap = drawable.getBitmap();
        //处理得到模糊效果的图
        RenderScript renderScript = RenderScript.create(mContext);
//                Log.i(TAG, "scale size:" + resource.getWidth() + "*" + resource.getHeight());
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
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bgBitmap);
        mIvPhoto.setBackground(bitmapDrawable);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_watch:{
                Intent intent = new Intent(MovieDetailActivity.this,MoviePlayActivity.class);
                intent.putExtra("url",mInfo.getMovie_download_url());
                startActivity(intent);
                break;
            }
            case R.id.tv_judge:{
                Intent intent = new Intent(MovieDetailActivity.this,MovieJudgeActivity.class);
                intent.putExtra("id",mInfo.getMovie_id());
                startActivity(intent);
                break;
            }
        }
    }
}
