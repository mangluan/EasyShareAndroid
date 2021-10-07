package com.easyshare.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.easyshare.database.entity.UserInfoEntity;

@Dao
public interface UserInfoDAO {

    /**
     * 插入用户数据
     *
     * @param entity 待插入的数据
     * @return 插入成功返回序号
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUserInfo(UserInfoEntity entity);

}
