package ru.cpc.smartflatview;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Вик on 017. 17.02.16.
 */
public class ScrollingDialog extends DialogFragment implements DialogInterface.OnClickListener
{
    protected static final String TAG = "SMARTFLAT" ;

    private static String _device, _room;
    public static void Init(String device, String room)
    {
        _device = device;
        _room = room;
        s_aSFControls.clear();
    }

    private static int m_iTextStyle = android.R.style.TextAppearance_Small;

    private interface ISFControl
    {
        public void AddView(LinearLayout pContentHolder);
    }

    public boolean m_bAccepted = false;

    private static ArrayList<ISFControl> s_aSFControls = new ArrayList<ISFControl>();

    public static ISFControl AddSwitcher(String sVariable, String sProp, boolean bChecked, CompoundButton.OnCheckedChangeListener listener)
    {
        ISFControl pSwitcher = new SFSwitcher(sProp, bChecked, listener);
        if(!sVariable.equals("-1"))
            s_aSFControls.add(pSwitcher);
        return pSwitcher;
    }

    public static class SFSwitcher implements ISFControl
    {
        public String m_sProp;
        public boolean m_bChecked;
        public CompoundButton.OnCheckedChangeListener m_dListener = null;

        public SFSwitcher(String sProp, boolean bChecked, CompoundButton.OnCheckedChangeListener listener)
        {
            m_sProp = sProp;
            m_bChecked = bChecked;
            m_dListener = listener;
        }

        @Override
        public void AddView(LinearLayout pContentHolder)
        {
            Switch pSwitcher = new Switch(pContentHolder.getContext());
            pSwitcher.setText(m_sProp);
            if (Build.VERSION.SDK_INT < 23)
                pSwitcher.setTextAppearance(pContentHolder.getContext(), m_iTextStyle);
            else
                pSwitcher.setTextAppearance(m_iTextStyle);

            Resources r = pContentHolder.getContext().getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    16,
                    r.getDisplayMetrics()
            );

//            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
//            params.setMarginStart(px);
//            params.setMarginEnd(px);
//            pSwitcher.setLayoutParams(params);
            pSwitcher.setPadding(px, px, px, px);

            pSwitcher.setChecked(m_bChecked);

            pSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                m_bChecked = isChecked;
                if(m_dListener != null)
                    m_dListener.onCheckedChanged(buttonView, isChecked);
            }});

            pContentHolder.addView(pSwitcher);
        }
    }

    public static ISFControl AddSeekBar(String sVariable, String sProp, int iValue, int iMinValue, int iMaxValue, String sPattern, SeekBar.OnSeekBarChangeListener listener)
    {
        ISFControl pSeeker = new SFSeeker(sProp, iValue, iMinValue, iMaxValue, sPattern, listener);
        if(!sVariable.equals("-1"))
            s_aSFControls.add(pSeeker);
        return pSeeker;
    }

    public static class SFSeeker implements ISFControl
    {
        public String m_sProp;
        public int m_iValue;
        public int m_iMinValue;
        public int m_iMaxValue;
        public SeekBar.OnSeekBarChangeListener m_dListener = null;
        public String m_sPattern;

        public SFSeeker(String sProp, int iValue, int iMinValue, int iMaxValue, String sPattern, SeekBar.OnSeekBarChangeListener listener)
        {
            m_sProp = sProp;
            m_iValue = iValue;
            m_iMinValue = iMinValue;
            m_iMaxValue = iMaxValue;
            m_dListener = listener;
            m_sPattern = sPattern;
        }

        @Override
        public void AddView(LinearLayout pContentHolder)
        {
            final TextView pText = new TextView(pContentHolder.getContext());
            pText.setText(m_sProp);
            if (Build.VERSION.SDK_INT < 23)
                pText.setTextAppearance(pContentHolder.getContext(), m_iTextStyle);
            else
                pText.setTextAppearance(m_iTextStyle);

            Resources r = pContentHolder.getContext().getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    16,
                    r.getDisplayMetrics()
            );

//            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
//            params.setMargins(px, px, px, px);
//            pText.setLayoutParams(params);

            pText.setPadding(px, px, px, 0);

            final TextView pText2 = new TextView(pContentHolder.getContext());
            pText2.setText(String.format(m_sPattern, m_iValue));
            if (Build.VERSION.SDK_INT < 23)
                pText2.setTextAppearance(pContentHolder.getContext(), m_iTextStyle);
            else
                pText2.setTextAppearance(m_iTextStyle);

//            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
//            params.setMargins(px, px, px, px);
//            pText.setLayoutParams(params);

            pText2.setPadding(px, 0, px, px);
            pText2.setGravity(Gravity.CENTER);

            final SeekBar pBar = new SeekBar(pContentHolder.getContext());
            pBar.setMax(m_iMaxValue - m_iMinValue);
            pBar.setProgress(m_iValue - m_iMinValue);

            pBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    m_iValue = pBar.getProgress() + m_iMinValue;
                    pText2.setText(String.format(m_sPattern, m_iValue));
                    if(m_dListener != null) m_dListener.onProgressChanged(seekBar, progress, fromUser);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if(m_dListener != null) m_dListener.onStartTrackingTouch(seekBar);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if(m_dListener != null) m_dListener.onStopTrackingTouch(seekBar);
                }
            });
            pBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    m_iValue = pBar.getProgress() + m_iMinValue;
                    pText2.setText(String.format(m_sPattern, m_iValue));
                    return false;
                }
            });
            pBar.setPadding(px, 0, px, 0);

            pContentHolder.addView(pText);
            pContentHolder.addView(pBar);
            pContentHolder.addView(pText2);
        }
    }

    public static ISFControl AddModeSelector(String sVariable, int iMode0, int iMode1, int iMode2, int iMode3, int iSelected, int defColor, int selColor, View.OnClickListener listener)
    {
        ISFControl pSwitcher = new SFModeSelector(iMode0, iMode1, iMode2, iMode3, iSelected, defColor, selColor, listener);
        if(!sVariable.equals("-1"))
            s_aSFControls.add(pSwitcher);
        return pSwitcher;
    }

    public static ISFControl AddModeSelector(String sVariable, int iMode0, int iMode1, int iSelected, int defColor, int selColor, View.OnClickListener listener)
    {
        ISFControl pSwitcher = new SFModeSelector(iMode0, iMode1, iSelected, defColor, selColor, listener);
        if(!sVariable.equals("-1"))
            s_aSFControls.add(pSwitcher);
        return pSwitcher;
    }

    public static class SFModeSelector implements ISFControl
    {
        //public String m_sProp;
        public int m_iSelected;
        public View.OnClickListener m_dListener = null;
        public int[] images;
        private ImageButton[] buttons;
        int modesCount;
        int selectedColorID;
        int defaultColorID;

        public SFModeSelector(int iMode0, int iMode1, int iMode2, int iMode3, int iSelected, int defColor, int selColor, View.OnClickListener listener)
        {
            //m_sProp = sProp;
            m_iSelected = iSelected;
            m_dListener = listener;

            modesCount = 4;
            images = new int[modesCount];
            images[0] = iMode0;
            images[1] = iMode1;
            images[2] = iMode2;
            images[3] = iMode3;

            defaultColorID = defColor;
            selectedColorID = selColor;
        }

        public SFModeSelector(int iMode0, int iMode1, int iSelected, int defColor, int selColor, View.OnClickListener listener)
        {
            //m_sProp = sProp;
            m_iSelected = iSelected;
            m_dListener = listener;

            modesCount = 2;
            images = new int[modesCount];
            images[0] = iMode0;
            images[1] = iMode1;

            defaultColorID = defColor;
            selectedColorID = selColor;
        }

        @Override
        public void AddView(LinearLayout pContentHolder)
        {
            Resources r = pContentHolder.getContext().getResources();
            final int defaultColor = r.getColor(defaultColorID);
            final int selectedColor = r.getColor(selectedColorID);

            TableLayout pSwitcher = new TableLayout(pContentHolder.getContext());
            TableRow NewRow1 = new TableRow(pContentHolder.getContext());
            NewRow1.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.FILL_PARENT,10.0f));

            NewRow1.setPadding(1, 1, 1, 1);

            buttons = new ImageButton[modesCount];
            for(int j=0; j<modesCount; j++)
            {
                buttons[j] = new ImageButton(pContentHolder.getContext());
                buttons[j].setTag(j);
                buttons[j].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.FILL_PARENT, 10.0f));
                buttons[j].setImageResource(images[j]);
                buttons[j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(m_iSelected >= 0 && m_iSelected < buttons.length)
                            buttons[m_iSelected].setBackgroundColor(defaultColor);

                        m_iSelected = (int) v.getTag();
                        buttons[m_iSelected].setBackgroundColor(selectedColor);
                        if(m_dListener != null)
                            m_dListener.onClick(v);
                    }
                });
                buttons[j].setBackgroundColor(defaultColor);
                NewRow1.addView(buttons[j]);
            }
            //tblsudoku.addView(NewRow1, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
            pSwitcher.addView(NewRow1);

            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    16,
                    r.getDisplayMetrics()
            );

