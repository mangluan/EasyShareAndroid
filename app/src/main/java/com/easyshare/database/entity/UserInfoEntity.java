package com.easyshare.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "User_Info")
public class UserInfoEntity {

    @PrimaryKey
    private String email;

}
