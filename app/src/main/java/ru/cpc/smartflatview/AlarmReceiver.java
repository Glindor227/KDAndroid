package ru.cpc.smartflatview;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Вик on 016. 16. 04. 18.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SFV-Service", "AlarmReceiver.onReceive");

        //PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        //PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
        //wl_cpu.acquire(14000);

        Intent background = new Intent(context, CheckAlarmService.class);
        // Add extras to the bundle
        Bundle extras = intent.getExtras();
        assert extras != null;
        background.putExtras(extras);
        startWakefulService(context, background);

        Intent alarm = new Intent(context, AlarmReceiver.class);
        alarm.putExtras(extras);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm,
                                                                 PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                                  SystemClock.elapsedRealtime() + 60000,
                                                  pendingIntent);
                Log.d("SFV-Service", "AlarmManager.setAndAllowWhileIdle() - OK");
            }
            else
            {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                 SystemClock.elapsedRealtime() + 60000, pendingIntent);
                Log.d("SFV-Service", "AlarmManager.set() - OK");
            }
        }
    }

}