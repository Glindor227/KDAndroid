package ru.cpc.smartflatview;

import android.content.Context;
import android.util.Log;
import android.view.View;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Indicator
{
	protected static final String TAG = "SMARTFLAT" ;
	protected static final boolean newDez  = false;

	protected int m_iOldResID = -1;
	protected int m_iNewResID = -1;

	protected boolean m_bQuick = true;
	protected int m_iReaction = 1;
	protected boolean m_bMetaIndicator = false;
	protected boolean m_bProtected = false;
	protected boolean m_bText2 = false;
	protected int m_iSubType = 1;
	
	protected SFServer m_pServer;

	protected int m_iFontSize;

	protected boolean m_bDoubleScale = false;
	protected boolean m_bDoubleWidth = false;

	protected float m_fXPercent;
	protected float m_fYPercent;
	protected String m_sName;
	protected Subsystem m_pSubsystem;

	protected IndicatorUI m_pUI = null;
	
	protected Indicator(float fX, float fY, int iResID, int iSubType, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleSize, boolean bQuick, int iReaction, int iScale)
	{
		m_pSubsystem = null;

		m_fXPercent = fX;
		m_fYPercent = fY;
		m_sName = sName;
		if(m_sName == null)
			m_sName = "";
		
		m_bMetaIndicator = bMetaInd;
		m_bProtected = bProtected;
		m_iSubType = iSubType;
		
		m_bDoubleScale = bDoubleSize;
		m_bQuick = bQuick;
		m_iReaction = iReaction;
		
		m_iOldResID = iResID;
		m_iNewResID = iResID;

		//m_iFontSize = 54/iScale;
		m_iFontSize = 33/iScale;
	}
	
	protected void WriteCommonAttributes(XmlSerializer serializer)
	{
        try 
        {
			serializer.attribute(null, "name", m_sName);
	        serializer.attribute(null, "subtype", String.valueOf(m_bMetaIndicator ? -m_iSubType : m_iSubType));
	        serializer.attribute(null, "x", String.valueOf((int)(m_fXPercent*10)));
	        serializer.attribute(null, "y", String.valueOf((int)(m_fYPercent*10)));
	        serializer.attribute(null, "double", String.valueOf(m_bDoubleScale ? 1 : 0));
			serializer.attribute(null, "secure", String.valueOf(m_bProtected ? 1 : 0));
			serializer.attribute(null, "quick", String.valueOf(m_bQuick ? 1 : 0));
			serializer.attribute(null, "reaction", String.valueOf(m_iReaction));
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
	
	protected abstract boolean Update();

	public abstract void Load(char code);
	public abstract String Save();

	public void Imitate()
	{
	}
	
	public boolean IsAlarmed()
	{
		return false;
	}
	
	public void Pressed(float iX, float iY)
	{}

	private Timer timer = null;

	class LonelyReleaseTask extends TimerTask {

		@Override
		public void run() {
			m_pUI.LonelyRelease();
		}
	}

	public void Released()
	{
		if(timer != null)
			timer.cancel();

		timer = new Timer();
		LonelyReleaseTask lonelyRelease = new LonelyReleaseTask();
		timer.schedule(lonelyRelease, 1000);
	}

	public void GestureConfirmed()
	{
		if(timer != null)
		{
			timer.cancel();
			timer = null;
		}
	}

	public abstract boolean SwitchOnOff(float iX, float iY);
	public abstract boolean SetValue(float iX, float iY);
	public abstract boolean SetValue(int iValue);
	
	public abstract void SaveXML(XmlSerializer serializer);
	
	public abstract void GetAddresses(AddressString sAddr);
	public abstract boolean Process(String sAddr, String sVal);
	
	public abstract boolean ShowPopup(Context context);

	public void BindUI(SFServer pServer, Context context, IndicatorUI pUI)
	{
		m_pServer = pServer;

		m_pUI = pUI;
		Update();
	}
	
	public void FixLayout(int l, int t, int r, int b)
	{}

	public View GetViewComponent(Context context)
	{
		return new View(context);
	}

}
