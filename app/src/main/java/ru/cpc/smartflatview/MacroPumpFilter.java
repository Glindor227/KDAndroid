package ru.cpc.smartflatview;

import android.content.Context;

public class MacroPumpFilter extends BaseMacro
{
	public MacroPumpFilter(int iX, int iY, String sName, String sButtonName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
		super(iX, iY,
				newDez?R.drawable.st13_1_2:R.drawable.st13_1,
				newDez?R.drawable.st13_0_2:R.drawable.st13_0,
				13, sName, sButtonName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
		// TODO Auto-generated constructor stub
		m_bText2 = false;
	}

	@Override
	public boolean ShowPopup(Context context)
	{
		return false;
	}
}
