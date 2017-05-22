package com.lvr.threerecom.ui.home.view;

import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.bean.SongListDetail;

import java.util.List;

/**
 * Created by lvr on 2017/5/22.
 */

public interface RecomMusicView {
    void returnRecomMusicList(List<SongListDetail.SongDetail> songDetailList);
    void returnSongDetail(SongDetailInfo info);
}
