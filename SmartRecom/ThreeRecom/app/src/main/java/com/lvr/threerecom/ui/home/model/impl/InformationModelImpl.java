package com.lvr.threerecom.ui.home.model.impl;

import com.lvr.threerecom.bean.InformationBean;
import com.lvr.threerecom.ui.home.model.InformationModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by lvr on 2017/5/15.
 */

public class InformationModelImpl implements InformationModel {
    @Override
    public Observable<List<InformationBean>> loadInformation() {
        return null;
    }
}
