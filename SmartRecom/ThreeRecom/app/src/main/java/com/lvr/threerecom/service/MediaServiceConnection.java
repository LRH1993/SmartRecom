package com.lvr.threerecom.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.ImageView;

import com.lvr.threerecom.R;

/**
 * Created by lvr on 2017/5/7.
 */

public class MediaServiceConnection implements ServiceConnection {
    private  MediaPlayService.MediaBinder mMediaBinder;
    public   MediaPlayService mMediaPlayService;
    private ImageView mImageView;

    public MediaServiceConnection(ImageView mImageView) {
        this.mImageView = mImageView;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mMediaBinder = (MediaPlayService.MediaBinder) service;
        mMediaPlayService = mMediaBinder.getMediaPlayService();
        if(mMediaPlayService.isPlaying()){
            //正在播放
            mImageView.setImageResource(R.drawable.play_rdi_btn_pause);
        }else {
            //正在暂停
            mImageView.setImageResource(R.drawable.play_rdi_btn_play);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }



}
