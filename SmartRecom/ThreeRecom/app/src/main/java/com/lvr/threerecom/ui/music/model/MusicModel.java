package com.lvr.threerecom.ui.music.model;

import com.lvr.threerecom.bean.RankingListItem;
import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.bean.SongListDetail;
import com.lvr.threerecom.bean.WrapperSongListInfo;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by lvr on 2017/4/26.
 */

public interface MusicModel {
    Observable<List<WrapperSongListInfo.SongListInfo>> loadSongListAll(String format, String from, String method, int page_size, int page_no);
    Observable<List<RankingListItem.RangkingDetail>> loadRankingListAll(String format, String from, String method,int kflag);
    Observable<List<SongListDetail.SongDetail>> loadSongListDetail(String format, String from, String method, String listid);
    Observable<SongDetailInfo> loadSongDetail(String format, String from, String method,String songid,String ts,String e);
}
