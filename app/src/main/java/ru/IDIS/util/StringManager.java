package ru.IDIS.util;

import android.content.Context;
import android.util.Log;

import com.idis.android.redx.CharacterSet;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import ru.cpc.smartflatview.MainActivity;

// 1. This class can provide resource string in a non-context based classes.
// 2. This class can provide locale-specific string from byte[].
public class StringManager 
{
    private static final String TAG = StringManager.class.getSimpleName();

    // Debug Log    
    private static final boolean DEBUG_LOG = false;

    private static Context mContext = null;

    static {
        mContext = MainActivity.getAppContext();
    }

    public static String getString(int resId)
    {
        if (mContext != null) {
            return mContext.getString(resId);
        }
        else {
            mContext = MainActivity.getAppContext();
            if (mContext != null) {
                return mContext.getString(resId);
            }
        }

        // SHOULD NOT ARRIVE HERE!!!!!
        if (DEBUG_LOG) {
            Log.e(TAG, "Oops, Can't retrieve MainApp Context....");
        }
        
        return null;
    }
    
    public static String getString(byte[] rawCharData, boolean fromUtf8)
    {
        String localized_string = null;
        String charset = fromUtf8 == false ? LocaleManager.getCurrentCharacterSet().toWindowsDefaultCharSetString() : CharacterSet.UTF8_CHARSET;
        
        try {
            if (fromUtf8) {
                int length = rawCharData.length;
                int i = 0;
                for (; i < length; ++i) {
                    if (rawCharData[i] == 0) {
                        break;
                    }
                }
                
                int nullTrimmedIndex = i;
                if (nullTrimmedIndex == 0) {
                    localized_string = "";
                }
                else {
                    byte[] nullTrimmed = Arrays.copyOf(rawCharData, nullTrimmedIndex);
                    localized_string = new String(nullTrimmed, charset);    
                }
                
            }
            else {
                localized_string = new String(rawCharData, charset);
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if (DEBUG_LOG) {
                Log.e(TAG, "Oops, UnsupportedEncodingException occurred.");
            }
            localized_string = new String(rawCharData);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            if (DEBUG_LOG) {
                Log.e(TAG, "Oops, NullPointerException occurred.");
            }
            localized_string = new String();
        }

        if (DEBUG_LOG) {
            Log.v(TAG, "localized string = " + localized_string);
        }

        return localized_string;
    }    

    public static String getString(byte[] rawCharData)
    {
        return getString(rawCharData, false);
    }
}