//            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
//            params.setMarginStart(px);
//            params.setMarginEnd(px);
//            pSwitcher.setLayoutParams(params);
            pSwitcher.setPadding(px, px, px, px);

            if(m_iSelected >= 0 && m_iSelected < buttons.length)
                buttons[m_iSelected].setBackgroundColor(selectedColor);


            pContentHolder.addView(pSwitcher);
        }
    }

    public static ISFControl AddAcceptBtn(Button.OnClickListener listener)
    {
        ISFControl pBtn = new SFButton(listener);
        s_aSFControls.add(pBtn);
        return pBtn;
    }

    public static class SFButton implements ISFControl
    {
        private Button m_pButton;

        Button.OnClickListener m_dListener = null;

        public void AddListener(final Button.OnClickListener listener)
        {
            m_pButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(m_dListener != null)
                        m_dListener.onClick((v));
                    if(listener != null)
                        listener.onClick(v);
                }
            });
        }

        public SFButton(Button.OnClickListener listener)
        {
            m_dListener = listener;
        }

        @Override
        public void AddView(LinearLayout pContentHolder)
        {
            m_pButton = new Button(pContentHolder.getContext());
            m_pButton.setText("Принять");
            if (Build.VERSION.SDK_INT < 23)
                m_pButton.setTextAppearance(pContentHolder.getContext(), m_iTextStyle);
            else
                m_pButton.setTextAppearance(m_iTextStyle);

            Resources r = pContentHolder.getContext().getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    16,
                    r.getDisplayMetrics()
            );

//            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT);
//            params.setMarginStart(px);
//            params.setMarginEnd(px);
//            pSwitcher.setLayoutParams(params);
            m_pButton.setPadding(px, px, px, px);

            pContentHolder.addView(m_pButton);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(_device);
        //View v = inflater.inflate(R.layout.content_scrolling, null);
        View v = inflater.inflate(R.layout.activity_scrolling, null);
        //Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        //v.setSupportActionBar(toolbar);
        TextView pHeader = (TextView) v.findViewById(R.id.logHeader);
        if(pHeader != null)
            pHeader.setText(_device);

        LinearLayout contentHolder = (LinearLayout) v.findViewById(R.id.base_view_container);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle(_device);
        //getSupportActionBar().setSubtitle(_room);

        contentHolder.removeAllViews();

        for(ISFControl pControl : s_aSFControls) {
            pControl.AddView(contentHolder);
            if(pControl instanceof SFButton)
                ((SFButton)pControl).AddListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        m_bAccepted = true;
                        dismiss();
                    }
                });
        }

        Log.d(TAG, "ScrollingDialog::onCreate() exit");
        return v;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
