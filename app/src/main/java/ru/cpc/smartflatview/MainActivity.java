package ru.cpc.smartflatview;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;


import com.idis.android.redx.RSize;
import com.idis.android.redx.core.RCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static Context mAppContext = null;

    public  static final boolean USE_ORIGINAL_RESOLUTION = true;

    public static Context getAppContext() {
        return mAppContext;
    }

    public static boolean canUseGL() {
        return false;
    }

    private static final int REQUEST_READWRITE_STORAGE = 100;

    private static final String PREF_INDICATORS = "indicators" ;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private HashMap<Integer, List<ExpandedMenuModel>> m_cMenu = new HashMap<>();



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READWRITE_STORAGE) {
            if ((grantResults.length <= 0) || (grantResults[0] != PackageManager.PERMISSION_GRANTED))
            {
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                dlgAlert.setMessage(R.string.error_FileAccess);
                dlgAlert.setTitle(R.string.app_name);
                dlgAlert.setPositiveButton(R.string.ok,
                                           new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                SafeExit();
//                                finish();
//                                System.exit(0);
                            }
                        });
                dlgAlert.setCancelable(false);
                dlgAlert.create().show();
            }
        }
    }

    private void StartCheckAlarmShedule()
    {
        Intent alarm = new Intent(this, AlarmReceiver.class);
        String sIP = Prefs.getExternalIP(this);
        alarm.putExtra("ip1", Config.Instance.m_sIP);
        alarm.putExtra("ip2", sIP);
        alarm.putExtra("port", Config.Instance.m_iPort);
        alarm.putExtra("mobile", Prefs.getMobile(this));
        alarm.putExtra("sound", Prefs.getSound(this));
        alarm.putExtra("wakeup", Prefs.getWakeup(this));
        alarm.putExtra("connection", Prefs.getConnectionNotify(this));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarm,
                                                                 PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null)
        {
            alarmManager.cancel(pendingIntent);
            Log.d("SFV-Service", "AlarmManager.cancel() - OK");

            if(Prefs.getBackround(this))
            {
                //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                //                          SystemClock.elapsedRealtime(), 60000, pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60000, pendingIntent);
                    Log.d("SFV-Service", "AlarmManager.setAndAllowWhileIdle() - OK");
                }
                else
                {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60000, pendingIntent);
                    Log.d("SFV-Service", "AlarmManager.set() - OK");
                }
            }
            else
            {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (mNotificationManager != null)
                {
                    mNotificationManager.cancelAll();
                    Log.d("SFV-Service", "NotificationManager.cancelAll() - OK");
                }
            }
        }
    }

    private void StopCheckAlarmShedule()
    {
        Intent alarm = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, PendingIntent.FLAG_NO_CREATE);
        if(pendingIntent != null)
        {
            pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            Log.d("SFV-Service", "AlarmManager.cancel() - OK");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Glindor3","MainActivity onCreate 0");

        RoboErrorReporter.bindReporter(this);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Log.d("Glindor3","MainActivity onCreate 1");

        if (android.os.Build.VERSION.SDK_INT >= 23)
        {
            int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_READWRITE_STORAGE);
            }
        }

        super.onCreate(savedInstanceState);
        RCore.getInstance().setResolution(new RSize());
        Log.d("Glindor3","MainActivity onCreate 2");

        mAppContext = getApplicationContext();

        //StartCheckAlarmShedule();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d("Glindor3","MainActivity onCreate 3");

        MenuItem item = null;
        if (toolbar != null)
        {
            item = toolbar.getMenu().findItem(R.id.action_alarm);
        }
        if(item != null)
            item.setIcon(R.drawable.ic_ok);
        Log.d("Glindor3","MainActivity onCreate 4");

        Log.d("SF", "MainActivity.onCreate()");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 0);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null)
        {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
        Log.d("Glindor3","MainActivity onCreate 5");

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null)
        {
            tabLayout.setupWithViewPager(mViewPager);
            tabLayout.setVisibility(View.GONE);
        }

        //Настраиваем боковое меню
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null)
        {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();
        Log.d("Glindor3","MainActivity onCreate 6");

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null)
        {
            navigationView.setItemIconTintList(null);
        }
        Log.d("Glindor3","MainActivity onCreate 7");

        if(Config.Instance != null)
            prepareListData();

        ExpandableListView expandableList= (ExpandableListView) findViewById(R.id.navigationmenu);
        if (expandableList != null)
        {
            expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
                {
                    //Toast.makeText(MainActivity.this, "clicked " + m_cListData.get(groupPosition).m_cNestedMenu.get(childPosition).toString(), Toast.LENGTH_SHORT).show();

                    int iRoom = m_cListData.get(groupPosition).m_cNestedMenu.get(childPosition).getRoom();

                    Log.d("111111111111111", "Clicked " + groupPosition + ":" + childPosition);
                    Log.d("111111111111111", "Switching to room #" + iRoom);

                    if(iRoom == -1)
                        return  true;

                    getSupportActionBar().setTitle(Config.Instance.m_cRooms.get(iRoom).m_sName);

                    mSectionsPagerAdapter.m_iRoom = iRoom;
                    mViewPager.getAdapter().notifyDataSetChanged();

                    tabLayout.removeAllTabs();
                    for (Subsystem pSubsystem : Config.Instance.m_cRooms.get(iRoom).m_cSubsystems) {
                        TabLayout.Tab newTab = tabLayout.newTab();
                        //if(pSubsystem.m_sName.equalsIgnoreCase("0"))
                        //    newTab.setIcon(R.drawable.tab_light_indicator);
                        //else
                        //    newTab.setIcon(R.drawable.tab_media_indicator);
                        newTab.setText(pSubsystem.m_sName);
                        tabLayout.addTab(newTab);
                    }
                    tabLayout.getTabAt(0).select();

                    if(Config.Instance.m_cRooms.get(iRoom).m_cSubsystems.size() > 1)
                        tabLayout.setVisibility(View.VISIBLE);
                    else
                        tabLayout.setVisibility(View.GONE);

                    //mSectionsPagerAdapter = (SectionsPagerAdapter)mViewPager.getAdapter();
                    mViewPager.getAdapter().notifyDataSetChanged();

                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer != null)
                    {
                        drawer.closeDrawers();
                    }

                    return true;
                }
            });
            expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
                {
                    if(m_cListData.get(groupPosition).m_cNestedMenu.size() == 0)
                    {
                        //Toast.makeText(MainActivity.this, "clicked " + m_cListData.get(groupPosition).toString(), Toast.LENGTH_SHORT).show();

                        int iGroup = m_cListData.get(groupPosition).getRoom();

                        if(iGroup == -1)
                            return true;

                        getSupportActionBar().setTitle(Config.Instance.m_cRooms.get(iGroup).m_sName);

                        Log.d("111111111111111", "Switching to group #" + id);

                        mSectionsPagerAdapter.m_iRoom = iGroup;
                        mViewPager.getAdapter().notifyDataSetChanged();

                        tabLayout.removeAllTabs();
                        for (Subsystem pSubsystem : Config.Instance.m_cRooms.get(iGroup).m_cSubsystems) {
                            TabLayout.Tab newTab = tabLayout.newTab();
                            //if(pSubsystem.m_sName.equalsIgnoreCase("0"))
                            //    newTab.setIcon(R.drawable.tab_light_indicator);
                            //else
                            //    newTab.setIcon(R.drawable.tab_media_indicator);
                            newTab.setText(pSubsystem.m_sName);
                            tabLayout.addTab(newTab);
                        }
                        tabLayout.getTabAt(0).select();

                        if(Config.Instance.m_cRooms.get(iGroup).m_cSubsystems.size() > 1)
                            tabLayout.setVisibility(View.VISIBLE);
                        else
                            tabLayout.setVisibility(View.GONE);

                        //mSectionsPagerAdapter = (SectionsPagerAdapter)mViewPager.getAdapter();
                        mViewPager.getAdapter().notifyDataSetChanged();

                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer != null)
                        {
                            drawer.closeDrawers();
                        }

                        return true;
                    }

                    return false;
                }
            });

            MyExpandableListAdapter mMenuAdapter = new MyExpandableListAdapter(this, m_cListData, expandableList);
            expandableList.setAdapter(mMenuAdapter);
        }

        String indicators = "";
        try
        {
            indicators = getPreferences(MODE_PRIVATE).getString(PREF_INDICATORS, "");
        }
        catch(Exception ex)
        {
            Log.e("SF", "getPreferences exception : " + ex.getMessage() );
        }
        Log.d("SF", "onCreate, loading = '" + indicators + "'" );
        Log.d("Glindor3","MainActivity onCreate 8");
        Load(indicators);
        Log.d("Glindor3","MainActivity onCreate 8.1");

        if(Config.Instance != null && Config.Instance.m_cSubsystems != null && Config.Instance.m_cSubsystems.size() > 0)
        {
            Log.d("Glindor3","MainActivity onCreate 8.1");
            SFServer.Instance = new SFServer(this);
            Log.d("Glindor3","MainActivity onCreate 8.2");

            if(!Config.DEMO)
                SFServer.Instance.Connect();
            Log.d("Glindor3","MainActivity onCreate 8.3");
        }
        Log.d("Glindor3","MainActivity onCreate 9");

    }

    List<ExpandedMenuModel> m_cListData;

    private void prepareListData()
    {
        m_cListData = new ArrayList<ExpandedMenuModel>();
        m_cMenu.clear();

        if(Config.Instance == null)
            return;

        if(Config.Instance.m_cFavorites != null)
        {
            Log.d("SF", "prepareListData, favorites count = " + Config.Instance.m_cFavorites.size());
            for (Room pRoom : Config.Instance.m_cFavorites)
            {
                Log.d("SF", "prepareListData, favorites room[" + pRoom.m_iIndex + "] = '" + pRoom.m_sName + "'");
                ExpandedMenuModel item0 = new ExpandedMenuModel(pRoom.m_sName, R.drawable.ic_ok, pRoom.m_iIndex);
                m_cListData.add(item0);
                List<ExpandedMenuModel> cList = m_cMenu.get(pRoom.m_iIndex);
                if (cList == null)
                {
                    cList = new ArrayList<>();
                    m_cMenu.put(pRoom.m_iIndex, cList);
                }
                cList.add(item0);
            }
        }

        if(Config.Instance.m_cGroups != null)
        {
            Log.d("SF", "prepareListData, groups count = " + Config.Instance.m_cGroups.size());

            for (String key : Config.Instance.m_cGroups.keySet())
            {
                if (Config.Instance.m_cGroups.get(key).size() > 0)
                {
                    if (Config.Instance.m_cGroups.get(key).size() > 1)
                    {
                        ExpandedMenuModel group = new ExpandedMenuModel(key, -1, -1);
                        m_cListData.add(group);
                        for (Room pRoom : Config.Instance.m_cGroups.get(key)) {
                            Log.d("SF", "prepareListData, room[" + pRoom.m_iIndex + "] = '" + pRoom.m_sName + "'");
                            ExpandedMenuModel subItem = new ExpandedMenuModel(pRoom.m_sName, R.drawable.ic_ok, pRoom.m_iIndex);
                            group.m_cNestedMenu.add(subItem);
                            List<ExpandedMenuModel> cList = m_cMenu.get(pRoom.m_iIndex);
                            if (cList == null) {
                                cList = new ArrayList<>();
                                m_cMenu.put(pRoom.m_iIndex, cList);
                            }
                            cList.add(subItem);
                        }
                    }
                    else
                    {
                        Room pRoom = Config.Instance.m_cGroups.get(key).get(0);
                        Log.d("SF", "prepareListData, room[" + pRoom.m_iIndex + "] = '" + pRoom.m_sName + "'");
                        ExpandedMenuModel item0 = new ExpandedMenuModel(pRoom.m_sName, R.drawable.ic_ok, pRoom.m_iIndex);
                        m_cListData.add(item0);
                        List<ExpandedMenuModel> cList = m_cMenu.get(pRoom.m_iIndex);
                        if (cList == null) {
                            cList = new ArrayList<>();
                            m_cMenu.put(pRoom.m_iIndex, cList);
                        }
                        cList.add(item0);
                    }
                }
            }
        }

        if(m_cMenu.isEmpty())
        {
            for (Room pRoom : Config.Instance.m_cRooms)
            {
                Log.d("SF", "prepareListData, room[" + pRoom.m_iIndex + "] = '" + pRoom.m_sName + "'");
                ExpandedMenuModel item0 = new ExpandedMenuModel(pRoom.m_sName, R.drawable.ic_ok, pRoom.m_iIndex);
                m_cListData.add(item0);
                List<ExpandedMenuModel> cList = m_cMenu.get(pRoom.m_iIndex);
                if (cList == null) {
                    cList = new ArrayList<>();
                    m_cMenu.put(pRoom.m_iIndex, cList);
                }
                cList.add(item0);
            }
        }
        /*
        ExpandedMenuModel item0 = new ExpandedMenuModel(Config.Instance.m_cRooms.get(0).m_sName, R.drawable.ic_ok, 0);
        m_cListData.add(item0);
        m_cMenu.add(item0);
        ExpandedMenuModel item1 = new ExpandedMenuModel("Группа 1", -1, -1);
        m_cListData.add(item1);
        for (int i = 1; i < Config.Instance.m_cRooms.size()/2; i++)
        {
            Log.d("SF", "prepareListData, room '" + Config.Instance.m_cRooms.get(i).m_sName + "'");
            ExpandedMenuModel subItem = new ExpandedMenuModel(Config.Instance.m_cRooms.get(i).m_sName, R.drawable.ic_ok, i);
            item1.m_cNestedMenu.add(subItem);
            m_cMenu.add(subItem);
        }
        ExpandedMenuModel item2 = new ExpandedMenuModel("Группа 2", -1, -1);
        m_cListData.add(item2);
        for (int i = Config.Instance.m_cRooms.size()/2; i < Config.Instance.m_cRooms.size(); i++)
        {
            Log.d("SF", "prepareListData, room '" + Config.Instance.m_cRooms.get(i).m_sName + "'");
            ExpandedMenuModel subItem = new ExpandedMenuModel(Config.Instance.m_cRooms.get(i).m_sName, R.drawable.ic_ok, i);
            item2.m_cNestedMenu.add(subItem);
            m_cMenu.add(subItem);
        }
        */
    }
    //По кнопке "Назад" закрываем боковое меню
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null)
        {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void ImportConfig()
    {
        new FileChooser(this).setFileListener(new FileChooser.FileSelectedListener()
        {
            @Override
            public void fileSelected(final File file)
            {
                InputStream is = null;
                try
                {
                    is = new FileInputStream(file);
                }
                catch(FileNotFoundException e)
                {
                    Log.e("FileNotFoundException", "can't create FileInputStream");
                }

                Config pConfig = new Config(is);
                if(pConfig.m_cRooms.size() != 0)
                {
                    SafeExit();
                    Config.Instance = pConfig;
                    Config.SaveXml(pConfig, MainActivity.this);
                    Toast.makeText(getApplicationContext(), R.string.importSuccess, Toast.LENGTH_LONG).show();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);

//                    Intent mStartActivity = new Intent(getApplicationContext(), LaunchScreenActivity.class);
//                    int mPendingIntentId = 123456;
//                    PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//                    AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);

                    finish();
                    //System.exit(0);
                }
            }
        }).showDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            String prefsCode = Prefs.getPrefs(this);
            if(prefsCode.isEmpty())
                startActivity(new Intent(MainActivity.this, Prefs.class));
            else {
                LoginActivity.s_sTrueCode = prefsCode;
                LoginActivity.s_pUnlocker = new LoginActivity.SFUnlocker() {
                    @Override
                    public void Unlock(boolean bUnlock) {
                        if (bUnlock)
                            startActivity(new Intent(MainActivity.this, Prefs.class));
                    }
                };
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
            return true;
        }
        if (id == R.id.action_load)
        {
            String prefsCode = Prefs.getPrefs(this);
            if(prefsCode.isEmpty())
                ImportConfig();
            else {
                LoginActivity.s_sTrueCode = prefsCode;
                LoginActivity.s_pUnlocker = new LoginActivity.SFUnlocker() {
                    @Override
                    public void Unlock(boolean bUnlock) {
                        if (bUnlock)
                            ImportConfig();
                    }
                };
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
            return true;
        }
        if(id == R.id.action_alarm)
        {
            int roomIndex = -1;
            int subsystemIndex = -1;
            for (Subsystem pSubsystem : Config.Instance.m_cSubsystems)
            {
                if (pSubsystem.GetAlarmedCount() > 0 && pSubsystem.m_pRoom.m_iIndex > mSectionsPagerAdapter.m_iRoom) {
                    roomIndex = pSubsystem.m_pRoom.m_iIndex;
                    subsystemIndex = pSubsystem.m_iIndex;
                    break;
                }
            }
            if(roomIndex == -1)
            {
                for (Subsystem pSubsystem : Config.Instance.m_cSubsystems)
                {
                    if (pSubsystem.GetAlarmedCount() > 0 && pSubsystem.m_pRoom.m_iIndex < mSectionsPagerAdapter.m_iRoom) {
                        roomIndex = pSubsystem.m_pRoom.m_iIndex;
                        subsystemIndex = pSubsystem.m_iIndex;
                        break;
                    }
                }
            }
            if(roomIndex != -1)
            {
                getSupportActionBar().setTitle(Config.Instance.m_cRooms.get(roomIndex).m_sName);

                Log.d("111111111111111", "Switching to room #" + roomIndex);

                //mSectionsPagerAdapter = (SectionsPagerAdapter)mViewPager.getAdapter();
                mSectionsPagerAdapter.m_iRoom = roomIndex;
                mViewPager.getAdapter().notifyDataSetChanged();

                tabLayout.removeAllTabs();
                for (Subsystem pSubsystem : Config.Instance.m_cRooms.get(roomIndex).m_cSubsystems) {
                    TabLayout.Tab newTab = tabLayout.newTab();
                    //if(pSubsystem.m_sName.equalsIgnoreCase("0"))
                    //    newTab.setIcon(R.drawable.tab_light_indicator);
                    //else
                    //    newTab.setIcon(R.drawable.tab_media_indicator);
                    newTab.setText(pSubsystem.m_sName);
                    tabLayout.addTab(newTab);
                }
                tabLayout.getTabAt(subsystemIndex).select();

                if(Config.Instance.m_cRooms.get(roomIndex).m_cSubsystems.size() > 1)
                    tabLayout.setVisibility(View.VISIBLE);
                else
                    tabLayout.setVisibility(View.GONE);

                mViewPager.getAdapter().notifyDataSetChanged();
            }
        }
        if(id == R.id.action_debug)
        {
            //Intent intent = new Intent(this, LogActivity.class);
            //Bundle b = new Bundle();
            //b.putStringArrayList("log", m_cLogLines);
            //intent.getExtras().putStringArrayList("log", m_cLogLines);
            //intent.putExtras(b);
            //startActivity(intent);
            LogActivity newFragment = LogActivity.newInstance(Logger.Instance.m_cDebugLines, "Отладочная информация:");
            newFragment.show(getFragmentManager(), "debugDialog");
        }
        if(id == R.id.action_connection)
        {
            //Intent intent = new Intent(this, LogActivity.class);
            //Bundle b = new Bundle();
            //b.putStringArrayList("log", m_cLogLines);
            //intent.getExtras().putStringArrayList("log", m_cLogLines);
            //intent.putExtras(b);
            //startActivity(intent);
            LogActivity newFragment = LogActivity.newInstance(m_cLogLines, "Состояние связи с оборудованием:");
            newFragment.show(getFragmentManager(), "logDialog");
        }
        if (id == R.id.action_exit)
        {
            SafeExit();
            finish();
            System.exit(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SwitchToSubsystem(String subsystemId)
    {
        for (Subsystem pSubsystem : Config.Instance.m_cSubsystems)
        {
            if (pSubsystem.m_sID.equals(subsystemId))
            {
                getSupportActionBar().setTitle(pSubsystem.m_pRoom.m_sName);

                Log.d("111111111111111", "Switching to room #" + pSubsystem.m_pRoom.m_iIndex);

                //mSectionsPagerAdapter = (SectionsPagerAdapter)mViewPager.getAdapter();
                mSectionsPagerAdapter.m_iRoom = pSubsystem.m_pRoom.m_iIndex;
                mViewPager.getAdapter().notifyDataSetChanged();

                tabLayout.removeAllTabs();
                for (Subsystem pRoomSubsystem : pSubsystem.m_pRoom.m_cSubsystems)
                {
                    TabLayout.Tab newTab = tabLayout.newTab();
                    //if(pSubsystem.m_sName.equalsIgnoreCase("0"))
                    //    newTab.setIcon(R.drawable.tab_light_indicator);
                    //else
                    //    newTab.setIcon(R.drawable.tab_media_indicator);
                    newTab.setText(pSubsystem.m_sName);
                    tabLayout.addTab(newTab);
                }
                tabLayout.getTabAt(pSubsystem.m_pRoom.m_cSubsystems.indexOf(pSubsystem)).select();

                if(pSubsystem.m_pRoom.m_cSubsystems.size() > 1)
                    tabLayout.setVisibility(View.VISIBLE);
                else
                    tabLayout.setVisibility(View.GONE);

                mViewPager.getAdapter().notifyDataSetChanged();
                break;
            }
        }

    }

    private boolean m_bStarted = false;

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();

        Log.d("SF", "onResume, calling StopCheckAlarmShedule()" );
        StopCheckAlarmShedule();

        if(SFServer.Instance != null)
            SFServer.Instance.Resume();

        m_bStarted = true;
        UpdateConnectionStatus();
    }

    public ArrayList<String> m_cLogLines = new ArrayList<String>();

    private boolean m_bOldAlarm = false;

    private void UpdateAlarmStatus()
    {
        if(!m_bStarted)
            return;

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //final Menu menu = navigationView.getMenu();

        boolean bAlarm = false;
        int alarmsCount = 0;
        if(Config.Instance != null)
        {
            for (Room pRoom : Config.Instance.m_cRooms)
            {
                boolean bRoomAlarm = false;
                for (Subsystem pSubsystem : pRoom.m_cSubsystems)
                {
                    int alarmedCount = pSubsystem.GetAlarmedCount();
                    if (alarmedCount > 0)
                    {
                        bRoomAlarm = true;
                        alarmsCount += alarmedCount;
                    }
                }

                List<ExpandedMenuModel> cList = m_cMenu.get(pRoom.m_iIndex);

                //Log.d("UpdateAlarmStatus", "Got item " + pRoom.m_iIndex + " = " + pItem);

                if (bRoomAlarm)
                {
                    bAlarm = true;
                }

                if (cList != null)
                {
                    for (ExpandedMenuModel pItem : cList)
                        pItem.setIcon(bRoomAlarm ? R.drawable.ic_alarm : R.drawable.ic_ok);
                }
                else
                    Log.d("UpdateAlarmStatus", "Can't get menu item " + pRoom.m_iIndex);
            }
        }
        else
            Log.e("UpdateAlarmStatus", "Config.Instance is NULL !!!!");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final MenuItem alarmItem =
                toolbar != null ? toolbar.getMenu().findItem(R.id.action_alarm) : null;
        if(alarmItem != null) {
            //alarmItem.setIcon(bAlarm ? R.drawable.ic_alarm : R.drawable.ic_ok);

            ImageView imageView = (ImageView)MenuItemCompat.getActionView(alarmItem);
            if(imageView == null)
            {
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                if (inflater != null)
                {
                    imageView = (ImageView) inflater.inflate(R.layout.alarm_refresh, null);
                    imageView.setImageResource(R.drawable.ic_ok);

                    imageView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            onOptionsItemSelected(alarmItem);
                            return false;
                        }
                    });

                    MenuItemCompat.setActionView(alarmItem, imageView);
                }
            }

            if(imageView != null && bAlarm != m_bOldAlarm)
            {
                if(bAlarm)
                {
                    imageView.setImageResource(R.drawable.ic_alarm);

                    Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
                    rotation.setRepeatCount(Animation.INFINITE);
                    imageView.startAnimation(rotation);
                }
                else
                {
                    imageView.setImageResource(R.drawable.ic_ok);

                    imageView.clearAnimation();
                }
            }

            m_bOldAlarm = bAlarm;

            if(bAlarm)
                showForegroundNotification(1, R.drawable.ic_alarm, getString(R.string.notifyAlarms) + alarmsCount);
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

    private void showForegroundNotification(int notifyId, int icon, String contentText) {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        //Log.d("MainActivity", "showForegroundNotification");
        Intent showTaskIntent = new Intent(getApplicationContext(), LaunchScreenActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
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

        if(Prefs.getSound(this))
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
        }
        //Log.d("MainActivity", "mNotificationManager.notify() - OK");
    }

    public void UpdateConnectionStatus()
    {
        if(!m_bStarted)
            return;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        MenuItem alarmItem = null;
        if (toolbar != null)
        {
            alarmItem = toolbar.getMenu().findItem(R.id.action_connection);
        }
        if(alarmItem != null)
            alarmItem.setIcon(SFServer.IsConnected() ? R.drawable.ic_connect : R.drawable.ic_disconnect);

        if(!SFServer.IsConnected() && Prefs.getConnectionNotify(this))
        {
            showForegroundNotification(2, R.drawable.ic_disconnect, getString(R.string.notiyNoCarrier));
        }
        else
        {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null)
            {
                mNotificationManager.cancel(2);
            }
        }

        UpdateAlarmStatus();
    }

    private void SafeExit()
    {
        String str = Save();
        Log.d("SF", "onPause, saving = '" + str + "'");
        // Save the current puzzle
        getPreferences(MODE_PRIVATE).edit().putString(PREF_INDICATORS, str).apply();

        try
        {
            if (SFServer.Instance != null)
                SFServer.Instance.Stop();
        }
        catch (Exception e)
        {
            Log.e("SF", "SFServer.Instance.Stop() exception: ", e);
        }
        Log.d("SF", "onPause, StartCheckAlarmShedule()...");
        StartCheckAlarmShedule();
    }

    @Override
    protected void onPause()
    {
        m_bStarted = false;
        super.onPause();
        SafeExit();
    }

    public void Load(String indicators)
    {
        Log.d("Glindor3","MainActivity Load 0");

        if(Config.Instance == null)
            return;

        if(Config.Instance.m_cSubsystems == null || Config.Instance.m_cRooms == null)
            return;

        if(Config.Instance.m_cSubsystems.size() == 0)
            return;

        if(Config.Instance.m_cRooms.size() == 0)
            return;
        Log.d("Glindor3","MainActivity Load 1");

        int iIndCount = 0;
        for (Subsystem pSubsystem : Config.Instance.m_cSubsystems)
            iIndCount += pSubsystem.getIndicatorsCount();
        Log.d("Glindor3","MainActivity Load 2");

        if(indicators.length() < iIndCount + 1)
        {
            mSectionsPagerAdapter.m_iRoom = 0;
            mViewPager.getAdapter().notifyDataSetChanged();

            tabLayout.removeAllTabs();
            for (Subsystem pSubsystem : Config.Instance.m_cRooms.get(mSectionsPagerAdapter.m_iRoom).m_cSubsystems) {
                TabLayout.Tab newTab = tabLayout.newTab();
                //if(pSubsystem.m_sName.equalsIgnoreCase("0"))
                //    newTab.setIcon(R.drawable.tab_light_indicator);
                //else
                //    newTab.setIcon(R.drawable.tab_media_indicator);
                newTab.setText(pSubsystem.m_sName);
                tabLayout.addTab(newTab);
            }
            tabLayout.getTabAt(0).select();

            if(Config.Instance.m_cRooms.get(mSectionsPagerAdapter.m_iRoom).m_cSubsystems.size() > 1) {
                tabLayout.setVisibility(View.VISIBLE);
            }
            else
                tabLayout.setVisibility(View.GONE);

            getSupportActionBar().setTitle(Config.Instance.m_cRooms.get(mSectionsPagerAdapter.m_iRoom).m_sName);
            return;
        }
        Log.d("Glindor3","MainActivity Load 3");

        if(Prefs.getStart(this) == 2)
            mSectionsPagerAdapter.m_iRoom = indicators.charAt(0);
        else
            mSectionsPagerAdapter.m_iRoom = 0;

        if(mSectionsPagerAdapter.m_iRoom >= Config.Instance.m_cRooms.size())
            mSectionsPagerAdapter.m_iRoom = 0;
        Log.d("Glindor3","MainActivity Load 4");

        mViewPager.getAdapter().notifyDataSetChanged();
        Log.d("Glindor3","MainActivity Load 5");

        tabLayout.removeAllTabs();
        for (Subsystem pSubsystem : Config.Instance.m_cRooms.get(mSectionsPagerAdapter.m_iRoom).m_cSubsystems) {
            TabLayout.Tab newTab = tabLayout.newTab();
            //if(pSubsystem.m_sName.equalsIgnoreCase("0"))
            //    newTab.setIcon(R.drawable.tab_light_indicator);
            //else
            //    newTab.setIcon(R.drawable.tab_media_indicator);
            newTab.setText(pSubsystem.m_sName);
            tabLayout.addTab(newTab);
        }
        Log.d("Glindor3","MainActivity Load 6");

        tabLayout.getTabAt(0).select();
        Log.d("Glindor3","MainActivity Load 7");

        if(Config.Instance.m_cRooms.get(mSectionsPagerAdapter.m_iRoom).m_cSubsystems.size() > 1) {
            tabLayout.setVisibility(View.VISIBLE);
        }
        else
            tabLayout.setVisibility(View.GONE);
        Log.d("Glindor3","MainActivity Load 9");

        getSupportActionBar().setTitle(Config.Instance.m_cRooms.get(mSectionsPagerAdapter.m_iRoom).m_sName);
        Log.d("Glindor3","MainActivity Load 10");

        int iPos = 1;
        for (Subsystem pSubsystem : Config.Instance.m_cSubsystems)
            iPos = pSubsystem.Load(indicators, iPos);
        Log.d("Glindor3","MainActivity Load 11");

    }

    public String Save()
    {
        StringBuilder sRes = new StringBuilder();

        sRes.append(String.valueOf((char) mSectionsPagerAdapter.m_iRoom));

        for (Subsystem pSubsystem : Config.Instance.m_cSubsystems)
        {
            if(pSubsystem != null)
                sRes.append(pSubsystem.Save());
        }

        return sRes.toString();
    }

    public void Imitate()
    {
        UpdateAlarmStatus();
        if(Config.DEMO)
        {
            for (Subsystem pSubsystem : Config.Instance.m_cSubsystems)
            {
                pSubsystem.Imitate();
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter
    {
        public int m_iRoom;

        public SectionsPagerAdapter(FragmentManager fm, int room)
        {
            super(fm);
            m_iRoom = room;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.d("111111111111111", "Creating fragment #" + position + " for room #" + m_iRoom);
            return PlaceholderFragment.newInstance(m_iRoom, position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            if(Config.Instance == null || Config.Instance.m_cRooms.size() <= m_iRoom)
                return 0;
            return Config.Instance.m_cRooms.get(m_iRoom).m_cSubsystems.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Config.Instance.m_cRooms.get(m_iRoom).m_cSubsystems.get(position).m_sName;
        }
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_ROOM_NUMBER = "room_number";
        private static final String ARG_SUBSYSTEM_NUMBER = "subsystem_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int room, int subsystem) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_ROOM_NUMBER, room);
            args.putInt(ARG_SUBSYSTEM_NUMBER, subsystem);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        private float lastX;
        private ViewFlipper viewFlipper;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            Log.d("111111111111111", "Create View for room #" + getArguments().getInt(ARG_ROOM_NUMBER) + ", subsystem #" + getArguments().getInt(ARG_SUBSYSTEM_NUMBER));
            View rootView = new SubsystemUI(SFServer.Instance, getContext(), Config.Instance.m_cRooms.get(getArguments().getInt(ARG_ROOM_NUMBER)).m_cSubsystems.get(getArguments().getInt(ARG_SUBSYSTEM_NUMBER)));

            SFServer.Instance.m_pRoomQuery = Config.Instance.m_cRooms.get(getArguments().getInt(ARG_ROOM_NUMBER)).m_cSubsystems.get(getArguments().getInt(ARG_SUBSYSTEM_NUMBER)).GetQueryString();

            Log.d("TCP", "Adding rooms...");

            for (Subsystem pSubsystem : Config.Instance.m_cSubsystems)
            {
                SFServer.Instance.m_pRoomQuery.Add(pSubsystem.m_sAlarm, pSubsystem);
            }

            SFServer.Instance.m_pRoomQuery.Build(getContext());
            SFServer.Instance.Poll(false);

            //View rootView = Config.Instance.m_cSubsystems.get(getArguments().getInt(ARG_ROOM_NUMBER)).m_pUI;
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, ));
/*            switch (getArguments().getInt(ARG_SECTION_NUMBER))
            {
                case 1:
                    rootView = inflater.inflate(R.layout.content3_main_view, container, false);
                    viewFlipper = (ViewFlipper) rootView.findViewById(R.id.viewflipper);
                    rootView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent touchevent) {
                            switch (touchevent.getAction()) {

                                case MotionEvent.ACTION_DOWN:
                                    lastX = touchevent.getX();
                                    break;
                                case MotionEvent.ACTION_UP:
                                    float currentX = touchevent.getX();

                                    // Handling left to right screen swap.
                                    if (lastX < currentX) {

                                        // If there aren't any other children, just break.
                                        if (viewFlipper.getDisplayedChild() == 0)
                                            break;

                                        // Next screen comes in from left.
                                        viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                                        // Current screen goes out from right.
                                        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);

                                        // Display next screen.
                                        viewFlipper.showNext();
                                    }

                                    // Handling right to left screen swap.
                                    if (lastX > currentX) {

                                        // If there is a child (to the left), kust break.
                                        if (viewFlipper.getDisplayedChild() == 1)
                                            break;

                                        // Next screen comes in from right.
                                        viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
                                        // Current screen goes out from left.
                                        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);

                                        // Display previous screen.
                                        viewFlipper.showPrevious();
                                    }
                                    break;
                            }
                            return false;
                        }
                    });

//                    ImageView image4 = (ImageView) rootView.findViewById(R.id.imageView14);
//                    image4.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            startActivity(new Intent(getActivity(), LoginActivity.class));
//                        }
//                    });
//                    ImageView image5 = (ImageView) rootView.findViewById(R.id.imageView15);
//                    image5.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            startActivity(new Intent(getActivity(), ScrollingActivity.class));
//                        }
//                    });

                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.content_main_view, container, false);
                    break;
            }*/
            return rootView;
        }
    }
}
