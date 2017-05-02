package com.lvr.threerecom.ui.music.view;


import com.lvr.threerecom.bean.RankingListItem;

import java.util.List;

/**
 * Created by lvr on 2017/4/27.
 */

public interface MusicRankingListView {
    void returnRankingListInfos(List<RankingListItem.RangkingDetail> rangkingListInfo);
}
