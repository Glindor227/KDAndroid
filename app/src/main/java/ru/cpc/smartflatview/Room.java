package ru.cpc.smartflatview;

import java.util.ArrayList;

/**
 * Created by Вик on 022. 22.01.16.
 */
public class Room
{
    public int m_iIndex;

    String m_sName;

    String m_sID;

    ArrayList<Subsystem> m_cSubsystems = new ArrayList<Subsystem>();

    public Room(int index, String sName, String sID)
    {
        m_iIndex = index;

        m_sName = sName;

        m_sID = sID;
    }

    public Room AddSubsystem(Subsystem pSubsystem)
    {
        if(pSubsystem != null)
        {
            m_cSubsystems.add(pSubsystem);
            pSubsystem.m_pRoom = this;
        }

        return this;
    }
}
