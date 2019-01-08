package com.example.dcang.myapplication;

import android.app.Application;

public class App extends Application {
    private HookUtil util;
    @Override
    public void onCreate() {
        super.onCreate();
        util = new HookUtil(this);

        try {
            util.hookStartActivity();
            util.hookLaunchActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
