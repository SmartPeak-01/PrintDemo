package com.basewin.printdemo;

import android.app.Application;

import com.basewin.services.ServiceManager;

/**
 * Author:28936
 * Date:2019/10/11 12:01
 * Description:
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ServiceManager.getInstence().init(this);
    }
}
