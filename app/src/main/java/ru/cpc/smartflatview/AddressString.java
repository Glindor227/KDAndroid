package ru.cpc.smartflatview;

import android.content.Context;

import java.util.ArrayList;
import java.util.Hashtable;

public class AddressString 
{
	public class QueryLine
	{
		private int mCount;
		private String mLine;

		public QueryLine(int count, String line)
		{
			mCount = count;
			mLine = line;
		}

		public String BuildLine(Context pContext)
		{
			return "<5|" + String.valueOf(mCount) + "|" + Prefs.getSecurityCode(pContext) + mLine + ">\r\n";
		}
	}

    public Hashtable<String, ArrayList<Indicator>> m_cAddresses = new Hashtable<String, ArrayList<Indicator>>();

    public Hashtable<String, ArrayList<Subsystem>> m_cAlarms = new Hashtable<String, ArrayList<Subsystem>>();
	
	private ArrayList<QueryLine> m_aQuery = new ArrayList<QueryLine>();

  private int m_aQueryPointer = 0;

  public boolean IsOver()
  {
    return m_aQueryPointer >= m_aQuery.size(); 
  }
	                                            
  public void ResetQuery()
  {
      m_aQueryPointer = 0;
  }

	public String GetNext(Context pContext)
	{
		if(IsOver())
			return "";

		return m_aQuery.get(m_aQueryPointer++).BuildLine(pContext);
	}

	public void GoBack()
	{
		if(m_aQueryPointer > 0)
			m_aQueryPointer--;
	}

	public boolean Build(Context pContext)
	{
		m_aQuery.clear();
    m_aQueryPointer = 0;

		if(m_cAddresses.size() > 0)
		{
			String sResult = new String();
			int iCount = 0;
			
			for(String sAddr : m_cAddresses.keySet())
			{
				if(sAddr.equalsIgnoreCase("-1"))
					continue;
				
				try
				{
					if(Integer.parseInt(sAddr) == -1)
						continue;
				}
				catch(Exception ex)
				{}
				
				sResult += "|" + sAddr;
				iCount++;
				
				if(iCount >= 32)
				{
					m_aQuery.add(new QueryLine(iCount, sResult));
					iCount = 0;
					sResult = "";
				}
			}
			
			for(String sAddr : m_cAlarms.keySet())
			{
				if(sAddr.equalsIgnoreCase("-1"))
					continue;

				try
				{
					if(Integer.parseInt(sAddr) == -1)
						continue;
				}
				catch(Exception ex)
				{}
				
				sResult += "|" + sAddr;
				iCount++;
				
				if(iCount >= 32)
				{
					m_aQuery.add(new QueryLine(iCount, sResult));
					iCount = 0;
					sResult = "";
				}
			}

			if(iCount > 0)
			{
				m_aQuery.add(new QueryLine(iCount, sResult));
			}
			
			return true;
		}

		return false;
	}

	public void Add(String sAddress, Indicator pIndicator)
	{
		if(sAddress.equals("-1") || sAddress.length() == 0)
			return;
		
		//Log.d("AddressString", "Added address for indicator: " + sAddress);
		
		if(m_cAddresses.containsKey(sAddress))
		{
			if(!m_cAddresses.get(sAddress).contains(pIndicator))
				m_cAddresses.get(sAddress).add(pIndicator);
		}
		else
		{
			m_cAddresses.put(sAddress, new ArrayList<Indicator>());
			m_cAddresses.get(sAddress).add(pIndicator);
		}
	}
	
	public void Add(String sAddress, Subsystem pSubsystem)
	{
		if(sAddress.equals("-1") || sAddress.length() == 0)
			return;
		
		//Log.d("AddressString", "Added address for room: " + sAddress);
		
		if(m_cAlarms.containsKey(sAddress))
		{
			if(!m_cAlarms.get(sAddress).contains(pSubsystem))
				m_cAlarms.get(sAddress).add(pSubsystem);
		}
		else
		{
			m_cAlarms.put(sAddress, new ArrayList<Subsystem>());
			m_cAlarms.get(sAddress).add(pSubsystem);
		}
	}
	
	public void Clear() 
	{
		m_cAddresses.clear();
		m_cAlarms.clear();
		m_aQuery.clear();
	}
}
