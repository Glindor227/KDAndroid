package ru.IDIS.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import ru.cpc.smartflatview.MainActivity;

// refer to 
// public static float applyDimension(int unit, float value, DisplayMetrics metrics)
// in TypedValue.java
public class MetricUtil {
    private static final String TAG = MetricUtil.class.getSimpleName();

    // Debug Log
    private static final boolean DEBUG_LOG = false;

    private static Context mContext = null;

    static {
        mContext = MainActivity.getAppContext();
    }

    // basic value
    
    //1 StatusBar
    //private static int mStatusBarWidthPixel = 0;  // use DisplayWidthPixel instead...
    private static int mStatusBarHeightPixel = 0;

    // TitleBar
    //private static int mTitleBarWidthPixel = 0;   // use DisplayWidthPixel instead...
    private static int mTitleBarHeightPixel = 0;

    // metric converter functions...
    public static int getPixelsFromPX(float pxValue)
    {
        // just return pixel value
        return (int)pxValue;
    }

    public static float getPXFromPixels(int pixelValue)
    {
        // just return pixel value
        return (float)pixelValue;
    }

    public static int getPixelsFromDIP(float dipValue)
    {
        if (mContext == null) {
            // SHOULD NOT ARRIVE HERE!!!
            mContext = MainActivity.getAppContext();
        }
    
        if (mContext != null) {     // simple safeguard
            Resources r = mContext.getResources();
            DisplayMetrics m = r.getDisplayMetrics();
            int pixels = (int)(dipValue * m.density);
            
            if (DEBUG_LOG) {
                Log.v(TAG, "current density = " + m.density);
                Log.v(TAG, "converted pixels = " + pixels);
            }
            
            return pixels;
        }
        else {
            return 0;
        }
    }

    public static float getDIPFromPixels(int pixelValue)
    {
        if (mContext == null) {
            // SHOULD NOT ARRIVE HERE!!!
            mContext = MainActivity.getAppContext();
        }

        if (mContext != null) { // simple safeguard
            Resources r = mContext.getResources();
            DisplayMetrics m = r.getDisplayMetrics();
            float dips = (float)((float)pixelValue / m.density);

            if (DEBUG_LOG) {
                Log.v(TAG, "current density = " + m.density);
                Log.v(TAG, "converted dips = " + dips);
            }

            return dips;
        }
        else {
            return (float)0;
        }
    }

    public static int getPixelsFromSP(float spValue)
    {
        if (mContext == null) {
            // SHOULD NOT ARRIVE HERE!!!
            mContext = MainActivity.getAppContext();
        }
    
        if (mContext != null) {    // simple safeguard
            Resources r = mContext.getResources();
            DisplayMetrics m = r.getDisplayMetrics();
            int pixels = (int)(spValue * m.scaledDensity);

            if (DEBUG_LOG) {
                Log.v(TAG, "current scaled density = " + m.scaledDensity);
                Log.v(TAG, "converted pixels = " + pixels);
            }

            return pixels;
        }
        else {
            return 0;
        }
    }

    public static float getSPFromPixels(float pixelValue)
    {
        if (mContext == null) {
            // SHOULD NOT ARRIVE HERE!!!
            mContext = MainActivity.getAppContext();
        }
    
        if (mContext != null) {    // simple safeguard
            Resources r = mContext.getResources();
            DisplayMetrics m = r.getDisplayMetrics();
            float sps = (float)(pixelValue / m.scaledDensity);

            if (DEBUG_LOG) {
                Log.v(TAG, "current scaled density = " + m.scaledDensity);
                Log.v(TAG, "converted sps = " + sps);
            }

            return sps;
        }
        else {
            return (float)0;
        }
    }

    public static int getPixelsFromPT(float ptValue)
    {
        if (mContext == null) {
            // SHOULD NOT ARRIVE HERE!!!
            mContext = MainActivity.getAppContext();
        }
    
        if (mContext != null) {    // simple safeguard
            Resources r = mContext.getResources();
            DisplayMetrics m = r.getDisplayMetrics();
            int pixels = (int)(ptValue * m.xdpi * (1.0f / 72));

            if (DEBUG_LOG) {
                Log.v(TAG, "current xdpi = " + m.xdpi);
                Log.v(TAG, "converted pixels = " + pixels);
            }

            return pixels;
        }
        else {
            return 0;
        }
    }

    public static float getPTFromPixels(int pixelValue)
    {
        if (mContext == null) {
            // SHOULD NOT ARRIVE HERE!!!
            mContext = MainActivity.getAppContext();
        }
    
        if (mContext != null) {    // simple safeguard
            Resources r = mContext.getResources();
            DisplayMetrics m = r.getDisplayMetrics();
            float pts = (float)((float)pixelValue / m.xdpi / (1.0f * 72));

            if (DEBUG_LOG) {
                Log.v(TAG, "current xdpi = " + m.xdpi);
                Log.v(TAG, "converted pixels = " + pts);
            }

            return pts;
        }
        else {
            return (float)0;
        }
    }

