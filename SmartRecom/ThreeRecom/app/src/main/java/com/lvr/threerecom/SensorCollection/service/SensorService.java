package com.lvr.threerecom.sensorcollection.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.lvr.threerecom.R;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.sensorcollection.listener.SensorListener;
import com.lvr.threerecom.sensorcollection.observer.SensorObserver;
import com.lvr.threerecom.sensorcollection.observer.SensorObserverable;
import com.lvr.threerecom.sensorcollection.utils.Constant;
import com.lvr.threerecom.sensorcollection.utils.HttpUtil;
import com.lvr.threerecom.sensorcollection.utils.JSONUtil;
import com.lvr.threerecom.sensorcollection.utils.SensorUtil;
import com.lvr.threerecom.ui.home.NotificationShowActivity;
import com.lvr.threerecom.utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


/**
 * Created by lvr on 2017/4/19.
 */

public class SensorService extends Service implements SensorObserver {
    private HttpUtil httpUtil = new HttpUtil();
    private JSONUtil jsonUtil = new JSONUtil();
    private SensorUtil sensorUtil;
    private SensorManager sensorManager;
    private SensorListener listener;
    private  int state = AppConstantValue.SENSOR_STATE_ERROR;
    private static final int MSG_SET_ALIAS = 1001;
    private int[] state_collect = new int[3];
    private int count = 0;
    private static String[] mStrings = {"无意义", "坐着", "站着", "躺着", "走路", "骑车", "上楼梯", "下楼梯", "跑步"};
    private NotificationManager mNotificationManager;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;

            }
        }
    };
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    SPUtils.setSharedBooleanData(AppApplication.getAppContext(),"setAlias",true);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;

            }
        }
    };
    private String mUserid;


    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("开启服务");

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //注册后才开启采集数据的功能
        // TODO: 2017/5/20 记得写入SharedPreferences 
        if(SPUtils.getSharedBooleanData(AppApplication.getAppContext(),"isLogin")){
            mUserid = SPUtils.getSharedStringData(AppApplication.getAppContext(), "userid");
            setAlias();
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            listener = new SensorListener();
            sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), Constant.samplingRate);
            sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), Constant.samplingRate);
            listener.registerAllObservers(new SensorService());
            beginForeService(); 
        }
        
    }

    private void beginForeService() {
        //创建通知
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon_launcher)
                .setContentTitle("SmartRecom为你推荐")
                .setContentText("正在检测您当前的状态");
        //创建点跳转的Intent(这个跳转是跳转到通知详情页)
        Intent intent = new Intent(this, NotificationShowActivity.class);
        // 设置PendingIntent
        //获取通知服务
        mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, intent, 0));
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //构建通知
        Notification notification = mBuilder.build();
        //显示通知
        nm.notify(0, notification);
        //启动前台服务
        startForeground(0, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void getSensorData(SensorObserverable observerale) {
        ArrayList<Object> sensorData = new ArrayList<Object>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        //实时识别的模式，直接上传数据
        listener = (SensorListener) observerale;
        sensorData = listener.getSensorData();
        HashMap<String, String> data = new HashMap<String, String>();
        String json = jsonUtil.toJSON(sensorData);
        data.put("sensorData", json);
        data.put("userId", mUserid);

        uploadData(data);

    }

    public void uploadData(final HashMap<String, String> input) {
        System.out.println("上传数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = httpUtil.post(Constant.CLASSIFY, input);
                Log.i(Constant.tag, response);
//                System.out.println(Thread.currentThread().getName() + "发送数据");
                if ("-1".equals(response)) {
                    System.out.println("上传数据无效");
                } else {
                    System.out.println("返回给的实际数值：" + response);
                }
            }
        }).start();

    }




    //Jpush设置别名。
    // TODO: 2017/5/20 退出之前 把别名设置为空 反之继续接收推送 
    private void setAlias() {
        //// TODO: 2017/5/20 用户id 暂时写死为1  只能是字母，数字
        String alias = mUserid;
        if (TextUtils.isEmpty(alias)) {
            return;
        }

        // 调用 Handler 来异步设置别名
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    @Override
    public void onDestroy() {
        System.out.println("服务被杀死了");
        listener.unregisterAllObservers();
        listener.clearDataBuffer();
        super.onDestroy();
    }


}
