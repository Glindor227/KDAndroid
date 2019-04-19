package ru.cpc.smartflatview;

import java.util.ArrayList;

public class Subsystem
{
	public int m_iIndex;
	public String m_sID;

	public String m_sName;

	protected ArrayList<Indicator> m_cIndicators = new ArrayList<Indicator>();
	protected int m_iGridWidth;
	protected int m_iGridHeight;

	protected Room m_pRoom = null;

	protected SubsystemUI m_pUI = null;

	public String m_sAlarm = "-1";
	
	public Subsystem(String sID, int index, int iScaleX, int iScaleY, String sName)
	{
		m_sID = sID;

		m_iIndex = index;

		m_iGridWidth = iScaleX;
		m_iGridHeight = iScaleY;
		m_sName = sName;
	}

	public Subsystem Bind(String sAddress)
	{
		m_sAlarm = sAddress;
		
		//Log.d("TEST", m_sName + ".alarmaddress = " + sAddress);
		
		return this;
	}

	public void AddIndicator(Indicator pNewInd)
	{
		if(pNewInd != null)
		{
			m_cIndicators.add(pNewInd);
			pNewInd.m_pSubsystem = this;
		}
	}

	protected boolean m_bAlarmed = false;
	
	public boolean IsAlarmed()
	{
		return m_bAlarmed;
	}
	
	public void Process(String sAddr, String sVal) 
	{
		if(m_sAlarm.equalsIgnoreCase(sAddr))
			m_bAlarmed = Float.parseFloat(sVal) == 1;

		m_pUI.Update();
	}	
	
	public int getIndicatorsCount()
	{
		return m_cIndicators.size();
	}

	public int Load(String indicators, int iPos) 
	{
		for(Indicator pInd : m_cIndicators)
			pInd.Load(indicators.charAt(iPos++));
		
		return iPos;
	}

	public String Save() 
	{
		StringBuilder sRes = new StringBuilder();
		
		for(Indicator pInd : m_cIndicators)
			sRes.append(pInd.Save());
			
		return sRes.toString();
	}
	
	public void Imitate() 
	{
		for(Indicator pInd : m_cIndicators)
			pInd.Imitate();
		
		CheckDemoAlarm();
	}

	public void CheckDemoAlarm()
	{
		m_bAlarmed = false;
		
		for(Indicator pInd : m_cIndicators)
			if(pInd.IsAlarmed())
			{
				m_bAlarmed = true;
				break;
			}

		if(m_pUI != null)
			m_pUI.Update();
	}

	public int GetAlarmedCount()
	{
		int count = 0;
		for(Indicator pInd : m_cIndicators)
		{
			if (pInd.IsAlarmed())
			{
				count++;
			}
		}

		return count;
	}
	
	public AddressString GetQueryString()
	{
		AddressString pAddr = new AddressString();
		for(Indicator pInd : m_cIndicators)
			pInd.GetAddresses(pAddr);
		
		return pAddr;
	}
	
	@Override
    public String toString() 
    {
        return m_sName;
    }
}
