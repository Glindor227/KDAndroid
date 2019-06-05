package ru.cpc.smartflatview;

public class FireSensor extends BaseAlarmSensor
{
	public FireSensor(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
	{
			super(iX, iY,
					newDez?(posDez?R.drawable.fire_sensor_alarm_p:R.drawable.fire_sensor_alarm):R.drawable.id072,
					newDez?(posDez?R.drawable.fire_sensor_on_p:R.drawable.fire_sensor_on):R.drawable.id073,
					newDez?(posDez?R.drawable.fire_sensor_off_p:R.drawable.fire_sensor_off):R.drawable.id071,
					1, sName, bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);


		// TODO Auto-generated constructor stub
		m_iImitateAlarmChance = 15;
	}
}
