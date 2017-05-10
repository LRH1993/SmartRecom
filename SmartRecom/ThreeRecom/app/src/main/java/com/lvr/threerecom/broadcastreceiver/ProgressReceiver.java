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
        int position = (int) extras.get("progress");
        Message message = Message.obtain();
        message.arg1 = position;
        mHandler.sendMessage(message);

    }
}
