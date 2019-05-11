package ru.cpc.smartflatview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.idis.android.redx.ConnectionType;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class CamVideo extends Indicator
{
    public int mConnectionType = ConnectionType.DIRECT;
    public boolean mUnityPort = true;
    public String m_sIP = "46.148.195.2";
    public int m_iPort = 8016;
    public int m_iCamID = 0;
    public String m_sLogin = "andrey";
    public String m_sPass = "42s7t58IDIS!";

    public CamVideo(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
    {
        super(iX, iY, R.drawable.cam1, 1, sName, bMetaInd, bProtected, bDoubleScale, false, iReaction, iScale);
    }

    public CamVideo Bind(String sIP, String sPort, String sCamID, String sLogin, String sPass)
    {
        m_sIP = sIP;
        m_iPort = Integer.valueOf(sPort);
        m_iCamID = Integer.valueOf(sCamID);
        m_sLogin = sLogin;
        m_sPass = sPass;

        return this;
    }

    @Override
    public void GetAddresses(AddressString sAddr)
    {
    }

    @Override
    public boolean Process(String sAddr, String sVal)
    {
        //Update();
        return false;
    }

    @Override
    public boolean SwitchOnOff(float iX, float iY)
    {
        return Update();
    }

    @Override
    public boolean SetValue(float iX, float iY)
    {
        return false;
    }

    @Override
    public boolean SetValue(int iValue)
    {
        return Update();
    }

    @Override
    public boolean IsAlarmed()
    {
        return false;
    }

    @Override
    public void Imitate()
    {
    }

    @Override
    protected boolean Update()
    {
        return false;
    }

    @Override
    public void Load(char code)
    {
        Update();
    }

    @Override
    public String Save()
    {
        return "";
    }

    @Override
    public boolean ShowPopup(Context context) {
        Log.v("IDISWatchDialog","IDISWatchDialog show+");
        try {
            IDISWatchDialog dlg = new IDISWatchDialog();
            dlg.Init(this);
            dlg.show(((Activity) context).getFragmentManager(), "dlg");
        }catch (Exception e)
        {
            Log.v("IDISWatchDialog","IDISWatchDialog Exception");
            Log.v("IDISWatchDialog",e.getStackTrace().toString());

        }
        finally {
            Log.v("IDISWatchDialog","IDISWatchDialog show-");
        }

        return false;
    }

    @Override
    public void SaveXML(XmlSerializer serializer)
    {
        try
        {
            serializer.startTag(null, "IDIS");
            WriteCommonAttributes(serializer);
            serializer.attribute(null, "IP", m_sIP);
            serializer.attribute(null, "port", String.valueOf(m_iPort));
            serializer.attribute(null, "camId", String.valueOf(m_iCamID));
            serializer.attribute(null, "id_user", m_sLogin);
            serializer.attribute(null, "password", m_sPass);
            serializer.endTag(null, "IDIS");
        }
        catch (IllegalArgumentException e)
        {
            Log.v("Glindor",e.getMessage());
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            Log.v("Glindor",e.getMessage());
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.v("Glindor",e.getMessage());
            e.printStackTrace();
        }
    }
}
