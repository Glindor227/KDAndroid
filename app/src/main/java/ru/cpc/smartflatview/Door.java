package ru.cpc.smartflatview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.Random;

public class Door extends Indicator 
{
	public boolean m_bOpened = false;
	public boolean m_bGuard = false;	
	public boolean m_bAlarm = false;	
	
	public Door(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
		super(iX, iY, R.drawable.id102, 1, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
	}

	public String m_sVariableOpened = "-1";
	public String m_sVariableGuard = "-1";
	public String m_sVariableAlarm = "-1";
	public String m_sVariableUnlockCommand = "-1";
	
	public Door Bind(String sAddressGuard, String sAddressOpened, String sAddressAlarm, String sVariableUnlockCommand)
	{
		m_sVariableGuard = sAddressGuard;
		m_sVariableOpened = sAddressOpened;
		m_sVariableAlarm = sAddressAlarm;
		m_sVariableUnlockCommand = sVariableUnlockCommand;
		
		return this;
	}

	@Override
	public void GetAddresses(AddressString sAddr)
	{
		sAddr.Add(m_sVariableGuard, this);
		sAddr.Add(m_sVariableOpened, this);
		sAddr.Add(m_sVariableAlarm, this);
	}

	@Override
	public boolean Process(String sAddr, String sVal)
	{
		boolean bGuardOld = m_bGuard;
		boolean bOpenedOld = m_bOpened;
		boolean bAlarmOld = m_bAlarm;

		if(m_sVariableGuard.equalsIgnoreCase(sAddr))
			m_bGuard = Float.parseFloat(sVal) > 0;
		
		if(m_sVariableOpened.equalsIgnoreCase(sAddr))
			m_bOpened = Float.parseFloat(sVal) > 0;
			
		if(m_sVariableAlarm.equalsIgnoreCase(sAddr))
			m_bAlarm = Float.parseFloat(sVal) > 0;

		if(m_bGuard != bGuardOld || m_bOpened != bOpenedOld || m_bAlarm != bAlarmOld)
		    return Update();

		return false;
	}
	
	@Override
	public boolean SwitchOnOff(float iX, float iY)
	{
		m_bGuard = !m_bGuard;
		
		if(Config.DEMO && !m_bGuard)
			m_bAlarm = false;
		
		if(Config.DEMO && m_bGuard && m_bOpened)
			m_bAlarm = true;
			
		m_pServer.SendCommand(m_sVariableGuard, m_bGuard ? "1" : "0");
		
		return Update();
	}

	@Override
	public boolean SetValue(float iX, float iY) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean SetValue(int iValue) 
	{
		//if(iValue > 0)
		{
			m_pServer.SendCommand(m_sVariableUnlockCommand, "0");
			m_pServer.SendCommand(m_sVariableUnlockCommand, "1");
			
			if(Config.DEMO)
				m_bOpened = !m_bOpened;
		
			if(Config.DEMO && m_bGuard && m_bOpened)
				m_bAlarm = true;
		}
		
		return Update();
	}

	@Override
	public boolean IsAlarmed() 
	{
		return m_bAlarm;
	}

	@Override
	public void Imitate() 
	{
		Random rnd = new Random();
		if(!m_bMetaIndicator)
		{
			if(rnd.nextInt(m_bOpened ? 6 : 9) == 1)
			{
				m_bOpened = !m_bOpened;
				if(m_bOpened && m_bGuard)
					m_bAlarm = true;
				Update();
			}
		}
	}
	
	@Override
	protected boolean Update() 
	{
		int iResId = -1;
		
		if(m_bAlarm && m_pUI != null)
		{
			m_pUI.StartAnimation(R.drawable.id100);
			
			if(m_bOpened)
				iResId = R.drawable.id105;
			else
				iResId = R.drawable.id103;
		}
		else
		{
			if(m_bGuard)
				iResId = R.drawable.id100;
			else
			{
				if(m_bOpened)
					iResId = R.drawable.id106;
				else
					iResId = R.drawable.id102;
			}
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
		m_bOpened = false;

		if(code == '1')
		{
			m_bAlarm = false;
			m_bGuard = true;
			m_bOpened = false;
		}
		if(code == '2')
		{
			m_bAlarm = true;
			m_bGuard = true;
			m_bOpened = false;
		}
		if(code == '3')
		{
			m_bAlarm = true;
			m_bGuard = true;
			m_bOpened = true;
		}
		if(code == '4')
		{
			m_bAlarm = false;
			m_bGuard = false;
			m_bOpened = true;
		}
		Update();
	}

	@Override
	public String Save() 
	{
		if(m_bAlarm)
		{
			if(m_bOpened)
				return "3";
			else
				return "2";
		}
		else
		{
			if(m_bGuard)
				return "1";
			else
			{
				if(m_bOpened)
					return "4";
				else
					return "0";
			}
		}
	}

	@Override
	public boolean ShowPopup(Context context) {
		ScrollingDialog.Init(m_sName, m_pSubsystem.m_sName);
		final ScrollingDialog.SFSwitcher pSwitcher = (ScrollingDialog.SFSwitcher)ScrollingDialog.AddSwitcher(m_sVariableGuard, context.getString(R.string.sdGuard), m_bGuard, m_iReaction != 0
                                                                                                                                                                              ? null : new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SwitchOnOff(0, 0);
			}
		});
		final ScrollingDialog.SFSwitcher pSwitcher2 = (ScrollingDialog.SFSwitcher)ScrollingDialog.AddSwitcher(m_sVariableUnlockCommand, context.getString(
						R.string.sdLocked), !m_bOpened, m_iReaction != 0 ? null : new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SetValue(isChecked ? 0 : 1);
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

					if(m_bOpened == pSwitcher2.m_bChecked)
						SetValue(pSwitcher2.m_bChecked ? 0:1);
				}
			});

		ScrollingDialog dlg = new ScrollingDialog();
		dlg.show(((Activity) context).getFragmentManager(), "dlg");

//		v.show();
		return false;
	}
	
	@Override
	public void SaveXML(XmlSerializer serializer) 
	{
        try 
        {
			serializer.startTag(null, "door");
			WriteCommonAttributes(serializer);
            serializer.attribute(null, "guardaddress", m_sVariableGuard);
            serializer.attribute(null, "openstateaddress", m_sVariableOpened);
            serializer.attribute(null, "alarmaddress", m_sVariableAlarm);
            serializer.attribute(null, "opencommandaddress", m_sVariableUnlockCommand);
	        serializer.endTag(null, "door");	
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
