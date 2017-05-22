package com.lvr.threerecom.ui.home.presenter;

/**
 * Created by lvr on 2017/5/22.
 */

public interface RecomMusicPresenter {
    void requestRecomMusic(String from,String version,String format, String method, int num);
    void requestSongDetail(String from,String version,String format, String method,String songid);
}
