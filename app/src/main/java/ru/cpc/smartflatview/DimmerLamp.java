package ru.cpc.smartflatview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

public class DimmerLamp extends BaseRegulator 
{
	public DimmerLamp(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
		super(iX, iY, R.drawable.lamp02, 1, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);

		m_fValueMin = 0;
		m_fValueMax = 100;
	}

	@Override
	public boolean SetValue(float iX, float iY)
	{
		//int iBrightOld = m_iBright;
		m_iValue = (int) (iX * m_iMaxValue / m_pUI.m_iWidth);
		
		if(m_iValue > m_iMaxValue)
			m_iValue = m_iMaxValue;
		
		if(m_iValue < 0)
			m_iValue = 0;
		
		//if(m_iBright > iBrightOld)
			m_bPower = true;
		Log.d(TAG, "bright = " + m_iValue );

		m_pServer.SendCommand(m_sVariablePower, m_bPower ? "1" : "0");
		if(m_bICP)
			m_pServer.SendCommand(m_sVariableValue, String.valueOf(3584 + m_iValue));
		else
			m_pServer.SendCommand(m_sVariableValue, String.valueOf(m_iValue));
		
		return Update();
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
		final ScrollingDialog.SFSeeker pSeeker = (ScrollingDialog.SFSeeker)ScrollingDialog.AddSeekBar(m_sVariableValue, context.getString(
						R.string.sdBrightness), m_iValue * 100 / m_iMaxValue, (int) m_fValueMin, (int) m_fValueMax, "%d %%", m_iReaction != 0
                                                                                                                             ? null : new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				//if (fromUser) {
				//	SetValue(progress*m_iMaxValue/100);
				//}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				SetValue((seekBar.getProgress() + (int) m_fValueMin) * m_iMaxValue / 100);
			}
		});

		if(m_iReaction == 1)
			ScrollingDialog.AddAcceptBtn(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(m_iValue * 100 / m_iMaxValue != pSeeker.m_iValue)
						SetValue(pSeeker.m_iValue * m_iMaxValue / 100);

					if(pSwitcher != null && m_bPower != pSwitcher.m_bChecked)
						SwitchOnOff(0, 0);
				}
			});

		//Intent myIntent = new Intent(context, ScrollingActivity.class);
		//context.startActivity(myIntent);
		ScrollingDialog dlg = new ScrollingDialog();
		dlg.show(((Activity)context).getFragmentManager(), "dlg");

//		v.show();
		return false;
	}

	@Override
	protected boolean Update()
	{
		//m_iMaxValue = 100;
		//if(m_iMaxValue == 0)
		//{
		//	Log.d(TAG, "111 m_iMaxBright == 0");
		//	return false;
		//}
		
		int iResId = -1;
		
		int iBright = m_iValue * 100 / m_iMaxValue;
		
		if(m_bPower)
		{
			iResId = R.drawable.lamp01;
//			iResId = R.drawable.id055;
//
//			if(iBright < 10)
//				iResId = R.drawable.id046;
//			else if(iBright < 20)
//				iResId = R.drawable.id047;
//			else if(iBright < 30)
//				iResId = R.drawable.id048;
//			else if(iBright < 40)
//				iResId = R.drawable.id049;
//			else if(iBright < 50)
//				iResId = R.drawable.id050;
//			else if(iBright < 60)
//				iResId = R.drawable.id051;
//			else if(iBright < 70)
//				iResId = R.drawable.id052;
//			else if(iBright < 80)
//				iResId = R.drawable.id053;
//			else if(iBright < 90)
//				iResId = R.drawable.id054;
		}
		else
		{
			iResId = R.drawable.lamp02;
//			iResId = R.drawable.id045;
//
//			if(iBright < 10)
//				iResId = R.drawable.id056;
//			else if(iBright < 20)
//				iResId = R.drawable.id057;
//			else if(iBright < 30)
//				iResId = R.drawable.id058;
//			else if(iBright < 40)
//				iResId = R.drawable.id059;
//			else if(iBright < 50)
//				iResId = R.drawable.id060;
//			else if(iBright < 60)
//				iResId = R.drawable.id061;
//			else if(iBright < 70)
//				iResId = R.drawable.id062;
//			else if(iBright < 80)
//				iResId = R.drawable.id063;
//			else if(iBright < 90)
//				iResId = R.drawable.id064;
		}

		Log.d(TAG, "111 power = " + m_bPower + "bright = " + m_iValue );

		if(m_pUI == null)
		{
			m_iOldResID = iResId;
			m_iNewResID = iResId;
			return false;
		}

		return m_pUI.StartAnimation(iResId);
	}
}
