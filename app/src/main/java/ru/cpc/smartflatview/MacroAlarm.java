package ru.cpc.smartflatview;

import android.content.Context;

public class MacroAlarm extends BaseMacro
{
	public MacroAlarm(int iX, int iY, String sName, String sButtonName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
		super(iX, iY, R.drawable.alarmnode1, R.drawable.alarmnode0, 17, sName, sButtonName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
		// TODO Auto-generated constructor stub
		m_bText2 = false;
	}

	@Override
	public boolean ShowPopup(Context context)
	{
		return false;
	}
}
