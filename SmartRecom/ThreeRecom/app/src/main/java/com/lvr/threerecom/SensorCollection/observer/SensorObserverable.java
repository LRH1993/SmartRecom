package com.lvr.threerecom.sensorcollection.observer;

import java.util.Vector;

/**
 * Created by caoliang on 2017/3/12.
 * 主要是为了观察者（mainactivity）提供传感器数据
 */

public class SensorObserverable {

    private Vector<SensorObserver> observers = new Vector<SensorObserver>();

    //注册所有的监听者
    public void registerAllObservers(SensorObserver observer) {
        for (SensorObserver item : observers) {
            if (observer.hashCode() == item.hashCode()) return;
        }
        observers.add(observer);
    }

    //注销所有的监听者
    public void unregisterAllObservers() {
        for (SensorObserver observer : observers) {
            observers.remove(observer);
        }
    }

    public void notifyAllObservers() {
        for (SensorObserver observer : observers) {
            observer.getSensorData(this);
        }
    }

}
