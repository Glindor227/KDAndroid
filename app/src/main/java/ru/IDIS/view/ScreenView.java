package ru.IDIS.view;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.RelativeLayout;

import java.nio.ByteBuffer;

import ru.cpc.smartflatview.MainActivity;

public class ScreenView
extends 
    RelativeLayout 
{
    private static final String TAG = "ScreenView";

    // DEBUG_LOG
    private static final boolean DEBUG_LOG = false;

    private static boolean mCanUseGL = MainActivity.canUseGL();

    public int SCREEN_VIEW_IMPL_ID       = 1234;

    private GLScreenView mGLScreenView = null;
    private BitmapScreenView mBitmapScreenView = null;
    
    GestureDetector mGestureDetector;

    private ScreenView(Context context)
    {
        super(context);
    }
    
    // ctor
    public ScreenView(Context context, int cacheKey)
    {
        super(context);

        // debug log
        if (DEBUG_LOG) {
            Log.v(TAG, "ScreenView() ctor");
        }

        if (mCanUseGL) {
            if (DEBUG_LOG) {
                Log.v(TAG, "ScreenView uses OpenGL ES system");
            }

            // mGLScreenView
            mGLScreenView = new GLScreenView(context);
            mGLScreenView.setId(SCREEN_VIEW_IMPL_ID);
            mGLScreenView.setVisibility(View.VISIBLE); // explicit setting
            RelativeLayout.LayoutParams screenView_param 
                = new RelativeLayout.LayoutParams(-1, -1);
            mGLScreenView.setLayoutParams(screenView_param);
            screenView_param.addRule(RelativeLayout.CENTER_VERTICAL);
            screenView_param.addRule(RelativeLayout.CENTER_HORIZONTAL);
            // Cautious!!!!
            // If you fill the background of screenview with setBackgroundColor method,
            // you can not see the image drawn by OpenGL ES
            //screenView.setBackgroundColor(0xff0000ff);  // set to red color for test
            this.addView(mGLScreenView);
        }
        else {  // using BitmapScreenView
            if (DEBUG_LOG) {
                Log.v(TAG, "ScreenView uses Bitmap system");
            }

            // mBitmapScreenView
            mBitmapScreenView = new BitmapScreenView(context, cacheKey);
            mBitmapScreenView.setId(SCREEN_VIEW_IMPL_ID);
            mBitmapScreenView.setVisibility(View.VISIBLE); // explicit setting
            //mBitmapScreenView.setAdjustViewBounds(true);            
            RelativeLayout.LayoutParams screenView_param 
                = new RelativeLayout.LayoutParams(-1, -1);
            mBitmapScreenView.setLayoutParams(screenView_param);
            screenView_param.addRule(RelativeLayout.CENTER_VERTICAL);
            screenView_param.addRule(RelativeLayout.CENTER_HORIZONTAL);
            this.addView(mBitmapScreenView);
        }
    }

    public void setSize(int newWidth, int newHeight)
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "Set size to (" + newWidth + ", " + newHeight + ")");
        }

        if (mCanUseGL) {
            mGLScreenView.setSize(newWidth, newHeight);
        }
        else {
            mBitmapScreenView.setSize(newWidth, newHeight);
        }
    }

    public void reset()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "reset()");
        }

        if (mCanUseGL == true) {
            mGLScreenView.reset();
        }
        else {
            mBitmapScreenView.reset();
        }
    }

    public void drawImage(byte[] byteArray, int width, int height)
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "drawImage()");
        }

        if (mCanUseGL) {
            ByteBuffer image_byte_buffer = ByteBuffer.wrap(byteArray);
            image_byte_buffer.position(0);
            
            mGLScreenView.drawImage(image_byte_buffer);
        }
        else {  // using bitmap drawing
            mBitmapScreenView.drawImage(byteArray, width, height);
        }
    }
}

