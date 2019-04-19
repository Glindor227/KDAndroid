package ru.cpc.smartflatview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Вик on 023. 23.03.16.
 */
public class MediaField extends Indicator implements PlaylistDialog.OnPlaylistDialogListener
{
    protected int m_iResIDOn;
    protected int m_iResIDOff;

    public String m_sButtonText = "";

    public MediaField(int iX, int iY, String sName, String sButtonName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
    {
        this(iX, iY, R.drawable.id113, R.drawable.id114, 1, sName, sButtonName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
        // TODO Auto-generated constructor stub
    }

    protected MediaField(float fX, float fY, int iResIDOn, int iResIDOff, int iSubType, String sName, String sButtonName,
                        boolean bMetaInd, boolean bProtected, boolean bDoubleSize, boolean bQuick, int iReaction,
                        int iScale)
    {
        super(fX, fY, iResIDOff, iSubType, sName, bMetaInd, bProtected, bDoubleSize, bQuick, iReaction, iScale);
        // TODO Auto-generated constructor stub
        m_iResIDOn = iResIDOn;
        m_iResIDOff = iResIDOff;

        m_bText2 = true;
        m_sButtonText = sButtonName;

        m_bDoubleWidth = true;
        //m_bQuick = false;

        Log.d("CTOR", "macro name: " + m_sName);
        Log.d("CTOR", "button name: " + m_sButtonText);
    }

    public boolean m_bActive = false;

    public String m_sVariableState = "-1";
    public String m_sVariableCommand = "-1";
    public float m_fValueActive = 1;
    public float m_fValueReset = 0;

    public MediaField Bind(String sAddressCommand, String sAddressState, String sActiveVal, String sResetVal)
    {
        m_sVariableCommand = sAddressCommand;
        m_sVariableState = sAddressState;

        m_fValueActive = Float.parseFloat(sActiveVal);
        m_fValueReset = Float.parseFloat(sResetVal);

        return this;
    }

    @Override
    public void BindUI(SFServer pServer, Context context, IndicatorUI pUI)
    {
        super.BindUI(pServer, context, pUI);
        m_pUI.m_pText2.setTextColor(context.getResources().getColor(android.R.color.primary_text_dark));//, context.getTheme()));
        m_pUI.m_pText2.setText(m_sButtonText);

        m_pUI.m_pFadeInAnimation.setDuration(500);
        m_pUI.m_pFadeOutAnimation.setDuration(500);
    }

    @Override
    public void FixLayout(int l, int t, int r, int b)
    {
        int iWidth = r-l;
        int iHeight = b-t;

        int iText = (int)(iHeight*1.25f/8);

        iHeight -= iText*2;

        m_pUI.m_pText2.setGravity(Gravity.LEFT);
//        if(m_bDoubleScale)
//        {
//            m_pUI.m_pText.layout(-5, (int) (iHeight-iText*2.2), iWidth - iHeight, iHeight - iText);
//            m_pUI.m_pText2.layout(iHeight/8, (int) (iHeight / 2 - iText*0.9), iWidth - iHeight/2, iHeight / 2 + iText);
//        }
//        else
//        {
//            m_pUI.m_pText.layout(-5, (int) (iHeight-iText*2.6), iWidth, iHeight - iText);
//            m_pUI.m_pText2.layout(iHeight/8, (int) (iHeight / 2 - iText * 0.6), iWidth - iHeight/2, iHeight / 2 + iText);
//        }
            if(m_bDoubleScale)
            {
                m_pUI.m_pText2.layout(iHeight/8, (int) (iHeight/2-iText * 0.8), iWidth - iHeight/2, iHeight/2 + iText);
            }
            else
            {
                m_pUI.m_pText2.layout(iHeight/8, (int) (iHeight/2-iText * 0.5), iWidth - iHeight/2, iHeight/2 + iText);
            }
    }
    @Override
    public boolean ShowPopup(Context context)
    {
        PlaylistDialog dlg = new PlaylistDialog();
        dlg.SetCallback(this);
        dlg.show(((Activity) context).getFragmentManager(), "dlg");

        return false;
    }

    @Override
    public void onPlaylistDialogSubmit(String friendEmail) {
        m_sButtonText = friendEmail;
        m_pUI.m_pText2.setText(m_sButtonText);

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

        return false;
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
            if(iResId != -1)
            {
                m_iOldResID = iResId;
                m_iNewResID = iResId;
            }
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
            serializer.startTag(null, "mediafield");
            WriteCommonAttributes(serializer);
            serializer.attribute(null, "valueaddress", m_sButtonText);
            serializer.attribute(null, "favoritesaddress", m_sButtonText);
            serializer.attribute(null, "listlinesaddress", m_sButtonText);
            serializer.attribute(null, "listoffsetaddress", m_sButtonText);
            serializer.attribute(null, "listline01address", m_sButtonText);
            serializer.attribute(null, "listline02address", m_sButtonText);
            serializer.attribute(null, "listline03address", m_sButtonText);
            serializer.attribute(null, "listline04address", m_sButtonText);
            serializer.attribute(null, "listline05address", m_sButtonText);
            serializer.attribute(null, "listline06address", m_sButtonText);
            serializer.attribute(null, "listline07address", m_sButtonText);
            serializer.attribute(null, "listline08address", m_sButtonText);
            serializer.attribute(null, "listline09address", m_sButtonText);
            serializer.attribute(null, "listline10address", m_sButtonText);
            serializer.attribute(null, "listline11address", m_sButtonText);
            serializer.attribute(null, "listline12address", m_sButtonText);
            serializer.attribute(null, "listline13address", m_sButtonText);
            serializer.attribute(null, "listline14address", m_sButtonText);
            serializer.attribute(null, "listline15address", m_sButtonText);
            serializer.attribute(null, "listline16address", m_sButtonText);
            serializer.attribute(null, "listline17address", m_sButtonText);
            serializer.attribute(null, "listline18address", m_sButtonText);
            serializer.attribute(null, "listline19address", m_sButtonText);
            serializer.attribute(null, "listline20address", m_sButtonText);
            serializer.attribute(null, "listmodeaddress", m_sButtonText);
            serializer.attribute(null, "listpositionaddress", m_sButtonText);
            serializer.attribute(null, "listremoveaddress", m_sButtonText);
            serializer.attribute(null, "listclearaddress", m_sButtonText);
            serializer.attribute(null, "librarytitlesaddress", m_sButtonText);
            serializer.attribute(null, "librarylinesaddress", m_sButtonText);
            serializer.attribute(null, "libraryoffsetaddress", m_sButtonText);
            serializer.attribute(null, "libraryline01address", m_sButtonText);
            serializer.attribute(null, "libraryline02address", m_sButtonText);
            serializer.attribute(null, "libraryline03address", m_sButtonText);
            serializer.attribute(null, "libraryline04address", m_sButtonText);
            serializer.attribute(null, "libraryline05address", m_sButtonText);
            serializer.attribute(null, "libraryline06address", m_sButtonText);
            serializer.attribute(null, "libraryline07address", m_sButtonText);
            serializer.attribute(null, "libraryline08address", m_sButtonText);
            serializer.attribute(null, "libraryline09address", m_sButtonText);
            serializer.attribute(null, "libraryline10address", m_sButtonText);
            serializer.attribute(null, "libraryline11address", m_sButtonText);
            serializer.attribute(null, "libraryline12address", m_sButtonText);
            serializer.attribute(null, "libraryline13address", m_sButtonText);
            serializer.attribute(null, "libraryline14address", m_sButtonText);
            serializer.attribute(null, "libraryline15address", m_sButtonText);
            serializer.attribute(null, "libraryline16address", m_sButtonText);
            serializer.attribute(null, "libraryline17address", m_sButtonText);
            serializer.attribute(null, "libraryline18address", m_sButtonText);
            serializer.attribute(null, "libraryline19address", m_sButtonText);
            serializer.attribute(null, "libraryline20address", m_sButtonText);
            serializer.attribute(null, "libraryline01selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline02selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline03selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline04selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline05selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline06selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline07selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline08selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline09selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline10selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline11selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline12selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline13selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline14selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline15selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline16selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline17selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline18selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline19selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryline20selectedaddress", m_sButtonText);
            serializer.attribute(null, "libraryaddaddress", m_sButtonText);
            serializer.attribute(null, "libraryreplaceaddress", m_sButtonText);
            serializer.attribute(null, "libraryupaddress", m_sButtonText);
            serializer.attribute(null, "librarytopaddress", m_sButtonText);
            serializer.attribute(null, "libraryenteraddress", m_sButtonText);
            serializer.endTag(null, "mediafield");
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
