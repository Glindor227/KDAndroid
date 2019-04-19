package ru.IDIS;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.idis.android.redx.ConnectionType;
import com.idis.android.redx.RDeviceInfo;
import com.idis.android.redx.annotation.JNIimplement;

/**
 * Created by weareff on 22/05/2017.
 */

@JNIimplement
public class SiteInfo
    implements Parcelable
{
    public static final int DEFAULT_WATCH_PORT = 8016;
    public static final int DEFAULT_SEARCH_PORT = 10019;
    public static final int DEFAULT_AUDIO_PORT = 8116;

    public int mConnectionType = ConnectionType.DIRECT;
    public String mAddress = "";
    public int mWatchPort = DEFAULT_WATCH_PORT;
    public int mSearchPort = DEFAULT_SEARCH_PORT;
    public int mAudioPort = DEFAULT_AUDIO_PORT;
    public boolean mUnityPort = true;

    public String mUserId = "admin";
    public String mPassword = "";

    @JNIimplement
    public SiteInfo() {}

    public void fromRDeviceInfo(RDeviceInfo di) {
        mConnectionType = di.useFen() ? ConnectionType.VIA_DVRNS : ConnectionType.DIRECT;
        mAddress = di.address().toString();
        mWatchPort = di.portWatch();
        mSearchPort = di.portSearch();
        mAudioPort = di.portAudio();
        mUnityPort = di.isUnity();

        mUserId = di.userId().toString();
        mPassword = di.password().toString();
    }

    @JNIimplement
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @JNIimplement
    public void writeToParcel(Parcel dest, int flags)
    {
        // TODO Auto-generated method stub
        dest.writeInt(mConnectionType);
        dest.writeString(mAddress);
        dest.writeInt(mWatchPort);
        dest.writeInt(mSearchPort);
        dest.writeInt(mAudioPort);
        dest.writeInt(mUnityPort ? 1 : 0);

        dest.writeString(mUserId);
        dest.writeString(mPassword);
    }

    @JNIimplement
    public static final Parcelable.Creator<SiteInfo> CREATOR = new Parcelable.Creator<SiteInfo>() {
        @JNIimplement
        public SiteInfo createFromParcel(Parcel source)
        {
            // TODO Auto-generated method stub
            SiteInfo lhs = new SiteInfo();
            lhs.mConnectionType = source.readInt();
            lhs.mAddress = source.readString();
            lhs.mWatchPort = source.readInt();
            lhs.mSearchPort = source.readInt();
            lhs.mAudioPort = source.readInt();
            lhs.mUnityPort = source.readInt() == 1;

            lhs.mUserId = source.readString();
            lhs.mPassword = source.readString();
            return lhs;
        }
        @JNIimplement
        public SiteInfo[] newArray(int size)
        {
            // TODO Auto-generated method stub
            return new SiteInfo[size];
        }
    };

    public void toSharedPreference(Context context, String name) {

        SharedPreferences.Editor pref = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit();

        pref.putInt("mConnectionType", mConnectionType);
        pref.putString("mAddress", mAddress);
        pref.putInt("mWatchPort", mWatchPort);
        pref.putInt("mSearchPort", mSearchPort);
        pref.putInt("mAudioPort", mAudioPort);
        pref.putBoolean("mUnityPort", mUnityPort);

        pref.putString("mUserId", mUserId);
        pref.putString("mPassword", mPassword);

        pref.commit();
    }

    public void fromSharedPreference(Context context, String name) {

        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);

        mConnectionType = pref.getInt("mConnectionType", mConnectionType);
        mAddress = pref.getString("mAddress", mAddress);
        mWatchPort = pref.getInt("mWatchPort", mWatchPort);
        mSearchPort = pref.getInt("mSearchPort", mSearchPort);
        mAudioPort = pref.getInt("mAudioPort", mAudioPort);
        mUnityPort = pref.getBoolean("mUnityPort", mUnityPort);

        mUserId = pref.getString("mUserId", mUserId);
        mPassword = pref.getString("mPassword", mPassword);
    }
}
