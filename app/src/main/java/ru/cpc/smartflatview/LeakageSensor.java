package ru.cpc.smartflatview;

public class LeakageSensor extends BaseAlarmSensor
{
	public LeakageSensor(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
		super(iX, iY,
				newDez?(posDez?R.drawable.leakage_sensor_alarm_p:R.drawable.leakage_sensor_alarm):R.drawable.id079,
				newDez?(posDez?R.drawable.leakage_sensor_on_p:R.drawable.leakage_sensor_on):R.drawable.id080,
				newDez?(posDez?R.drawable.leakage_sensor_off_p:R.drawable.leakage_sensor_off):R.drawable.id081,
				3, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);

		// TODO Auto-generated constructor stub
		m_iImitateAlarmChance = 9;
	}
}
