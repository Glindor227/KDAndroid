package ru.cpc.smartflatview;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Вик on 016. 16. 04. 18.
 */

public class CheckAlarmService extends IntentService
{
    // Must create a default constructor
    public CheckAlarmService() {
        // Used to name the worker thread, important only for debugging.
        super("check_alarm_service");
        Log.d("SFV-Service", "ctor");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        Log.d("SFV-Service", "onCreate");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("SFV-Service", "onDestroy");
    }

    private void showForegroundNotification(int notifyId, int icon, String contentText, boolean sound, boolean wakeup) {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Log.d("SFV-Service", "showForegroundNotification");
        if(wakeup)
        {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = false;
            if (pm != null)
            {
                isScreenOn = pm.isScreenOn();
                if (!isScreenOn)
                {
                    PowerManager.WakeLock wl = pm.newWakeLock(
                            PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,
                            "MyLock");
                    wl.acquire(5000);
                }
            }
        }

        Intent showTaskIntent = new Intent(getApplicationContext(), LaunchScreenActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap iconBmp = BitmapFactory.decodeResource(getResources(),icon);

        Notification.Builder nBuilder = new Notification.Builder(getApplicationContext())
                .setContentTitle(contentText)//getString(R.string.app_name))
                //.setContentText(contentText)
                .setSmallIcon(icon)
                .setLargeIcon(iconBmp)
                .setWhen(System.currentTimeMillis())
//                .setAutoCancel(true)
                .setContentIntent(contentIntent);
        //startForeground(1, notification);

        if(sound)
        {
            nBuilder.setVibrate(new long[]{1000,
                                           1000,
                                           1000,
                                           1000,
                                           1000})
                    .setLights(Color.RED, 3000, 3000)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setOnlyAlertOnce(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nBuilder.setColor(Color.RED);
        }

        Notification notification = nBuilder.build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null)
        {
            mNotificationManager.notify(notifyId, notification);
            Log.d("SFV-Service", "mNotificationManager.notify() - OK");
        }
    }

    @Nullable
    private Socket TryConnect(String ip, int port, int timeout)
    {
        Log.d("SFV-Service", "C: Connecting to " + ip + ":" + port);

        Socket sock = new Socket();

        try
        {
            //MainActivity.m_cLogLines.add("+++");
            InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName(ip), port);
            //MainActivity.m_cLogLines.add("***");
            sock.connect(addr, timeout);
            //MainActivity.m_cLogLines.add("!!!");
            Log.d("SFV-Service", "TryConnect success!");
            return sock;
        }
        catch (IllegalArgumentException e)
        {
            Log.d("SFV-Service", "C: IllegalArgumentException", e);
            //MainActivity.m_cLogLines.add("EX - " + e.getMessage());
            e.printStackTrace();
            //return null;
        }
        catch (UnknownHostException e)
        {
            Log.d("SFV-Service", "C: UnknownHostException", e);
            //MainActivity.m_cLogLines.add("EX - " + e.getMessage());
            //e.printStackTrace();
            //return null;
        }
        catch (SocketTimeoutException e)
        {
            //this.ed.setText("Android"+e);
            Log.d("SFV-Service", "C: SocketTimeoutException");//, e);
            //MainActivity.m_cLogLines.add("EX - " + e.getMessage());
            //e.printStackTrace();
            //return null;
        }
        catch (IOException e)
        {
            //this.ed.setText("Android"+e);
            Log.d("SFV-Service", "C: IOException");//, e);
            //MainActivity.m_cLogLines.add("EX - " + e.getMessage());
            //e.printStackTrace();
            //return null;
        } catch (Exception e)
        {
            //MainActivity.m_cLogLines.add("EX - " + e.getMessage());
            e.printStackTrace();
            //return null;
        }

        return null;
    }

    private static boolean tryIP2first = false;

