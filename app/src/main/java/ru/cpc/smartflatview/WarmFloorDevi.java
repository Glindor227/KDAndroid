package ru.cpc.smartflatview;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

/**
 * Created by Вик on 025. 25.04.16.
 */
public class WarmFloorDevi extends BaseRegulator
{
    public WarmFloorDevi(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
    {
        super(iX, iY, newDez?R.drawable.pol_off:R.drawable.id088, 4, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);

        m_iValue = 20;
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
            iResId = newDez?R.drawable.pol_on:R.drawable.id087;
        else
            iResId = newDez?R.drawable.pol_off:R.drawable.id088;

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
        final ScrollingDialog.SFSwitcher pSwitcher = m_bNoPower ? null : (ScrollingDialog.SFSwitcher)ScrollingDialog.AddSwitcher(m_sVariablePower, context.getString(R.string.sdPower), m_bPower, m_iReaction != 0
                                                                                                                                                                                                  ? null : new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SwitchOnOff(0, 0);
            }
        });
        final ScrollingDialog.SFSeeker pSeeker = (ScrollingDialog.SFSeeker)ScrollingDialog.AddSeekBar(m_sVariableValue, context.getString(R.string.sdTemperature), m_iValue, (int) m_fValueMin, (int) m_fValueMax, "%d °C", m_iReaction != 0
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
                SetValue(seekBar.getProgress() + (int) m_fValueMin);
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

                    if(pSwitcher != null && m_bPower != pSwitcher.m_bChecked)
                        SwitchOnOff(0, 0);
                }
            });

        ScrollingDialog dlg = new ScrollingDialog();
        dlg.show(((Activity) context).getFragmentManager(), "dlg");

//		v.show();
        return false;
    }
}
