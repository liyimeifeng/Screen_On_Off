package com.dftc.deviceonoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * 开机自启动应用
 * Created by Lee on 2017/7/14 0014.
 */

public class BootReceiver extends BroadcastReceiver {
    private final static String TAG = BootReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.e(TAG, "设备已开机" );
//            Intent i = new Intent(context, MainActivity.class);
            Intent i = context.getPackageManager()
                    .getLaunchIntentForPackage("com.dftc.deviceonoff");

            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else{
            if (intent.getAction().equals("com.android.settings.action.REQUEST_POWER_OFF")){
                shutDown(context);
            }
        }
    }

    /**
     * 测试无效
     * @param context
     */
    private void shutDown(Context context) {
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
//        intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
