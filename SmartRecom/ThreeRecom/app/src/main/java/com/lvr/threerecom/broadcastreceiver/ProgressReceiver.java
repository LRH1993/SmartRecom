package com.lvr.threerecom.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by lvr on 2017/5/10.
 */

public class ProgressReceiver extends BroadcastReceiver {
    private Handler mHandler;

    public ProgressReceiver() {
    }

    public ProgressReceiver(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mHandler==null){
            return;
        }
        Bundle extras = intent.getExtras();
        Message message = Message.obtain();
        if(extras.get("progress")!=null){
            int position = (int) extras.get("progress");
            message.arg1 = position;
        }
        if(extras.get("max")!=null){
            int max = (int) extras.get("max");
            message.arg2 = max;
        }
        mHandler.sendMessage(message);


    }
}
