package ru.cpc.smartflatview;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Вик on 026. 26.04.16.
 */
public abstract class BaseSensor extends Indicator
{
    public String m_sButtonText = "";
    protected String m_sPostfix = "";
    protected float m_fValue = 0;

    protected BaseSensor(float fX, float fY, int iSubType, String sName, String sPostfix,
                        boolean bMetaInd, boolean bProtected, boolean bDoubleSize, boolean bQuick, int iReaction,
                        int iScale)
    {
        super(fX, fY, R.drawable.empty, iSubType, sName, bMetaInd, bProtected, bDoubleSize, bQuick, iReaction, iScale);
        // TODO Auto-generated constructor stub

        m_bText2 = true;
        m_sPostfix = sPostfix;
        m_sButtonText = String.format("%.1f %s", m_fValue, m_sPostfix);

        Log.d("CTOR", "sensor name: " + m_sName);
    }

    public String m_sVariableValue = "-1";

    public BaseSensor Bind(String sVariableValue)
    {
        m_sVariableValue = sVariableValue;

        return this;
    }

    @Override
    public void BindUI(SFServer pServer, Context context, IndicatorUI pUI)
    {
        super.BindUI(pServer, context, pUI);
        m_pUI.m_pText2.setTextColor(context.getResources().getColor(android.R.color.primary_text_dark));//, context.getTheme()));
//        m_pUI.m_pText2.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));//, context.getTheme()));
        m_pUI.m_pText2.setText(m_sButtonText);
    }

    @Override
    public void GetAddresses(AddressString sAddr)
    {
        sAddr.Add(m_sVariableValue, this);
    }

    @Override
    public boolean Process(String sAddr, String sVal)
    {
        String sButtonTextOld = m_sButtonText;

        if(m_sVariableValue.equalsIgnoreCase(sAddr))
        {
            try
            {
                sVal = sVal.replace(',', '.');
                m_fValue = Float.parseFloat(sVal);
                m_sButtonText = String.format("%.1f %s", m_fValue, m_sPostfix);
            }
            catch(NumberFormatException e)
            {
                m_sButtonText = String.format("%s %s", sVal, m_sPostfix);
            }
        }

        if(!m_sButtonText.equalsIgnoreCase(sButtonTextOld))
            return Update();

        return false;
    }
    @Override
    public boolean SwitchOnOff(float iX, float iY)
    {
        return false;
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
    public void FixLayout(int l, int t, int r, int b)
    {
        int iWidth = r-l;
        int iHeight = b-t;

        int iText = iWidth/6;
//
        iHeight -= iText*2;
//
//        if(m_bDoubleScale)
//        {
//            m_pUI.m_pText.layout(-5, (int) (iHeight-iText*2.2), iWidth, iHeight - iText);
//        }
//        else
//        {
//            m_pUI.m_pText.layout(-5, (int) (iHeight-iText*2.6), iWidth, iHeight - iText);
//        }
        if(m_bDoubleScale)
        {
            m_pUI.m_pText2.layout(0, (int) (iHeight/2-iText*1.3f), iWidth, iHeight/2 + iText);
            m_pUI.m_pText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, iText*1.75f);
        }
        else
        {
            m_pUI.m_pText2.layout(0, (int) (iHeight/2-iText*1.125f), iWidth, iHeight/2 + iText);
            m_pUI.m_pText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, iText*1.5f);
        }
    }

    @Override
    public boolean ShowPopup(Context context)
    {
        return false;
    }

    @Override
    protected boolean Update()
    {
        if(m_pUI == null)
            return false;

        m_pUI.m_pText2.setText(m_sButtonText);
        return true;
    }

    @Override
    public void Load(char code)
    {
        m_fValue = code;
        m_sButtonText = String.format("%.1f %s", m_fValue, m_sPostfix);
        Update();
    }

    @Override
    public String Save()
    {
        return String.valueOf((char)((int)m_fValue));
    }

    @Override
    public void SaveXML(XmlSerializer serializer)
    {
        try
        {
            serializer.startTag(null, "sensor");
            WriteCommonAttributes(serializer);
            serializer.attribute(null, "valueaddress", m_sVariableValue);
            serializer.endTag(null, "sensor");
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