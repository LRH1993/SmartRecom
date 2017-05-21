package com.lvr.threerecom.ui.home.view;

import com.lvr.threerecom.bean.InformationBean;

import java.util.List;

/**
 * Created by lvr on 2017/5/15.
 */

public interface InformationView {
    void returnInformation(List<InformationBean> informationBeanList);
    void returnUpdateInformationResult(boolean result);
    void returnUpdatePhotoResult(boolean result);
}
