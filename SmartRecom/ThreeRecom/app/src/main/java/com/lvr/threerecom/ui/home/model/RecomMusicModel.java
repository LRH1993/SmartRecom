package com.lvr.threerecom.ui.home.model;

import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.bean.SongListDetail;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by lvr on 2017/5/22.
 */

public interface RecomMusicModel {
    Observable<List<SongListDetail.SongDetail>> loadRecomMusic(String from,String version,String format, String method, int num);
    Observable<SongDetailInfo> loadSongDetail(String from, String version, String format, String method, String songid);
}
