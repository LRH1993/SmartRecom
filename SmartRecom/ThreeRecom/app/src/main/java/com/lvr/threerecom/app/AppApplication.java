package com.lvr.threerecom.app;


import com.lvr.threerecom.base.BaseApplication;

import cn.jpush.android.api.JPushInterface;


/**
 * APPLICATION
 */
public class AppApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this); // 初始化 JPush
    }
}
