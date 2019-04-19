package ru.cpc.smartflatview;

import android.util.Log;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by Вик on 028. 28. 12. 16.
 */

public abstract class BaseClimat extends Indicator
{
    protected static final String TAG = "SMARTFLAT" ;

    protected BaseClimat(float fX, float fY, int iResID, int iSubType,
                            String sName, boolean bMetaInd, boolean bProtected,
                            boolean bDoubleSize, boolean bQuick, int iReaction, int iScale)
    {
        super(fX, fY, iResID, iSubType, sName, bMetaInd, bProtected, bDoubleSize, bQuick, iReaction,
                iScale);
        // TODO Auto-generated constructor stub
    }

    public int m_iTemp = 0;
    public int m_iSpeed = 0;
    public int m_iMode = 0;
    public int m_iDefaultMode = 0;
    public boolean m_bPower = false;

    public String m_sVariableTemp = "-1";
    public String m_sVariableSpeed = "-1";
    public String m_sVariablePower = "-1";
    public String m_sVariableMode = "-1";
    public float m_fValueOn = 1;
    public float m_fValueOff = 0;

    public float m_fTempMin = 16;
    public float m_fTempMax = 30;

    public float m_fSpeedMin = 16;
    public float m_fSpeedMax = 30;

    protected boolean m_bNoPower = false;

    public BaseClimat Bind(String sAddressPower, String sAddressTemp, String sAddressSpeed, String sAddressMode, String sValueOn, String sValueOff, boolean bNoPower, String sTempMin, String sTempMax, String sSpeedMin, String sSpeedMax, String sDefMode)
    {
        m_sVariablePower = sAddressPower;
        m_sVariableTemp = sAddressTemp;
        m_sVariableSpeed = sAddressSpeed;
        m_sVariableMode = sAddressMode;

        Log.d(TAG, "power = '" + m_sVariablePower + "'");
        Log.d(TAG, "temp = '" + m_sVariableTemp  + "'");
        Log.d(TAG, "speed = '" + m_sVariableSpeed  + "'");
        Log.d(TAG, "mode = '" + m_sVariableMode  + "'");

        m_fValueOn = Float.parseFloat(sValueOn);
        m_fValueOff = Float.parseFloat(sValueOff);

        m_fTempMin = Float.parseFloat(sTempMin);
        m_fTempMax = Float.parseFloat(sTempMax);
        m_fSpeedMin = Float.parseFloat(sSpeedMin);
        m_fSpeedMax = Float.parseFloat(sSpeedMax);

        m_bNoPower = bNoPower;

        m_iDefaultMode = Integer.parseInt(sDefMode);

        m_iMode = m_iDefaultMode;

        return this;
    }

    @Override
    public void GetAddresses(AddressString sAddr)
    {
        sAddr.Add(m_sVariablePower, this);
        sAddr.Add(m_sVariableTemp, this);
        sAddr.Add(m_sVariableSpeed, this);
        sAddr.Add(m_sVariableMode, this);
    }

    @Override
    public boolean Process(String sAddr, String sVal)
    {
        boolean bPowerOld = m_bPower;
        int iTempOld = m_iTemp;
        int iSpeedOld = m_iSpeed;
        int iModeOld = m_iMode;

        if(m_sVariablePower.equalsIgnoreCase(sAddr))
            m_bPower = Float.parseFloat(sVal) == m_fValueOn;

        if(m_sVariableTemp.equalsIgnoreCase(sAddr))
            m_iTemp = (int) Float.parseFloat(sVal);

        if(m_sVariableSpeed.equalsIgnoreCase(sAddr))
            m_iSpeed = (int) Float.parseFloat(sVal);

        if(m_sVariableMode.equalsIgnoreCase(sAddr))
            m_iMode = (int) Float.parseFloat(sVal);

        if(m_bPower != bPowerOld || m_iTemp != iTempOld || m_iSpeed != iSpeedOld || m_iMode != iModeOld)
            return Update();

        return false;
    }

    @Override
    public boolean SwitchOnOff(float iX, float iY)
    {
        if(m_bNoPower)
            return false;

        m_bPower = !m_bPower;

        m_pServer.SendCommand(m_sVariablePower, String.valueOf(m_bPower ? m_fValueOn : m_fValueOff));

        return Update();
    }

    @Override
    public boolean SetValue(int iValue)
    {
        m_iMode = iValue;
        m_bPower = true;
        Log.d(TAG, "mode = " + m_iMode );

        m_pServer.SendCommand(m_sVariablePower, String.valueOf(m_bPower ? m_fValueOn : m_fValueOff));
        m_pServer.SendCommand(m_sVariableMode, String.valueOf(m_iMode));

        return Update();
    }

    public boolean SetTemp(int iValue)
    {
        m_iTemp = iValue;
        m_bPower = true;
        Log.d(TAG, "t = " + m_iTemp );

        m_pServer.SendCommand(m_sVariablePower, String.valueOf(m_bPower ? m_fValueOn : m_fValueOff));
        m_pServer.SendCommand(m_sVariableTemp, String.valueOf(m_iTemp));

        return Update();
    }

    public boolean SetSpeed(int iValue)
    {
        m_iSpeed = iValue;
        m_bPower = true;
        Log.d(TAG, "speed = " + m_iSpeed );

        m_pServer.SendCommand(m_sVariablePower, String.valueOf(m_bPower ? m_fValueOn : m_fValueOff));
        m_pServer.SendCommand(m_sVariableSpeed, String.valueOf(m_iSpeed));

        return Update();
    }

    @Override
    public void Load(char code)
    {
        m_bPower = code < 128;
        m_iMode = m_bPower ? code : code - 128;
        Update();
    }

    @Override
    public String Save()
    {
        return m_bPower ? String.valueOf((char)m_iMode) : String.valueOf((char)(128 + m_iMode));
    }

    @Override
    public void SaveXML(XmlSerializer serializer)
    {
        try
        {
            serializer.startTag(null, "climatic");
            WriteCommonAttributes(serializer);
            serializer.attribute(null, "poweraddress", m_sVariablePower);
            serializer.attribute(null, "tempaddress", m_sVariableTemp);
            serializer.attribute(null, "speedaddress", m_sVariableSpeed);
            serializer.attribute(null, "modeaddress", m_sVariableMode);
            serializer.attribute(null, "onvalue", String.valueOf(m_fValueOn));
            serializer.attribute(null, "offvalue", String.valueOf(m_fValueOff));
            serializer.attribute(null, "nopower", m_bNoPower ? "1":"0");
            serializer.attribute(null, "mintemp", String.valueOf(m_fTempMin));
            serializer.attribute(null, "maxtemp", String.valueOf(m_fTempMax));
            serializer.attribute(null, "minspeed", String.valueOf(m_fSpeedMin));
            serializer.attribute(null, "maxspeed", String.valueOf(m_fSpeedMax));
            serializer.attribute(null, "defaultmode", String.valueOf(m_iDefaultMode));
            serializer.endTag(null, "climatic");
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
