package ru.IDIS.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;

import ru.IDIS.ScreenRenderer;

import java.nio.ByteBuffer;

public class GLScreenView 
extends 
    GLSurfaceView 
{
    private static final String TAG = "ScreenView";

    private static final boolean DEBUG_LOG = false;

    ScreenRenderer mRenderer;

    GestureDetector mGestureDetector;

    // dirty fix for controlling aspect ratio
    private int mScreenViewWidth = 0;
    private int mScreenViewHeight = 0;

    public void setSize(int viewWidth, int viewHeight)
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "new width = " + viewWidth);
            Log.v(TAG, "new height = " + viewHeight);
        }
    
        mScreenViewWidth = viewWidth;
        mScreenViewHeight = viewHeight;
    }

    public GLScreenView(Context context) 
    {
        super(context);

        if (DEBUG_LOG) {
            Log.v(TAG, "ScreenView() CTOR!!!!!");
        }

        // trick for speedup
        mRenderer = new ScreenRenderer();
        //mRenderer = MainApp.getScreenRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);        
        //setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public synchronized void reset()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "reset()");
        }

        mRenderer.reset();
    }

    public synchronized void drawImage(ByteBuffer bb)
    {
        final boolean RUNNABLE_DEBUG_LOG = DEBUG_LOG;

        final ByteBuffer byte_buffer = bb;

        queueEvent(new Runnable() 
        {
            public void run() 
            {
                if (RUNNABLE_DEBUG_LOG) {
                    Log.v(TAG, "drawImage()");
                }
                
                mRenderer.drawImage(byte_buffer);
                requestRender();
            }
        });
    }
    
	@Override
	public void onPause()
	{
		mRenderer.onPause();

        // call super    
		super.onPause();
	}
    
    @Override
    public void onResume()
    {
    	mRenderer.onResume();

        // call super
    	super.onResume();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (mScreenViewWidth == 0 || mScreenViewHeight == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        else {
            setMeasuredDimension(mScreenViewWidth, mScreenViewHeight);
        }
    }
}

