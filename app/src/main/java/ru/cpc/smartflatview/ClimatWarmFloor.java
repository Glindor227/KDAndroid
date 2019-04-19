package ru.cpc.smartflatview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

/**
 * Created by Вик on 029. 29. 12. 16.
 */

public class ClimatWarmFloor extends BaseClimat
{
    public ClimatWarmFloor(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
    {
        super(iX, iY, R.drawable.cwarmfloor2off, 3, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);

        m_iTemp = 20;
        m_iSpeed = 50;
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

        if(m_bPower)
        {
            switch(m_iMode)
            {
                case 0:
                    iResId = R.drawable.cwarmfloor1on;
                    break;
                case 1:
                    iResId = R.drawable.cwarmfloor2on;
                    break;
            }
        }
        else
        {
            switch(m_iMode)
            {
                case 0:
                    iResId = R.drawable.cwarmfloor1off;
                    break;
                case 1:
                    iResId = R.drawable.cwarmfloor2off;
                    break;
            }
        }

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
    public boolean ShowPopup(Context context)
    {
        Log.d(TAG, "power = '" + m_sVariablePower + "'");
        Log.d(TAG, "temp = '" + m_sVariableTemp  + "'");
        Log.d(TAG, "speed = '" + m_sVariableSpeed  + "'");
        Log.d(TAG, "mode = '" + m_sVariableMode  + "'");

        ScrollingDialog.Init(m_sName, m_pSubsystem.m_sName);
        final ScrollingDialog.SFSwitcher pSwitcher = m_bNoPower ? null : (ScrollingDialog.SFSwitcher) ScrollingDialog.AddSwitcher(m_sVariablePower, context.getString(R.string.sdPower), m_bPower, m_iReaction != 0
                                                                                                                                                                                                   ? null : new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SwitchOnOff(0, 0);
            }
        });
        final ScrollingDialog.SFSeeker pSeeker = (ScrollingDialog.SFSeeker)ScrollingDialog.AddSeekBar(m_sVariableTemp, context.getString(R.string.sdTemperature), m_iTemp, (int) m_fTempMin, (int) m_fTempMax, "%d °C", m_iReaction != 0
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
                SetTemp(seekBar.getProgress() + (int) m_fTempMin);
            }
        });
        final ScrollingDialog.SFSeeker pSeeker2 = (ScrollingDialog.SFSeeker)ScrollingDialog.AddSeekBar(m_sVariableSpeed, context.getString(R.string.sdSpeed), m_iSpeed, (int) m_fSpeedMin, (int) m_fSpeedMax, "%d", m_iReaction != 0
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
                SetSpeed(seekBar.getProgress() + (int) m_fSpeedMin);
            }
        });
        Log.d(TAG, "speed == '-1' = " + (m_sVariableSpeed.equals("-1")));
        Log.d(TAG, "pSeeker2 == 'null' = " + (pSeeker2 == null));
        final ScrollingDialog.SFModeSelector pSelector = (ScrollingDialog.SFModeSelector)ScrollingDialog.AddModeSelector(m_sVariableMode, R.drawable.cradiator_mode1, R.drawable.ccond_mode1, m_iMode, R.color.playlistBackground, R.color.colorAccent,
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetValue((int) v.getTag());
            }
        });

        //Intent myIntent = new Intent(context, ScrollingActivity.class);
        //context.startActivity(myIntent);

        if(m_iReaction == 1)
            ScrollingDialog.AddAcceptBtn(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pSeeker != null && m_iTemp != pSeeker.m_iValue)
                        SetTemp(pSeeker.m_iValue);

                    if(pSeeker2 != null && m_iSpeed != pSeeker2.m_iValue)
                        SetSpeed(pSeeker2.m_iValue);

                    if(pSwitcher != null && m_bPower != pSwitcher.m_bChecked)
                        SwitchOnOff(0, 0);

                    if(pSelector != null && m_iMode != pSelector.m_iSelected)
                        SetValue(pSelector.m_iSelected);
                }
            });

        ScrollingDialog dlg = new ScrollingDialog();
        dlg.show(((Activity) context).getFragmentManager(), "dlg");


//		v.show();
        return false;
    }
}
