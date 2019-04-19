package ru.cpc.smartflatview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Вик on 018. 18.01.16.
 */
public class SFServer
{
    public static SFServer Instance = null;

    public AddressString m_pRoomQuery = new AddressString();

    private ArrayList<String> m_aSendQueue = new ArrayList<String>();
    
    private String m_sLastSendPacket = "";

    private MainActivity m_pMainActivity;

    public SFServer(MainActivity context)
    {
        m_pMainActivity = context;
        m_pPollThread = new RoomViewPollThread(this);
        m_pWorkThread = new SFServerWorkThread(this);
    }

    public MainActivity getMainActivity()
    {
        return m_pMainActivity;
    }

    public void SendCommand(String sAddr, String sValue)
    {
        if(Config.DEMO)
            return;

        Log.i("SFServer", "S: SendCommand: " + sAddr + " := " + sValue);
        //MainActivity.m_cLogLines.add("S: SendCommand: " + sAddr + " := " + sValue);

        if(sAddr.equals("-1") || sAddr.length() == 0)
        {
            Log.d("SFServer", "wrong address");
//            MainActivity.m_cLogLines.add("Ошибка WrongAddress: не удаётся отправить команду. Свяжитесь с разработчиками!");
            return;
        }

        String sCommand = "<3|1|" + Prefs.getSecurityCode(m_pMainActivity) + "|" + sAddr + "|0|0|0|" + sValue + ">\r\n";

        Log.d("SFServer", "C: Sending: '" + sCommand + "'");
        //MainActivity.m_cLogLines.add("C: Sending: '" + sCommand + "'");

//            PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(client.getOutputStream())),true);
//            out.println(sCommand);
        m_aSendQueue.add(sCommand);

//        try
//        {
//            Thread.sleep(500);
//        }
//        catch (InterruptedException e)
//        {
//            Log.e("TCP", "Sleep Error", e);
//            e.printStackTrace();
//        }
    }

    private boolean waitingAnswer = false;      
    private boolean waitingRE = false;
    private boolean errorMessage = false;
    private int errorCounter = 0;
    private int maxErrorCounter = 2;
    
    private boolean pollingTime = false;
    private boolean pingTime = false;

    private boolean attach = false;

    public static boolean IsConnected()
    {
        return Config.DEMO || (Instance != null && Instance.client != null && Instance.attach);
    }

    public int waitingCounter = 0;

