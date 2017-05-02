package com.lvr.threerecom.ui.music.view;

import com.lvr.threerecom.bean.WrapperSongListInfo;

import java.util.List;

/**
 * Created by lvr on 2017/4/26.
 */

public interface MusicSongListView {
    void returnSongListInfos(List<WrapperSongListInfo.SongListInfo> songListInfos);
}
