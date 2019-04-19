package ru.cpc.smartflatview;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Вик on 016. 16. 04. 18.
 */

public class BootBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Launch the specified service when this message is received
        Log.d("SFV-Service", "BootBroadcastReceiver.onReceive");
        Intent startServiceIntent = new Intent(context, MyWakefulService.class);
        startWakefulService(context, startServiceIntent);
    }
}