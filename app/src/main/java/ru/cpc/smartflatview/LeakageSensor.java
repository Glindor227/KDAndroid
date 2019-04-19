package ru.cpc.smartflatview;

public class LeakageSensor extends BaseAlarmSensor
{
	public LeakageSensor(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
//		super(iX, iY, R.drawable.id079, R.drawable.id080, R.drawable.id081, 3, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
		super(iX, iY, R.drawable.leakage_sensor_alarm, R.drawable.leakage_sensor_on, R.drawable.leakage_sensor_off, 3, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);

		// TODO Auto-generated constructor stub
		m_iImitateAlarmChance = 9;
	}
}
