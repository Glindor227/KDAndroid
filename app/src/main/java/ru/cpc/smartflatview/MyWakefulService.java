package ru.cpc.smartflatview;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Вик on 016. 16. 04. 18.
 */

public class MyWakefulService extends IntentService {
    public MyWakefulService() {
        super("SimpleWakefulService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // At this point SimpleWakefulReceiver is still holding a wake lock
        // for us.  We can do whatever we need to here and then tell it that
        // it can release the wakelock.  This sample just does some slow work,
        // but more complicated implementations could take their own wake
        // lock here before releasing the receiver's.
        //
        // Note that when using this approach you should be aware that if your
        // service gets killed and restarted while in the middle of such work
        // (so the Intent gets re-delivered to perform the work again), it will
        // at that point no longer be holding a wake lock since we are depending
        // on SimpleWakefulReceiver to that for us.  If this is a concern, you can
        // acquire a separate wake lock here.
        Log.d("SFV-Service", "MyWakefulService.onHandleIntent");
        Config config = Config.LoadXml(Uri.EMPTY, this);
        if(config != null)
        {
            Intent alarm = new Intent(this, AlarmReceiver.class);
            String sIP = Prefs.getExternalIP(this);
            alarm.putExtra("ip1", config.m_sIP);
            alarm.putExtra("ip2", sIP);
            alarm.putExtra("port", config.m_iPort);
            alarm.putExtra("mobile", Prefs.getMobile(this));
            boolean alarmRunning = (PendingIntent.getBroadcast(this, 0, alarm,
                                                               PendingIntent.FLAG_NO_CREATE) != null);
            if (!alarmRunning)
            {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null)
                {
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                              SystemClock.elapsedRealtime(), 60000, pendingIntent);
                    Log.d("SFV-Service", "AlarmManager.setRepeating() - OK");
                }
            }
        }
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }
}


