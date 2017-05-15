package com.lvr.threerecom.ui.home.model;

import com.lvr.threerecom.bean.InformationBean;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by lvr on 2017/5/15.
 */

public interface InformationModel {
    Observable<List<InformationBean>> loadInformation();
}
