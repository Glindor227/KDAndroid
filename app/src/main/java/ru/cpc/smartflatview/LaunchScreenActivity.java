package ru.cpc.smartflatview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class LaunchScreenActivity extends AppCompatActivity
{
    private static final int SPLASH_TIME = 500;

    private Uri importData = Uri.EMPTY;

    private class BackgroundTask extends AsyncTask<Uri, Integer, Config> {
        Intent intent;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("LaunchScreen", "onPreExecute()");
            intent = new Intent(LaunchScreenActivity.this, MainActivity.class);
        }
        @Override
        protected Config doInBackground(Uri... params) {
                /*  Use this method to load background
                * data that your app needs. */
            Config pConfig = null;
            Log.i("LaunchScreen", "Starting task with url: "+params[0]);
            //if(importData != Uri.EMPTY)
            //    throw new InvalidParameterException();

            if(Config.DEMO)
                pConfig = new Config();
            else
            {
//                if(importData != Uri.EMPTY)
//                {
//                    throw new InvalidParameterException();
//                        LaunchScreenActivity.this.runOnUiThread(new Runnable()
//                        {
//                            public void run()
//                            {
//                                showDialog(MainActivity.DIALOG_REALLY_UPDATE_CONFIG_ID);
//                            }
//                        });
//                }
//                else
//                {
                    pConfig = Config.LoadXml(importData, LaunchScreenActivity.this);
                    if(pConfig == null || pConfig.m_cRooms.size() == 0)
                    {
                        pConfig = new Config();
//                      LaunchScreenActivity.this.runOnUiThread(new Runnable()
//                      {
//                          public void run()
//                          {
//                             showDialog(MainActivity.DIALOG_NO_CONFIG_ID);
//                          }
//                      });
                    }
//                  else
                    Config.SaveXml(pConfig, LaunchScreenActivity.this);
//                }
            }

            Log.d("LaunchScreen", "doInBackground()");
            try {
                Thread.sleep(SPLASH_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return pConfig;
        }
        @Override
        protected void onPostExecute(Config o) {
            super.onPostExecute(o);
            Log.d("LaunchScreen", "onPostExecute()");

            Config.Instance = o;
            TextView pTextName = (TextView) findViewById(R.id.summary_name);
            TextView pTextDesc = (TextView) findViewById(R.id.summary_description);

            if (pTextName != null)
            {
                pTextName.setText(o.m_sSummaryName);
            }
            if (pTextDesc != null)
            {
                pTextDesc.setText(o.m_sSummaryText);
            }

            try
            {
                Thread.sleep(200);
            }
            catch (InterruptedException e)
            {
                Log.v("Glindor",e.getMessage());
                e.printStackTrace();
            }

            //            Pass your loaded data here using Intent
            //            intent.putExtra("data_key", "");
            String loginCode = Prefs.getLogin(LaunchScreenActivity.this);
            if(loginCode.isEmpty())
                startActivity(intent);
            else {
                LoginActivity.s_sTrueCode = loginCode;
                LoginActivity.s_pUnlocker = new LoginActivity.SFUnlocker() {
                    @Override
                    public void Unlock(boolean bUnlock) {
                        if (bUnlock)
                            startActivity(intent);
                    }
                };
                Intent intentLogin = new Intent(LaunchScreenActivity.this, LoginActivity.class);
                startActivity(intentLogin);
            }
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Transparent Status Bar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_launch_screen);

        TextView pTextName = (TextView) findViewById(R.id.summary_name);
        TextView pTextDesc = (TextView) findViewById(R.id.summary_description);

        if (pTextName != null)
        {
            pTextName.setText(R.string.app_name);
        }
        if (pTextDesc != null)
        {
            Context context = getApplicationContext(); // or activity.getApplicationContext()
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();

            String myVersionName = "not available"; // initialize String

            try
            {
                myVersionName = packageManager.getPackageInfo(packageName, 0).versionName;
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }
            pTextDesc.setText(String.format(Locale.US, "Version %s", myVersionName));
        }

        if(!Config.DEMO)
        {
            Intent i = getIntent();
            if(i != null)
            {
                Uri u = i.getData();
                if(u != null)
                {
                    importData = u;
//                    name.setText("Импорт конфигурации");
//                    text.setText(u.getEncodedPath());
                }
            }
        }
        new BackgroundTask().execute(importData);
    }

}
