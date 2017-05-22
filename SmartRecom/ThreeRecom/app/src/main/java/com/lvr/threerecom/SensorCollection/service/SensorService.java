package com.lvr.threerecom.sensorcollection.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lvr.threerecom.R;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.sensorcollection.utils.Constant;
import com.lvr.threerecom.sensorcollection.utils.HttpUtil;
import com.lvr.threerecom.sensorcollection.utils.JSONUtil;
import com.lvr.threerecom.ui.MainActivity;
import com.lvr.threerecom.ui.home.RecomMovieActivity;
import com.lvr.threerecom.ui.home.RecomMusicActivity;
import com.lvr.threerecom.utils.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by lvr on 2017/5/22.
 */

public class SensorService extends Service implements SensorEventListener {
    private HashMap<String, Object> accData = new HashMap<String, Object>();
    private ArrayList<Float> acc_x_Data = new ArrayList<Float>();
    private ArrayList<Float> acc_y_Data = new ArrayList<Float>();
    private ArrayList<Float> acc_z_Data = new ArrayList<Float>();
    private HashMap<String, Object> gyroData = new HashMap<String, Object>();
    private ArrayList<Float> gyro_x_Data = new ArrayList<Float>();
    private ArrayList<Float> gyro_y_Data = new ArrayList<Float>();
    private ArrayList<Float> gyro_z_Data = new ArrayList<Float>();
    private ArrayList<Object> sensorData = new ArrayList<Object>();
    private boolean accRecorded = false;
    private boolean gyroRecorded = false;
    private boolean isTrans = false;
    private int index = 0;


