package ru.cpc.smartflatview;

public class MotionSensor extends BaseAlarmSensor
{
	public MotionSensor(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
		super(iX, iY,
				newDez?(posDez?R.drawable.guard_sensor_alarm_p:R.drawable.guard_sensor_alarm):R.drawable.id076,
				newDez?(posDez?R.drawable.guard_sensor_on_p: R.drawable.guard_sensor_on):R.drawable.id077,
				newDez?(posDez?R.drawable.guard_sensor_off_p:R.drawable.guard_sensor_off):R.drawable.id075,
				2, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
		// TODO Auto-generated constructor stub
		m_iImitateAlarmChance = 6;
	}
}
