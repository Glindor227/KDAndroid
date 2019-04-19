package ru.cpc.smartflatview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.Random;

public class NodeAlarm extends Indicator
{
    protected int m_iResIDAlarmOff;
    protected int m_iResIDAlarmOn;

    protected NodeAlarm(float fX, float fY, int iResIDAlarmOn, int iResIDAlarmOff, int iSubType,
                              String sName, boolean bMetaInd, boolean bProtected,
                              boolean bDoubleSize, boolean bQuick, int iReaction, int iScale)
    {
        super(fX, fY, iResIDAlarmOff, iSubType, sName, bMetaInd, bProtected, bDoubleSize, bQuick, iReaction,
              iScale);
        // TODO Auto-generated constructor stub
        m_iResIDAlarmOn = iResIDAlarmOn;
        m_iResIDAlarmOff = iResIDAlarmOff;
    }

    public boolean m_bAlarm = false;

    public String m_sVariableAlarm = "-1";
    public float m_fValueAlarm = 1;
    public String m_sSubsystemID = "";

    public NodeAlarm Bind(String sAddressAlarm, String sValueAlarm, String sSubsystemID)
    {
        m_sVariableAlarm = sAddressAlarm;
        m_fValueAlarm = Float.parseFloat(sValueAlarm);

        m_sSubsystemID = sSubsystemID;

        return this;
    }

    @Override
    public void GetAddresses(AddressString sAddr)
    {
        sAddr.Add(m_sVariableAlarm, this);
    }

    @Override
    public boolean Process(String sAddr, String sVal)
    {
        boolean bAlarmOld = m_bAlarm;

        if(m_sVariableAlarm.equalsIgnoreCase(sAddr))
            m_bAlarm = Float.parseFloat(sVal) == m_fValueAlarm;

        if(m_bAlarm != bAlarmOld)
            return Update();

        return false;
    }
    @Override
    public boolean SwitchOnOff(float iX, float iY)
    {
        final MainActivity mainActivity = m_pServer.getMainActivity();

        if(mainActivity != null)
        {
            mainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    mainActivity.SwitchToSubsystem(m_sSubsystemID);
                }
            });
        }

        return Update();
    }

    @Override
    public boolean SetValue(float iX, float iY)
    {
        //if(m_bGuard)
        //	m_bAlarm = true;
        return Update();
    }

    @Override
    public boolean SetValue(int iValue)
    {
        //if(m_bGuard)
        //	m_bAlarm = true;
        return Update();
    }

    @Override
    public boolean IsAlarmed()
    {
        return m_bAlarm;
    }

    @Override
    public boolean ShowPopup(Context context) { return false; }

    @Override
    protected boolean Update()
    {
        int iResId = -1;

        if(m_bAlarm && m_pUI!= null)
            iResId = m_iResIDAlarmOn;
        else
            iResId = m_iResIDAlarmOff;

        if(m_pUI == null)
        {
            m_iOldResID = iResId;
            m_iNewResID = iResId;
            return false;
        }

        m_pUI.m_bLoopAnimation = m_bAlarm;
        return m_pUI.StartAnimation(iResId);
    }

    @Override
    public void Load(char code)
    {
        m_bAlarm = false;

        if(code == '1')
        {
            m_bAlarm = true;
        }
        Update();
    }

    @Override
    public String Save()
    {
        if(m_bAlarm)
            return "1";
        else
            return "0";
    }

    protected int m_iImitateAlarmChance = 15;

    @Override
    public void Imitate()
    {
        if(!m_bMetaIndicator)
        {
            Random rnd = new Random();
            if(rnd.nextInt(m_iImitateAlarmChance) == 1)
            {
                m_bAlarm = true;
                //m_bAlarm = !m_bAlarm;
                Update();
            }
        }
    }

    @Override
    public void SaveXML(XmlSerializer serializer)
    {
        try
        {
            serializer.startTag(null, "link");
            WriteCommonAttributes(serializer);
            serializer.attribute(null, "alarmaddress", m_sVariableAlarm);
            serializer.attribute(null, "alarmvalue", String.valueOf(m_fValueAlarm));
            serializer.attribute(null, "subsystem", m_sSubsystemID);
            serializer.endTag(null, "link");
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
