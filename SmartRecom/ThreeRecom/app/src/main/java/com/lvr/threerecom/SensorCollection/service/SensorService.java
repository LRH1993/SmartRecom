package com.lvr.threerecom.sensorcollection.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.lvr.threerecom.R;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.sensorcollection.listener.SensorListener;
import com.lvr.threerecom.sensorcollection.observer.SensorObserver;
import com.lvr.threerecom.sensorcollection.observer.SensorObserverable;
import com.lvr.threerecom.sensorcollection.utils.Constant;
import com.lvr.threerecom.sensorcollection.utils.HttpUtil;
import com.lvr.threerecom.sensorcollection.utils.JSONUtil;
import com.lvr.threerecom.sensorcollection.utils.SensorUtil;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by lvr on 2017/4/19.
 */

public class SensorService extends Service implements SensorObserver {
    private HttpUtil httpUtil = new HttpUtil();
    private JSONUtil jsonUtil = new JSONUtil();
    private SensorUtil sensorUtil;
    private SensorManager sensorManager;
    private SensorListener listener;
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private int state = AppConstantValue.SENSOR_STATE_ERROR;
    private int[] state_collect = new int[3];
    private int count = 0;
    private static String[] mStrings = {"无意义", "坐着", "站着", "躺着", "走路", "骑车", "上楼梯", "下楼梯", "跑步"};
    private NotificationManager mNotificationManager;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String hint = "您当前的状态为：" + mStrings[msg.what + 1];
            System.out.println(hint);
            Toast.makeText(getApplicationContext(), hint, Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("开启服务");
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.setReferenceCounted(true);
        wl.acquire();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        listener = new SensorListener();
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), Constant.samplingRate);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), Constant.samplingRate);
        listener.registerAllObservers(new SensorService());


        showNotification("SmartRecom为你推荐", "正在检测您当前的状态");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
//        uploadData(data);

    }

    public void uploadData(final HashMap<String, String> input) {
        System.out.println("上传数据");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = httpUtil.post(Constant.CLASSIFY, input);
                Log.i(Constant.tag, response);
                System.out.println(Thread.currentThread().getName() + "发送数据");
                if ("-1".equals(response)) {
                    System.out.println("上传数据无效");
                } else {
                    System.out.println("返回给的实际数值：" + response);
                    state_collect[count] = Integer.parseInt(response);
                    count++;
                    if (count == 3) {
                        //判断当前数组中数是否一致
                        boolean steady = isSteady(state_collect);
                        System.out.println("数据是否一致，是否稳定：" + steady);
                        if (steady) {
                            int cur_state = state_collect[0];
                            if (cur_state != state) {
                                //状态改变 发送通知
                                System.out.println("发送通知栏");
                                Message message = Message.obtain();
                                message.what = cur_state;
                                mHandler.sendMessage(message);
                                state = cur_state;

                            }
                        }
                        count = 0;
                    }

                }


            }
        }).start();
    }

    private boolean isSteady(int[] collect) {
        if (collect[0] == collect[1] && collect[1] == collect[2]) {
            return true;
        }
        return false;
    }


    @Override
    public void onDestroy() {
        System.out.println("服务被杀死了");
        listener.unregisterAllObservers();
        listener.clearDataBuffer();
        wl.release();
        super.onDestroy();
    }

    private void showNotification(String title, String text) {
        System.out.println("创建通知");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.icon_launcher);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        Notification notification = builder.build();
        //显示通知
        mNotificationManager.notify(0, notification);

    }

}
