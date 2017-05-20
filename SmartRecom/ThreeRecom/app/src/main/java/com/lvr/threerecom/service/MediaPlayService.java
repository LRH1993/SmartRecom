package com.lvr.threerecom.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.app.AppConstantValue;
import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.bean.SongUpdateInfo;
import com.lvr.threerecom.bean.UpdateViewPagerBean;
import com.lvr.threerecom.client.NetworkUtil;
import com.lvr.threerecom.ui.music.PlayingActivity;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;

/**
 * Created by lvr on 2017/5/2.
 */

public class MediaPlayService extends Service {
    //音乐列表 获取的是音乐详情 还需要进行网络请求 获取在线播放的url
    private List<SongDetailInfo> musicsList = new ArrayList<>();
    //播放音乐对象
    private SongDetailInfo mSongDetailInfo;
    //MediaPlayer
    private MediaPlayer mediaPlayer;
    //播放的时间
    private int currentTime = 0;
    //播放歌曲在列表中的索引
    private int position = 0;
    //当前歌曲的时长
    private int duration = 0;
    private AudioManager mAm;
    private boolean isPlayAll = false;
    private boolean flag = true;
    //创建单线程池
    private ExecutorService es = Executors.newSingleThreadExecutor();
    //播放状态 默认是列表循环
    private int play_mode = AppConstantValue.PLAYING_MODE_REPEAT_ALL;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = getMediaPlayer(AppApplication.getAppContext());
        mAm = (AudioManager) getSystemService(AUDIO_SERVICE);
    }

    private static MediaPlayer getMediaPlayer(Context context) {

        MediaPlayer mediaplayer = new MediaPlayer();

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return mediaplayer;
        }

        try {
            Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
            Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
            Class<?> iSubtitleControllerAnchor = Class.forName("android.media.SubtitleController$Anchor");
            Class<?> iSubtitleControllerListener = Class.forName("android.media.SubtitleController$Listener");

            Constructor constructor = cSubtitleController.getConstructor(new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});

            Object subtitleInstance = constructor.newInstance(context, null, null);

            Field f = cSubtitleController.getDeclaredField("mHandler");

            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler());
            } catch (IllegalAccessException e) {
                return mediaplayer;
            } finally {
                f.setAccessible(false);
            }

            Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor", cSubtitleController, iSubtitleControllerAnchor);

            setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
            //Log.e("", "subtitle is setted :p");
        } catch (Exception e) {
        }

        return mediaplayer;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return new MediaBinder();
    }


    public class MediaBinder extends Binder {
        public MediaPlayService getMediaPlayService() {

            return MediaPlayService.this;

        }
    }


    //创建线程 动态更新歌曲播放的进度
    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                int position = mediaPlayer.getCurrentPosition();
                //发送广播 通知PlayActivity界面更新UI
                Intent intent = new Intent();
                intent.putExtra("max",duration);
                intent.putExtra("progress", position);
                intent.setAction("com.lvr.progress");
                sendBroadcast(intent);
                SystemClock.sleep(500);
            }
        }
    };

    /**
     * 播放
     */
    public void playSong(int position, boolean isLocal) {
        this.position = position;
        SongDetailInfo info = musicsList.get(position);
        if (mSongDetailInfo == null || !info.getSonginfo().getSong_id().equals(mSongDetailInfo.getSonginfo().getSong_id())) {
            //不是同一首歌
            if (mSongDetailInfo != null) {
                mSongDetailInfo.setOnClick(false);
            }
            mSongDetailInfo = info;
        }

        if (!mSongDetailInfo.isOnClick()) {
            mSongDetailInfo.setOnClick(true);
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            play(info.getBitrate().getFile_link(), isLocal);
        } else {
            startPlayingActivity(mSongDetailInfo);

        }


    }

    /**
     * 开启PlayingActivity
     *
     * @param info 对应的音乐信息
     */
    private void startPlayingActivity(SongDetailInfo info) {
        Intent intent = new Intent(MediaPlayService.this, PlayingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //时长
        int duration = 0;
        //当前位置
        int currentTime = 0;
        //歌曲名称
        String title = info.getSonginfo().getTitle();
        //演唱者
        String author = info.getSonginfo().getAuthor();
        //封面
        String picUrl = info.getSonginfo().getPic_premium();
        boolean playing = isPlaying();
        intent.putExtra("isPlaying",playing);
        intent.putExtra("duration", duration);
        intent.putExtra("position",position);
        intent.putExtra("curPostion", currentTime);
        intent.putExtra("title", title);
        intent.putExtra("author", author);
        intent.putExtra("picUrl", picUrl);
        startActivity(intent);
    }
    /**
     * 给PlayingActivity传送SongUpdateInfo列表
     */
    public List<SongUpdateInfo> getPlayingList(){
        List<SongUpdateInfo> list = new ArrayList<>();
        for(int i=0;i<musicsList.size();i++){
            SongDetailInfo info = musicsList.get(i);
            SongUpdateInfo updateInfo = new SongUpdateInfo();
            updateInfo.setAuthor(info.getSonginfo().getAuthor());
            updateInfo.setTitle(info.getSonginfo().getTitle());
            updateInfo.setPicUrl(info.getSonginfo().getPic_premium());
            list.add(updateInfo);
        }
        return list;
    }


    /**
     * 音乐播放
     *
     * @param musicUrl
     */
    private void play(String musicUrl, boolean isLocal) {
        //给予无网络提示
        if (!NetworkUtil.isNetworkAvailable(AppApplication.getAppContext())) {
            if (!isLocal) {
                Toast.makeText(AppApplication.getAppContext(), "没有网络了哟，请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        }
        if (null == mediaPlayer) return;
        if (requestFocus()) {
            try {
                currentTime = 0;
                mediaPlayer.setDataSource(musicUrl);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnCompletionListener(completionListener);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        currentTime = mp.getCurrentPosition();
                        duration = mp.getDuration();
                        //发送广播 通知PlayActivity界面更新UI
                        es.execute(progressRunnable);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 播放全部
     *
     * @param musicUrl
     */
    private void playForPlayAll(String musicUrl, boolean isLocal) {
        //给予无网络提示
        if (!NetworkUtil.isNetworkAvailable(AppApplication.getAppContext())) {
            if (!isLocal) {
                Toast.makeText(AppApplication.getAppContext(), "没有网络了哟，请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        }
        if (null == mediaPlayer) return;
        if (requestFocus()) {
            try {
                currentTime = 0;
                mediaPlayer.setDataSource(musicUrl);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnCompletionListener(completionListener);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        currentTime = mp.getCurrentPosition();
                        duration = mp.getDuration();
                        //发送广播 通知PlayActivity界面更新UI
                        es.execute(progressRunnable);
                        startPlayingActivity(mSongDetailInfo);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 播放全部
     */
    public void playAll(boolean isLocal) {
        setPlayAll(true);
        SongDetailInfo info = musicsList.get(0);
        if (mSongDetailInfo == null || !info.getSonginfo().getSong_id().equals(mSongDetailInfo.getSonginfo().getSong_id())) {
            //不是同一首歌
            mSongDetailInfo = info;
        }

        if (isPlayAll) {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mSongDetailInfo.setOnClick(true);
            playForPlayAll(mSongDetailInfo.getBitrate().getFile_link(), isLocal);

        }
    }


    /**
     * 获取音乐焦点
     *
     * @return
     */
    private boolean requestFocus() {
        // Request audio focus for playback
        int result = mAm.requestAudioFocus(onAudioFocusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    //音频焦点监听处理
    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    //获取焦点 继续播放
                    resume();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    //永久失去 音频焦点
                    stop();
                    mAm.abandonAudioFocus(onAudioFocusChangeListener);//放弃音频焦点
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //暂时失去 音频焦点，并会很快再次获得。必须停止Audio的播放，但是因为可能会很快再次获得AudioFocus，这里可以不释放Media资源
                    pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //TODO 暂时失去 音频焦点 ，但是可以继续播放，不过要在降低音量。
                    break;

            }
        }
    };

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer player) {
            System.out.println("音乐播放完毕");
            if(play_mode==AppConstantValue.PLAYING_MODE_REPEAT_ALL){
                //列表循环
                mSongDetailInfo.setOnClick(false);
                EventBus.getDefault().post(new UpdateViewPagerBean());
            }
            if(play_mode ==AppConstantValue.PLAYING_MODE_REPEAT_CURRENT){
                //单曲循环
                mSongDetailInfo.setOnClick(false);
                playSong(position,false);
            }
            if(play_mode ==AppConstantValue.PLAYING_MODE_SHUFFLE_NORMAL){
                //随机播放
                mSongDetailInfo.setOnClick(false);
                int temp = new Random().nextInt(musicsList.size());
                position = temp;
                playSong(position,false);
            }


        }
    };

    /**
     * 设置播放模式
     * @param state 播放模式
     */
    public void setPlayMode(int state){
        play_mode = state;
    }

    /**
     * 获得当前的播放模式
     * @return 播放模式
     */
    public int getPlayMode(){
        return  play_mode;
    }



    /**
     * 向集合中添加待播放歌曲
     *
     * @param info
     */
    public void addMusicList(SongDetailInfo info) {
        musicsList.add(info);
    }

    /**
     * 清空播放集合
     */
    public void clearMusicList() {
        musicsList.clear();
    }


    /**
     * 设置是否播放全部
     *
     * @param isPlayAll
     */
    public void setPlayAll(boolean isPlayAll) {
        this.isPlayAll = isPlayAll;

    }

    /**
     * 获取播放全部状态
     */
    public boolean isPlayAll() {
        return isPlayAll;
    }


    /**
     * 暂停
     */
    public void pause() {
        if (null == mediaPlayer) return;
        if (mediaPlayer.isPlaying()) {
            currentTime = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();

        }
    }

    public void stop() {

    }

    /**
     * 音乐继续播放
     */
    public void resume() {
        if (null == mediaPlayer) return;
        mediaPlayer.start();
        //播放的同时，更新进度条
        if (currentTime > 0) {
            mediaPlayer.seekTo(currentTime);
        }

    }

    /**
     * 拖动Seekbar音乐跳转
     *
     * @param position
     */
    public void seekTo(int position) {
        if (null == mediaPlayer) return;
        mediaPlayer.seekTo(position);
//        if (position < duration) {
//            if (!mediaPlayer.isPlaying()) {
//                mediaPlayer.start();
//            }
//        }
    }


    /**
     * 播放下一首
     */
    public void next(boolean isLocal) {
        System.out.println("next方法的调用");
        currentTime = 0;
        if (position < 0) {
            position = 0;
        }
        if (musicsList.size() > 0) {
            position++;
            if (position < musicsList.size()) {//当前歌曲的索引小于歌曲集合的长度

            } else {
                //循环从第一首开始播放
                position = 0;
            }
            playSong(position, isLocal);
            updatePlayingUI(mSongDetailInfo);
        }
    }

    /**
     * 更新正在播放歌曲的UI
     *
     * @param info 歌曲信息
     */
    private void updatePlayingUI(SongDetailInfo info) {
        flag = true;
        final SongUpdateInfo updateInfo = new SongUpdateInfo();
        updateInfo.setAuthor(info.getSonginfo().getAuthor());
        updateInfo.setTitle(info.getSonginfo().getTitle());
        updateInfo.setPicUrl(info.getSonginfo().getPic_premium());
        updateInfo.setIndex(position);
        System.out.println("发送更新UI的消息，索引为："+position);
        EventBus.getDefault().post(updateInfo);




    }

    /**
     * 播放上一首
     */
    public void pre(boolean isLocal) {
        currentTime = 0;
        if (position < 0) {
            position = 0;
        }
        if (musicsList.size() > 0) {
            position--;
            if (position >= 0) {//大于等于0的情况
                playSong(position, isLocal);
            } else {
                position = 0;
                mSongDetailInfo.setOnClick(false);
                playSong(position, isLocal);
            }
        }
        updatePlayingUI(mSongDetailInfo);
    }


    /**
     * 音乐是否播放
     *
     * @return
     */
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        es.shutdownNow();
        mAm.abandonAudioFocus(onAudioFocusChangeListener);
    }


}
