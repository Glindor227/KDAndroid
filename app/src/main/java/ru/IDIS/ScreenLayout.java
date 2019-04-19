package ru.IDIS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.IDIS.util.MetricUtil;
import ru.IDIS.view.ScreenView;
import ru.cpc.smartflatview.R;
import ru.cpc.smartflatview.VerticalTextView;

@SuppressLint("DefaultLocale")
public class ScreenLayout 
extends 
    RelativeLayout 
{    
    private static final String TAG = "ScreenLayout";
    
    // DEBUG_LOG
    private static final boolean DEBUG_LOG = false;

    private ScreenView mScreenView;
//    private VerticalTextView mDateTimeTextView;
    
    // image size
    private int mImageWidth = 0;
    private int mImageHeight = 0;
    
    public ScreenLayout(Context context) {
        super(context);

        // children...
        if (DEBUG_LOG) {
            Log.v(TAG, "ScreenLayout() ctor");
        }

        // layout...
        this.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.setBackgroundColor(getResources().getColor(R.color.playlistBackground));

        // mScreenView
        mScreenView = new ScreenView(context, 0);
        mScreenView.setId(2000);        
        mScreenView.setVisibility(View.VISIBLE);    // explicit setting
        RelativeLayout.LayoutParams screenView_param = new RelativeLayout.LayoutParams(-1, -1);
        mScreenView.setLayoutParams(screenView_param);
        screenView_param.addRule(RelativeLayout.CENTER_VERTICAL);
        screenView_param.addRule(RelativeLayout.CENTER_HORIZONTAL);
        this.addView(mScreenView);
        
        //mDateTimeTextView = new VerticalTextView(context);
        //mDateTimeTextView.setId(4000);
        //mDateTimeTextView.setGravity(Gravity.FILL_VERTICAL);
        //mDateTimeTextView.setGravity(Gravity.CENTER);
        //mDateTimeTextView.setDirection(VerticalTextView.ORIENTATION_UP_TO_DOWN);
        //RelativeLayout.LayoutParams textView_param = new RelativeLayout.LayoutParams(-2, -1);
        //textView_param.addRule(RelativeLayout.CENTER_VERTICAL);
        //textView_param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        //textView_param.setMargins(-50, 0, 0, 0);
        //mDateTimeTextView.setLayoutParams(textView_param);
        //this.addView(mDateTimeTextView);

        // keep screen turned on
        this.setKeepScreenOn(true);
    }

    public void drawImage(byte[] byteArray, int width, int height)
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "drawImage...");
        }

        // tell image size
        setImageSize(width, height);

        // pass image data to mScreenView
        mScreenView.drawImage(byteArray, width, height);
    }
    
//    public void setDateTime(int YYYY, int MM, int DD, int hh, int mm, int ss) {
//        String datetime = String.format("%04d-%02d-%02d %02d:%02d:%02d", YYYY, MM, DD, hh, mm, ss);
//        mDateTimeTextView.setText(datetime);
//    }

    public void setImageSize(long imageWidth, long imageHeight)
    {
        int resFactor = 1;

        // should check if the image is half-resolution.
        if (imageHeight*2 <= imageWidth) { // e.g. Half-D1
            resFactor = 2;
        }
    
        boolean didWidthChange = (mImageWidth == (int)imageWidth) ? false : true;
        boolean didHeightChange = (mImageHeight == (int)imageHeight*resFactor) ? false : true;

        if (didWidthChange == true) {
            mImageWidth = (int)imageWidth;
        }

        if (didHeightChange == true) {
            mImageHeight = (int)imageHeight * resFactor;
        }

        if (didWidthChange || didHeightChange) {
            // change aspect ratio
            updateScreen();
        }
    }

    private void updateScreen()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "updateScreen");
        }

        int display_width = getWidth();// MetricUtil.getDisplayWidthPixel();
        int display_height = getHeight();// MetricUtil.getDisplayHeightPixel();
        
        //if (DEBUG_LOG)
        {
            Log.v(TAG, "display width = " + display_width);
            Log.v(TAG, "display height = " + display_height);
        }
    
        if (DEBUG_LOG) {
            Log.v(TAG, "Portrait");
        }
        float aspect_ratio_value = (float)mImageHeight / (float)mImageWidth;
        //float aspect_ratio_value = (float)mImageWidth / (float)mImageHeight;
        //int visible_image_height = (int)((float)display_width * aspect_ratio_value);
        int visible_image_height = display_height;
        int visible_image_width = (int)((float)display_height * aspect_ratio_value);
        //VP: чтобы картинка с камеры всегда выводилась горизонтально
        //setScreenViewSize(display_height, visible_image_width);

        if(visible_image_width > display_width)
        {
            visible_image_width = display_width;
            visible_image_height = (int)((float)display_width / aspect_ratio_value);
        }
        setScreenViewSize(visible_image_width, visible_image_height);

        // reload layout
        requestLayout();
    }
    
    private void setScreenViewSize(int viewWidth, int viewHeight)
    {
        //if (DEBUG_LOG)
        {
            Log.v(TAG, "Setting size of mScreenView to (" + viewWidth + ", " + viewHeight + ")");
        }
        
        mScreenView.setSize(viewWidth, viewHeight);
    }
    
    public void reset()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "reset()");
        }

        //mScreenView.reset();
    }
}

