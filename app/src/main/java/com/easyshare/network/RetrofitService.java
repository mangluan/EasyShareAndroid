package com.easyshare.network;


import android.util.Pair;

import com.easyshare.base.BaseDataBean;
import com.easyshare.entity.ClassificationEntity;
import com.easyshare.entity.LoginRecordEntity;
import com.easyshare.entity.OSSTokenEntity;
import com.easyshare.entity.PhotoAlbumEntity;
import com.easyshare.entity.UserInfoEntity;

import java.util.List;
import java.util.Map;

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

    /**
     * 查询全部图文、图册
     * 参数选填：分类ID、页码、页长
     */
    @POST(Constants.URL_ALBUM_GET_ALL_LIST)
    Observable<BaseDataBean<List<PhotoAlbumEntity>>> getAllAlbumList(@Nullable @Query("classificationId") String classificationId,
                                                                     @Nullable @Query("page") String page, @Nullable @Query("limit") String limit);

    /**
     * 查询点赞的图文、图册
     */
    @POST(Constants.URL_ALBUM_GET_LIKE_LIST)
    Observable<BaseDataBean<List<PhotoAlbumEntity>>> getLikeAlbumList(@Nullable @Query("page") String page, @Nullable @Query("limit") String limit);

    /**
     * 发布图册
     */
    @POST(Constants.URL_ALBUM_PUBLISH_IMAGE_TEXT)
    Observable<BaseDataBean<String>> publishImageText(@Nullable @Query("title") String title,
                                                      @Nullable @Query("classificationId") int classificationId,
                                                      @Nullable @Query("urls")  String urls, // key 图片地址  value  图片比
                                                      @Nullable @Query("displayStyle") String displayStyle);

    /**
     * 获取用户关注列表
     */
    @POST(Constants.URL_USER_MORE_INFO)
    Observable<BaseDataBean<UserInfoEntity>> getMoreUserInfo();

    /**
     * 获取用户关注列表
     */
    @POST(Constants.URL_USER_GET_ATTENTION_LIST)
    Observable<BaseDataBean<List<UserInfoEntity>>> getUserAttentionList(@Nullable @Query("page") String page, @Nullable @Query("limit") String limit);

    /**
     * 获取用户粉丝列表
     */
    @POST(Constants.URL_USER_GET_FANS_LIST)
    Observable<BaseDataBean<List<UserInfoEntity>>> getUserFansList(@Nullable @Query("page") String page, @Nullable @Query("limit") String limit);

    /**
     * 关注用户
     */
    @POST(Constants.URL_USER_APPEND_ATTENTION)
    Observable<BaseDataBean<Void>> appendAttention(@Query("friendId") String friendId);

    /**
     * 获取主页推荐用户
     */
    @POST(Constants.URL_USER_GET_RECOMMEND_LIST)
    Observable<BaseDataBean<List<UserInfoEntity>>> getUserRecommendList();

    /**
     * 取消关注用户
     */
    @POST(Constants.URL_USER_CANCEL_ATTENTION)
    Observable<BaseDataBean<Void>> cancelAttention(@Query("friendId") String friendId);

    /**
     * 获取登录记录列表
     */
    @POST(Constants.URL_USER_GET_LOGIN_RECORD_LIST)
    Observable<BaseDataBean<List<LoginRecordEntity>>> getLoginRecord();

    /**
     * 获取全部分类
     */
    @POST(Constants.URL_CLASSIFICATION_GET_ALL_LIST)
    Observable<BaseDataBean<List<ClassificationEntity>>> getClassificationList();

    /**
     * 设置分类喜好
     */
    @POST(Constants.URL_CLASSIFICATION_SET_PREFERENCES)
    Observable<BaseDataBean<Void>> setClassificationPreferences(@Query("classification") String classificationIds);

}
