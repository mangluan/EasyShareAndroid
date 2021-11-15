package com.easyshare.network;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.easyshare.utils.SharedPreferenceUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private Retrofit mRetrofit;
    private RetrofitService mService;

    private static volatile RetrofitFactory sInstance;

    private RetrofitFactory() {
    }

    private RetrofitFactory(Context context) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // 通过拦截器的方式添加header
        httpClient.addInterceptor(chain -> {
            Request.Builder request = chain.request().newBuilder();
            // 添加 token 、 渠道
            request.addHeader("token", SharedPreferenceUtils.getString(context, Constants.TOKEN));
            request.addHeader("source", Build.BRAND + " " + Build.MODEL);
            Response response = chain.proceed(request.build());
            String token = response.header("token");
            if (!TextUtils.isEmpty(token))  // 保存token
                SharedPreferenceUtils.putString(context, Constants.TOKEN, token);
            return response;
        });
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build();
        mService = mRetrofit.create(RetrofitService.class);
    }

    public static RetrofitService getsInstance(Context context) {
        if (sInstance == null) {
            synchronized (RetrofitFactory.class) {
                if (sInstance == null) {
                    sInstance = new RetrofitFactory(context.getApplicationContext());
                }
            }
        }
        return sInstance.getService();
    }

//    public Retrofit getRetrofit() {
//        return mRetrofit;
//    }

    public RetrofitService getService() {
        return mService;
    }

}
