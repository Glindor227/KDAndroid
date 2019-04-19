package ru.cpc.smartflatview;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by Вик on 022. 22. 08. 16.
 */
public class Battery extends BaseRegulator
{
    public Battery(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
    {
        super(iX, iY, R.drawable.radiator_cold, 5, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);

        m_iValue = 20;
        m_bPower = true;
    }

    @Override
    public boolean SwitchOnOff(float iX, float iY)
    {
        m_bPower = true;

        if(m_bICP)
        {
            if(m_bPower)
            {
                if(m_iValue == 0)
                    m_pServer.SendCommand(m_sVariableValue, "3711");
                else
                    m_pServer.SendCommand(m_sVariableValue, String.valueOf(3584 + m_iValue));
            }
            else
                m_pServer.SendCommand(m_sVariableValue, "3584");
        }
        else
            m_pServer.SendCommand(m_sVariablePower, String.valueOf(m_bPower ? m_fValueOn : m_fValueOff));

        return Update();
    }

    @Override
    public boolean SetValue(float iX, float iY)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean Update()
    {
        int iResId = -1;

        iResId = R.drawable.radiator_cold;

        if(m_iValue > m_fValueMed)
            iResId = R.drawable.radiator_hot;

        if(m_pUI == null)
        {
            m_iOldResID = iResId;
            m_iNewResID = iResId;
            return false;
        }

        return m_pUI.StartAnimation(iResId);
    }

    @Override
    public boolean ShowPopup(Context context)
    {
        ScrollingDialog.Init(m_sName, m_pSubsystem.m_sName);
        final ScrollingDialog.SFSeeker pSeeker = (ScrollingDialog.SFSeeker)ScrollingDialog.AddSeekBar(m_sVariableValue, context.getString(
                        R.string.sdTemperature), m_iValue, (int)m_fValueMin, (int)m_fValueMax, "%d °C", m_iReaction != 0
                                                                                                        ? null : new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //if (fromUser) {
                //    SetValue(16 + progress);
                //}
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SetValue(seekBar.getProgress() + (int)m_fValueMin);
            }
        });

        //Intent myIntent = new Intent(context, ScrollingActivity.class);
        //context.startActivity(myIntent);

        if(m_iReaction == 1)
            ScrollingDialog.AddAcceptBtn(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(m_iValue != pSeeker.m_iValue)
                        SetValue(pSeeker.m_iValue);
                }
            });

        ScrollingDialog dlg = new ScrollingDialog();
        dlg.show(((Activity) context).getFragmentManager(), "dlg");


//		v.show();
        return false;
    }
}

