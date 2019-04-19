package ru.cpc.smartflatview;

import android.util.Log;

public class RoomViewPollThread extends Thread
{
    private SFServer m_pServer;
    private boolean m_bRun = false;
    private int pollCounter = 0;
    
    public RoomViewPollThread(SFServer pRoomView)
    {
        m_pServer = pRoomView;
    }
 
    public void setRunning(boolean run) 
    {
        //Log.d("SFServer", "C: RoomViewPollThread setRunning = " + run);
        if(run)
        {
            m_pServer.Poll(false);
            pollCounter = 1;
        }

        //Log.d("SFServer", "C: RoomViewPollThread pollCounter = " + pollCounter);
        m_bRun = run;

        if(!m_bRun)
            interrupt();
    }
    
    @Override
    public void run() 
    {
        while (m_bRun) 
        {
            try 
            {
            	m_pServer.Poll(pollCounter > 0);
            	m_pServer.Imitate();
//            	if(m_pServer.post(new Runnable()
//					            	{
//										public void run()
//										{
//							            	//m_pServer.Poll();
//							            	m_pServer.Imitate();
//										}
//									}))
            	sleep(Config.Instance.m_iPollPeriod);

                if(pollCounter >= 60)
                {
                    pollCounter = 0;
                }
                else
                {
                    pollCounter++;
                }
                //Log.d("SFServer", "C: RoomViewPollThread pollCounter = " + pollCounter);
            }
            catch (InterruptedException e) 
            {
				Log.v("Glindor","Фигня:"+ e.getMessage());
				//e.printStackTrace();
			} 
            finally 
            {
            }
        }
    }    
}
