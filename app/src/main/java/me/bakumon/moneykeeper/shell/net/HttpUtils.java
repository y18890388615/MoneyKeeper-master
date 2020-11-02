package me.bakumon.moneykeeper.shell.net;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

public class HttpUtils {

    //解析固定参数
    private static HttpUtils httpUtils;

    public static HttpUtils getInstance() {
        if (httpUtils == null) {
            synchronized (HttpUtils.class) {
                if (httpUtils == null) {
                    httpUtils = new HttpUtils();
                }
            }
        }
        return httpUtils;
    }

    public void init(Application context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //配置LOG
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");

        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);
        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        //配置cookie
        //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(context)));
        //使用数据库保持cookie，如果cookie不过期，则一直有效
//        missionBuilder.cookieJar(new CookieJarImpl(new DBCookieStore(context)));
        //使用内存保持cookie，app退出后，cookie消失
//        missionBuilder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));

        //全局公共参数
        OkGo.getInstance().init(context)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
//                .addCommonHeaders(headers)                      //全局公共头
                .setRetryCount(3);                 //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
    }


    public void postJson(Context context, String url, HashMap params, AbsCallback callback) {
        OkGo.<String>post(url)
                .tag(context)
                .upJson(new Gson().toJson(params))
                .execute(callback);
    }

    public void post(Context context, String url, AbsCallback callback) {
        OkGo.<String>post(url)
                .tag(context)
                .execute(callback);
    }

    public void post(Context context, String url, HashMap params, AbsCallback callback) {
        OkGo.<String>post(url)
                .tag(context)
                .params(params)
                .execute(callback);
    }


    public void get(Context context, String url, HashMap params, AbsCallback callback) {
        OkGo.<String>get(url)
                .tag(context)
                .params(params)
                .execute(callback);
    }

    public void get(Context context, String url, AbsCallback callback) {
        OkGo.<String>get(url)
                .tag(context)
                .execute(callback);
    }



    public void postUpFile(Context context, String url, HashMap params, String fileKey, List<File> fileList, AbsCallback callback) {
        OkGo.<String>post(url)
                .tag(context)
                .params(params)
                .addFileParams(fileKey, fileList)
                .execute(callback);
    }


}
