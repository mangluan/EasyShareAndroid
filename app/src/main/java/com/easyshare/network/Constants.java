package com.easyshare.network;

public class Constants {

    public static final int LOGIN_SUCCESSFULLY = 0x0001;
    public static final int LOGOUT = 0x0002;

    // 是否初始化软件
    public static final String IS_INITIALIZATION_APPLICATION = "is initiation application";
    public static final String TOKEN = "easy share token";
    public static final String USER_MAIL = "last login mail";

    public static final String BASE_URL = "http://api.easyshare.shop/";     // api接口协议地址

    /* ----------------  oss  -----------------------*/
    public static final String URL_OSS_GET_TOKEN = "/OSS/getToken"; // oss 获取token


    /* ----------------  user -----------------------*/
    public static final String URL_USER_LOGIN_FOR_MAIL = "/user/loginForMail"; // 通过邮箱-验证码登录
    public static final String URL_USER_LOGIN_FOR_PASSWORD = "/user/loginForPassword"; // 通过邮箱-密码登录
    public static final String URL_USER_LOGIN_FOR_TOKEN = "/user/loginForToken"; // 通过登录令牌登录
    public static final String URL_USER_SEND_AUTH_CODE = "/user/sendAuthCode"; // 发送验证码
    public static final String URL_USER_CHANGE_INFO = "/user/changeUserInfo"; // 修改用户信息
    public static final String URL_USER_GET_ATTENTION_LIST = "/user/getUserAttentionList"; // 获取用户关注列表
    public static final String URL_USER_GET_FANS_LIST = "/user/getUserFansList"; // 获取用户关注列表
    public static final String URL_USER_GET_LOGIN_RECORD_LIST = "/user/getLoginRecord"; // 获取用户登录记录
    public static final String URL_USER_APPEND_ATTENTION = "/user/appendAttention"; // 添加关注
    public static final String URL_USER_CANCEL_ATTENTION = "/user/cancelAttention"; // 添加关注
    public static final String URL_USER_MORE_INFO = "/user/getMoreUserInfo"; // 获取浏览、关注、粉丝数量

    /* ----------------  album -----------------------*/
    public static final String URL_ALBUM_GET_ALL_LIST = "/album/getAllAlbum"; // 获取全部图册

}
