package com.lvr.threerecom.ui.music.view;

import com.lvr.threerecom.bean.RankingListDetail;
import com.lvr.threerecom.bean.SongDetailInfo;

/**
 * Created by lvr on 2017/5/12.
 */

public interface MusicRankingListDetailView {
    void returnRankingListDetail(RankingListDetail detail);
    void returnSongDetail(SongDetailInfo info);
}
