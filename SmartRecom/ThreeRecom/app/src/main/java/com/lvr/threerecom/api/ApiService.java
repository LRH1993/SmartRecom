package com.lvr.threerecom.api;

import com.lvr.threerecom.bean.LogInAndSignUpBean;
import com.lvr.threerecom.bean.MovieInfo;
import com.lvr.threerecom.bean.RankingListDetail;
import com.lvr.threerecom.bean.RankingListItem;
import com.lvr.threerecom.bean.RatingResultBean;
import com.lvr.threerecom.bean.SongDetailInfo;
import com.lvr.threerecom.bean.SongListDetail;
import com.lvr.threerecom.bean.WrapperSongListInfo;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by lvr on 2017/4/22.
 */

public interface ApiService {
    public static final String MOVIE_BASE_URL_TYPE = "http://121.42.174.147:8080/RecommendMovie/";
    public static final String MOVIE_BASE_URL_HOT = "http://121.42.174.147:8080/RecommendMovie/";
    public static final String MUSIC_BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/";

    //获取具体种类的电影
    @GET("getMoviesByPage.action")
    Observable<Map<String, List<MovieInfo>>> getMoiveByType(
            @Query("pageSize") int pageSize, @Query("pageNow") int pageNow,
            @Query("type") String type, @Query("genres") String genres);

    //获取热门电影
    @GET("getHotMovies.action")
    Observable<Map<String, List<MovieInfo>>> getHotMoive();



    //获取全部歌单
    @GET("ting")
    @Headers("user-agent:Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
    Observable<WrapperSongListInfo> getSongListAll(@Query("format") String format,
                                                   @Query("from") String from,
                                                   @Query("method") String method,
                                                   @Query("page_size") int page_size,
                                                   @Query("page_no") int page_no);

    //获取全部榜单
    @GET("ting")
    @Headers("user-agent:Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
    Observable<RankingListItem> getRankingListAll(@Query("format") String format,
                                                  @Query("from") String from,
                                                  @Query("method") String method,
                                                  @Query("kflag") int kflag);

    //获取某个榜单中歌曲信息
    @GET("ting")
    @Headers("user-agent:Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
    Observable<RankingListDetail> getRankingListDetail(@Query("format") String format,
                                                       @Query("from") String from,
                                                       @Query("method") String method,
                                                       @Query("type") int type,
                                                       @Query("offset") int offset,
                                                       @Query("size") int size,
                                                       @Query("fields") String fields);

    //获取某个歌单中的信息
    @GET("ting")
    @Headers("user-agent:Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
    Observable<SongListDetail> getSongListDetail(@Query("format") String format,
                                                 @Query("from") String from,
                                                 @Query("method") String method,
                                                 @Query("listid") String listid);

    //获取某个歌曲的信息
    @GET("ting")
    @Headers("user-agent:Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
    Observable<SongDetailInfo> getSongDetail(@Query("from") String from,
                                             @Query("version") String version,
                                             @Query("format") String format,
                                             @Query("method") String method,
                                             @Query("songid") String songid);
    //注册请求 暂时为GET方法 后期转换为POST方法 还有加密
    @GET("register.action")
    Observable<LogInAndSignUpBean> getSignUpResult(
            @Query("username") String username, @Query("password") String password);
    //登录请求 暂时为GET方法 后期转换为POST方法 还有加密
    @GET("loginValidation.action")
    Observable<LogInAndSignUpBean> getLogInResult(
            @Query("username") String username, @Query("password") String password);
    //提交评分
    @GET("saveRating.action")
    Observable<RatingResultBean> getRatingResult(
            @Query("userid") String userid, @Query("movie_id") int movie_id,@Query("rating") int rating);
}
