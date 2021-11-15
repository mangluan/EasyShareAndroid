package com.easyshare.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.easyshare.base.BaseDataBean;
import com.easyshare.base.ValueCallBack;
import com.easyshare.entity.OSSTokenEntity;
import com.easyshare.network.RetrofitFactory;

import java.io.IOException;

import retrofit2.Call;

public class OSSUtils {

    private static Handler mHandler;
    private static final String bucketName = "easy-share-app";
    private static final String endpoint = "http://oss-cn-chengdu.aliyuncs.com";
    private static final String filePrefix = "https://easy-share-app.oss-cn-chengdu.aliyuncs.com/";

    private static OSS mOSS;
    private static ValueCallBack<Integer> mProgressCallBack;

    /**
     * 初始化 OOS
     */
    public static void initOOS(Context context) {
        mHandler = new Handler(Looper.getMainLooper());
        // oss
        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {

            @Override
            public OSSFederationToken getFederationToken() {
                try {
                    // 从服务器中获取Token。
                    Call<BaseDataBean<OSSTokenEntity>> tokenCall = RetrofitFactory.getsInstance(context).getOSSToken();
                    BaseDataBean<OSSTokenEntity> tokenEntity = tokenCall.execute().body();
                    if (tokenEntity.getCode() == 0) {
                        OSSTokenEntity token = tokenEntity.getData();
                        return new OSSFederationToken(token.getAccessKeyId(), token.getAccessKeySecret(), token.getSecurityToken(), token.getExpiration());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        mOSS = new OSSClient(context.getApplicationContext(), endpoint, credentialProvider);
        OSSLog.enableLog();
    }

    /**
     * 添加上传监听
     *
     * @param progressCallBack 添加上传监听
     */
    public static void addProgressCallBack(ValueCallBack<Integer> progressCallBack) {
        mProgressCallBack = progressCallBack;
    }

    /**
     * 上传图片
     *
     * @param uri                图片Uri
     * @param fileName           服务器储存名字，带路径
     * @param uploadFileCallBack 上传回调
     */
    public static void uploadImage(Uri uri, String fileName, ValueCallBack<String> uploadFileCallBack) {
        // 构造上传请求。
        PutObjectRequest put = new PutObjectRequest(bucketName, fileName, uri);
        // 异步上传时可以设置进度回调。
        put.setProgressCallback((request, currentSize, totalSize) -> {
            if (mProgressCallBack != null) {
                mHandler.post(() -> mProgressCallBack.onSuccess((int) (currentSize * 100.0 / totalSize)));
            }
        });
        mOSS.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                mHandler.post(() -> uploadFileCallBack.onSuccess(filePrefix + request.getObjectKey()));
                mProgressCallBack = null;
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                mProgressCallBack = null;
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                    mHandler.post(() -> uploadFileCallBack.onFail("网络异常"));
                }
                if (serviceException != null) {
                    // 服务异常。
                    serviceException.printStackTrace();
                    mHandler.post(() -> uploadFileCallBack.onFail("服务器异常"));
                }
            }
        });
    }

}
