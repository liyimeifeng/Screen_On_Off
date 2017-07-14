package com.dftc.deviceonoff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Lee on 2017/7/13 0013.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals("com.android.settings.action.REQUEST_POWER_OFF")){
            shutDown(context);
      }

    }

    private void shutDown(Context context) {
        Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
//        intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
