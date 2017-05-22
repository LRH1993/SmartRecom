package com.lvr.threerecom.app;

/**
 * Created by lvr on 2017/4/17.
 */

public class AppConstantValue {
    public  static  final int PAGE_SIZE = 10;
    public  static  final String TYPE_GENRES = "byGenres";
    public  static  final String TYPE_TIME = "byReleaseTime";
    public  static  final String TYPE_RATING = "byRating";
    public static final int REQUEST_SIGNUP = 00;
    public static final  String TRANSITION_IMAGE_MOVIE = "电影图片";
    public static final  String MUSIC_URL_FROM = "webapp_music";
    public static final String MUSIC_URL_FORMAT = "json";
    public static final String MUSIC_URL_METHOD_GEDAN ="baidu.ting.diy.gedan";
    public static final String MUSIC_URL_METHOD_RANKINGLIST ="baidu.ting.billboard.billCategory";
    public static final String MUSIC_URL_METHOD_SONGLIST_DETAIL ="baidu.ting.diy.gedanInfo";
    public static final String MUSIC_URL_METHOD_SONG_DETAIL ="baidu.ting.song.play";
    public static final String MUSIC_URL_METHOD_RANKING_DETAIL ="baidu.ting.billboard.billList";
    public static final String MUSIC_URL_METHOD_RECOM ="baidu.ting.song.getEditorRecommend";
    public static final  int MUSIC_URL_RANKINGLIST_FLAG = 1;
    public static final String MUSIC_URL_FROM_2 = "android";
    public static final String MUSIC_URL_VERSION = "5.6.5.6";
    public static final int SENSOR_STATE_ERROR =-1;
    public static final int SENSOR_STATE_SIT =0;
    public static final int SENSOR_STATE_STAND =1;
    public static final int SENSOR_STATE_LAY =2;
    public static final int SENSOR_STATE_WALK =3;
    public static final int SENSOR_STATE_RIDE =4;
    public static final int SENSOR_STATE_UPSTAIRS =5;
    public static final int SENSOR_STATE_DOWNSTAIRS =6;
    public static final int SENSOR_STATE_RUN =7;
    //列表循环
    public static final int PLAYING_MODE_REPEAT_ALL = 0;
    //随机播放
    public static final int PLAYING_MODE_SHUFFLE_NORMAL = 1;
    //单曲循环
    public static final int PLAYING_MODE_REPEAT_CURRENT = 2;

}