    public static int getPixelsFromIN(float inValue)
    {
        if (mContext == null) {
            // SHOULD NOT ARRIVE HERE!!!
            mContext = MainActivity.getAppContext();
        }
    
        if (mContext != null) {    // simple safeguard
            Resources r = mContext.getResources();
            DisplayMetrics m = r.getDisplayMetrics();
            int pixels = (int)(inValue * m.xdpi);

            if (DEBUG_LOG) {
                Log.v(TAG, "current xdpi = " + m.xdpi);
                Log.v(TAG, "converted pixels = " + pixels);
            }

            return pixels;
        }
        else {
            return 0;
        }
    }

    public static float getINFromPixels(int pixelValue)
    {
        if (mContext == null) {
            // SHOULD NOT ARRIVE HERE!!!
            mContext = MainActivity.getAppContext();
        }
    
        if (mContext != null) {    // simple safeguard
            Resources r = mContext.getResources();
            DisplayMetrics m = r.getDisplayMetrics();
            float ins = (float)((float)pixelValue / m.xdpi);

            if (DEBUG_LOG) {
                Log.v(TAG, "current xdpi = " + m.xdpi);
                Log.v(TAG, "converted ins = " + ins);
            }

            return ins;
        }
        else {
            return (float)0;
        }
    }

    public static int getPixelsFromMM(float mmValue)
    {
        if (mContext == null) {
            // SHOULD NOT ARRIVE HERE!!!
            mContext = MainActivity.getAppContext();
        }
    
        if (mContext != null) {    // simple safeguard
            Resources r = mContext.getResources();
            DisplayMetrics m = r.getDisplayMetrics();
            int pixels = (int)(mmValue * m.xdpi * (1.0f / 25.4f));

            if (DEBUG_LOG) {
                Log.v(TAG, "current xdpi = " + m.xdpi);
                Log.v(TAG, "converted pixels = " + pixels);
            }

            return pixels;
        }
        else {
            return 0;
        }
    }

    public static float getMMFromPixels(int pixelValue)
    {
        if (mContext == null) {
            // SHOULD NOT ARRIVE HERE!!!
            mContext = MainActivity.getAppContext();
        }
    
        if (mContext != null) {    // simple safeguard
            Resources r = mContext.getResources();
            DisplayMetrics m = r.getDisplayMetrics();
            float mms = (float)(pixelValue / m.xdpi / (1.0f * 25.4f));

            if (DEBUG_LOG) {
                Log.v(TAG, "current xdpi = " + m.xdpi);
                Log.v(TAG, "converted mms = " + mms);
            }

            return mms;
        }
        else {
            return (float)0;
        }
    }

    public static int getDisplayWidthPixel()
    {
        if (mContext == null) {
            // SHOULD NOT ARRIVE HERE!!!
            mContext = MainActivity.getAppContext();
        }
    
        if (mContext != null) {    // simple safeguard
            Resources r = mContext.getResources();
            DisplayMetrics m = r.getDisplayMetrics();

            if (DEBUG_LOG) {
                Log.v(TAG, "Display width pixel = " + m.widthPixels);
            }
            
            return m.widthPixels;
        }
        else {
            return 0;   // abnormal case
        }
    }

    public static int getDisplayHeightPixel()
    {
        if (mContext == null) {
            // SHOULD NOT ARRIVE HERE!!!
            mContext = MainActivity.getAppContext();
        }
    
        if (mContext != null) {    // simple safeguard
            Resources r = mContext.getResources();
            DisplayMetrics m = r.getDisplayMetrics();

            if (DEBUG_LOG) {
                Log.v(TAG, "Display height pixel = " + m.heightPixels);
            }

            return m.heightPixels;
        }
        else {
            return 0;   // abnormal case
        }
    }

    // dimension of status bar 
    public static int getStatusBarWidthPixel()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "return getDisplayWidthPixel() instead");
        }
        
        //return MetricUtil.getDisplayWidthPixel(c);
        return MetricUtil.getDisplayWidthPixel();
    }

    public static void setStatusBarWidthPixel(int widthPixel)
    {
        if (DEBUG_LOG) {
            Log.e(TAG, "this method is not working!!!");
        }
        
        // dummy
        // no op....
    }

    public static int getStatusBarHeightPixel()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "mStatusBarHeightPixel = " + mStatusBarHeightPixel);
        }

        return mStatusBarHeightPixel;
    }

    public static void setStatusBarHeightPixel(int heightPixel)
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "changing status bar height to " + heightPixel);
        }

        mStatusBarHeightPixel = heightPixel;
    }

    // dimension of title bar
    public static int getTitleBarWidthPixel()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "return getDisplayWidthPixel() instead");
        }
        
        //return MetricUtil.getDisplayWidthPixel(c);
        return MetricUtil.getDisplayWidthPixel();
    }

    public static void setTitleBarWidthPixel(int widthPixel)
    {
        if (DEBUG_LOG) {
            Log.e(TAG, "this method is not working!!!");
        }
        
        // dummy
        // no op....
    }

    public static int getTitleBarHeightPixel()
        
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "mTitleBarHeightPixel = " + mTitleBarHeightPixel);
        }

        return mTitleBarHeightPixel;
    }

    public static void setTitleBarHeightPixel(int heightPixel)
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "changing title bar height to " + heightPixel);
        }

        mTitleBarHeightPixel = heightPixel;
    }
}

