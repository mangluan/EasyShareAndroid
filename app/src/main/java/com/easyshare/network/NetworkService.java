package com.easyshare.network;


import com.easyshare.base.BaseDataBean;
import com.easyshare.database.Constants;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NetworkService {

    /**
     * 登录
     */
    @POST(Constants.URL_USER_LOGIN)
    Observable<BaseDataBean<Object>> login(@Query("email") String email, @Query("password") String password);


}
