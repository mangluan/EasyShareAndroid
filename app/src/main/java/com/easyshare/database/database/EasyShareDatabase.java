package com.easyshare.database.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.easyshare.database.dao.UserInfoDAO;
import com.easyshare.entity.UserInfoEntity;


@Database(entities = {UserInfoEntity.class}, version = 1, exportSchema = false)
public abstract class EasyShareDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "Easy_Share_db";

    private volatile static EasyShareDatabase databaseInstance;

    public static synchronized EasyShareDatabase getInstance(Context context) {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(), EasyShareDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration() // 数据库更新时删除数据重新创建，只能测试时打开
                    .build();
        }
        return databaseInstance;
    }

    public abstract UserInfoDAO mBodyStatsDAO();


}