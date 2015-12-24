package me.yugy.github.developquicksetting;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;

public class DevelopSettingsService extends IntentService {

    @IntDef({
            ACTION_SET_SHOW_LAYOUT_BORDER,
            ACTION_SET_DISPLAY_OVERDRAW,
            ACTION_SET_PROFILE_GPU_RENDERING, ACTION_SET_IMMEDIATELY_DESTROY_ACTIVITIES,
            ACTION_SET_ADB_THROUGH_WIFI
//            ,ACTION_SHOW_TOOL_IN_SYSTEM_BAR
             }
    )
    public @interface Action {}

    public static final int ACTION_SET_SHOW_LAYOUT_BORDER = 1;
    public static final int ACTION_SET_DISPLAY_OVERDRAW = 2;
    public static final int ACTION_SET_PROFILE_GPU_RENDERING = 3;
    public static final int ACTION_SET_IMMEDIATELY_DESTROY_ACTIVITIES = 4;
    public static final int ACTION_SET_ADB_THROUGH_WIFI = 5;
//    public static final int ACTION_SHOW_TOOL_IN_SYSTEM_BAR = 6;

    public static void newTask(Context context, @Action int action) {
        context.startService(getIntent(context, action));
    }

    public static Intent getIntent(Context context, @Action int action) {
        Intent intent = new Intent(context, DevelopSettingsService.class);
        intent.putExtra("action", action);
        return intent;
    }

    public static PendingIntent getPendingIntent(Context context, @Action int action) {
        Intent intent = getIntent(context, action);
        return PendingIntent.getService(context, action, intent, 0);
    }

    public DevelopSettingsService() {
        super("WidgetService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            int action = intent.getIntExtra("action", 0);
            Utils.log("onHandleIntent(), action: " + action);
            switch (action) {
                case ACTION_SET_SHOW_LAYOUT_BORDER:
                    refreshUIState(DeveloperSettings.toggleDebugLayout());
                    break;
                case ACTION_SET_DISPLAY_OVERDRAW:
                    refreshUIState(DeveloperSettings.toggleShowOverdraw());
                    break;
                case ACTION_SET_PROFILE_GPU_RENDERING:
                    refreshUIState(DeveloperSettings.toggleProfileGPURendering());
                    break;
                case ACTION_SET_IMMEDIATELY_DESTROY_ACTIVITIES:
                    refreshUIState(DeveloperSettings.toggleImmediatelyDestroyActivity(this));
                    break;
                case ACTION_SET_ADB_THROUGH_WIFI:
                    refreshUIState(DeveloperSettings.toggleAdbThroughWifi());
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (DeveloperSettings.isAdbThroughWifiEnabled()) {
                        String port = DeveloperSettings.getAdbThroughWifiPort();
                        String wifiIp = DeveloperSettings.getWifiIp();
                        if (port.equals("-1") || port.length() == 0 || wifiIp == null) {
                            return;
                        }
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_adb_wifi_enabled)
                                .setContentTitle(getString(R.string.adb_through_wifi_enabled))
                                .setContentText(wifiIp + ":" + port);
                        Intent notificationIntent = new Intent(this, MainActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(notificationIntent);
                        PendingIntent resultPendingIntent = stackBuilder
                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        notificationManager.notify(Conf.NOTIFICATION_ID, mBuilder.build());
                    } else {
                        notificationManager.cancel(Conf.NOTIFICATION_ID);
                    }
                    break;
//                case ACTION_SHOW_TOOL_IN_SYSTEM_BAR:
//                    break;
            }
        } catch (IOException | InterruptedException | NullPointerException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            refreshUIState(false);
        }
    }

    private void refreshUIState(boolean success) {
        Utils.log("refreshUIState");
        //refresh widget state if exists.
        Intent intent = new Intent(this, DevelopWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] appWidgetIds = AppWidgetManager.getInstance(this).getAppWidgetIds(
                new ComponentName(this, DevelopWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        sendBroadcast(intent);

        //refresh activity state if exists.
        intent = new Intent(Conf.ACTION_REFRESH_UI);

        if(DeveloperSettings.isShowOnStatusBar(this)){
            DeveloperSettings.setShowOnStatusBar(this,true);
        }
        intent.putExtra("result", success);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
