package ru.cpc.smartflatview;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by Вик on 019. 19. 04. 18.
 */

public class AutoStart extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            try {
                Log.d("SFV-Service", "AutoStart.onHandleIntent");
                Config config = Config.LoadXml(Uri.EMPTY, context);
                if(config != null)
                {
                    Log.d("SFV-Service", "config OK");

                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    String sIP = Prefs.getExternalIP(context);
                    alarmIntent.putExtra("ip1", config.m_sIP);
                    alarmIntent.putExtra("ip2", sIP);
                    alarmIntent.putExtra("port", config.m_iPort);
                    alarmIntent.putExtra("mobile", Prefs.getMobile(context));
                    alarmIntent.putExtra("sound", Prefs.getSound(context));
                    alarmIntent.putExtra("wakeup", Prefs.getWakeup(context));
                    alarmIntent.putExtra("connection", Prefs.getConnectionNotify(context));
                    Log.d("SFV-Service", "alarmIntent OK");
                    boolean alarmRunning = (PendingIntent.getBroadcast(context, 0, alarmIntent,
                                                                       PendingIntent.FLAG_NO_CREATE) != null);
                    if (!alarmRunning)
                    {
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                                                                                 alarmIntent, 0);
                        Log.d("SFV-Service", "pendingIntent OK");
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                                Context.ALARM_SERVICE);
                        if(alarmManager != null)
                        {
                            //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60000, pendingIntent);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            {
                                alarmManager.setAndAllowWhileIdle(
                                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                        SystemClock.elapsedRealtime() + 60000,
                                        pendingIntent);
                                Log.d("SFV-Service", "AlarmManager.setAndAllowWhileIdle() - OK");
                            }
                            else
                            {
                                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                                 SystemClock.elapsedRealtime() + 60000,
                                                 pendingIntent);
                                Log.d("SFV-Service", "AlarmManager.set() - OK");
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Log.e("SFV-Service", "showForegroundNotification", ex);
            }
        }
    }
}
