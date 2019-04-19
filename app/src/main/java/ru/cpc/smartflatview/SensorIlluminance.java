package ru.cpc.smartflatview;

/**
 * Created by Вик on 026. 26.04.16.
 */
public class SensorIlluminance extends BaseSensor
{
    public SensorIlluminance(int iX, int iY, String sName, boolean bMetaInd, boolean bProtected, boolean bDoubleScale, boolean bQuick, int iReaction, int iScale)
    {
        super(iX, iY, 3, sName, "lx", bMetaInd, bProtected, bDoubleScale, bQuick, iReaction, iScale);
        // TODO Auto-generated constructor stub
    }
}