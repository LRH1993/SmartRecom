package com.lvr.threerecom.sensorcollection.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by caoliang on 2017/3/6.
 * 传感器的工具类
 */

public class SensorUtil {

    private SensorManager sensorManager;

    public SensorManager getSensorManager(Context context) {
        //获取SensorManager对象
        return sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
    }

    //为传感器注册监听器
    public void registerSensor(SensorManager sensorManager, SensorEventListener lister, int type, int delayTime) {
        sensorManager.registerListener(lister, sensorManager.getDefaultSensor(type), delayTime);
    }


    //检查是否存在某种传感器
    public boolean isExist(int... types) {
        int flag = 0;
        List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (int type : types) {
            for (Sensor sensor : list) {
                if (sensor.getType() == type) {
                    //存在这种类型的传感器
                    flag++;
                    break;
                }
            }
        }
        if (flag == types.length) {
            return true;
        } else {
            return false;
        }
    }

}
