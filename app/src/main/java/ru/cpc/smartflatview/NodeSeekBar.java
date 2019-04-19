package ru.cpc.smartflatview;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class NodeSeekBar extends BaseRegulator
{
    public String m_sButtonText = "";
    private String m_sPattern = "%d °C";

    public NodeSeekBar(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
    {
        super(iX, iY, -1, 8, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);

        m_iValue = 20;
        m_bPower = true;

        m_bText2 = true;
        m_sButtonText = String.format(m_sPattern, m_iValue);

        m_bDoubleWidth = true;
    }

    @Override
    public void BindUI(SFServer pServer, Context context, IndicatorUI pUI)
    {
        super.BindUI(pServer, context, pUI);
        m_pUI.m_pText2.setTextColor(context.getResources().getColor(android.R.color.primary_text_light));//, context.getTheme()));
        m_pUI.m_pText2.setText(m_sButtonText);
    }

    @Override 
    public boolean Process(String sAddr, String sVal)
    {
        if(m_bLockUpdate)
            return false;

        Logger.Instance.AddDebugInfo("NodeSeekBar: Process() addr = " + sAddr + ", value = " + sVal );
        //Log.d(TAG, "NodeSeekBar: Process() addr = " + sAddr + ", value = " + sVal );
        boolean bUpdated = super.Process(sAddr, sVal);

        if(bUpdated)
        {
            if (m_pBar != null)
                m_pBar.setProgress(m_iValue - (int) m_fValueMin);

            m_sButtonText = String.format(m_sPattern, m_iValue);

            return Update();
        }

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
        super.Load(code);
        m_sButtonText = String.format(m_sPattern, m_iValue);
        Update();
    }

    @Override
    public boolean SetValue(float iX, float iY)
    {
        return false;
    }

    @Override
    public boolean ShowPopup(Context context)
    {
        return false;
    }

    @Override
    public void FixLayout(int l, int t, int r, int b)
    {
        int iWidth = r-l;
        int iHeight = b-t;

        IndicatorLayout pLayout = new IndicatorLayout(this, m_pUI.m_fK, l, t, r, b);

        int iText = (int)(1.2f*pLayout.m_iTextSize);

        iHeight -= pLayout.m_iTextSize*2.2;

        //m_pUI.m_pText2.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        //m_iHeight -= iText*2.2;
        //int iText = iWidth/6;
//
        //iHeight -= iText*2.2;
//
//        if(m_bDoubleScale)
//        {
//            m_pUI.m_pText.layout(-5, (int) (iHeight-iText*2.2), iWidth, iHeight - iText);
//        }
//        else
//        {
//            m_pUI.m_pText.layout(-5, (int) (iHeight-iText*2.6), iWidth, iHeight - iText);
//        }
        m_pUI.m_pOldView.layout(0, (int) (iHeight/2 - iText), iWidth, (int)(iHeight*0.65f + iText));//iHeight/2 + iText*2);

        if(m_bDoubleScale)
        {
            m_pUI.m_pText2.layout(0, (int) (iHeight/2-iText*2f), iWidth, iHeight/2 + iText);
            //m_pUI.m_pText2.layout(0, (int) (iHeight/2+iText*0.3f), iWidth, iHeight);
            m_pUI.m_pText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, iText*1.5f);
            //m_pUI.m_pText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, iText*0.95f);
        }
        else
        {
            m_pUI.m_pText2.layout(0, (int) (iHeight/2-iText*1.6f), iWidth, (int)(iHeight/2 + iText*0.8f));
            //m_pUI.m_pText2.layout(0, (int) (iHeight/2+iText*0.125f), iWidth, iHeight);
            m_pUI.m_pText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, iText*1.25f);
            //m_pUI.m_pText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, iText*0.75f);
        }
    }

    public boolean m_bLockUpdate = false;

    @Override
    public void Pressed(float iX, float iY)
    {
        super.Pressed(iX, iY);

        m_bLockUpdate = true;

        Logger.Instance.AddDebugInfo("NodeSeekBar: Pressed - set LockUpdate=true");
        //Log.d(TAG, "NodeSeekBar: Pressed - set LockUpdate=true" );
    }

    @Override
    public void Released()
    {
        super.Released();

        m_bLockUpdate = false;

        Logger.Instance.AddDebugInfo("NodeSeekBar: Released - set LockUpdate=false" );
        //Log.d(TAG, "NodeSeekBar: Released - set LockUpdate=false" );

        if(m_pBar != null)
            SetValue(m_pBar.getProgress() + (int) m_fValueMin);
    }

    @Override
    public boolean SetValue(int iValue)
    {
        Logger.Instance.AddDebugInfo("NodeSeekBar: SetValue = " + iValue );
        //Log.d(TAG, "NodeSeekBar: SetValue = " + iValue );
        return super.SetValue(iValue);
    }


    protected SeekBar m_pBar = null;

    @Override
    public View GetViewComponent(Context context)
    {
        //if(m_pBar == null)
        {
            //Resources r = context.getResources();
            //int px = (int) TypedValue.applyDimension(
            //       TypedValue.COMPLEX_UNIT_DIP,
            //        16,
            //        r.getDisplayMetrics()
            //);

//            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
//            params.setMargins(px, px, px, px);
//            pText.setLayoutParams(params);

            Logger.Instance.AddDebugInfo("NodeSeekBar: GetViewComponent - seekBar.progress = " + (m_iValue - (int) m_fValueMin));
            //Log.d(TAG, "NodeSeekBar: GetViewComponent - seekBar.progress = " + (m_iValue - (int) m_fValueMin));

            m_pBar = new SeekBar(context);
            m_pBar.setMax((int) m_fValueMax - (int) m_fValueMin);
            m_pBar.setProgress(m_iValue - (int) m_fValueMin);

            m_pBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                {
                    int iValue = m_pBar.getProgress() + (int) m_fValueMin;
                    m_sButtonText = String.format(m_sPattern, iValue);
                    Update();
                    //if(m_dListener != null)
                    // m_dListener.onProgressChanged(seekBar, progress, fromUser);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {
                    //if(m_dListener != null)
                    // m_dListener.onStartTrackingTouch(seekBar);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {
                    //Logger.Instance.AddDebugInfo("NodeSeekBar: onStopTrackingTouch" );
                    //SetValue(seekBar.getProgress() + (int) m_fValueMin);
                }
            });
            /*m_pBar.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    int iValue = m_pBar.getProgress() + (int) m_fValueMin;
                    m_sButtonText = String.format(m_sPattern, iValue);
                    Update();
                    return false;
                }
            });*/
            //m_pBar.setPadding(px, 0, px, 0);
        }

        return m_pBar;
    }
}