    public void WorkStep()
    {                    
        if(Config.DEMO)
        {
            m_pMainActivity.m_cLogLines.clear();

            m_pMainActivity.m_cLogLines.add(m_pMainActivity.getString(R.string.logWarningDemo));
            UpdateConnectionStatus();
            return;
        }

        Connect();

        if(client == null)
            Log.e("SFServer", "WorkStep() - not connected!");
        else
        {
            if (waitingAnswer || waitingRE)
            {
                if (waitingCounter++ < 40) //50*40 = 2000мс.
                {
                    return;
                }

                if (!errorMessage)
                {
                    Log.e("SFServer", "No answer for 2000ms! errorCounter := " + errorCounter);

                    if (errorCounter > maxErrorCounter)
                    {
                        errorMessage = true;
                        //Log.d("TCP", "Poll1 : waitingAnswer = " + String.valueOf(waitingAnswer));
                        m_pMainActivity.m_cLogLines.add(m_pMainActivity.getString(R.string.logWarningNoAnswer));
                        Disconnect();
                        UpdateConnectionStatus();
                        m_pRoomQuery.ResetQuery();
                        pollingTime = !m_pRoomQuery.IsOver();
                    }
                    else
                    {
                        errorCounter++;
                        try
                        {
                            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                            //PrintWriter in = new PrintWriter( new BufferedWriter(new InputStreamWriter(socket.getInputStream()));

                            out.println(m_sLastSendPacket);
                            Log.d("SFServer", "C: Last packet resent.");
                            waitingCounter = 0;

                            if(m_sLastSendPacket == "<1|0>\r\n" && pingTime)
                            {
                                pingTime = false;
                            }
                        }
                        catch (Exception e)
                        {
                            Log.e("SFServer", "Last packet resent Error", e);
                            //connectSocket();
                        }
                    }
                }
                else
                {
                    errorCounter = 0;
                    errorMessage = false;
                }
            }
            else if (m_aSendQueue.size() > 0)
            {
                m_sLastSendPacket = m_aSendQueue.get(0);
                try
                {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                    //PrintWriter in = new PrintWriter( new BufferedWriter(new InputStreamWriter(socket.getInputStream()));

                    out.println(m_sLastSendPacket);
                    Log.d("SFServer", "C: Command sent.");
                    waitingRE = true;
                    waitingCounter = 0;
                    errorCounter = 0;
                }
                catch (Exception e)
                {
                    Log.e("SFServer", "Command send Error", e);
                    //connectSocket();
                }
            }
            else if (pollingTime)
            {
                Log.d("SFServer", "WorkStep pollingTime = " + pollingTime);
                pollingTime = !m_pRoomQuery.IsOver();
                Log.d("SFServer", "WorkStep pollingTime = " + pollingTime);
                if (pollingTime)
                {
                    m_sLastSendPacket = m_pRoomQuery.GetNext(m_pMainActivity);
                    Log.d("SFServer", "C: Sending: '" + m_sLastSendPacket + "'");
                    //m_pMainActivity.m_cLogLines.add("C: Sending: '" + sPacket + "'");
                    try
                    {
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                        //PrintWriter in = new PrintWriter( new BufferedWriter(new InputStreamWriter(socket.getInputStream()));

                        out.println(m_sLastSendPacket);
                        Log.d("SFServer", "C: Sent.");
                        waitingAnswer = true;
                        waitingCounter = 0;
                        errorCounter = 0;

                        if(!attach)
                        {
                            m_pMainActivity.m_cLogLines.add(m_pMainActivity.getString(R.string.logRequestSend));
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e("SFServer", "Send Error", e);
                        //connectSocket();
                    }
                }
            }
            else if (pingTime)
            {
                pingTime = false;
                m_sLastSendPacket = "<1|0>\r\n";
                try
                {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);

                    out.println(m_sLastSendPacket);
                    Log.d("SFServer", "C: PING Sent.");
                    waitingRE = true;
                    waitingCounter = 0;
                    errorCounter = 0;
                }
                catch (Exception e)
                {
                    Log.e("SFServer", "PING Send Error", e);
                    //connectSocket();
                }
            }
        }

        if(m_pMainActivity.m_cLogLines.size() == 0)
            m_pMainActivity.m_cLogLines.add(m_pMainActivity.getString(R.string.logNoErrors));

        UpdateConnectionStatus();
    }

    public void Poll(boolean bFast)
    {
        //if(client != null)
        {
            if(bFast)
            {
                pingTime = true;
            }
            else
            {
                m_pRoomQuery.ResetQuery();
                pollingTime = true;
                Log.d("SFServer", "C: Poll full");
            }
        }
    }

