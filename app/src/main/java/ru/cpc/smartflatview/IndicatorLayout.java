package ru.cpc.smartflatview;

public class IndicatorLayout
{
    public int m_iWidth;
    public int m_iHeight;

    public int m_iLeft;
    public int m_iTop;

    public int m_iTextSize;

    public IndicatorLayout(Indicator pIndicator, float fK, int l, int t, int r, int b)
    {
        int iWidth = r-l;
        int iHeight = b-t;

        //Высота, отведённая под подпись индикатора.
        //int iText = (int)(m_iHeight*1.25f/8);
        //VP 05.10.15 - для индикаторов двойного размера размер текста уменьшаем вдвое, чтобы он был такой же, как размер текста обычных индикаторов.
        float fTextScale = 1.25f;
        if(pIndicator.m_bDoubleScale)
            fTextScale *= 0.5f;
        if(pIndicator.m_bDoubleWidth)
            fTextScale *= 0.5f;

        m_iTextSize = (int)(iWidth*fTextScale/8);

        iHeight -= m_iTextSize*2.2;

        m_iWidth = iWidth;
        m_iHeight = iHeight;
        if(m_iHeight *fK < m_iWidth)
        {
            m_iWidth = (int)(m_iHeight *fK);
            //Log.d(TAG, "origin = " + m_pOriginOld.width() + " : " + m_pOriginOld.height() + "   k = " + fK);
            //Log.d(TAG, "bounds = " + (int)(height*fK) + " : " + height + "   k = " + ((int)(height*fK))/height);
        }
        else
        {
            m_iHeight = (int)(m_iWidth /fK);
        }

        if(pIndicator.m_bDoubleWidth)
        {
            m_iHeight /= 1.1;
            m_iWidth *= 2;
        }

        m_iLeft = (iWidth - m_iWidth)/2;
        m_iTop = (iHeight - m_iHeight)/2;
    }
}