    private HttpUtil httpUtil = new HttpUtil();
    private JSONUtil jsonUtil = new JSONUtil();
    private SensorManager sensorManager;
    private int state = AppConstantValue.SENSOR_STATE_ERROR;
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_STATE_CHANGE =404;
    private static String[] mStrings = {"无意义", "坐着", "站着", "躺着", "走路", "骑车", "上楼梯", "下楼梯", "跑步"};
    private int[] sensor_collect = new int[2];
    private int count;
    //用户id
    private String mUserid;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:{
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;
                }
                case MSG_STATE_CHANGE:{
                    //状态改变了
                    String curState;
                    if(state==1||state==0){
                        curState ="坐着/站着";
                    }else{
                        curState = mStrings[state+1];
                    }
                    Toast.makeText(AppApplication.getAppContext(),"当前状态是："+curState,Toast.LENGTH_SHORT).show();
                    Notification.Builder mBuilder = new Notification.Builder(SensorService.this)
                            .setSmallIcon(R.drawable.icon_launcher)
                            .setContentTitle("SmartRecom为你推荐")
                            .setContentText("您当前的状态为："+curState);
                    //创建点跳转的Intent(这个跳转是跳转到通知详情页)
                    Intent intent = null;
                    if(curState.equals("躺着")){
                        intent = new Intent(SensorService.this, RecomMovieActivity.class);
                    }else if(curState.equals("跑步")){
                        intent = new Intent(SensorService.this, RecomMusicActivity.class);
                    }else{
                        intent = new Intent(SensorService.this, MainActivity.class);
                    }

                    // 设置PendingIntent
                    //获取通知服务
                    mBuilder.setContentIntent(PendingIntent.getActivity(SensorService.this, 0, intent, 0));
                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    //构建通知
                    Notification notification = mBuilder.build();
                    //显示通知
                    nm.notify(0, notification);
                    break;
                }

            }
        }
    };
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    SPUtils.setSharedBooleanData(AppApplication.getAppContext(), "setAlias", true);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;

            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mUserid = "0";
        System.out.println("开启服务");

        //登录后才开启采集数据的功能
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), Constant.samplingRate);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), Constant.samplingRate);
        beginForeService();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                handleAccData(event);
                break;
            case Sensor.TYPE_GYROSCOPE:
                handleGyroData(event);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void handleAccData(SensorEvent event) {
        if (acc_x_Data.size() < Constant.windowLength) {
            //将数据放到相应的arrayList
            acc_x_Data.add(event.values[0]);
            acc_y_Data.add(event.values[1]);
            acc_z_Data.add(event.values[2]);
        }
        if (acc_x_Data.size() == Constant.windowLength) {
            accData.put("category", Constant.ACC);
            accData.put("x", acc_x_Data);
            accData.put("y", acc_y_Data);
            accData.put("z", acc_z_Data);
            accRecorded = true;
            sensorData.add(accData);
            if (checkData()) {
                //一个时间窗的数据采集完成
//                System.out.println("一个时间窗的数据采集完成");
                //为减少误差采集数据是前面的数据应该丢弃
                if (index == Constant.delayTime) {
                    isTrans = true;
//                    System.out.println("为减小误差延时结束，正式开始采集数据");
                }
                if (isTrans) {
                    //进入了发送模式
                    sendSensorData();
                    index = 0;
                }
                if (!isTrans) {
                    index++;
                }
                //清空缓存区数据为下一个时间窗做好准备
                clearDataBuffer();
            }
        }
    }

    private void sendSensorData() {
        HashMap<String, String> data = new HashMap<String, String>();
        String json = jsonUtil.toJSON(sensorData);
        data.put("sensorData", json);
        // TODO: 2017/5/21 跨进程获取用户id

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
                    synchronized (this){

                        sensor_collect[count]=Integer.parseInt(response);
                        count++;
                        if (count == 2) {
                            count=0;
                            if (isSteady()) {
                                //当前状态
                                int cur_state = sensor_collect[0];
                                if (cur_state != state) {
                                    //状态变更了
                                    state = cur_state;
                                    Message message = Message.obtain();
                                    message.what = MSG_STATE_CHANGE;
                                    mHandler.sendMessage(message);
                                }
                            }
                        }
                    }

                }
            }
        }).start();

    }

    /**
     * 判断3次返回的状态码是否一致
     * @return
     */
    private boolean isSteady() {
        if(sensor_collect[0]==sensor_collect[1]){
            return true;
        }else{
            return false;
        }
    }

    public void handleGyroData(SensorEvent event) {
        //说明sensorData中还没有gyrodata
        if (gyro_x_Data.size() < Constant.windowLength) {
            //将数据放到相应的arrayList
            gyro_x_Data.add(event.values[0]);
            gyro_y_Data.add(event.values[1]);
            gyro_z_Data.add(event.values[2]);
        }
        if (gyro_x_Data.size() == Constant.windowLength) {
            //这里将数据进行封装
            gyroData.put("category", Constant.GYRO);
            gyroData.put("x", gyro_x_Data);
            gyroData.put("y", gyro_y_Data);
            gyroData.put("z", gyro_z_Data);
            sensorData.add(gyroData);
            gyroRecorded = true;
            if (checkData()) {
//                System.out.println("一个时间窗的数据采集完成");
                if (index == Constant.delayTime) {
                    isTrans = true;
//                    System.out.println("为减小误差延时结束，正式开始采集数据");
                }
                if (isTrans) {
                    //进入了发送模式
                    sendSensorData();
                    index = 0;
                }
                if (!isTrans) {
                    index++;
                }
                clearDataBuffer();
            }
        }
    }

    //检查是否在一个时间窗内同时有acc和gyro传感器的数据
    public boolean checkData() {
        return accRecorded && gyroRecorded;
    }

    public void clearDataBuffer() {
        acc_x_Data.clear();
        acc_y_Data.clear();
        acc_z_Data.clear();
        gyro_x_Data.clear();
        gyro_y_Data.clear();
        gyro_z_Data.clear();
        sensorData.clear();
        accRecorded = false;
        gyroRecorded = false;

    }

    private void beginForeService() {
        //创建通知
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.icon_launcher)
                .setContentTitle("SmartRecom为你推荐")
                .setContentText("正在检测您当前的状态");
        //创建点跳转的Intent(这个跳转是跳转到通知详情页)
        Intent intent = new Intent(this, RecomMovieActivity.class);
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
    //Jpush设置别名。
    // TODO: 2017/5/20 退出之前 把别名设置为空 反之继续接收推送
    private void setAlias() {
        //// TODO: 2017/5/20 用户id 暂时写死为1  只能是字母，数字
        String alias = mUserid+"";
        if (TextUtils.isEmpty(alias)) {
            return;
        }

        // 调用 Handler 来异步设置别名
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}

