package com.lvr.threerecom.ui.home.presenter;

/**
 * Created by lvr on 2017/5/15.
 */

public interface InformationPresenter {
    void requestInformation();

    void requestUpdateInformation(String userid, String nickname, String age, String sex, String movie_preference, String music_preference);

    void requestUpdatePhoto(String userid,String path);
}
