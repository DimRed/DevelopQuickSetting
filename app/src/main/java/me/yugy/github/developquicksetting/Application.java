package me.yugy.github.developquicksetting;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

public class Application extends android.app.Application {

    private static Application sInstance;


    private DeveloperConf mDeveloperConf = new DeveloperConf(this);

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);
        sInstance = this;
    }

    public synchronized static Application getInstance() {
        return sInstance;
    }

    public void setDeveloperConf(boolean[] result) {
        mDeveloperConf.setConf(result);
    }
    public void setAllDisable(){
        mDeveloperConf.setAllDisable();
    }
}
