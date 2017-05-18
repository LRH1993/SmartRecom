package com.lvr.threerecom.sensorcollection.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.lvr.threerecom.sensorcollection.observer.SensorObserverable;
import com.lvr.threerecom.sensorcollection.utils.Constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by caoliang on 2017/3/18.
 */

public class SensorListener extends SensorObserverable implements SensorEventListener, Serializable {
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
                    notifyAllObservers();
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


    public void handleGyroData(SensorEvent event) {
        //说明sensorData中还没有gyrodata
        if (gyro_x_Data.size() < Constant.windowLength) {
            //将数据放到相应的arrayList
            gyro_x_Data.add(event.values[0]);
            gyro_y_Data.add(event.values[1]);
            gyro_z_Data.add(event.values[2]);
        }
        if (gyro_x_Data.size()== Constant.windowLength) {
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
                    notifyAllObservers();
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

    public ArrayList<Object> getSensorData() {
        return sensorData;
    }

    public void setSensorData() {
        this.sensorData = sensorData;
    }

    public void setAccRecorded(boolean accRecorded) {
        this.accRecorded = accRecorded;
    }

    public void setGyroRecorded(boolean gyroRecorded) {
        this.gyroRecorded = gyroRecorded;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean isTrans() {
        return isTrans;
    }

    public void setTrans(boolean trans) {
        isTrans = trans;
    }
}
