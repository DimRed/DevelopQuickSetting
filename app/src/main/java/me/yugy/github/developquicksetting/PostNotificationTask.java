package me.yugy.github.developquicksetting;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;

/**
 * Created by zzz40500 on 15/12/25.
 */
public class PostNotificationTask extends AsyncTask<Void, Void, boolean[]> {


    private final Context mContext;

    public PostNotificationTask(Context context) {
        mContext = context;
    }

    @Override
    protected boolean[] doInBackground(@NonNull Void... params) {
        try {
            return new boolean[]{
                    DeveloperSettings.isDebugLayoutEnabled(),
                    DeveloperSettings.isShowOverdrawEnabled(),
                    DeveloperSettings.isShowProfileGPURendering(),
                    DeveloperSettings.isImmediatelyDestroyActivities(mContext),
                    DeveloperSettings.isAdbThroughWifiEnabled(),
                    DeveloperSettings.isLayoutUpdate()
            };
        } catch (IOException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(@NonNull boolean[] results) {
        Application.getInstance().setDeveloperConf(results);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.layout_notifycation);
        //update widget state
        views.setImageViewResource(R.id.layout_border_image,
                results[0] ? R.drawable.ic_debug_layout_enabled : R.drawable.ic_debug_layout_disabled);
        views.setImageViewResource(R.id.layout_border_indicator,
                results[0] ? R.color.appwidget_indicator_enabled : R.color.appwidget_indicator_disabled);

        views.setImageViewResource(R.id.overdraw_image,
                results[1] ? R.drawable.ic_overdraw_enabled : R.drawable.ic_overdraw_disabled);
        views.setImageViewResource(R.id.overdraw_indicator,
                results[1] ? R.color.appwidget_indicator_enabled : R.color.appwidget_indicator_disabled);

        views.setImageViewResource(R.id.gpu_rendering_image,
                results[2] ? R.drawable.ic_gpu_rendering_enabled : R.drawable.ic_gpu_rendering_disabled);
        views.setImageViewResource(R.id.gpu_rendering_indicator,
                results[2] ? R.color.appwidget_indicator_enabled : R.color.appwidget_indicator_disabled);

        views.setImageViewResource(R.id.destroy_activities_image,
                results[3] ? R.drawable.ic_destroy_enabled : R.drawable.ic_destroy_disabled);
        views.setImageViewResource(R.id.destroy_activities_indicator,
                results[3] ? R.color.appwidget_indicator_enabled : R.color.appwidget_indicator_disabled);

        views.setImageViewResource(R.id.adb_wifi_image,
                results[4] ? R.drawable.ic_adb_wifi_enabled : R.drawable.ic_adb_wifi_disabled);
        views.setImageViewResource(R.id.adb_wifi_indicator,
                results[4] ? R.color.appwidget_indicator_enabled : R.color.appwidget_indicator_disabled);

        views.setImageViewResource(R.id.layout_update_indicator,
                results[5] ? R.color.appwidget_indicator_enabled : R.color.appwidget_indicator_disabled);
        views.setImageViewResource(R.id.layout_update_image,
                results[5] ? R.drawable.ic_layout_update_enabled : R.drawable.ic_layout_update_disabled);

        //set widget click listener
        views.setOnClickPendingIntent(R.id.layout_update, DevelopSettingsService.getPendingIntent(
                mContext, DevelopSettingsService.ACTION_SET_LAYOUT_UPDATE));
        views.setOnClickPendingIntent(R.id.layout_border, DevelopSettingsService.getPendingIntent(
                mContext, DevelopSettingsService.ACTION_SET_SHOW_LAYOUT_BORDER));
        views.setOnClickPendingIntent(R.id.overdraw, DevelopSettingsService.getPendingIntent(
                mContext, DevelopSettingsService.ACTION_SET_DISPLAY_OVERDRAW));
        views.setOnClickPendingIntent(R.id.gpu_rendering, DevelopSettingsService.getPendingIntent(
                mContext, DevelopSettingsService.ACTION_SET_PROFILE_GPU_RENDERING));
        views.setOnClickPendingIntent(R.id.destroy_activities, DevelopSettingsService.getPendingIntent(
                mContext, DevelopSettingsService.ACTION_SET_IMMEDIATELY_DESTROY_ACTIVITIES));
        views.setOnClickPendingIntent(R.id.adb_wifi, DevelopSettingsService.getPendingIntent(
                mContext, DevelopSettingsService.ACTION_SET_ADB_THROUGH_WIFI));

        //hide the button that device api level not supported.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            views.setViewVisibility(R.id.gpu_rendering, View.GONE);
        }
        Notification notification = new NotificationCompat.Builder(mContext).setAutoCancel(false).setOngoing(true).build();
        notification.icon = R.mipmap.ic_launcher;
        notification.contentView = views;
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Conf.TOOL_NOTIFICATION_ID, notification);

    }

}
