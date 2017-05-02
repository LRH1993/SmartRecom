package com.lvr.threerecom.utils;

import android.content.Context;
import android.widget.ImageView;

import com.youth.banner.loader.ImageLoader;

/**
 * Created by lvr on 2017/4/24.
 */

public class GlideLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        ImageLoaderUtils.display(context,imageView,(String)path);
    }
}
