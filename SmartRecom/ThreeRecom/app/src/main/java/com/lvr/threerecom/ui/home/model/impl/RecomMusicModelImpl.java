package com.lvr.threerecom.ui.home.model.impl;

import com.lvr.threerecom.api.ApiService;
import com.lvr.threerecom.app.AppApplication;
import com.lvr.threerecom.bean.RecomMusicBean;
import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.bean.SongListDetail;
import com.lvr.threerecom.client.RetrofitClient;
import com.lvr.threerecom.ui.home.model.RecomMusicModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by lvr on 2017/5/22.
 */

public class RecomMusicModelImpl implements RecomMusicModel {
    @Override
    public Observable<List<SongListDetail.SongDetail>> loadRecomMusic(String from, String version, String format, String method, int num) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MUSIC_BASE_URL);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.getRecomMusicList(from,version,format,method,num).map(new Function<RecomMusicBean, List<SongListDetail.SongDetail>>() {
            @Override
            public List<SongListDetail.SongDetail> apply(RecomMusicBean bean) throws Exception {
                List<RecomMusicBean.ContentBean> content = bean.getContent();
                RecomMusicBean.ContentBean contentBean = content.get(0);
                List<RecomMusicBean.ContentBean.SongListBean> list = contentBean.getSong_list();
                List<SongListDetail.SongDetail> songDetailList = new ArrayList<SongListDetail.SongDetail>();
                for(int i=0;i<list.size();i++){
                    RecomMusicBean.ContentBean.SongListBean songListBean = list.get(i);
                    SongListDetail.SongDetail detail = new SongListDetail.SongDetail();
                    detail.setAlbum_id(songListBean.getAlbum_id());
                    detail.setAlbum_title(songListBean.getAlbum_title());
                    detail.setAuthor(songListBean.getAuthor());
                    detail.setSong_id(songListBean.getSong_id());
                    detail.setTitle(songListBean.getTitle());
                    songDetailList.add(detail);
                }
                return songDetailList;
            }
        }).compose(RetrofitClient.schedulersTransformer);
    }
    @Override
    public Observable<SongDetailInfo> loadSongDetail(String from, String version, String format, String method, String songid) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(AppApplication.getAppContext(), ApiService.MUSIC_BASE_URL);
        ApiService api = retrofitClient.create(ApiService.class);
        return api.getSongDetail(from,version,format,  method, songid).compose(RetrofitClient.schedulersTransformer);
    }
}
