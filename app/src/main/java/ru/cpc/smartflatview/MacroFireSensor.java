package ru.cpc.smartflatview;

import android.content.Context;

/**
 * Created by Вик on 010. 10. 05. 17.
 */

public class MacroFireSensor extends BaseMacro
{
    public MacroFireSensor(int iX, int iY, String sName, String sButtonName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
    {
        super(iX, iY, R.drawable.id073, R.drawable.id071, 7, sName, sButtonName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
        // TODO Auto-generated constructor stub

        m_bText2 = false;
    }

//    @Override
//    public void FixLayout(int l, int t, int r, int b)
//    {
//        int iWidth = r-l;
//        int iHeight = b-t;
//
//        int iText = iHeight/7;
//
//        iHeight -= iText;
//
//        if(m_bDoubleScale)
//        {
//            m_pUI.m_pText.layout(-5, (int) (iHeight-iText*2.2), iWidth, iHeight - iText);
//        }
//        else
//        {
//            m_pUI.m_pText.layout(-5, (int) (iHeight-iText*2.6), iWidth, iHeight - iText);
//        }
//    }

    @Override
    public boolean ShowPopup(Context context)
    {
        return false;
    }
}