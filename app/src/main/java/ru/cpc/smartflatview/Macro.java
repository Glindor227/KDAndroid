package ru.cpc.smartflatview;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

public class Macro extends BaseMacro 
{
	public Macro(int iX, int iY, String sName, String sButtonName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
		super(iX, iY,
				newDez?R.drawable.macro_on:R.drawable.id112s,
				newDez?R.drawable.macro_off:R.drawable.id111s,
				1, sName, sButtonName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
		// TODO Auto-generated constructor stub
		m_bText2 = false;
	}

//	@Override
//	public void BindUI(SFServer pServer, Context context, IndicatorUI pUI)
//	{
//		super.BindUI(pServer, context, pUI);
//		m_pUI.m_pText2.setTextColor(context.getResources().getColor(android.R.color.primary_text_dark));//, context.getTheme()));
//		m_pUI.m_pText2.setText(m_sButtonText);
//	}
//
//	@Override
//	public void FixLayout(int l, int t, int r, int b)
//	{
//		int iWidth = r-l;
//		int iHeight = b-t;
//
//		int iText = iHeight/6;
//
//		iHeight -= iText*2;
//
//		if(m_bDoubleScale)
//		{
//			m_pUI.m_pText2.layout(-5, (int) (iHeight/2-iText), iWidth, iHeight/2 + iText);
//			m_pUI.m_pText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, iText*1.25f);
//		}
//		else
//		{
//			m_pUI.m_pText2.layout(-5, (int) (iHeight/2-iText), iWidth, iHeight/2 + iText);
//			m_pUI.m_pText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, iText*1.5f);
//		}
//	}

	@Override
	public boolean ShowPopup(Context context)
	{
		return false;
	}
}
