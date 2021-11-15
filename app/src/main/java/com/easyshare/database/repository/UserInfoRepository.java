package com.easyshare.database.repository;

import android.content.Context;

import com.easyshare.database.dao.UserInfoDAO;
import com.easyshare.database.database.EasyShareDatabase;
import com.easyshare.entity.UserInfoEntity;

public class UserInfoRepository {

    private volatile static UserInfoRepository sRepository;

    private UserInfoDAO mUserInfoDAO;

    private UserInfoRepository() {
    }

    private UserInfoRepository(Context context) {
        mUserInfoDAO = EasyShareDatabase.getInstance(context).mBodyStatsDAO();
    }

    public static synchronized UserInfoRepository getInstance(Context context) {
        if (sRepository == null) {
            sRepository = new UserInfoRepository(context);
        }
        return sRepository;
    }

    /**
     * 插入用户数据
     *
     * @param entity 待插入的数据
     * @return 插入成功返回id
     */
    public long insertUserInfo(UserInfoEntity entity) {
        return mUserInfoDAO.insertUserInfo(entity);
    }

}