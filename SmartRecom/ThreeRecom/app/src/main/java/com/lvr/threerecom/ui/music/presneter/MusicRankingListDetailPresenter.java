package com.lvr.threerecom.ui.music.presneter;

/**
 * Created by lvr on 2017/5/12.
 */

public interface MusicRankingListDetailPresenter {
    void requestRankListDetail(String format, String from, String method, int type, int offset, int size, String fields);
    void requestSongDetail(String from,String version,String format, String method,String songid);
}
