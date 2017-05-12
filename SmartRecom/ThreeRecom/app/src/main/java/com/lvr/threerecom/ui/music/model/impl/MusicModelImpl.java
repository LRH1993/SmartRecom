package com.lvr.threerecom.ui.music.model.impl;

import com.lvr.threerecom.api.ApiService;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.bean.RankingListDetail;
import com.lvr.threerecom.bean.RankingListItem;
import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.bean.SongListDetail;
import com.lvr.threerecom.bean.WrapperSongListInfo;
import com.lvr.threerecom.client.RetrofitClient;
import com.lvr.threerecom.ui.music.model.MusicModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by lvr on 2017/4/26.
 */

public class MusicModelImpl implements MusicModel {
    @Override
    public Observable<List<WrapperSongListInfo.SongListInfo>> loadSongListAll(String format, String from, String method, int page_size, int page_no) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MUSIC_BASE_URL);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.getSongListAll(format, from, method, page_size, page_no).map(new Function<WrapperSongListInfo, List<WrapperSongListInfo.SongListInfo>>() {
            @Override
            public List<WrapperSongListInfo.SongListInfo> apply(WrapperSongListInfo info) throws Exception {
                return info.getContent();
            }
        }).compose(RetrofitClient.schedulersTransformer);//线程调度
    }

    @Override
    public Observable<List<RankingListItem.RangkingDetail>> loadRankingListAll(String format, String from, String method, int kflag) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MUSIC_BASE_URL);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.getRankingListAll(format, from, method, kflag).map(new Function<RankingListItem, List<RankingListItem.RangkingDetail>>() {
            @Override
            public List<RankingListItem.RangkingDetail> apply(RankingListItem item) throws Exception {
                System.out.println("排行榜个数：" + item.getContent().size());
                return item.getContent();
            }
        }).compose(RetrofitClient.schedulersTransformer);
    }

    @Override
    public Observable<List<SongListDetail.SongDetail>> loadSongListDetail(String format, String from, String method, String listid) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MUSIC_BASE_URL);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.getSongListDetail(format, from, method, listid).map(new Function<SongListDetail, List<SongListDetail.SongDetail>>() {
            @Override
            public List<SongListDetail.SongDetail> apply(SongListDetail detail) throws Exception {
                return detail.getContent();
            }
        }).compose(RetrofitClient.schedulersTransformer);
    }

    @Override
    public Observable<SongDetailInfo> loadSongDetail(String from,String version,String format, String method, String songid) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MUSIC_BASE_URL);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.getSongDetail(from,version,format,  method, songid).compose(RetrofitClient.schedulersTransformer);
    }

    @Override
    public Observable<RankingListDetail> loadRankListDetail(String format, String from, String method, int type, int offset, int size, String fields) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MUSIC_BASE_URL);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.getRankingListDetail(format,from,method,type,offset,size,fields).compose(RetrofitClient.schedulersTransformer);
    }

}
