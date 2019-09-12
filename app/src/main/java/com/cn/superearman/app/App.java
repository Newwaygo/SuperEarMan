package com.cn.superearman.app;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.qiniu.droid.rtc.QNLogLevel;
import com.qiniu.droid.rtc.QNRTCEnv;

public class App extends Application {

    private static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();
    }

    private void init() {
        QNRTCEnv.setLogLevel(QNLogLevel.ERROR);
        QNRTCEnv.init(getApplicationContext());
        Fresco.initialize(this);
        
    }

    public static Application getInstance() {
        return instance;
    }


}
