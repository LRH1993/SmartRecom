package com.lvr.threerecom.sensorcollection.utils;

/**
 * Created by caoliang on 2017/3/9.
 */

public class Constant {
    //定义传感器种类
    public final static String ACC = "acc";
    public final static String GYRO = "gyro";
    //上传数据
    public final static String URL = "http://123.207.185.21:8080/ihealth/uploadData/uploadCachedData.action";
    public final static String CLASSIFY = "http://123.207.185.21:8080/ihealth/classification/classify.action";
    //采样频率为50Hz
    public final static int samplingRate = 20000;
    //延时6s就是2个时间窗
    public final static long delayTime = 3;
    //samplingFrequencey为50HZ，时间窗为3s因此就有150个点
    public final static int windowLength = 150;
    public final static String tag = "com.ihealth";

    //activity
    public final static String WALK = "walk";

    //设置上传的间隔
    public final static int interval = 10;
}
