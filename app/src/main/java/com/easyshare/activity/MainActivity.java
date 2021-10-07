package com.easyshare.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.easyshare.R;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}