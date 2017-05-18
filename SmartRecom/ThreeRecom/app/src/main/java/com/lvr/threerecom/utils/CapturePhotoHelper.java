package com.lvr.threerecom.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 拍照辅助类
 * <p/>
 * Created by Clock on 2016/5/21.
 */
public class CapturePhotoHelper {

    private final static String TIMESTAMP_FORMAT = "yyyy_MM_dd_HH_mm_ss";

    public final static int CAPTURE_PHOTO_REQUEST_CODE = 0x1111;

    private Activity mActivity;
    /**
     * 存放图片的目录
     */
    private File mPhotoFolder;
    /**
     * 拍照生成的图片文件
     */
    private File mPhotoFile;

    /**
     * @param activity
     * @param photoFolder 存放生成照片的目录，目录不存在时候会自动创建，但不允许为null;
     */
    public CapturePhotoHelper(Activity activity, File photoFolder) {
        this.mActivity = activity;
        this.mPhotoFolder = photoFolder;
    }

    /**
     * 拍照
     */
    public void capture() {
        if (hasCamera()) {
            createPhotoFile();

            if (mPhotoFile == null) {
                Toast.makeText(mActivity, "启动相机失败", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri fileUri = Uri.fromFile(mPhotoFile);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            mActivity.startActivityForResult(captureIntent, CAPTURE_PHOTO_REQUEST_CODE);

        } else {
            Toast.makeText(mActivity, "启动相机失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 创建照片文件
     */
    public void createPhotoFile() {
        if (mPhotoFolder != null) {
            if (!mPhotoFolder.exists()) {//检查保存图片的目录存不存在
                mPhotoFolder.mkdirs();
            }

            String fileName = new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date());
            mPhotoFile = new File(mPhotoFolder, fileName + BitmapUtils.JPG_SUFFIX);
            if (mPhotoFile.exists()) {
                mPhotoFile.delete();
            }
            try {
                mPhotoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                mPhotoFile = null;
            }
        } else {
            mPhotoFile = null;
            Toast.makeText(mActivity, "未指定存储目录", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 判断系统中是否存在可以启动的相机应用
     *
     * @return 存在返回true，不存在返回false
     */
    public boolean hasCamera() {
        PackageManager packageManager = mActivity.getPackageManager();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 获取当前拍到的图片文件
     *
     * @return
     */
    public File getPhoto() {
        return mPhotoFile;
    }

    /**
     * 设置照片文件
     *
     * @param photoFile
     */
    public void setPhoto(File photoFile) {
        this.mPhotoFile = photoFile;
    }
}
