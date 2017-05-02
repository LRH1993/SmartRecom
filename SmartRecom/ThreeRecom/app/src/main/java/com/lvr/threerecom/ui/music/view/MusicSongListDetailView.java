package com.lvr.threerecom.ui.music.view;

import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.bean.SongListDetail;

import java.util.List;

/**
 * Created by lvr on 2017/4/27.
 */

public interface MusicSongListDetailView {
    void returnSongListDetailInfos(List<SongListDetail.SongDetail> songDetails);
    void returnSongDetail(SongDetailInfo info);
}
