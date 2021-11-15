package com.easyshare.network;


import com.easyshare.base.BaseDataBean;
import com.easyshare.entity.OSSTokenEntity;
import com.easyshare.entity.UserInfoEntity;

import io.reactivex.Observable;
import io.reactivex.annotations.Nullable;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {

    /**
     * 获取 OSS Token
     */
    @POST(Constants.URL_OSS_GET_TOKEN)
    Call<BaseDataBean<OSSTokenEntity>> getOSSToken();

    /**
     * 登录 根据验证码
     */
    @POST(Constants.URL_USER_LOGIN_FOR_MAIL)
    Observable<BaseDataBean<UserInfoEntity>> loginForMail(@Query("email") String email, @Query("authCode") String authCode);

    /**
     * 登录 工具密码
     */
    @POST(Constants.URL_USER_LOGIN_FOR_PASSWORD)
    Observable<BaseDataBean<UserInfoEntity>> loginForPassword(@Query("email") String email, @Query("password") String password);

    /**
     * 登录 Token
     */
    @POST(Constants.URL_USER_LOGIN_FOR_TOKEN)
    Observable<BaseDataBean<UserInfoEntity>> loginForToken();

    /**
     * 发送验证码
     */
    @POST(Constants.URL_USER_SEND_AUTH_CODE)
    Observable<BaseDataBean<String>> sendAuthCode(@Query("email") String email);

    /**
     * 修改用户信息
     * 需要修改什么传什么，除了邮件和密码不能通过此接口改，其他都可以
     */
    @POST(Constants.URL_USER_CHANGE_INFO)
    Observable<BaseDataBean<String>> changeUserInfo(@Nullable @Query("name") String name, @Nullable @Query("avatarImage") String avatarImage,
                                                    @Nullable @Query("sex") String sex, @Nullable @Query("birthday") String birthday,
                                                    @Nullable @Query("sign") String sign, @Nullable @Query("phone") String phone);

}
