package ru.cpc.smartflatview;

import android.content.Context;

public class MacroPumpWorkMode extends BaseMacro
{
	public MacroPumpWorkMode(int iX, int iY, String sName, String sButtonName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
		super(iX, iY, R.drawable.st14_1, R.drawable.st14_0, 14, sName, sButtonName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
		// TODO Auto-generated constructor stub
		m_bText2 = false;
	}

	@Override
	public boolean ShowPopup(Context context)
	{
		return false;
	}
}
