package ru.cpc.smartflatview;

import android.util.Log;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public abstract class BaseMacro extends Indicator 
{
	protected int m_iResIDOn;
	protected int m_iResIDOff;
	
	public String m_sButtonText = "";
	
	protected BaseMacro(float fX, float fY, int iResIDOn, int iResIDOff, int iSubType, String sName, String sButtonName,
			boolean bMetaInd, boolean bProtected, boolean bDoubleSize, boolean bQuick, int iReaction,
			int iScale) 
	{
		super(fX, fY, iResIDOff, iSubType, sName, bMetaInd, bProtected, bDoubleSize, bQuick, iReaction, iScale);
		// TODO Auto-generated constructor stub
		m_iResIDOn = iResIDOn;
		m_iResIDOff = iResIDOff;
		
		m_bText2 = true;
		m_sButtonText = sButtonName;

		Log.d("CTOR", "macro name: " + m_sName);
		Log.d("CTOR", "button name: " + m_sButtonText);
	}

	public boolean m_bActive = false;
	
	public String m_sVariableState = "-1";
	public String m_sVariableCommand = "-1";
	public float m_fValueActive = 1;
	public float m_fValueReset = 0;
	
	public BaseMacro Bind(String sAddressCommand, String sAddressState, String sActiveVal, String sResetVal)
	{
		m_sVariableCommand = sAddressCommand;
		m_sVariableState = sAddressState;
		
		m_fValueActive = Float.parseFloat(sActiveVal);
		m_fValueReset = Float.parseFloat(sResetVal);
		
		return this;
	}

	@Override
	public void GetAddresses(AddressString sAddr)
	{
		sAddr.Add(m_sVariableCommand, this);
		sAddr.Add(m_sVariableState, this);
	}

	@Override
	public boolean Process(String sAddr, String sVal)
	{
		boolean bActiveOld = m_bActive;

		if(m_sVariableState.equalsIgnoreCase(sAddr))
			m_bActive = Float.parseFloat(sVal) == m_fValueActive;

		if(m_bActive != bActiveOld)
		    return Update();

		return  false;
	}	
	@Override
	public boolean SwitchOnOff(float iX, float iY) 
	{
		m_bActive = !m_bActive;

		m_pServer.SendCommand(m_sVariableCommand, String.valueOf(m_fValueReset));
		m_pServer.SendCommand(m_sVariableCommand, String.valueOf(m_fValueActive));
	
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
		
		if(m_bActive)
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
		m_bActive = code == '1';
		Update();
	}

	@Override
	public String Save() 
	{
		return m_bActive ? "1" : "0";
	}

	@Override
	public void SaveXML(XmlSerializer serializer) 
	{
        try 
        {
			serializer.startTag(null, "macro");
			WriteCommonAttributes(serializer);
            serializer.attribute(null, "namebutton1", m_sButtonText);
            serializer.attribute(null, "namebutton2", "");
            serializer.attribute(null, "namebutton3", "");
            serializer.attribute(null, "onvalue", String.valueOf(m_fValueActive));
            serializer.attribute(null, "offvalue", String.valueOf(m_fValueReset));
            serializer.attribute(null, "stateaddress", m_sVariableState);
            serializer.attribute(null, "commandaddress", m_sVariableCommand);
	        serializer.endTag(null, "macro");	
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
