package ru.cpc.smartflatview;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

public class Curtains extends BaseRelay
{
	public Curtains(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
		super(iX, iY,
				newDez?(posDez?R.drawable.curtains_close_p:R.drawable.curtains_close):R.drawable.id066,
				newDez?(posDez?R.drawable.curtains_open_p:R.drawable.curtains_open):R.drawable.id067,
				2, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean ShowPopup(Context context)
	{
		ScrollingDialog.Init(m_sName, m_pSubsystem.m_sName);
		final ScrollingDialog.SFSwitcher pSwitcher = (ScrollingDialog.SFSwitcher)ScrollingDialog.AddSwitcher(m_sVariable, context.getString(
						R.string.sdCurtainsOpened), !m_bValue, m_iReaction != 0 ? null : new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SwitchOnOff(0, 0);
			}
		});

		//Intent myIntent = new Intent(context, ScrollingActivity.class);
		//context.startActivity(myIntent);

		if(m_iReaction == 1)
			ScrollingDialog.AddAcceptBtn(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(m_bValue == pSwitcher.m_bChecked)
						SwitchOnOff(0, 0);
				}
			});

		ScrollingDialog dlg = new ScrollingDialog();
		dlg.show(((Activity) context).getFragmentManager(), "dlg");


//		v.show();
		return false;
	}}
