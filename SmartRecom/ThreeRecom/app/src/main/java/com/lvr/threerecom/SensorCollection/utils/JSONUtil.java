package com.lvr.threerecom.sensorcollection.utils;

import com.alibaba.fastjson.JSON;

/**
 * Created by caoliang on 2017/3/18.
 */

public class JSONUtil {
    //转换成json字符串
    public String toJSON(Object object){
        return JSON.toJSONString(object);
    }
}
