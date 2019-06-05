package ru.cpc.smartflatview;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class Prefs extends AppCompatPreferenceActivity
{
    private static final String OPT_LOGIN = "login_code" ;
    private static final String OPT_LOGIN_DEF = "";

    private static final String OPT_PREFS = "pref_code" ;
    private static final String OPT_PREFS_DEF = "";

    private static final String OPT_SEC = "security_code" ;
    private static final String OPT_SEC_DEF = "";

    private static final String OPT_FIREWALL = "fire_code" ;
    private static final String OPT_FIREWALL_DEF = "";

    private static final String OPT_START = "start" ;
    private static final String OPT_START_DEF = "2";

    private static final String OPT_EXIT = "exit" ;
    private static final boolean OPT_EXIT_DEF = true;

    private static final String OPT_NEW_DIS = "new_dis" ;
    private static final boolean OPT_NEW_DIS_DEF = true;

    private static final String OPT_POST_DIS = "post_dis" ;
    private static final boolean OPT_POST_DIS_DEF = true;

    private static final String OPT_MOBILE = "mobile" ;
    private static final boolean OPT_MOBILE_DEF = false;

    private static final String OPT_BACKGROUND = "background" ;
    private static final boolean OPT_BACKGROUND_DEF = true;

    private static final String OPT_WAKEUP = "wakeup" ;
    private static final boolean OPT_WAKEUP_DEF = true;

    private static final String OPT_SOUND = "sound" ;
    private static final boolean OPT_SOUND_DEF = true;

    private static final String OPT_CONNECTION = "connectionNotify" ;
    private static final boolean OPT_CONNECTION_DEF = true;

    private static final String OPT_IP = "ip" ;
    private static final String OPT_IP_DEF = "192.168.1.1";

    /** Get the current value of the hints option */
    public static String getLogin(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(OPT_LOGIN, OPT_LOGIN_DEF);
    }

    /** Get the current value of the hints option */
    public static String getPrefs(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(OPT_PREFS, OPT_PREFS_DEF);
    }

    public static boolean getExit(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_EXIT, OPT_EXIT_DEF);
    }

    public static boolean getNewDis(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_NEW_DIS, OPT_NEW_DIS_DEF);
    }

    public static boolean getPostDis(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_POST_DIS, OPT_POST_DIS_DEF);
    }


    public static boolean getMobile(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_MOBILE, OPT_MOBILE_DEF);
    }

    public static boolean getBackround(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_BACKGROUND, OPT_BACKGROUND_DEF);
    }

    public static boolean getWakeup(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_WAKEUP, OPT_WAKEUP_DEF);
    }

    public static boolean getSound(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_SOUND, OPT_SOUND_DEF);
    }

    public static boolean getConnectionNotify(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(OPT_CONNECTION, OPT_CONNECTION_DEF);
    }

    public static int getStart(Context context)
    {
        return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(OPT_START, OPT_START_DEF));
    }

    public static String getExternalIP(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(OPT_IP, OPT_IP_DEF);
    }

    public static String getSecurityCode(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(OPT_SEC, OPT_SEC_DEF);
    }

    public static String getFirewall(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(OPT_FIREWALL, OPT_FIREWALL_DEF);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || ConnectionPreferenceFragment.class.getName().equals(fragmentName)
                || BackgroundPreferenceFragment.class.getName().equals(fragmentName)
                || SecurityPreferenceFragment.class.getName().equals(fragmentName)
                || InterfacePreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class BackgroundPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_background);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference(OPT_IP));
//            bindPreferenceSummaryToValue(findPreference(OPT_SEC));
//            bindPreferenceSummaryToValue(findPreference(OPT_MOBILE));
        }

//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), Prefs.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ConnectionPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_connection);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference(OPT_IP));
//            bindPreferenceSummaryToValue(findPreference(OPT_SEC));
//            bindPreferenceSummaryToValue(findPreference(OPT_MOBILE));
        }

//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), Prefs.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class InterfacePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_interface);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference(OPT_START));
//            bindPreferenceSummaryToValue(findPreference(OPT_EXIT));
        }

//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), Prefs.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SecurityPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_security);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference(OPT_LOGIN));
//            bindPreferenceSummaryToValue(findPreference(OPT_FIREWALL));
//            bindPreferenceSummaryToValue(findPreference(OPT_PREFS));
        }

//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                startActivity(new Intent(getActivity(), Prefs.class));
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
    }
}
