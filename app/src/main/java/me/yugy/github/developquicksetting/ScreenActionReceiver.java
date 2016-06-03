package me.yugy.github.developquicksetting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

/**
 * ScreenActionReceiver <br/>
 * Created by dim on 2016-06-03.
 */
public class ScreenActionReceiver extends BroadcastReceiver {


    private static final String TAG = "ScreenActionReceiver";

    public void saveLastScreenOffTimeMillis(Context context) {
        PreferenceManager
                .getDefaultSharedPreferences(context).edit()
                .putLong("current_screen_off", System.currentTimeMillis()).apply();
    }

    public long getLastScreenOffTimeMillis(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getLong("current_screen_off", 0);
    }

    public boolean isVailOff(Context context) {

        long l = System.currentTimeMillis() - getLastScreenOffTimeMillis(context);
        return l > Conf.IDE_DURATION;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_ON:

                if (isVailOff(context)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Application.getInstance().setAllDisable();

                        }
                    }).start();
                }
                break;
            case Intent.ACTION_SCREEN_OFF:
                // 保存息屏时间
                saveLastScreenOffTimeMillis(context);
                break;
        }
    }
}
