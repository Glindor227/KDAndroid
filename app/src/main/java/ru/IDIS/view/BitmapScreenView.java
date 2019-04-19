package ru.IDIS.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.nio.ByteBuffer;

public class BitmapScreenView 
extends 
    VideoBitmapImageView 
{
    private static final String TAG = "BitmapScreenView";

    // Debug Log    
    private static final boolean DEBUG_LOG = false;

    // dirty fix for controlling aspect ratio
    private int mScreenViewWidth = 0;
    private int mScreenViewHeight = 0;
    
    private int _cacheKey = 0;

    private BitmapScreenView(Context context) {
        super(context);
    }
    // ctor
    public BitmapScreenView(Context context, int cacheKey)
    {
        super(context);
        
        if (DEBUG_LOG) {
            Log.v(TAG, "BitmapScreenView() ctor");
        }
        
        _cacheKey = cacheKey;
    }

    public void setSize(int viewWidth, int viewHeight)
    {
        //if (DEBUG_LOG)
        {
            Log.v(TAG, "viewWidth = " + viewWidth);
            Log.v(TAG, "viewHeight = " + viewHeight);
        }

        mScreenViewWidth = viewWidth;
        mScreenViewHeight = viewHeight;
        this.setScaleType(ScaleType.FIT_CENTER);
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        //return Bitmap.createBitmap(source, 0, 0, source.getHeight(), source.getWidth(), matrix, true);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void drawImage(byte[] byteArray, int width, int height)
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "started drawImage()");
        }

        Log.v(TAG, "drawImage " + this.getWidth() + ", " + this.getHeight());

        ByteBuffer image_byte_buffer = ByteBuffer.wrap(byteArray);
        image_byte_buffer.position(0);

        //VP: чтобы картинка с камеры всегда выводилась горизонтально
        BitmapDrawable bd = VideoBitmapPool.getInstance().get(_cacheKey, height, width);
        //BitmapDrawable bd = VideoBitmapPool.getInstance().get(_cacheKey, width, height);

        Bitmap bitmap = bd.getBitmap();
        if (bitmap != null) {
            Bitmap tempBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
            tempBitmap.copyPixelsFromBuffer(image_byte_buffer);
            tempBitmap = RotateBitmap(tempBitmap, 90);
            int [] pixels = new int[width * height];
            tempBitmap.getPixels(pixels, 0, height, 0, 0, height, width);
            bitmap.setPixels(pixels, 0, height, 0, 0, height, width);
            //bitmap.copyPixelsFromBuffer(image_byte_buffer);
            setImageDrawable(bd);
        }
        else {  // bitmap == null
            if (DEBUG_LOG) {
                Log.e(TAG, "Oops, failed to create bitmap");
            }
        }

        if (DEBUG_LOG) {
            Log.v(TAG, "finished drawImage()");
        }
    }

    public void reset()
    {
        if (DEBUG_LOG) {
            Log.v(TAG, "reset()");
        }

        // what to do?
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

