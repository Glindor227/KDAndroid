package ru.cpc.smartflatview;

import android.provider.CalendarContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class Logger
{
    public static Logger Instance = new Logger();

    public ArrayList<String> m_cDebugLines = new ArrayList<String>();

    public void AddDebugInfo(String message)
    {
        Log.d("LOGGER", message);
        /*
        Calendar now = Calendar.getInstance();
        if(m_cDebugLines != null)
        {
            if(m_cDebugLines.size() > 100)
                m_cDebugLines.clear();

            m_cDebugLines.add(String.format("%d:%d:%d.%d - %s", now.get(Calendar.HOUR_OF_DAY),
                                            now.get(Calendar.MINUTE),
                                            now.get(Calendar.SECOND),
                                            now.get(Calendar.MILLISECOND),
                                            message));
        }
        */
    }
}
