package com.lvr.threerecom.bean;

import java.util.List;

/**
 * Created by lvr on 2017/5/16.
 */

public class FavorListBean {
    private boolean isMovie;
    private List<String> mList;

    public List<String> getList() {
        return mList;
    }

    public void setList(List<String> list) {
        mList = list;
    }

    public boolean isMovie() {
        return isMovie;
    }

    public void setMovie(boolean movie) {
        isMovie = movie;
    }
}
