package ru.cpc.smartflatview;

import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Вик on 016. 16.03.16.
 */
public class ExpandedMenuModel {

    String m_sName = "";
    int m_iIcon = -1; // menu icon resource id
    int m_iRoomIndex = -1;

    public String getName() {
        return m_sName;
    }
    public int getIcon() { return m_iIcon; }
    public void setIcon(int iIcon) {
        m_iIcon = iIcon;
        if(m_pView != null)
        {
            ImageView childIcon = (ImageView)m_pView.findViewById(R.id.iconimage);
            if(childIcon != null && m_iIcon != -1)
                childIcon.setImageResource(m_iIcon);
        }
    }
    public int getRoom() { return m_iRoomIndex; }

    public ExpandedMenuModel(String sName, int iIcon, int iRoom)
    {
        m_sName = sName;
        m_iIcon = iIcon;
        m_iRoomIndex = iRoom;
    }

    public ArrayList<ExpandedMenuModel> m_cNestedMenu = new ArrayList<ExpandedMenuModel>();

    public View m_pView = null;

    public String toString()
    {
        return m_sName;
    }
}
