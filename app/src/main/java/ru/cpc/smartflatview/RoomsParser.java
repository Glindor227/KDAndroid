package ru.cpc.smartflatview;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class RoomsParser extends DefaultHandler 
{
    // ===========================================================
    // Fields
    // ===========================================================

	private Room m_pRoom = null;
	private Subsystem m_pSubsystem = null;
	private String m_sGroup = "";

	private ArrayList<Room> m_cRooms = new ArrayList<Room>();
	private HashMap<String, Room> m_cRoomID = new HashMap<>();
	private LinkedHashMap<String, List<Room>> m_cGroups = new LinkedHashMap<>();
	private ArrayList<Room> m_cFavorites = new ArrayList<Room>();
    // ===========================================================
    // Getter & Setter
    // ===========================================================

	public ArrayList<Room> getRooms()
	{
		return m_cRooms;
	}

	public ArrayList<Room> getFavorites()
	{
		return m_cFavorites;
	}

	public LinkedHashMap<String, List<Room>> getGroups()
	{
		return m_cGroups;
	}

	private String m_sIP = "192.168.1.1";
    
    public String getIP()
    {
    	return m_sIP;
    }
    
    private String m_sSummaryName = "SMARTFLAT Mobile";
    
    public String getSummaryName()
    {
    	return m_sSummaryName;
    }
    
    private String m_sSummaryText = "SMARTFLAT Mobile";
    
    public String getSummaryText()
    {
    	return m_sSummaryText;
    }
    
    private int m_iPort = 10000;
    
    public int getPort()
    {
    	return m_iPort;
    }
    
//    private int m_iPortExt = 10277;
//    
//    public int getPortExt()
//    {
//    	return m_iPortExt;
//    }
    
    private int m_iPollPeriod = 2000;
    
    public int getPollPeriod()
    {
    	return m_iPollPeriod;
    }
    
    private String m_sMasterCode = "1234";

    public String getMasterCode()
    {
    	return m_sMasterCode;
    }
    
    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void startDocument() throws SAXException 
    {
        m_cRooms = new ArrayList<Room>();
        m_iRoomsIndex = 0;
		m_iSubsystemsIndex = 0;
    }

    @Override
    public void endDocument() throws SAXException 
    {
        // Nothing to do
    }

	private int m_iRoomsIndex = 0;
	private int m_iSubsystemsIndex = 0;
	private Boolean m_bGroupsMode = false;
	private Boolean m_bFavoritesMode = false;
    
    /** Gets be called on opening tags like:
     * <tag>
     * Can provide attribute(s), when xml was like:
     * <tag attribute="attributeValue">*/
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException 
    {
    	//Log.d("XML", "startElement " + localName);
    	try
    	{
	    	if(localName.equals("android"))
	    	{
	    		m_sIP = atts.getValue("kdserverip");
	    		
	    		String sPort = atts.getValue("kdserverport");
	    		m_iPort = Integer.parseInt(sPort);
	    		
//	    		if(atts.getIndex("kdserverportexternal") != -1)
//	    		{
//	    			String sPortExt = atts.getValue("kdserverportexternal");
//	    			m_iPortExt = Integer.parseInt(sPortExt);
//	    		}

	    		if(atts.getIndex("pollperiod") != -1)
	    		{
	    			String sPollPeriod = atts.getValue("pollperiod");
	    			m_iPollPeriod = Integer.parseInt(sPollPeriod);
	    		}
	    		
	    		if(atts.getIndex("mastercode") != -1)
	    		{
					m_sMasterCode = atts.getValue("mastercode");
	    		}
	    	}
	    	else if(localName.equals("smartflat"))
	    	{
	    		m_sIP = atts.getValue("serverip");
	    		
	    		String sPort = atts.getValue("serverport");
	    		m_iPort = Integer.parseInt(sPort);
	    		
    			String sPollPeriod = atts.getValue("pollperiod");
    			m_iPollPeriod = Integer.parseInt(sPollPeriod);

				m_sMasterCode = atts.getValue("mastercode");
	    	}
	    	else if (localName.equals("summary")) 
	        {
	    		m_sSummaryName = atts.getValue("name");
	    		m_sSummaryText = atts.getValue("text");
				m_bGroupsMode = false;
				m_bFavoritesMode = false;
				Log.d("XML", "summary/");
	        }
			else if(localName.equals("groups"))
			{
				Log.d("XML", "groups");
				m_bGroupsMode = true;
			}
			else if(localName.equals("favorites"))
			{
				Log.d("XML", "favorites");
				m_bFavoritesMode = true;
			}
			else if(localName.equals("group"))
			{
				if(m_bGroupsMode)
				{
					m_sGroup = atts.getValue("name");
					Log.d("XML", "group: " + m_sGroup);
					m_cGroups.put(m_sGroup, new ArrayList<Room>());
				}
			}
			else if (localName.equals("room"))
			{
				if(m_bGroupsMode)
				{
					String attrID = atts.getValue("id");
					Log.d("XML", "room: " + attrID);
					m_cGroups.get(m_sGroup).add(m_cRoomID.get(attrID));
				}
				else if(m_bFavoritesMode)
				{
					String attrID = atts.getValue("id");
					Log.d("XML", "room: " + attrID);
					m_cFavorites.add(m_cRoomID.get(attrID));
				}
				else
				{
					String attrName = atts.getValue("name");
					String attrID = atts.getValue("id");

					Log.d("XML", "room: " + attrName);

					m_pRoom = new Room(m_iRoomsIndex++, attrName, attrID);
					m_iSubsystemsIndex = 0;
				}
			}
			else if (localName.equals("subsystem"))
			{
				if(m_pRoom != null)
				{
					String sAttrName;

					if (atts.getIndex("scaleX") != -1)
						sAttrName = "scaleX";
					else
						sAttrName = "scalex";
					String attrScaleX = atts.getValue(sAttrName);
					int iScaleX = Integer.parseInt(attrScaleX);

					if (atts.getIndex("scaleY") != -1)
						sAttrName = "scaleY";
					else
						sAttrName = "scaley";
					String attrScaleY = atts.getValue(sAttrName);
					int iScaleY = Integer.parseInt(attrScaleY);

					String attrAddress = atts.getValue("alarmaddress");
					//Log.d("XML", "alarmaddress = " + attrAddress);

					String typeAttrName = atts.getValue("type");

					String idAttrName = atts.getValue("id");

					Log.d("XML", "subsystem: " + typeAttrName);

					m_pSubsystem = new Subsystem(idAttrName, m_iSubsystemsIndex++, iScaleX, iScaleY, typeAttrName).Bind(attrAddress);
					//m_pRoom.AddSubsystem(m_pSubsystem);
					//m_pRoom.m_cSubsystems.add(m_pSubsystem);
				}
			}
	        else if (localName.equals("relay"))
	        {
	        	if(m_pSubsystem != null)
	        	{
	                String attrPosX = atts.getValue("x");
	                int iPosX = Integer.parseInt(attrPosX);
	            	
	                String attrPosY = atts.getValue("y");
	                int iPosY = Integer.parseInt(attrPosY);

					String attrDoubleScale = atts.getValue("double");
					boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

					String attrQuick = atts.getValue("quick");
					boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;

					String attrReaction = atts.getValue("reaction");
					int iReaction = attrReaction == null ? 0 : Integer.parseInt(attrReaction);


					String attrSubType = atts.getValue("subtype");
	                int iSubType = Integer.parseInt(attrSubType);
	
	                String attrAddress = atts.getValue("openaddress");
	                
	    			String attrValue1 = "1";
	    			String attrValue0 = "0";
		    		if(atts.getIndex("onvalue") != -1)
		    		{
		    			attrValue1 = atts.getValue("onvalue");
		    			attrValue0 = atts.getValue("offvalue");
		    		}
		    		
	                String attrName = atts.getValue("name");
					Log.d("XML", "relay: " + attrName);

		            String attrProtected = atts.getValue("secure");
		            boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;
	            	
	                switch (iSubType) 
	                {
						case 1:
							m_pSubsystem.AddIndicator(new Lamp(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
							break;
						case -1:
							m_pSubsystem.AddIndicator(new Lamp(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
							break;
						case 2:
							m_pSubsystem.AddIndicator(new Curtains(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
							break;
						case -2:
							m_pSubsystem.AddIndicator(new Curtains(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
							break;
						case 3:
							m_pSubsystem.AddIndicator(new Valve(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
							break;
						case -3:
							m_pSubsystem.AddIndicator(new Valve(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
							break;
						case 4:
							m_pSubsystem.AddIndicator(new Fan(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
							break;
						case -4:
							m_pSubsystem.AddIndicator(new Fan(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
							break;
						case 5:
							m_pSubsystem.AddIndicator(new WarmFloor(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
							break;
						case -5:
							m_pSubsystem.AddIndicator(new WarmFloor(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
							break;
//						case 6:
//							m_pSubsystem.AddIndicator(new ButtonOn(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
//							break;
//						case -6:
//							m_pSubsystem.AddIndicator(new ButtonOn(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
//							break;
//						case 7:
//							m_pSubsystem.AddIndicator(new ButtonOff(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
//							break;
//						case -7:
//							m_pSubsystem.AddIndicator(new ButtonOff(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
//							break;
                        case 8:
                            m_pSubsystem.AddIndicator(new PowerControl(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
                            break;
                        case -8:
                            m_pSubsystem.AddIndicator(new PowerControl(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
                            break;
                        case 9:
                            m_pSubsystem.AddIndicator(new Radiator(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
                            break;
                        case -9:
                            m_pSubsystem.AddIndicator(new Radiator(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
                            break;
                        case 10:
                            m_pSubsystem.AddIndicator(new Fountain1(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
                            break;
                        case -10:
                            m_pSubsystem.AddIndicator(new Fountain1(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
                            break;
                        case 11:
                            m_pSubsystem.AddIndicator(new Fountain2(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
                            break;
                        case -11:
                            m_pSubsystem.AddIndicator(new Fountain2(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress, attrValue1, attrValue0));
                            break;
					}
	        	}
	        }
	        else if (localName.equals("regulator")) 
	        {
	        	if(m_pSubsystem != null)
	        	{
	                String attrPosX = atts.getValue("x");
	                int iPosX = Integer.parseInt(attrPosX);
	            	
	                String attrPosY = atts.getValue("y");
	                int iPosY = Integer.parseInt(attrPosY);
	
		            String attrDoubleScale = atts.getValue("double");
		            boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

					String attrQuick = atts.getValue("quick");
					boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;

					String attrReaction = atts.getValue("reaction");
					int iReaction = attrReaction == null ? 0 : Integer.parseInt(attrReaction);

	                String attrSubType = atts.getValue("subtype");
	                int iSubType = Integer.parseInt(attrSubType);
	
	                String attrAddress1 = atts.getValue("poweraddress");
	
	                String attrAddress2 = atts.getValue("valueaddress");

                    String attrNoPower = atts.getValue("nopower");
                    boolean bNoPower = attrNoPower != null && Integer.parseInt(attrNoPower) != 0;

	    			String attrValue1 = "1";
	    			String attrValue0 = "0";
					if(atts.getIndex("onvalue") != -1)
					{
						attrValue1 = atts.getValue("onvalue");
						attrValue0 = atts.getValue("offvalue");
					}

					String attrValueMin = "16";
                    String attrValueMax = "30";
                    if(atts.getIndex("minvalue") != -1)
                    {
                        attrValueMin = atts.getValue("minvalue");
                        attrValueMax = atts.getValue("maxvalue");
                    }

                    String attrValueMed = Double.toString((Double.parseDouble(attrValueMin) + Double.parseDouble(attrValueMax)) / 2);
                    if(atts.getIndex("mediumvalue") != -1)
                    {
                        attrValueMed = atts.getValue("mediumvalue");
                    }


					String attrName = atts.getValue("name");
					Log.d("XML", "regulator: " + attrName);

		            String attrProtected = atts.getValue("secure");
		            boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;
	            	
	                switch (iSubType) 
	                {
						case 1:
							m_pSubsystem.AddIndicator(new DimmerLamp(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
							break;
						case -1:
							m_pSubsystem.AddIndicator(new DimmerLamp(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
							break;
						case 2:
							m_pSubsystem.AddIndicator(new DimmerFan(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
							break;
						case -2:
							m_pSubsystem.AddIndicator(new DimmerFan(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
							break;
						case 3:
							m_pSubsystem.AddIndicator(new Conditioner(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
							break;
						case -3:
							m_pSubsystem.AddIndicator(new Conditioner(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
							break;
						case 4:
							m_pSubsystem.AddIndicator(new WarmFloorDevi(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
							break;
						case -4:
							m_pSubsystem.AddIndicator(new WarmFloorDevi(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
							break;
                        case 5:
                            m_pSubsystem.AddIndicator(new Battery(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
                            break;
                        case -5:
                            m_pSubsystem.AddIndicator(new Battery(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
                            break;
                        case 6:
                            m_pSubsystem.AddIndicator(new Battery2(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
                            break;
                        case -6:
                            m_pSubsystem.AddIndicator(new Battery2(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
                            break;
						case 7:
							m_pSubsystem.AddIndicator(new WarmFloorDevi2(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
							break;
						case -7:
							m_pSubsystem.AddIndicator(new WarmFloorDevi2(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, bNoPower, attrValueMin, attrValueMax, attrValueMed));
							break;
						case 8:
							m_pSubsystem.AddIndicator(new NodeSeekBar(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, true, attrValueMin, attrValueMax, attrValueMed));
							break;
						case -8:
							m_pSubsystem.AddIndicator(new NodeSeekBar(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0, true, attrValueMin, attrValueMax, attrValueMed));
							break;
//						case 3:
//							m_pSubsystem.AddIndicator(new GaugeH(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0));
//							break;
//						case -3:
//							m_pSubsystem.AddIndicator(new GaugeH(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0));
//							break;
//						case 4:
//							m_pSubsystem.AddIndicator(new GaugeV(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0));
//							break;
//						case -4:
//							m_pSubsystem.AddIndicator(new GaugeV(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrValue1, attrValue0));
//							break;
					}
	        	}
	        }            
	        else if (localName.equals("alarmsensor")) 
	        {
	        	if(m_pSubsystem != null)
	        	{
	                String attrPosX = atts.getValue("x");
	                int iPosX = Integer.parseInt(attrPosX);
	            	
	                String attrPosY = atts.getValue("y");
	                int iPosY = Integer.parseInt(attrPosY);
	
		            String attrDoubleScale = atts.getValue("double");
		            boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

					String attrQuick = atts.getValue("quick");
					boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;

					String attrReaction = atts.getValue("reaction");
					int iReaction = attrReaction == null ? 0 : Integer.parseInt(attrReaction);

	                String attrSubType = atts.getValue("subtype");
	                int iSubType = Integer.parseInt(attrSubType);
	
	                String attrAddress1 = atts.getValue("guardaddress");
	
	                String attrAddress2 = atts.getValue("alarmaddress");
	                
	    			String attrValue1 = "1";
	    			String attrValue0 = "0";
	    			String attrValueA = "1";
		    		if(atts.getIndex("onvalue") != -1)
		    		{
		    			attrValue1 = atts.getValue("onvalue");
		    			attrValue0 = atts.getValue("offvalue");
		    			attrValueA = atts.getValue("alarmvalue");
		    		}
		    		
	                String attrName = atts.getValue("name");
					Log.d("XML", "alarmsensor: " + attrName);

		            String attrProtected = atts.getValue("secure");
		            boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;
	            	
	                switch (iSubType) 
	                {
						case 1:
							m_pSubsystem.AddIndicator(new FireSensor(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0, attrValueA));
							break;
						case -1:
							m_pSubsystem.AddIndicator(new FireSensor(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0, attrValueA));
							break;
						case 2:
							m_pSubsystem.AddIndicator(new MotionSensor(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0, attrValueA));
							break;
						case -2:
							m_pSubsystem.AddIndicator(new MotionSensor(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0, attrValueA));
							break;
						case 3:
							m_pSubsystem.AddIndicator(new LeakageSensor(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0, attrValueA));
							break;
						case -3:
							m_pSubsystem.AddIndicator(new LeakageSensor(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0, attrValueA));
							break;
					}
	        	}
	        }
			else if (localName.equals("sensor"))
			{
				if(m_pSubsystem != null)
				{
					String attrPosX = atts.getValue("x");
					int iPosX = Integer.parseInt(attrPosX);

					String attrPosY = atts.getValue("y");
					int iPosY = Integer.parseInt(attrPosY);

					String attrDoubleScale = atts.getValue("double");
					boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

					String attrQuick = atts.getValue("quick");
					boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;

					String attrReaction = atts.getValue("reaction");
					int iReaction = attrReaction == null ? 0 : Integer.parseInt(attrReaction);

					String attrSubType = atts.getValue("subtype");
					int iSubType = Integer.parseInt(attrSubType);

					String attrAddress1 = atts.getValue("valueaddress");

					String attrName = atts.getValue("name");
					Log.d("XML", "sensor: " + attrName);

					String attrProtected = atts.getValue("secure");
					boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;

					switch (iSubType)
					{
						case 1:
							m_pSubsystem.AddIndicator(new SensorTemperature(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1));
							break;
						case -1:
							m_pSubsystem.AddIndicator(new SensorTemperature(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1));
							break;
						case 2:
							m_pSubsystem.AddIndicator(new SensorHumidity(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1));
							break;
						case -2:
							m_pSubsystem.AddIndicator(new SensorHumidity(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1));
							break;
						case 3:
							m_pSubsystem.AddIndicator(new SensorIlluminance(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1));
							break;
						case -3:
							m_pSubsystem.AddIndicator(new SensorIlluminance(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1));
							break;
						case 4:
							m_pSubsystem.AddIndicator(new SensorCO(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1));
							break;
						case -4:
							m_pSubsystem.AddIndicator(new SensorCO(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1));
							break;
					}
				}
			}
	        else if (localName.equals("macro"))
	        {
	        	if(m_pSubsystem != null)
	        	{
	                String attrPosX = atts.getValue("x");
	                int iPosX = Integer.parseInt(attrPosX);
	            	
	                String attrPosY = atts.getValue("y");
	                int iPosY = Integer.parseInt(attrPosY);
	
		            String attrDoubleScale = atts.getValue("double");
		            boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

					String attrQuick = atts.getValue("quick");
					boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;

					String attrReaction = atts.getValue("reaction");
					int iReaction = attrReaction == null ? 0 : Integer.parseInt(attrReaction);

	                String attrSubType = atts.getValue("subtype");
	                int iSubType = Integer.parseInt(attrSubType);
	
	                String attrAddress1 = atts.getValue("stateaddress");
	
	                String attrAddress2 = atts.getValue("commandaddress");
	                
	    			String attrValue1 = "1";
	    			String attrValue0 = "0";
		    		if(atts.getIndex("onvalue") != -1)
		    		{
		    			attrValue1 = atts.getValue("onvalue");
		    			attrValue0 = atts.getValue("offvalue");
		    		}
		    		
	                String attrName = atts.getValue("name");
	                String attrName2 = atts.getValue("namebutton1");
//	        		Log.d("XML", "macro name: " + attrName);
//	        		Log.d("XML", "button name: " + attrName2);
					Log.d("XML", "macro: " + attrName);

		            String attrProtected = atts.getValue("secure");
		            boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;
	            	
	                switch (iSubType) 
	                {
						case 1:
							m_pSubsystem.AddIndicator(new Macro(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
							break;
						case -1:
							m_pSubsystem.AddIndicator(new Macro(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
							break;
//						case 2:
//							m_pSubsystem.AddIndicator(new ButtonBig(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
//							break;
//						case -2:
//							m_pSubsystem.AddIndicator(new ButtonBig(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
//							break;
//						case 3:
//							m_pSubsystem.AddIndicator(new ButtonSmall(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
//							break;
//						case -3:
//							m_pSubsystem.AddIndicator(new ButtonSmall(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
//							break;
						case 4:
							m_pSubsystem.AddIndicator(new MacroCamOn(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
							break;
						case -4:
							m_pSubsystem.AddIndicator(new MacroCamOn(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
							break;
						case 5:
							m_pSubsystem.AddIndicator(new MacroCamOff(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
							break;
						case -5:
							m_pSubsystem.AddIndicator(new MacroCamOff(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
							break;
						case 6:
							m_pSubsystem.AddIndicator(new MacroMotionSensor(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
							break;
						case -6:
							m_pSubsystem.AddIndicator(new MacroMotionSensor(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
							break;
						case 7:
							m_pSubsystem.AddIndicator(new MacroFireSensor(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
							break;
						case -7:
							m_pSubsystem.AddIndicator(new MacroFireSensor(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
							break;
                        case 8:
                            m_pSubsystem.AddIndicator(new MacroFreePass(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case -8:
                            m_pSubsystem.AddIndicator(new MacroFreePass(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case 9:
                            m_pSubsystem.AddIndicator(new MacroLowLevel(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case -9:
                            m_pSubsystem.AddIndicator(new MacroLowLevel(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case 10:
                            m_pSubsystem.AddIndicator(new MacroDezinfection(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case -10:
                            m_pSubsystem.AddIndicator(new MacroDezinfection(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case 11:
                            m_pSubsystem.AddIndicator(new MacroPumpFail(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case -11:
                            m_pSubsystem.AddIndicator(new MacroPumpFail(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case 12:
                            m_pSubsystem.AddIndicator(new MacroFilterFail(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case -12:
                            m_pSubsystem.AddIndicator(new MacroFilterFail(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case 13:
                            m_pSubsystem.AddIndicator(new MacroPumpFilter(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case -13:
                            m_pSubsystem.AddIndicator(new MacroPumpFilter(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case 14:
                            m_pSubsystem.AddIndicator(new MacroPumpWorkMode(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case -14:
                            m_pSubsystem.AddIndicator(new MacroPumpWorkMode(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case 15:
                            m_pSubsystem.AddIndicator(new MacroFilterCleaning(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case -15:
                            m_pSubsystem.AddIndicator(new MacroFilterCleaning(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case 16:
                            m_pSubsystem.AddIndicator(new MacroPumpAddWater(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case -16:
                            m_pSubsystem.AddIndicator(new MacroPumpAddWater(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case 17:
                            m_pSubsystem.AddIndicator(new MacroAlarm(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case -17:
                            m_pSubsystem.AddIndicator(new MacroAlarm(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case 18:
                            m_pSubsystem.AddIndicator(new MacroFlashlight(iPosX/10, iPosY/10, attrName, attrName2, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
                        case -18:
                            m_pSubsystem.AddIndicator(new MacroFlashlight(iPosX/10, iPosY/10, attrName, attrName2, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress2, attrAddress1, attrValue1, attrValue0));
                            break;
					}
	        	}
	        }
	        else if (localName.equals("door")) 
	        {
	        	if(m_pSubsystem != null)
	        	{
	                String attrPosX = atts.getValue("x");
	                int iPosX = Integer.parseInt(attrPosX);
	            	
	                String attrPosY = atts.getValue("y");
	                int iPosY = Integer.parseInt(attrPosY);
	
		            String attrDoubleScale = atts.getValue("double");
		            boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

					String attrQuick = atts.getValue("quick");
					boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;

					String attrReaction = atts.getValue("reaction");
					int iReaction = attrReaction == null ? 0 : Integer.parseInt(attrReaction);

		            int iSubType = 1;
		    		if(atts.getIndex("subtype") != -1)
		    		{
	                   String attrSubType = atts.getValue("subtype");
	                   iSubType = Integer.parseInt(attrSubType);
		    		}
		    		
	                String attrAddress1 = atts.getValue("guardaddress");
	            	
	                String attrAddress2 = atts.getValue("openstateaddress");
	            	
	                String attrAddress3 = atts.getValue("alarmaddress");

	                String attrAddress4 = atts.getValue("opencommandaddress");

					String attrAddress5 = "-1";
					if(atts.getIndex("lockstateaddress") != -1)
					{
						attrAddress5 = atts.getValue("lockstateaddress");
					}

					String attrName = atts.getValue("name");
					Log.d("XML", "door: " + attrName);

		            String attrProtected = atts.getValue("secure");
		            boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;
	            	
	                switch (iSubType) 
	                {
						case 1:
							m_pSubsystem.AddIndicator(new Door(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4));
							break;
						case -1:
							m_pSubsystem.AddIndicator(new Door(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4));
							break;
						case 2:
							m_pSubsystem.AddIndicator(new DoorLock(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
							break;
						case -2:
							m_pSubsystem.AddIndicator(new DoorLock(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
							break;
					}
	        	}
	        }
	        else if (localName.equals("door2")) 
	        {
	        	if(m_pSubsystem != null)
	        	{
	                String attrPosX = atts.getValue("x");
	                int iPosX = Integer.parseInt(attrPosX);
	            	
	                String attrPosY = atts.getValue("y");
	                int iPosY = Integer.parseInt(attrPosY);
	
		            String attrDoubleScale = atts.getValue("double");
		            boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

					String attrQuick = atts.getValue("quick");
					boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;

					String attrReaction = atts.getValue("reaction");
					int iReaction = attrReaction == null ? 0 : Integer.parseInt(attrReaction);

		            int iSubType = 1;
		    		if(atts.getIndex("subtype") != -1)
		    		{
	                   String attrSubType = atts.getValue("subtype");
	                   iSubType = Integer.parseInt(attrSubType);
		    		}
		    		
	                String attrAddress1 = atts.getValue("blockaddress");
	            	
	                String attrAddress2 = atts.getValue("fireaddress");
	            	
	                String attrName = atts.getValue("name");
					Log.d("XML", "door2: " + attrName);

		            String attrProtected = atts.getValue("secure");
		            boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;
	            	
	                switch (iSubType) 
	                {
						case 1:
							m_pSubsystem.AddIndicator(new Door2(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2));
							break;
						case -1:
							m_pSubsystem.AddIndicator(new Door2(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2));
							break;
					}
	        	}
	        }
            else if (localName.equals("climatic"))
            {
                if(m_pSubsystem != null)
                {
                    String attrPosX = atts.getValue("x");
                    int iPosX = Integer.parseInt(attrPosX);

                    String attrPosY = atts.getValue("y");
                    int iPosY = Integer.parseInt(attrPosY);

                    String attrDoubleScale = atts.getValue("double");
                    boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

                    String attrQuick = atts.getValue("quick");
                    boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;

                    String attrReaction = atts.getValue("reaction");
					int iReaction = attrReaction == null ? 0 : Integer.parseInt(attrReaction);

                    int iSubType = 1;
                    if(atts.getIndex("subtype") != -1)
                    {
                        String attrSubType = atts.getValue("subtype");
                        iSubType = Integer.parseInt(attrSubType);
                    }

                    String attrAddress1 = atts.getValue("poweraddress");

                    String attrAddress2 = atts.getValue("tempaddress");

                    String attrAddress3 = atts.getValue("speedaddress");

                    String attrAddress4 = atts.getValue("modeaddress");

                    String attrNoPower = atts.getValue("nopower");
                    boolean bNoPower = attrNoPower != null && Integer.parseInt(attrNoPower) != 0;

					String attrValue1 = "1";
					String attrValue0 = "0";
					if(atts.getIndex("onvalue") != -1)
					{
						attrValue1 = atts.getValue("onvalue");
						attrValue0 = atts.getValue("offvalue");
					}

					String attrTMin = "0";
					String attrTMax = "100";
					String attrSMin = "0";
					String attrSMax = "100";
					String attrDefMode = "0";
					if(atts.getIndex("mintemp") != -1)
					{
						attrTMin = atts.getValue("mintemp");
						attrTMax = atts.getValue("maxtemp");
						attrSMin = atts.getValue("minspeed");
						attrSMax = atts.getValue("maxspeed");
						attrDefMode = atts.getValue("defaultmode");
					}

                    String attrName = atts.getValue("name");
                    Log.d("XML", "climat: " + attrName);

                    String attrProtected = atts.getValue("secure");
                    boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;

                    switch (iSubType)
                    {
                        case 1:
                            m_pSubsystem.AddIndicator(new ClimatConditioner(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrValue1, attrValue0, bNoPower, attrTMin, attrTMax, attrSMin, attrSMax, attrDefMode));
                            break;
                        case -1:
                            m_pSubsystem.AddIndicator(new ClimatConditioner(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrValue1, attrValue0, bNoPower, attrTMin, attrTMax, attrSMin, attrSMax, attrDefMode));
                            break;
                        case 2:
                            m_pSubsystem.AddIndicator(new ClimatFan(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrValue1, attrValue0, bNoPower, attrTMin, attrTMax, attrSMin, attrSMax, attrDefMode));
                            break;
                        case -2:
                            m_pSubsystem.AddIndicator(new ClimatFan(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrValue1, attrValue0, bNoPower, attrTMin, attrTMax, attrSMin, attrSMax, attrDefMode));
                            break;
                        case 3:
                            m_pSubsystem.AddIndicator(new ClimatWarmFloor(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrValue1, attrValue0, bNoPower, attrTMin, attrTMax, attrSMin, attrSMax, attrDefMode));
                            break;
                        case -3:
                            m_pSubsystem.AddIndicator(new ClimatWarmFloor(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrValue1, attrValue0, bNoPower, attrTMin, attrTMax, attrSMin, attrSMax, attrDefMode));
                            break;
                        case 4:
                            m_pSubsystem.AddIndicator(new ClimatRadiator(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrValue1, attrValue0, bNoPower, attrTMin, attrTMax, attrSMin, attrSMax, attrDefMode));
                            break;
                        case -4:
                            m_pSubsystem.AddIndicator(new ClimatRadiator(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrValue1, attrValue0, bNoPower, attrTMin, attrTMax, attrSMin, attrSMax, attrDefMode));
                            break;
                    }
                }
            }
            else if (localName.equals("mediabutton"))
            {
                if(m_pRoom != null)
                {
                    String attrPosX = atts.getValue("x");
                    int iPosX = Integer.parseInt(attrPosX);

                    String attrPosY = atts.getValue("y");
                    int iPosY = Integer.parseInt(attrPosY);

                    String attrDoubleScale = atts.getValue("double");
                    boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

//                    String attrQuick = atts.getValue("quick");
//                    boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;
//
//                    String attrReaction = atts.getValue("reaction");
//                    int iReaction = attrReaction == null && Integer.parseInt(attrReaction) != 0;

                    String attrSubType = atts.getValue("subtype");
                    int iSubType = Integer.parseInt(attrSubType);

                    String attrAddress1 = atts.getValue("valueaddress");

                    String attrAddress2 = atts.getValue("stateaddress");

                    Log.d("XML", "mediabutton: " + attrSubType);

//                    String attrProtected = atts.getValue("secure");
//                    boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;

                    m_pSubsystem.AddIndicator(new ButtonMedia(iPosX/10, iPosY/10, iSubType, "", false, false, bDoubleScale, true, 0, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2));

//	                switch (iSubType)
//	                {
//						case 1:
//							m_pSubsystem.AddIndicator(new Cursor(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
//							break;
//						case -1:
//							m_pSubsystem.AddIndicator(new Cursor(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
//							break;
//					}
                }
            }
            else if (localName.equals("swing"))
            {
                if(m_pRoom != null)
                {
                    String attrPosX = atts.getValue("x");
                    int iPosX = Integer.parseInt(attrPosX);

                    String attrPosY = atts.getValue("y");
                    int iPosY = Integer.parseInt(attrPosY);

                    String attrDoubleScale = atts.getValue("double");
                    boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

//                    String attrQuick = atts.getValue("quick");
//                    boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;
//
//                    String attrReaction = atts.getValue("reaction");
//                    int iReaction = attrReaction == null && Integer.parseInt(attrReaction) != 0;

                    String attrSubType = atts.getValue("subtype");
                    int iSubType = Integer.parseInt(attrSubType);

                    String attrAddress1 = atts.getValue("upaddress");

                    String attrAddress2 = atts.getValue("downaddress");

                    String attrName = atts.getValue("name");
                    Log.d("XML", "swing: " + attrName);

//                    String attrProtected = atts.getValue("secure");
//                    boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;

                    m_pSubsystem.AddIndicator(new PlusMinus(iPosX/10, iPosY/10, iSubType, attrName, false, false, bDoubleScale, true, 0, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2));

//	                switch (iSubType)
//	                {
//						case 1:
//							m_pSubsystem.AddIndicator(new Cursor(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
//							break;
//						case -1:
//							m_pSubsystem.AddIndicator(new Cursor(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
//							break;
//					}
                }
            }
            else if (localName.equals("selector"))
            {
                if(m_pRoom != null)
                {
                    String attrPosX = atts.getValue("x");
                    int iPosX = Integer.parseInt(attrPosX);

                    String attrPosY = atts.getValue("y");
                    int iPosY = Integer.parseInt(attrPosY);

                    String attrDoubleScale = atts.getValue("double");
                    boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

//                    String attrQuick = atts.getValue("quick");
//                    boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;
//
//                    String attrReaction = atts.getValue("reaction");
//                    int iReaction = attrReaction == null && Integer.parseInt(attrReaction) != 0;

                    String attrSubType = atts.getValue("subtype");
                    int iSubType = Integer.parseInt(attrSubType);

                    String attrAddress1 = atts.getValue("sourceaddress");

                    String attrName = atts.getValue("name");
                    Log.d("XML", "selector: " + attrName);

//                    String attrProtected = atts.getValue("secure");
//                    boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;

                    m_pSubsystem.AddIndicator(new SourceSelector(iPosX/10, iPosY/10, attrName, false, false, bDoubleScale, true, 0, m_pSubsystem.m_iGridWidth).Bind(attrAddress1));

//	                switch (iSubType)
//	                {
//						case 1:
//							m_pSubsystem.AddIndicator(new Cursor(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
//							break;
//						case -1:
//							m_pSubsystem.AddIndicator(new Cursor(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
//							break;
//					}
                }
            }
            else if (localName.equals("mediafield"))
            {
                if(m_pRoom != null)
                {
                    String attrPosX = atts.getValue("x");
                    int iPosX = Integer.parseInt(attrPosX);

                    String attrPosY = atts.getValue("y");
                    int iPosY = Integer.parseInt(attrPosY);

                    String attrDoubleScale = atts.getValue("double");
                    boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

//                    String attrQuick = atts.getValue("quick");
//                    boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;
//
//                    String attrReaction = atts.getValue("reaction");
//                    int iReaction = attrReaction == null && Integer.parseInt(attrReaction) != 0;

                    String attrSubType = atts.getValue("subtype");
                    int iSubType = Integer.parseInt(attrSubType);

                    String attrAddress1 = atts.getValue("valueaddress");

                    String attrName = atts.getValue("name");
                    Log.d("XML", "mediafield: " + attrName);

//                    String attrProtected = atts.getValue("secure");
//                    boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;

                    m_pSubsystem.AddIndicator(new MediaField(iPosX/10, iPosY/10, attrName, "Track 1", false, false, bDoubleScale, true, 0, m_pSubsystem.m_iGridWidth));

//	                switch (iSubType)
//	                {
//						case 1:
//							m_pSubsystem.AddIndicator(new Cursor(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
//							break;
//						case -1:
//							m_pSubsystem.AddIndicator(new Cursor(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
//							break;
//					}
                }
            }
            else if (localName.equals("cursor"))
            {
                if(m_pRoom != null)
                {
                    String attrPosX = atts.getValue("x");
                    int iPosX = Integer.parseInt(attrPosX);

                    String attrPosY = atts.getValue("y");
                    int iPosY = Integer.parseInt(attrPosY);

                    String attrDoubleScale = atts.getValue("double");
                    boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

                    String attrQuick = atts.getValue("quick");
                    boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;

                    String attrReaction = atts.getValue("reaction");
					int iReaction = attrReaction == null ? 0 : Integer.parseInt(attrReaction);

                    String attrSubType = atts.getValue("subtype");
                    int iSubType = Integer.parseInt(attrSubType);

                    String attrAddress1 = atts.getValue("poweraddress");

                    String attrAddress2 = atts.getValue("leftaddress");

                    String attrAddress3 = atts.getValue("rightaddress");

                    String attrAddress4 = atts.getValue("upaddress");

                    String attrAddress5 = atts.getValue("downaddress");

                    String attrName = atts.getValue("name");
                    Log.d("XML", "cursor: " + attrName);

                    String attrProtected = atts.getValue("secure");
                    boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;

//	                switch (iSubType)
//	                {
//						case 1:
//							m_pSubsystem.AddIndicator(new Cursor(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
//							break;
//						case -1:
//							m_pSubsystem.AddIndicator(new Cursor(iPosX/10, iPosY/10, attrName, true, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
//							break;
//					}
                }
            }
			else if (localName.equals("IDIS"))
			{
				if(m_pSubsystem != null)
				{
					String attrPosX = atts.getValue("x");
					int iPosX = Integer.parseInt(attrPosX);

					String attrPosY = atts.getValue("y");
					int iPosY = Integer.parseInt(attrPosY);

					String attrDoubleScale = atts.getValue("double");
					boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

					String attrQuick = atts.getValue("quick");
					boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;

					String attrReaction = atts.getValue("reaction");
					int iReaction = attrReaction == null ? 0 : Integer.parseInt(attrReaction);

					int iSubType = 1;
					if(atts.getIndex("subtype") != -1)
					{
						String attrSubType = atts.getValue("subtype");
						iSubType = Integer.parseInt(attrSubType);
					}

					String attrAddress1 = atts.getValue("IP");

					String attrAddress2 = atts.getValue("port");

					String attrAddress3 = atts.getValue("camId");

					String attrAddress4 = atts.getValue("id_user");

					String attrAddress5 = atts.getValue("password");

					String attrName = atts.getValue("name");
					Log.d("XML", "IDIS: " + attrName);

					String attrProtected = atts.getValue("secure");
					boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;

					switch (iSubType)
					{
						case 1:
							m_pSubsystem.AddIndicator(new CamVideo(iPosX/10, iPosY/10, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrAddress2, attrAddress3, attrAddress4, attrAddress5));
							break;
					}
				}
			}
			//          <links count="1">
			//            <link name="Тревога на ноде" subtype="1" alarmaddress="_AN_001" alarmvalue="1" subsystem="MobileRoom_8_subsystem" x="480" y="380" double="0" secure="0" quick="1" reaction="1" />
			//          </links>
			else if (localName.equals("link"))
			{
				if(m_pSubsystem != null)
				{
					String attrPosX = atts.getValue("x");
					int iPosX = Integer.parseInt(attrPosX);

					String attrPosY = atts.getValue("y");
					int iPosY = Integer.parseInt(attrPosY);

					String attrDoubleScale = atts.getValue("double");
					boolean bDoubleScale = attrDoubleScale != null && Integer.parseInt(attrDoubleScale) != 0;

					String attrQuick = atts.getValue("quick");
					boolean bQuick = attrQuick != null && Integer.parseInt(attrQuick) != 0;

					String attrReaction = atts.getValue("reaction");
					int iReaction = attrReaction == null ? 0 : Integer.parseInt(attrReaction);

					int iSubType = 1;
					if(atts.getIndex("subtype") != -1)
					{
						String attrSubType = atts.getValue("subtype");
						iSubType = Integer.parseInt(attrSubType);
					}

					String attrAddress1 = atts.getValue("alarmaddress");
					String attrAddress2 = atts.getValue("subsystem");

					String attrValueA = "1";
					if(atts.getIndex("alarmvalue") != -1)
						attrValueA = atts.getValue("alarmvalue");

					String attrName = atts.getValue("name");
					Log.d("XML", "link: " + attrName);

					String attrProtected = atts.getValue("secure");
					boolean bProtected = attrProtected != null && Integer.parseInt(attrProtected) != 0;

					switch (iSubType)
					{
						case 1:
							m_pSubsystem.AddIndicator(new NodeAlarm(iPosX/10, iPosY/10, R.drawable.alarmnode1, R.drawable.alarmnode0, 1, attrName, false, bProtected, bDoubleScale, bQuick, iReaction, m_pSubsystem.m_iGridWidth).Bind(attrAddress1, attrValueA, attrAddress2));
							break;
					}
				}
			}
    	}
    	catch(Exception ex)
    	{
    		Log.e("XML", "Parsing exception: ", ex);
    	}
    }
   
    /** Gets be called on closing tags like:
     * </tag> */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
                    throws SAXException 
    {
		if(localName.equals("groups"))
		{
			Log.d("XML", "/groups");
			m_bGroupsMode = false;
		}

		if(localName.equals("favorites"))
		{
			Log.d("XML", "/favorites");
			m_bFavoritesMode = false;
		}

		if (localName.equals("room"))
        {
			Log.d("XML", "/room");
			if(!m_bGroupsMode) {
				if (m_pRoom != null) {
					m_cRooms.add(m_pRoom);
					m_cRoomID.put(m_pRoom.m_sID, m_pRoom);
				}
				m_pRoom = null;
			}
        }

		if(localName.equals("subsystem"))
		{
			Log.d("XML", "/subsystem");
			if(m_pRoom != null && m_pSubsystem != null)
				m_pRoom.AddSubsystem(m_pSubsystem);
			m_pSubsystem = null;
		}
    }
   
    /** Gets be called on the following structure:
     * <tag>characters</tag> */
    @Override
    public void characters(char ch[], int start, int length) 
    {
    }
}
