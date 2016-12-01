package com.vp6.anish.madguysmarketers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by anish on 23-10-2016.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("TAG", "Inside Boot Complete Receiver");
        Intent service = new Intent(context, LocationService.class);
        context.startService(service);
    }
}
