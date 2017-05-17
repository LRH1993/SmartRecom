package com.lvr.threerecom.ui.home;

import android.graphics.Bitmap;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.lvr.threerecom.R;
import com.lvr.threerecom.base.BaseActivity;
import com.lvr.threerecom.utils.BitmapUtils;
import com.lvr.threerecom.utils.DisplayUtil;

import java.io.File;

import butterknife.BindView;

/**
 * Created by lvr on 2017/5/17.
 */

public class PhotoPreviewActivity extends BaseActivity {
    private final static String EXTRA_PHOTO = "extra_photo";
    @BindView(R.id.iv_preview_photo)
    ImageView mIvPreviewPhoto;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private File mPhotoFile;

    @Override
    public int getLayoutId() {
        return R.layout.activity_photo_preview;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        mToolbar.setTitle("裁剪头像");
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPhotoFile = (File) getIntent().getSerializableExtra(EXTRA_PHOTO);
        //存放到相册
        BitmapUtils.displayToGallery(this, mPhotoFile);
        //按照屏幕宽高进行显示
        int requestWidth = (int) (DisplayUtil.getScreenWidth(this));
        int requestHeight = (int) (DisplayUtil.getScreenHeight(this));
        Bitmap bitmap = BitmapUtils.decodeBitmapFromFile(mPhotoFile, requestWidth, requestHeight);
        if (bitmap != null) {
            //检查是否有被旋转，并进行纠正
            int degree = BitmapUtils.getBitmapDegree(mPhotoFile.getAbsolutePath());
            if (degree != 0) {
                bitmap = BitmapUtils.rotateBitmapByDegree(bitmap, degree);
            }
            mIvPreviewPhoto.setImageBitmap(bitmap);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo_picker, menu);
        MenuItem item = menu.getItem(0);

        return true;
    }


}
