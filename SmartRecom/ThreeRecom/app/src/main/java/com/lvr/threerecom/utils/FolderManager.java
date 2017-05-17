package com.lvr.threerecom.utils;

import android.os.Environment;

import java.io.File;

/**
 * 目录管理器
 * <p/>
 * Created by Clock on 2016/5/27.
 */
public class FolderManager {

    /**
     * 应用程序在SD卡上的主目录名称
     */
    private final static String APP_FOLDER_NAME = "SmartRecom";
    /**
     * 存放图片目录名
     */
    private final static String PHOTO_FOLDER_NAME = "photo";
    /**
     * 存放闪退日志目录名
     */
    private final static String CRASH_LOG_FOLDER_NAME = "crash";


    private FolderManager() {
    }

    /**
     * 获取app在sd卡上的主目录
     *
     * @return 成功则返回目录，失败则返回null
     */
    public static File getAppFolder() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File appFolder = new File(Environment.getExternalStorageDirectory(), APP_FOLDER_NAME);
            return createOnNotFound(appFolder);

        } else {
            return null;
        }
    }

    /**
     * 获取应用存放图片的目录
     *
     * @return 成功则返回目录名，失败则返回null
     */
    public static File getPhotoFolder() {
        File appFolder = getAppFolder();
        if (appFolder != null) {

            File photoFolder = new File(appFolder, PHOTO_FOLDER_NAME);
            return createOnNotFound(photoFolder);

        } else {
            return null;
        }
    }

    /**
     * 获取闪退日志存放目录
     *
     * @return
     */
    public static File getCrashLogFolder() {
        File appFolder = getAppFolder();
        if (appFolder != null) {

            File crashLogFolder = new File(appFolder, CRASH_LOG_FOLDER_NAME);
            return createOnNotFound(crashLogFolder);
        } else {
            return null;
        }
    }

    /**
     * 创建目录
     *
     * @param folder
     * @return 创建成功则返回目录，失败则返回null
     */
    private static File createOnNotFound(File folder) {
        if (folder == null) {
            return null;
        }

        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (folder.exists()) {
            return folder;
        } else {
            return null;
        }
    }
}
