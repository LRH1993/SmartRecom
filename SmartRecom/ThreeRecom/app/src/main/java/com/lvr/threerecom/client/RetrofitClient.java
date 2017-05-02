package com.lvr.threerecom.client;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * RetrofitClient
 */
public class RetrofitClient {

    private static final int DEFAULT_TIMEOUT = 20;
    private BaseApiService apiService;
    private static OkHttpClient okHttpClient;
    public static String baseUrl = BaseApiService.Base_URL;
    private static Context mContext;
    private static RetrofitClient sNewInstance;
    private static ErrorTransformer transformer = new ErrorTransformer();

    private static Retrofit retrofit;
    private Cache cache = null;
    private File httpCacheDirectory;


    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(baseUrl);
    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder()
                    .addNetworkInterceptor(
                            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    private static Class<? extends Class> aClass;

    //默认baseUrl的单例
    private static class SingletonHolder {
        private static RetrofitClient INSTANCE = new RetrofitClient(
                mContext);
    }
    public static RetrofitClient getInstance(Context context) {
        if (context != null) {
            mContext = context;
        }
        return SingletonHolder.INSTANCE;
    }

    //自己提供url
    public static RetrofitClient getInstance(Context context, String url) {
        if (context != null) {
            mContext = context;
        }

        return new RetrofitClient(context, url);
    }

    //不仅有url还有headers
    public static RetrofitClient getInstance(Context context, String url, Map<String, String> headers) {
        if (context != null) {
            mContext = context;
        }
        return new RetrofitClient(context, url, headers);
    }

    private RetrofitClient() {

    }

    private RetrofitClient(Context context) {

        this(context, baseUrl, null);
    }

    private RetrofitClient(Context context, String url) {

        this(context, url, null);
    }

    private RetrofitClient(Context context, String url, Map<String, String> headers) {
        //url为空，则默认使用baseUrl
        if (TextUtils.isEmpty(url)) {
            url = baseUrl;
        }
        //缓存地址
        if (httpCacheDirectory == null) {
            httpCacheDirectory = new File(mContext.getCacheDir(), "app_cache");
        }

        try {
            if (cache == null) {
                cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
            }
        } catch (Exception e) {
            Log.e("OKHttp", "Could not create http cache", e);
        }
        //okhttp创建了
        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .cache(cache)
                .addInterceptor(new BaseInterceptor(headers))
                .addInterceptor(new CaheInterceptor(context))
                .addNetworkInterceptor(new CaheInterceptor(context))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        //retrofit创建了
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .build();

    }


    public  void get(String url, Map parameters, Observer<?> observer) {
        apiService.executeGet(url, parameters)
                .compose(schedulersTransformer)
                .compose(transformer)
                .subscribe(observer);
    }

    public  void post(String url, Map<String, String> parameters, Observer<?> observer) {
        apiService.executePost(url, parameters)
                .compose(schedulersTransformer)
                .compose(transformer)
                .subscribe(observer);
    }

    public  void json(String url, RequestBody jsonStr, Observer<ResponseBody> observer) {

        apiService.json(url, jsonStr)
                .compose(schedulersTransformer)
                .compose(transformer)
                .subscribe(observer);
    }

    public void upload(String url, RequestBody requestBody, Observer<ResponseBody> observer) {
        apiService.upLoadFile(url, requestBody)
                .compose(schedulersTransformer)
                .compose(transformer)
                .subscribe(observer);
    }
    //处理线程调度的变换
    public static ObservableTransformer schedulersTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(Observable upstream) {
            return ((Observable) upstream).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };
    //处理错误的变换
    private static class ErrorTransformer<T> implements ObservableTransformer{

        @Override
        public ObservableSource apply(Observable upstream) {
            //onErrorResumeNext当发生错误的时候，由另外一个Observable来代替当前的Observable并继续发射数据
            return (Observable<T>) upstream.map(new HandleFuc<T>()).onErrorResumeNext(new HttpResponseFunc<T>());
        }
    }


    public static class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(Throwable throwable) throws Exception {
            return Observable.error(ExceptionHandle.handleException(throwable));
        }
    }

    public static class HandleFuc<T> implements Function<BaseResponse<T>, T> {
        @Override
        public T apply(BaseResponse<T> response) throws Exception {
            //response中code码不会0 出现错误
            if (!response.isOk())
                throw new RuntimeException(response.getCode() + "" + response.getMsg() != null ? response.getMsg() : "");
            return response.getData();
        }
    }
    /**
     * 创建默认url的api类
     * @return ApiManager
     */
    public RetrofitClient createBaseApi() {
        apiService = create(BaseApiService.class);
        return this;
    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public  <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

}
