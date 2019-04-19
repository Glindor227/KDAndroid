package ru.cpc.smartflatview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.Random;

public abstract class BaseAlarmSensor extends Indicator 
{
	protected int m_iResIDGuardOff; 
	protected int m_iResIDGuardOn; 
	protected int m_iResIDAlarm; 
	
	protected BaseAlarmSensor(float fX, float fY, int iResIDAlarm, int iResIDGuardOn, int iResIDGuardOff, int iSubType,
			String sName, boolean bMetaInd, boolean bProtected,
			boolean bDoubleSize, boolean bQuick, int iReaction, int iScale)
	{
		super(fX, fY, iResIDGuardOff, iSubType, sName, bMetaInd, bProtected, bDoubleSize, bQuick, iReaction,
				iScale);
		// TODO Auto-generated constructor stub
		m_iResIDAlarm = iResIDAlarm;
		m_iResIDGuardOff = iResIDGuardOff;
		m_iResIDGuardOn = iResIDGuardOn;
	}

	public boolean m_bGuard = false;
	public boolean m_bAlarm = false;

	public String m_sVariableGuard = "-1";
	public String m_sVariableAlarm = "-1";
	public float m_fValueGuardOn = 1;
	public float m_fValueGuardOff = 0;
	public float m_fValueAlarm = 1;
	
	public BaseAlarmSensor Bind(String sAddressAlarm, String sAddressGuard, String sValueGuardOn, String sValueGuardOff, String sValueAlarm)
	{
		m_sVariableAlarm = sAddressAlarm;
		m_sVariableGuard = sAddressGuard;
		
		m_fValueGuardOn = Float.parseFloat(sValueGuardOn);
		m_fValueGuardOff = Float.parseFloat(sValueGuardOff);
		m_fValueAlarm = Float.parseFloat(sValueAlarm);
		
		return this;
	}
	
	@Override
	public void GetAddresses(AddressString sAddr)
	{
		sAddr.Add(m_sVariableAlarm, this);
		sAddr.Add(m_sVariableGuard, this);
	}

	@Override
	public boolean Process(String sAddr, String sVal)
	{
		boolean bAlarmOld = m_bAlarm;
		boolean bGuardOld = m_bGuard;

		if(m_sVariableAlarm.equalsIgnoreCase(sAddr))
			m_bAlarm = Float.parseFloat(sVal) == m_fValueAlarm;
		
		if(m_sVariableGuard.equalsIgnoreCase(sAddr))
			m_bGuard = Float.parseFloat(sVal) == m_fValueGuardOn;

		if(m_bAlarm != bAlarmOld || m_bGuard != bGuardOld)
		    return Update();

		return false;
	}
	@Override
	public boolean SwitchOnOff(float iX, float iY) 
	{
		m_bGuard = !m_bGuard;
		
		if(Config.DEMO && !m_bGuard)
			m_bAlarm = false;

		m_pServer.SendCommand(m_sVariableGuard, String.valueOf(m_bGuard ? m_fValueGuardOn : m_fValueGuardOff));
		
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
	public boolean ShowPopup(Context context) {
		ScrollingDialog.Init(m_sName, m_pSubsystem.m_sName);
		final ScrollingDialog.SFSwitcher pSwitcher = (ScrollingDialog.SFSwitcher)ScrollingDialog.AddSwitcher(m_sVariableGuard, context.getString(
						R.string.sdGuard), m_bGuard, m_iReaction != 0 ? null : new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SwitchOnOff(0, 0);
			}
		});

		//Intent myIntent = new Intent(context, ScrollingActivity.class);
		//context.startActivity(myIntent);

		if(m_iReaction == 1)
			ScrollingDialog.AddAcceptBtn(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(m_bGuard != pSwitcher.m_bChecked)
						SwitchOnOff(0, 0);
				}
			});

		ScrollingDialog dlg = new ScrollingDialog();
		dlg.show(((Activity) context).getFragmentManager(), "dlg");

//		v.show();
		return false;
	}

	@Override
	protected boolean Update() 
	{
		int iResId = -1;
		
		if(m_bAlarm && m_pUI!= null)
			iResId = m_iResIDAlarm;
		else
		{
			if(m_bGuard)
				iResId = m_iResIDGuardOn;
			else
				iResId = m_iResIDGuardOff;
		}

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
		m_bGuard = false;

		if(code == '1')
		{
			m_bAlarm = false;
			m_bGuard = true;
		}
		if(code == '2')
		{
			m_bAlarm = true;
			m_bGuard = true;
		}
		Update();
	}

	@Override
	public String Save() 
	{
		if(m_bAlarm)
			return "2";
		else
		{
			if(m_bGuard)
				return "1";
			else
				return "0";
		}
	}

	protected int m_iImitateAlarmChance = 15;
	
	@Override
	public void Imitate() 
	{
		if(!m_bMetaIndicator && m_bGuard)
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
			serializer.startTag(null, "alarmsensor");
			WriteCommonAttributes(serializer);
            serializer.attribute(null, "guardaddress", m_sVariableGuard);
            serializer.attribute(null, "alarmaddress", m_sVariableAlarm);
            serializer.attribute(null, "onvalue", String.valueOf(m_fValueGuardOn));
            serializer.attribute(null, "offvalue", String.valueOf(m_fValueGuardOff));
            serializer.attribute(null, "alarmvalue", String.valueOf(m_fValueAlarm));
            serializer.endTag(null, "alarmsensor");	
		} 
        catch (IllegalArgumentException e) 
        {
			Log.v("Glindor"," "+e.getMessage());
			e.printStackTrace();
		} 
        catch (IllegalStateException e) 
        {
			Log.v("Glindor"," "+e.getMessage());
			e.printStackTrace();
		} 
        catch (IOException e) 
        {
			Log.v("Glindor"," "+e.getMessage());
			e.printStackTrace();
		}
    }
}
