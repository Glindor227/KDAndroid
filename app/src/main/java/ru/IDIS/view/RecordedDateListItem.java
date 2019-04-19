package ru.IDIS.view;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.IDIS.util.MetricUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordedDateListItem 
extends 
    RelativeLayout
{
    private static final String TAG = "RecordedDateListItem";

    // Debug Log    
    private static final boolean DEBUG_LOG = false;

    protected static final int TEXT_ID       = 1000;

    // minimum row height of list is 66dip 
    // refer to /frameworks/base/core/res/res/layout-port/icon_menu_layout.xml
    public static final int ROW_HEIGHT = 66;

    // Padding values...
    protected static final int LEFT_PADDING_DIP       = 0;
    protected static final int TOP_PADDING_DIP        = 2; 
    protected static final int RIGHT_PADDING_DIP      = 0;
    protected static final int BOTTOM_PADDING_DIP     = 2;

    // Margin values...
    protected static final int LEFT_MARGIN_DIP          = 16;
    protected static final int TOP_MARGIN_DIP           = 6;
    protected static final int RIGHT_MARGIN_DIP         = 0;
    protected static final int BOTTOM_MARGIN_DIP        = 0;

    // members...
    protected TextView mText;
    protected Date mDate;

    // ctor
    public RecordedDateListItem(Context context)
    {
        // call super
        //super(context);
        super(context, null, android.R.attr.listPreferredItemHeight);

        // customization
        int minHeight = MetricUtil.getPixelsFromDIP(ROW_HEIGHT);
        this.setMinimumHeight(minHeight);

        // set background resource
        setBackgroundResource(android.R.drawable.list_selector_background);

        // padding
        int lPad = MetricUtil.getPixelsFromDIP(LEFT_PADDING_DIP);
        int tPad = MetricUtil.getPixelsFromDIP(TOP_PADDING_DIP);
        int rPad = MetricUtil.getPixelsFromDIP(RIGHT_PADDING_DIP);
        int bPad = MetricUtil.getPixelsFromDIP(BOTTOM_PADDING_DIP);
        this.setPadding(lPad, tPad, rPad, bPad);

        // mText : Name TextView
        mText = new TextView(context, null, android.R.attr.textAppearanceLarge);
        mText.setId(TEXT_ID);
        RelativeLayout.LayoutParams mText_layout_param 
            = new RelativeLayout.LayoutParams(-1, -2);
        mText.setLayoutParams(mText_layout_param);
        //mText.setFocusable(false);
        //mText.setFocusableInTouchMode(false);
        //mText.setClickable(true);        
        
        // margin setting
        int lMargin = MetricUtil.getPixelsFromDIP(LEFT_MARGIN_DIP);
        int tMargin = MetricUtil.getPixelsFromDIP(TOP_MARGIN_DIP);
        int rMargin = MetricUtil.getPixelsFromDIP(RIGHT_MARGIN_DIP);
        int bMargin = MetricUtil.getPixelsFromDIP(BOTTOM_MARGIN_DIP);
        mText_layout_param.setMargins(lMargin, tMargin, rMargin, bMargin);
        mText_layout_param.addRule(RelativeLayout.CENTER_VERTICAL);
        this.addView(mText);
    }

    public Date getDate()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "getDate()");
        }

        return mDate;
    }

    public void setDate(Date date)
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "setDate");
        }

        if (date != null) {
            if (date.equals(mDate) == true) {
                // same date, no op.
                return;
            }
            else {
                mDate = date;        
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                mText.setText(sdf.format(mDate));
                if (DEBUG_LOG) {
                    Log.v(TAG, "Date is converted to String: " + mText.getText());
                }
            }
        }
        else {
            mDate = date;
            mText.setText("");
        }
    }
}

