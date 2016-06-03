package me.yugy.github.developquicksetting;

import android.content.Intent;
import android.content.IntentFilter;

/**
 * DeveloperConf <br/>
 * Created by dim on 2016-06-02.
 */
public class DeveloperConf {

    public boolean isDebugLayout;
    public boolean isShowOverdraw;
    public boolean isShowProfileGPURendering;
    public boolean isImmediatelyDestroyActivities;
    public boolean isAdbThroughWifiEnabled;
    public boolean isLayoutUpdate;
    private Application mApplication;
    private ScreenActionReceiver mScreenActionReceiver = new ScreenActionReceiver();

    private boolean mRegistered = false;

    public DeveloperConf(Application application) {
        mApplication = application;
    }

    public void setConf(boolean[] results) {
        isDebugLayout = results[0];
        isShowOverdraw = results[1];
        isShowProfileGPURendering = results[2];
        isImmediatelyDestroyActivities = results[3];
        isAdbThroughWifiEnabled = results[4];
        isLayoutUpdate = results[4];
        checkALlDisable();
    }

    private void checkALlDisable() {
        if (!mRegistered && isDebugLayout || isShowOverdraw || isShowProfileGPURendering || isImmediatelyDestroyActivities || isAdbThroughWifiEnabled || isLayoutUpdate) {
            //  有一个为true  注册屏幕亮起广播.
            mApplication.getApplicationContext().registerReceiver(mScreenActionReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
            mApplication.getApplicationContext().registerReceiver(mScreenActionReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
            mRegistered = true;
        } else if (mRegistered) {

            mApplication.getApplicationContext().unregisterReceiver(mScreenActionReceiver);
            mRegistered = false;

        }
    }

    public void setAllDisable() {

        if (isDebugLayout) {
            DevelopSettingsService.newTask(mApplication.getBaseContext(), DevelopSettingsService.ACTION_SET_SHOW_LAYOUT_BORDER);
        }
        if (isShowOverdraw) {
            DevelopSettingsService.newTask(mApplication.getBaseContext(), DevelopSettingsService.ACTION_SET_DISPLAY_OVERDRAW);
        }
        if (isShowProfileGPURendering) {
            DevelopSettingsService.newTask(mApplication.getBaseContext(), DevelopSettingsService.ACTION_SET_PROFILE_GPU_RENDERING);
        }
        if (isImmediatelyDestroyActivities) {
            DevelopSettingsService.newTask(mApplication.getBaseContext(), DevelopSettingsService.ACTION_SET_IMMEDIATELY_DESTROY_ACTIVITIES);
        }
        if (isAdbThroughWifiEnabled) {
            DevelopSettingsService.newTask(mApplication.getBaseContext(), DevelopSettingsService.ACTION_SET_ADB_THROUGH_WIFI);
        }
        if (isLayoutUpdate) {
            DevelopSettingsService.newTask(mApplication.getBaseContext(), DevelopSettingsService.ACTION_SET_LAYOUT_UPDATE);
        }

    }
}
