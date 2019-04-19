package ru.cpc.smartflatview;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by Вик on 021. 21.03.16.
 */
public class ButtonMedia extends Indicator
{
    public static final int EJECT = 1;
    public static final int PLAY = 2;
    public static final int STOP = 3;
    public static final int PAUSE = 4;
    public static final int REC = 5;
    public static final int FF = 6;
    public static final int REW = 7;
    public static final int SKIP_F = 8;
    public static final int SKIP_B = 9;
    public static final int MUTE = 10;
    public static final int POWER = 11;

    private int m_iType;
    private boolean m_bPressed = false;

    protected ButtonMedia(float fX,
                          float fY, int iType, String sName, boolean bMetaInd, boolean bProtected,
                          boolean bDoubleSize, boolean bQuick, int iReaction, int iScale)
    {
        super(fX, fY, GetResId(iType, false), iType, sName, bMetaInd, bProtected, bDoubleSize, bQuick, iReaction, iScale);
        // TODO Auto-generated constructor stub

        m_iType = iType;
    }

    public String m_sVariable = "-1";
    public String m_sStateVariable = "-1";

    public ButtonMedia Bind(String sAddress, String sAddress2)
    {
        m_sVariable = sAddress;
        m_sStateVariable = sAddress2;

        return this;
    }

    @Override
    public boolean SwitchOnOff(float iX, float iY) {
        if(m_sVariable.equals("-1")) {
            m_bPressed = !m_bPressed;
            Update();
        }
        return false;
    }

    @Override
    public boolean SetValue(float iX, float iY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean SetValue(int iValue) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void Pressed(float iX, float iY)
    {
        if(m_sVariable.equals("-1")) {
            m_bPressed = true;
            Update();
        }
    }

    @Override
    public void Released()
    {
        super.Released();
        if(m_sVariable.equals("-1")) {
            m_bPressed = false;
            Update();
        }
    }

    static int GetResId(int iType, boolean bPressed)
    {
        int iResId = R.drawable.empty;

        switch (iType)
        {
            case EJECT:
                if(bPressed)
                    iResId = R.drawable.eject_pressed;
                else
                    iResId = R.drawable.eject;
                break;
            case PLAY:
                if(bPressed)
                    iResId = R.drawable.play_pressed;
                else
                    iResId = R.drawable.play;
                break;
            case PAUSE:
                if(bPressed)
                    iResId = R.drawable.pause_pressed;
                else
                    iResId = R.drawable.pause;
                break;
            case STOP:
                if(bPressed)
                    iResId = R.drawable.stop_pressed;
                else
                    iResId = R.drawable.stop;
                break;
            case REC:
                if(bPressed)
                    iResId = R.drawable.rec_pressed;
                else
                    iResId = R.drawable.rec;
                break;
            case FF:
                if(bPressed)
                    iResId = R.drawable.forward_pressed;
                else
                    iResId = R.drawable.forward;
                break;
            case REW:
                if(bPressed)
                    iResId = R.drawable.rewind_pressed;
                else
                    iResId = R.drawable.rewind;
                break;
            case SKIP_F:
                if(bPressed)
                    iResId = R.drawable.skip_frwd_pressed;
                else
                    iResId = R.drawable.skip_frwd;
                break;
            case SKIP_B:
                if(bPressed)
                    iResId = R.drawable.skip_back_pressed;
                else
                    iResId = R.drawable.skip_back;
                break;
            case MUTE:
                if(bPressed)
                    iResId = R.drawable.mute_pressed;
                else
                    iResId = R.drawable.mute;
                break;
            case POWER:
                if(bPressed)
                    iResId = R.drawable.power_pressed;
                else
                    iResId = R.drawable.power;
                break;
        }

        return iResId;
    }

    @Override
    protected boolean Update()
    {
        int iResId = GetResId(m_iType, m_bPressed);

//		m_bLoopAnimation = m_bPressed;

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
    public void Load(char code) {
        m_bPressed = code == '1';
    }

    @Override
    public String Save() {
        if(m_bPressed)
            return "1";
        else
            return "0";
    }

    @Override
    public void SaveXML(XmlSerializer serializer) {
        try
        {
            serializer.startTag(null, "mediabutton");
            WriteCommonAttributes(serializer);
            serializer.attribute(null, "valueaddress", m_sVariable);
            serializer.attribute(null, "stateaddress", m_sStateVariable);
            serializer.endTag(null, "mediabutton");
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

    @Override
    public void GetAddresses(AddressString sAddr) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean Process(String sAddr, String sVal) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean ShowPopup(Context context) {
        return false;
    }

}

