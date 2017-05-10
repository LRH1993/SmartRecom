package com.lvr.threerecom.ui.music.fragment;

import android.os.Bundle;
import android.widget.ImageView;

import com.lvr.threerecom.R;
import com.lvr.threerecom.base.BaseFragment;
import com.lvr.threerecom.utils.ImageLoaderUtils;

import butterknife.BindView;

/**
 * Created by lvr on 2017/5/2.
 */

public class RoundFragment extends BaseFragment {
    @BindView(R.id.civ_photo)
    ImageView mCivPhoto;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_round;
    }

    @Override
    protected void initView() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            String picUrl = bundle.getString("picUrl");
            ImageLoaderUtils.displayRound(getActivity(),mCivPhoto,picUrl);
        }
    }

}
