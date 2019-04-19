package ru.cpc.smartflatview;

import android.util.Log;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public abstract class BaseRelay extends Indicator 
{
	protected int m_iResIDOn;
	protected int m_iResIDOff;
	
	protected BaseRelay(float fX, float fY, int iResIDOn, int iResIDOff, int iSubType, String sName,
			boolean bMetaInd, boolean bProtected, boolean bDoubleSize, boolean bQuick, int iReaction,
			int iScale) 
	{
		super(fX, fY, iResIDOff, iSubType, sName, bMetaInd, bProtected, bDoubleSize, bQuick, iReaction, iScale);
		// TODO Auto-generated constructor stub
		m_iResIDOn = iResIDOn;
		m_iResIDOff = iResIDOff;
	}

	public boolean m_bValue = false;
	public String m_sVariable = "-1";
	public float m_fValueOn = 1;
	public float m_fValueOff = 0;
	
	public BaseRelay Bind(String sAddress, String sValueOn, String sValueOff)
	{
		m_sVariable = sAddress;
		m_fValueOn = Float.parseFloat(sValueOn);
		m_fValueOff = Float.parseFloat(sValueOff);
		
		return this;
	}

	@Override
	public void GetAddresses(AddressString sAddr)
	{
		sAddr.Add(m_sVariable, this);
	}

	@Override
	public boolean Process(String sAddr, String sVal)
	{
		boolean bValueOld = m_bValue;

		if(m_sVariable.equalsIgnoreCase(sAddr))
			m_bValue = Float.parseFloat(sVal) == m_fValueOn;

		if(m_bValue != bValueOld)
		    return Update();

		return false;
	}	
	@Override
	public boolean SwitchOnOff(float iX, float iY) 
	{
		m_bValue = !m_bValue;

		m_pServer.SendCommand(m_sVariable, String.valueOf(m_bValue ? m_fValueOn : m_fValueOff));
		
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean Update() 
	{
		int iResId = -1;
		
		if(m_bValue)
			iResId = m_iResIDOn;
		else
			iResId = m_iResIDOff;

        if(m_pUI == null)
        {
            m_iOldResID = iResId;
            m_iNewResID = iResId;
            return false;
        }

        return m_pUI.StartAnimation(iResId);
	}

	@Override
	public void Load(char code) 
	{	
		m_bValue = code == '1';
		Update();
	}

	@Override
	public String Save() 
	{
		return m_bValue ? "1" : "0";
	}
	
	@Override
	public void SaveXML(XmlSerializer serializer) 
	{
        try 
        {
			serializer.startTag(null, "relay");
			WriteCommonAttributes(serializer);
	        serializer.attribute(null, "openaddress", m_sVariable);
            serializer.attribute(null, "onvalue", String.valueOf(m_fValueOn));
            serializer.attribute(null, "offvalue", String.valueOf(m_fValueOff));
            serializer.endTag(null, "relay");	
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
