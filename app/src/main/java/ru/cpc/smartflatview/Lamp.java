package ru.cpc.smartflatview;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

public class Lamp extends BaseRelay
{
	public Lamp(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
		super(iX, iY,
				newDez?(posDez?R.drawable.lamp_on_p:R.drawable.lamp_on):R.drawable.lamp03,
				newDez?(posDez?R.drawable.lamp_off_p:R.drawable.lamp_off):R.drawable.lamp04,
				1, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean ShowPopup(Context context)
	{
		ScrollingDialog.Init(m_sName, m_pSubsystem.m_sName);
		final ScrollingDialog.SFSwitcher pSwitcher = (ScrollingDialog.SFSwitcher)ScrollingDialog.AddSwitcher(m_sVariable, context.getString(R.string.sdPower), m_bValue, m_iReaction != 0
                                                                                                                                                                         ? null : new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SwitchOnOff(0, 0);
			}
		});

		//Intent myIntent = new Intent(context, ScrollingActivity.class);
		//myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		//context.startActivity(myIntent);

		if(m_iReaction == 1)
			ScrollingDialog.AddAcceptBtn(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(m_bValue != pSwitcher.m_bChecked)
						SwitchOnOff(0,0);
				}
			});

		ScrollingDialog dlg = new ScrollingDialog();
        dlg.show(((Activity) context).getFragmentManager(), "dlg");

		Log.d(TAG, "startActivity...");

//		v.show();
		return false;
	}
}
