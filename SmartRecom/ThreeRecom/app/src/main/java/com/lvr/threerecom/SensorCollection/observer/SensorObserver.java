package com.lvr.threerecom.sensorcollection.observer;

/**
 * Created by caoliang on 2017/3/12.
 * 为了从传感器那里获取数据
 */

public interface SensorObserver {
    //从被观察者那里获取数据
    void getSensorData(SensorObserverable observerale);
}
