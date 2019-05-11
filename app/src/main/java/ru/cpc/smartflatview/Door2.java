package ru.cpc.smartflatview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class Door2 extends Indicator 
{
	public boolean m_bFireMode = false;
	public boolean m_bBlock = false;	
	
	public Door2(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
		super(iX, iY, newDez?R.drawable.door_unblock_2:R.drawable.door_unblock, 1, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
	}

	public String m_sVariableFire = "-1";
	public String m_sVariableBlock = "-1";
	
	public Door2 Bind(String sAddressBlock, String sAddressFire)
	{
		m_sVariableBlock = sAddressBlock;
		m_sVariableFire = sAddressFire;
		
		return this;
	}

	@Override
	public void GetAddresses(AddressString sAddr)
	{
		sAddr.Add(m_sVariableBlock, this);
		sAddr.Add(m_sVariableFire, this);
	}

	@Override
	public boolean Process(String sAddr, String sVal)
	{
		boolean bBlockOld = m_bBlock;
		boolean bFireModeOld = m_bFireMode;

		if(m_sVariableBlock.equalsIgnoreCase(sAddr))
			m_bBlock = Float.parseFloat(sVal) > 0;
		
		if(m_sVariableFire.equalsIgnoreCase(sAddr))
			m_bFireMode = Float.parseFloat(sVal) > 0;

		if(m_bBlock != bBlockOld || m_bFireMode != bFireModeOld)
		    return Update();

		return false;
	}
	
	@Override
	public boolean SwitchOnOff(float iX, float iY)
	{
		m_bBlock = !m_bBlock;
		
		m_pServer.SendCommand(m_sVariableBlock, m_bBlock ? "1" : "0");
		
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
		m_bFireMode = iValue > 0;
		
		m_pServer.SendCommand(m_sVariableFire, m_bFireMode ? "1" : "0");
		
		return Update();
	}

	@Override
	protected boolean Update() 
	{
		int iResId = -1;
		
		if(m_bBlock)
			iResId = newDez?R.drawable.door_block_2:R.drawable.door_block;
		else
		{
			if(m_bFireMode)
				iResId = newDez?R.drawable.door_free_2:R.drawable.door_free;
			else
				iResId = newDez?R.drawable.door_unblock_2:R.drawable.door_unblock;
		}

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
		m_bBlock = false;
		m_bFireMode = false;

		if(code == '1')
		{
			m_bBlock = true;
			m_bFireMode = false;
		}
		if(code == '2')
		{
			m_bBlock = false;
			m_bFireMode = true;
		}

		Update();
	}

	@Override
	public String Save() 
	{
		if(m_bBlock)
			return "1";
		else
		{
			if(m_bFireMode)
				return "2";
			else
				return "0";
		}
	}

	@Override
	public boolean ShowPopup(Context context)
	{
		ScrollingDialog.Init(m_sName, m_pSubsystem.m_sName);
		final ScrollingDialog.SFSwitcher pSwitcher = (ScrollingDialog.SFSwitcher)ScrollingDialog.AddSwitcher(m_sVariableBlock, context.getString(
						R.string.sdBlock), m_bBlock, m_iReaction != 0 ? null : new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SwitchOnOff(0, 0);
			}
		});
		final ScrollingDialog.SFSwitcher pSwitcher2 = (ScrollingDialog.SFSwitcher)ScrollingDialog.AddSwitcher(m_sVariableFire, context.getString(
						R.string.sdFreePass), m_bFireMode, m_iReaction != 0 ? null : new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SetValue(isChecked ? 1 : 0);
			}
		});

		//Intent myIntent = new Intent(context, ScrollingActivity.class);
		//context.startActivity(myIntent);

		if(m_iReaction == 1)
			ScrollingDialog.AddAcceptBtn(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(m_bBlock != pSwitcher.m_bChecked)
						SwitchOnOff(0, 0);

					if(m_bFireMode != pSwitcher2.m_bChecked)
						SetValue(pSwitcher2.m_bChecked ? 1:0);
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
			serializer.startTag(null, "door2");
			WriteCommonAttributes(serializer);
            serializer.attribute(null, "blockaddress", m_sVariableBlock);
            serializer.attribute(null, "fireaddress", m_sVariableFire);
	        serializer.endTag(null, "door2");	
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