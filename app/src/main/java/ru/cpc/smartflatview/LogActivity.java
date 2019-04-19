package ru.cpc.smartflatview;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class LogActivity extends DialogFragment// implements DialogInterface.OnClickListener
{
    static LogActivity newInstance(ArrayList<String> cLogLines, String title)
    {
        LogActivity f = new LogActivity();

        // Supply num input as an argument.
        Bundle b = new Bundle();
        b.putStringArrayList("log", cLogLines);
        b.putString("title", title);
        f.setArguments(b);

        return f;
    }
    ArrayList<String> cLog = null;
    String title = "Состояние связи с оборудованием:";

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
        //setHasOptionsMenu(true);
        cLog = getArguments().getStringArrayList("log");
        title = getArguments().getString("title");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getDialog().setTitle(title);
        View v = inflater.inflate(R.layout.activity_log, null);
        //Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        //v.setSupportActionBar(toolbar);
        //toolbar.setTitle(title);

        TextView pHeader = (TextView) v.findViewById(R.id.logHeader);
        pHeader.setText(title);

        TextView pText = (TextView) v.findViewById(R.id.log_text);
        pText.setText("");

        if (cLog!= null)
        {
            for(String sLine : cLog)
                pText.append(sLine + "\n\r");
        }
        return v;
    }

//    @Override
//    public void onClick(DialogInterface dialog, int which)
//    {
//
//    }
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return new Dialog(getActivity(), getTheme()){
//            @Override
//            public void onBackPressed() {
//                dismiss();
//            }
//        };
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