    private static int connectionCounter = 0;

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d("SFV-Service", "CheckAlarmService.onHandleIntent");
        // This describes what will happen when service is triggered
        Bundle extras = intent.getExtras();
        if(extras != null)
        {
            //каждые 10 циклов принудительно переключаемся на внутренний ip - потому что если он доступен, то лучше всё-же использовать его
            if(connectionCounter++ > 10)
            {
                tryIP2first = false;
            }

            String ip1 = extras.getString("ip1");
            String ip2 = extras.getString("ip2");
            int port = extras.getInt("port");
            boolean sound = extras.getBoolean("sound");
            boolean wakeup = extras.getBoolean("wakeup");
            boolean connectionNotify = extras.getBoolean("connection");

            ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = null;
            if (conMgr != null)
            {
                activeNetworkInfo = conMgr.getActiveNetworkInfo();
            }

            if(activeNetworkInfo == null || activeNetworkInfo.getType() != ConnectivityManager.TYPE_WIFI)
            {
                Log.d("SFV-Service", "WiFi network not connected");

                if(extras.getBoolean("mobile"))
                {
                    if(activeNetworkInfo == null || activeNetworkInfo.getType() != ConnectivityManager.TYPE_MOBILE)
                    {
                        Log.d("SFV-Service", "Mobile network not connected");
                        //showForegroundNotification("WiFi and Mobile networks not available.");
                        AlarmReceiver.completeWakefulIntent(intent);
                        return;
                    }
                }
                else
                {
                    Log.d("SFV-Service", "Mobile network not allowed");
                    //showForegroundNotification("WiFi networks not available and mobile internet isn't allowed.");
                    AlarmReceiver.completeWakefulIntent(intent);
                    return;
                }
            }
            Socket client = null;
            for (int i = 0; i < 2 && client == null; i++)
            {
                client = TryConnect(tryIP2first ? ip2 : ip1, port, 5000);
            }

            for (int i = 0; i < 2 && client == null; i++)
            {
                client = TryConnect(tryIP2first ? ip1 : ip2, port, 5000);
                if(client != null)
                    tryIP2first = !tryIP2first;
            }

            if(client == null && connectionNotify)
            {
                showForegroundNotification(2, R.drawable.ic_disconnect, getString(R.string.notiyNoCarrier), sound, wakeup);
            }
            else
            {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (mNotificationManager != null)
                {
                    mNotificationManager.cancel(2);
                }
            }

            if(client == null)
            {
                AlarmReceiver.completeWakefulIntent(intent);
                return;
            }

            try
            {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);

                out.println("<7|0>\r\n");
                Log.d("SFV-Service", "C: REQUEST_EMG Sent.");
            }
            catch (Exception e)
            {
                Log.e("SFV-Service", "REQUEST_EMG Send Error", e);
                //showForegroundNotification(R.drawable.ic_disconnect, "send command fail");
            }

            try
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                int read = 0;
                char[] data = new char[4096];

                for(int i=0; i<4096; i++)
                {
                    data[i] = 0;
                }
                String response = "";
                read = reader.read(data, 0, 4096);
                //while((read = reader.read(data, 0, 4096)) > 0)
                if(read > 0)
                {
                    response += String.valueOf(data).substring(0, read).trim();
                    Log.d("SFV-Service", "read " + read + " bytes");
                }

                String[] sPackets = response.split("[<>]");

                Log.d("SFV-Service", "got packets = " + sPackets.length);

                for(String sPct: sPackets)
                {
                    Log.d("SFV-Service", "packet = '" + sPct + "'");
                    if(sPct.length() > 0)
                    {
                        String[] sTokens = sPct.split("[|]");

                        if(sTokens.length > 1)
                        {
                            try
                            {
                                if(Integer.parseInt(sTokens[0]) == 8)
                                {
                                    int iCount = (int)Float.parseFloat(sTokens[4]);

                                    if(iCount > 0)
                                        showForegroundNotification(1, R.drawable.ic_alarm, getString(R.string.notifyAlarms) + iCount, sound, wakeup);
                                    else
                                    {
                                        //showForegroundNotification(R.drawable.ic_ok, "Тревог на объекте: " + iCount);
                                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        if (mNotificationManager != null)
                                        {
                                            mNotificationManager.cancel(1);
                                        }
                                    }
                                }
                            }
                            catch(Exception ex)
                            {
                                Log.w("SFV-Service", "parsing exception: " + ex.getMessage());
                                //showForegroundNotification(R.drawable.ic_disconnect, "parsing exception: " + ex.getMessage());
                            }
                       }
                    }
                }

                reader.close();
            }
            catch (IOException e)
            {
                Log.w("SFV-Service", "socket read IOException: " + e);
            }
            finally
            {
                if (client != null)
                {
                    try
                    {
                        client.close();
                    }
                    catch (IOException e)
                    {
                        Log.w("SFV-Service", "client.close() exception: " + e);
                    }
                }
            }
        }
        AlarmReceiver.completeWakefulIntent(intent);
    }
}