    public void Parse(String sPacket, int length)
    {
        Log.i("SFServer", "RCV(" + String.valueOf(length) + "): '" + sPacket + "'");

        if(sPacket.length() < length)
        {
            Log.e("SFServer", "Wrong packet length!");
            length = sPacket.length();
        }

//        String hex = "";
//        if(length>0)
//            hex = String.valueOf((int)sPacket.charAt(0));
//        for(int i=1; i<length; i++)
//            hex = hex + " ! " + String.valueOf((int)sPacket.charAt(i));
//
//        Log.i("TCP", "DMP(" + String.valueOf(length) + "): '" + hex + "'");

        String[] sPackets = sPacket.substring(0,length).split("[<>]");

        for(String sPct: sPackets)
        {
            while(sPct.startsWith("null"))
            {
                sPct = sPct.substring(4);
            }
            if(sPct.startsWith("RE"))
            {
                Log.d("SFServer", "C: Got 'RE'");
                if(waitingRE)
                {
                    if(m_aSendQueue.size() > 0 && m_sLastSendPacket.equals(m_aSendQueue.get(0)))
                    {
                        m_aSendQueue.remove(0);
                    }
                }
                waitingRE = false;
                errorCounter = 0;
            }
            else if(sPct.length() > 0)
            {
                String[] sTokens = sPct.split("[|]");

                if(sTokens.length > 1)
                {
                    //Log.i("TCP", "-----------------");

                    try
                    {
                        if(Integer.parseInt(sTokens[0]) == 1)
                        {
                            try
                            {
                                if(client != null)
                                {
                                    Log.d("SFServer", "C: Sending: 'RE'");
                                    //MainActivity.m_cLogLines.add("C: Sending: '" + sCommand + "'");
                                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
                                    //PrintWriter in = new PrintWriter( new BufferedWriter(new InputStreamWriter(socket.getInputStream()));

                                    out.println("RE");
                                    Log.d("SFServer", "C: Sent.");
                                }
                            }
                            catch(Exception e)
                            {
                                Log.e("SFServer", "S: Error", e);
//            MainActivity.m_cLogLines.add("Ошибка: не удаётся отправить команду. Свяжитесь с разработчиками!");
                                //connectSocket();
                            }
                        }
                        else if(Integer.parseInt(sTokens[0]) == 3)
                        {
                            int iCount = Integer.parseInt(sTokens[1]);

                            if(sTokens.length < 3 + iCount*5)
                                Log.w("SFServer", "wrong packet!");

                            for(int i=0; i<iCount; i++)
                            {
                                boolean addressProcessed = false;

                                if (m_pRoomQuery.m_cAddresses.containsKey(sTokens[3 + i * 5]))
                                {
                                    for (Indicator pInd : m_pRoomQuery.m_cAddresses.get(sTokens[3 + i * 5]))
                                    {
                                        pInd.Process(sTokens[3 + i * 5], sTokens[7 + i * 5]);
                                        addressProcessed = true;
                                    }
                                }

                                if(m_pRoomQuery.m_cAlarms.containsKey(sTokens[3+i*5]))
                                    for (Subsystem pSubsystem : m_pRoomQuery.m_cAlarms.get(sTokens[3 + i * 5]))
                                        pSubsystem.Process(sTokens[3 + i * 5], sTokens[7 + i * 5]);

                                if(!addressProcessed)
                                {
                                    for (Subsystem subSystem : Config.Instance.m_cSubsystems)
                                    {
                                        for (Indicator pInd : subSystem.m_cIndicators)
                                        {
                                            if(pInd.Process(sTokens[3 + i * 5], sTokens[7 + i * 5]))
                                            {
                                                addressProcessed = true;
                                                break;
                                            }
                                        }

                                        if(addressProcessed)
                                        {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        else if(Integer.parseInt(sTokens[0]) == 6 || Integer.parseInt(sTokens[0]) == 2)
                        {
                            if(!attach)
                            {
                                m_pMainActivity.m_cLogLines.add(m_pMainActivity.getString(R.string.logDataReceived));
                                attach = true;
                                Log.d("SFServer", "Parse : attach := " + String.valueOf(attach));
                            }
                            waitingAnswer = false;
                            Log.d("SFServer", "Parse : waitingAnswer := " + String.valueOf(waitingAnswer));

                            int iCount = Integer.parseInt(sTokens[1]);

                            if(sTokens.length < 3 + iCount*2)
                                Log.w("SFServer", "wrong packet!");

                            for(int i=0; i<iCount; i++)
                            {
                                boolean addressProcessed = false;
                                if(m_pRoomQuery.m_cAddresses.containsKey(sTokens[3+i*2]))
                                {
                                    for (Indicator pInd : m_pRoomQuery.m_cAddresses.get(
                                            sTokens[3 + i * 2]))
                                    {
                                        pInd.Process(sTokens[3 + i * 2], sTokens[4 + i * 2]);
                                        addressProcessed = true;
                                    }
                                }

                                if(m_pRoomQuery.m_cAlarms.containsKey(sTokens[3+i*2]))
                                    for (Subsystem pSubsystem : m_pRoomQuery.m_cAlarms.get(sTokens[3 + i * 2]))
                                        pSubsystem.Process(sTokens[3 + i * 2], sTokens[4 + i * 2]);

                                if(!addressProcessed)
                                {
                                    for (Subsystem subSystem : Config.Instance.m_cSubsystems)
                                    {
                                        for (Indicator pInd : subSystem.m_cIndicators)
                                        {
                                            if(pInd.Process(sTokens[3 + i * 2], sTokens[4 + i * 2]))
                                            {
                                                addressProcessed = true;
                                                break;
                                            }
                                        }

                                        if(addressProcessed)
                                            break;
                                    }
                                }
                            }
                            errorCounter = 0;
                        }
                        else
                        {
                            Log.w("SFServer", "wrong packet type (" + sTokens[0] + ")!");
                        }
                    }
                    catch(Exception ex)
                    {
                        Log.w("SFServer", "parsing exception: " + ex.getMessage());
                    }
                }
            }
        }
    }

    private Socket client=null;

    public static int lookupHost(String hostname)
    {
        InetAddress inetAddress;
        try
        {
            inetAddress = InetAddress.getByName(hostname);
        }
        catch (UnknownHostException e)
        {
            return -1;
        }

        byte[] addrBytes;
        int addr;
        addrBytes = inetAddress.getAddress();
        addr = ((addrBytes[3] & 0xff) << 24)
                | ((addrBytes[2] & 0xff) << 16)
                | ((addrBytes[1] & 0xff) << 8)
                |  (addrBytes[0] & 0xff);
        return addr;
    }

    boolean isHostReachable(String address)
    {
		/*
	    ConnectivityManager conMgr = (ConnectivityManager) this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if(!netInfo.isAvailable() || !netInfo.isConnected())
		{
            Log.d("TCP", "WiFi not connected");
			return false;
		}

		int iIp = lookupHost(address);
		if(iIp == -1)
		{
            Log.d("TCP", address + " not recognized");
			return false;
		}
		if(!conMgr.requestRouteToHost(ConnectivityManager.TYPE_WIFI, iIp))
		{
            Log.d("TCP", address + " not reachable");
			return false;
		}
	    */
        return true;
    }

    public void Imitate() {
        m_pMainActivity.runOnUiThread(new Runnable() {
            public void run() {
                //m_pRoomView.Poll();
                m_pMainActivity.Imitate();
            }
        });
    }

    class ConnectSocket extends AsyncTask<Void, Void, Socket>
    {
        @Nullable
        private Socket TryConnect(String ip, int port, int timeout, int trying)
        {
            Log.d("SFServer", "C: Connecting to " + ip + ":" + port);
            m_pMainActivity.m_cLogLines.add(String.format(m_pMainActivity.getString(R.string.logConnectionTry), ip, port, trying));

            Socket sock = new Socket();

            try
            {
                //MainActivity.m_cLogLines.add("+++");
                InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName(ip), port);
                //MainActivity.m_cLogLines.add("***");
                sock.connect(addr, timeout);
                //MainActivity.m_cLogLines.add("!!!");
                m_pMainActivity.m_cLogLines.add(m_pMainActivity.getString(R.string.logConnectionOK));
                Log.d("SFServer", "ConnectSocket.doInBackground success!");
                return sock;
            }
            catch (IllegalArgumentException e)
            {
                Log.d("SFServer", "C: IllegalArgumentException", e);
                //MainActivity.m_cLogLines.add("EX - " + e.getMessage());
                e.printStackTrace();
                //return null;
            }
            catch (UnknownHostException e)
            {
                Log.d("SFServer", "C: UnknownHostException", e);
                //MainActivity.m_cLogLines.add("EX - " + e.getMessage());
                //e.printStackTrace();
                //return null;
            }
            catch (SocketTimeoutException e)
            {
                //this.ed.setText("Android"+e);
                Log.d("SFServer", "C: SocketTimeoutException");//, e);
                //MainActivity.m_cLogLines.add("EX - " + e.getMessage());
                //e.printStackTrace();
                //return null;
            }
            catch (IOException e)
            {
                //this.ed.setText("Android"+e);
                Log.d("SFServer", "C: IOException");//, e);
                //MainActivity.m_cLogLines.add("EX - " + e.getMessage());
                //e.printStackTrace();
                //return null;
            } catch (Exception e)
            {
                //MainActivity.m_cLogLines.add("EX - " + e.getMessage());
                e.printStackTrace();
                //return null;
            }

            return null;
        }

        protected Socket doInBackground(Void... arg0)
        {
            Log.d("SFServer", "ConnectSocket::doInBackground()...");
            for (int i = 0; i < 2; i++)
            {
                client = TryConnect(Config.Instance.m_sIP, Config.Instance.m_iPort, 1000, i+1);
                if (client != null)
                    return client;
            }

            m_pMainActivity.m_cLogLines.add(String.format(m_pMainActivity.getString(R.string.logNoAccess), Config.Instance.m_sIP, Config.Instance.m_iPort));

            String sIP = Prefs.getExternalIP(m_pMainActivity);
            if(!sIP.isEmpty())
            {
                for (int i = 0; i < 2; i++)
                {
                    client = TryConnect(sIP, Config.Instance.m_iPort, 5000, i+1);
                    if(client != null)
                        return client;
                }
            }

            m_pMainActivity.m_cLogLines.add(String.format(m_pMainActivity.getString(R.string.logNoAccess), sIP, Config.Instance.m_iPort));
            return null;
        }

        protected void onPostExecute(Socket sct)
        {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    private GetPacket getPacketTask = null;

    @NonNull
    private Boolean Connect2()
    {
        try
        {
            //this.ed.setText("Connecting");

            Log.d("SFServer", "calling ConnectSocket()...");
            //client = new Socket(serverAddr, iPort);
            client = new ConnectSocket().execute().get();

            if(client != null)
            {
                SocketData data = new SocketData();
                data.sock = client;
                data.owner = this; // заменить на нужный TextView
                Log.d("SFServer", "calling GetPacket().execute()...");
                getPacketTask = new GetPacket();
                getPacketTask.execute(data);
                Log.d("SFServer", "Connect2 success!");
//                MainActivity.m_cLogLines.add("Успешно!");
                return true;
            }
            else
            {
                Log.e("SFServer", "Connect2 failure! socket := NULL");
                //MainActivity.m_cLogLines.add("Не успешно!");
                return false;
            }
        }
        catch (InterruptedException e)
        {
            Log.d("SFServer", "C: InterruptedException", e);
            //MainActivity.m_cLogLines.add("Ошибка Interrupted: не удаётся открыть соединение. Свяжитесь с разработчиками!");
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            Log.d("SFServer", "C: ExecutionException", e);
            //MainActivity.m_cLogLines.add("Ошибка Execution: не удаётся открыть соединение. Свяжитесь с разработчиками!");
            e.printStackTrace();

            Log.d("SFServer", "C: InnerException", e.getCause());
            e.getCause().printStackTrace();
        }

        return false;
    }

    private RoomViewPollThread m_pPollThread;

    private SFServerWorkThread m_pWorkThread;

    public void Resume()
    {
        Log.d("SFServer", "C: Resume");
        try
        {
            if (m_pWorkThread.getState() == Thread.State.TERMINATED)
            {
                m_pWorkThread = new SFServerWorkThread(this);
                m_pWorkThread.setRunning(true);
                m_pWorkThread.start();
            }
            else
            {
                m_pWorkThread.setRunning(true);
                m_pWorkThread.start();
            }
        }
        catch(Exception e)
        {
            Log.e("SFServer", "WorkThread exception:", e);
        }
        try
        {
            if (m_pPollThread.getState() == Thread.State.TERMINATED)
            {
                m_pPollThread = new RoomViewPollThread(this);
                m_pPollThread.setRunning(true);
                m_pPollThread.start();
            }
            else
            {
                m_pPollThread.setRunning(true);
                m_pPollThread.start();
            }
        }
        catch(Exception e)
        {
            Log.e("SFServer", "PollThread exception:", e);
        }
    }

    public void Stop()
    {
        Log.d("SFServer", "C: Stop");
        // simply copied from sample application LunarLander:
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;

        retry = true;
        m_pPollThread.setRunning(false);
        while (retry)
        {
            try
            {
                m_pPollThread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
                // we will try it again and again...
            }
        }

        retry = true;
        m_pWorkThread.setRunning(false);
        while (retry)
        {
            try
            {
                m_pWorkThread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
                // we will try it again and again...
            }
        }

        Disconnect();
    }

    public void Disconnect()
    {
        Log.d("SFServer", "C: Disconnect");
        try
        {
            if(client != null)
            {
                if(client.isConnected())
//                    return;
                client.close();
            }
        }
        catch (UnknownHostException e)
        {
            Log.d("SFServer", "C: UnknownHostException", e);
            //MainActivity.m_cLogLines.add("Ошибка UnknownHost: не удаётся закрыть соединение. Свяжитесь с разработчиками!");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.d("SFServer", "C: IOException", e);
            //MainActivity.m_cLogLines.add("Ошибка IO: не удаётся закрыть соединение. Свяжитесь с разработчиками!");
            e.printStackTrace();
        }

        Log.d("SFServer", "C: Disconnected!");
        if(getPacketTask != null)
        {
            getPacketTask.cancel(true);
        }
        client = null;

        waitingAnswer = false;
        waitingRE = false;
        errorCounter = 0;
        attach = false;
        Log.d("SFServer", "Disconnect : attach := " + String.valueOf(attach));
    }

    public void Connect()
    {
        //try
        //{
            if(client != null)
            {
                if(client.isConnected())
                    return;
                    //client.close();
            }
        /*
        }
        catch (UnknownHostException e)
        {
            Log.d("TCP", "C: UnknownHostException", e);
            //MainActivity.m_cLogLines.add("Ошибка UnknownHost: не удаётся закрыть соединение. Свяжитесь с разработчиками!");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.d("TCP", "C: IOException", e);
            //MainActivity.m_cLogLines.add("Ошибка IO: не удаётся закрыть соединение. Свяжитесь с разработчиками!");
            e.printStackTrace();
        }
*/
        Log.d("SFServer", "C: Connect");

        m_pMainActivity.m_cLogLines.clear();
        errorMessage = false;
        attach = false;

        client = null;

        try
        {
            ConnectivityManager conMgr = (ConnectivityManager) m_pMainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = conMgr.getActiveNetworkInfo();

            if (activeNetworkInfo == null || activeNetworkInfo.getType() != ConnectivityManager.TYPE_WIFI) {
                Log.d("SFServer", "WiFi network not connected");
                m_pMainActivity.m_cLogLines.add(m_pMainActivity.getString(R.string.logNoWiFi));

                if (Prefs.getMobile(m_pMainActivity)) {
                    m_pMainActivity.m_cLogLines.add(m_pMainActivity.getString(R.string.logUseMobile));

                    if (activeNetworkInfo == null || activeNetworkInfo.getType() != ConnectivityManager.TYPE_MOBILE) {
                        Log.d("SFServer", "Mobile network not connected");
                        m_pMainActivity.m_cLogLines.add(m_pMainActivity.getString(R.string.logNoMobile));
                        UpdateConnectionStatus();
                        return;
                    }
                } else {
                    Log.d("SFServer", "Mobile network not allowed");
                    m_pMainActivity.m_cLogLines.add(m_pMainActivity.getString(R.string.logMobileNotAllowed));
                    UpdateConnectionStatus();
                    return;
                }
            }
        }
        catch(Exception ex)
        {
            Log.e("SFServer", "Checking connectivity exception: ", ex);
        }

        try
        {
            Log.d("SFServer", "calling Connect2()");
            Connect2();
        }
        catch(Exception e)
        {
            client = null;
            Log.e("SFServer", "Connect exception ", e);
            m_pMainActivity.m_cLogLines.add(e.getMessage());
            UpdateConnectionStatus();
            return;
        }

        if(client != null)
            Log.d("SFServer", "Connect success!");

        if(client != null) {
            UpdateConnectionStatus();
            return;
        }

        m_pMainActivity.m_cLogLines.add(m_pMainActivity.getString(R.string.error_ip_message));
        UpdateConnectionStatus();
    }

    private void UpdateConnectionStatus()
    {
        //if(IsConnected())
        //    Log.d("TCP", "Connection status = " + IsConnected());
        //else
        //    Log.e("TCP", "Connection status = " + IsConnected());

        m_pMainActivity.runOnUiThread(new Runnable() {
            public void run() {
                //m_pRoomView.Poll();
                m_pMainActivity.UpdateConnectionStatus();
            }
        });
    }
}

class SocketData
{
    Socket sock;
    SFServer owner;
}

class GetPacket extends AsyncTask<SocketData, Integer, Integer>
{
    SFServer pOwner;
    char[] mData;
    int read, read2;
    String data;

    protected void onProgressUpdate(Integer... progress)
    {
//        String hex = "";
//        if(read>0)
//            hex = String.valueOf((int)mData[0]);
//        for(int i=1; i<read; i++)
//            hex = hex + " ! " + String.valueOf((int)mData[i]);
//
//        Log.i("TCP", "DMP1(" + String.valueOf(read) + "): '" + hex + "'");

        read2 = data.lastIndexOf('>') + 1;

        int read3 = data.lastIndexOf("RE") + 2;
        if(read3 > 1 && read3 > read2)
            read2 = read3;

        //Log.i("TCP", "Got update: " + read2 + " (" + read + ")");
        if(read2 != 0)
        {
            pOwner.Parse(data, read2);//read);

            data = data.substring(read2, data.length()).trim();
        }
    }

    protected void onPostExecute(Integer result)
    {
        // Это выполнится после завершения работы потока
    }

    protected Integer doInBackground(SocketData... param)
    {
        Socket mySock = param[0].sock;

        if(mySock == null)
            return -1;

        pOwner = param[0].owner;

        mData = new char[4096];

        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(mySock.getInputStream()));
            read = 0;

            //while ((read = reader.read(mData)) >= 0 && !isCancelled())
            while (!isCancelled())
            {
                for(int i=0; i<4096; i++)
                    mData[i] = 0;
                read = reader.read(mData, 0, 4096);

                // "Вызываем" onProgressUpdate
                if(read > 0)
                {
                    data += String.valueOf(mData).substring(0, read).trim();
                    publishProgress(read);
                }
                else
                {
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        Log.e("SFServer", "Sleep Error", e);
                        //e.printStackTrace();
                    }
                }
            }

            reader.close();
        }
        catch (IOException e)
        {
            return -1;
        }
        return 0;
    }
}

class SFServerWorkThread extends Thread
{
    private SFServer m_pServer;
    private boolean m_bRun = false;

    public SFServerWorkThread(SFServer pServer)
    {
        m_pServer = pServer;
    }

    public void setRunning(boolean run)
    {
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
                m_pServer.WorkStep();
                super.sleep(50);
            }
            catch (InterruptedException e)
            {
                Log.v("Glindor","InterruptedException");
                Log.v("Glindor",e.getMessage());
                //e.printStackTrace();
            }
            finally
            {
            }
        }
    }
}
