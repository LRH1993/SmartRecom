package com.lvr.threerecom.ui.home.model;

import com.lvr.threerecom.bean.InformationBean;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by lvr on 2017/5/15.
 */

public interface InformationModel {
    Observable<List<InformationBean>> loadInformation();
    Observable<Boolean> updateInformation(String userid, String nickname, String age, String sex, String movie_preference, String music_preference);
    Observable<Boolean> updatePhoto(String userid,String path);
}
