package ru.IDIS.view;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import ru.IDIS.util.SysInfoManager;

import java.util.Arrays;

public final class VideoBitmapPool {
    
    public static final String TAG = VideoBitmapPool.class.getSimpleName();
    public static final boolean DEBUG_LOG = false;
    
    private static final int ERASE_COLOR = Color.BLACK;
    
    public static final Config  COLOR_CONFIG    = Config.RGB_565;
    
    public static final int MAX_DEFAULT_BITMAP_PER_SCREEN   = 4;
    public static final int MAX_EXTRA_BITMAP_PER_SCREEN     = 1;
    
    // Default cache
    public  static final int DEFAULT_ELEMENT_WIDTH  = 512*2;
//    public  static final int DEFAULT_ELEMENT_WIDTH  = 1920;
    public  static final int DEFAULT_ELEMENT_HEIGHT = 256*2;
//    public  static final int DEFAULT_ELEMENT_HEIGHT = 1080;
    public  static final int DEFAULT_ELEMENT_SIZE  = DEFAULT_ELEMENT_WIDTH * DEFAULT_ELEMENT_HEIGHT;
    private static final int DEFAULT_ELEMENT_COUNT  = MAX_DEFAULT_BITMAP_PER_SCREEN * 2;
    private static final int DEFAULT_CACHE_SIZE  = DEFAULT_ELEMENT_SIZE * DEFAULT_ELEMENT_COUNT;
    
    // Extra cache
    public  static final int EXTRA_ELEMENT_WIDTH    = 2600;
    public  static final int EXTRA_ELEMENT_HEIGHT   = 2000;
    public  static final int EXTRA_ELEMENT_SIZE     = EXTRA_ELEMENT_WIDTH * EXTRA_ELEMENT_HEIGHT;
    private static final int EXTRA_ELEMENT_COUNT    = MAX_EXTRA_BITMAP_PER_SCREEN * 2;
    public  static final int EXTRA_CACHE_SIZE  = EXTRA_ELEMENT_SIZE * EXTRA_ELEMENT_COUNT;
    
    private static interface CacheProxyApiDelegate {
        public void     clearDefault(int key);
        public void     clearExtra(int key);
        public BitmapDrawable   getDefault(int key, int width, int height);
        public BitmapDrawable   getExtra(int key, int width, int height);
        public BitmapDrawable   findDefault(int key);
        public BitmapDrawable   findExtra(int key);
    }
    
    //***********************************************************************************
    //
    // Gingerbread
    //
    //***********************************************************************************
    
    private static class CacheProxyApiGingerbread implements CacheProxyApiDelegate {

        private VideoBitmapLruCache _cache
            = new VideoBitmapLruCache(DEFAULT_CACHE_SIZE + EXTRA_CACHE_SIZE, COLOR_CONFIG);
    
        public void clearDefault(int key) {
            // TODO Auto-generated method stub

            BitmapDrawable drawable = _cache.get(key);
            if (drawable != null) {
                Bitmap bitmap = drawable.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.eraseColor(ERASE_COLOR);
                }
            }
        }
        
        public void clearExtra(int key) {
            // TODO Auto-generated method stub
            clearDefault(DEFAULT_ELEMENT_COUNT + key);
        }
        
        public BitmapDrawable getDefault(int key, int width, int height) {
            // TODO Auto-generated method stub
            return _cache.get(key, width, height);
        }
        
        public BitmapDrawable getExtra(int key, int width, int height) {
            return getDefault(DEFAULT_ELEMENT_COUNT + key, width, height);
        }
        
        public BitmapDrawable   findDefault(int key) {
            return _cache.get(key);
        }
        
        public BitmapDrawable   findExtra(int key) {
            return findDefault(key);
        }
    }
    
    //***********************************************************************************
    //
    // Kitkat
    //
    //***********************************************************************************
    
    // Kitkat
    @TargetApi(SysInfoManager.API.HONEYCOMB_MR1)
    private static class CacheProxyApiHoneycomb implements CacheProxyApiDelegate {

        // Strategy :
        
        // Allocate large enough memory and maintain it.
        // - If the first-time-allocation size is not enough to reconfigure with the size passed, 
        // reconfigure() will be failed, otherwise with smaller size will be succeeded every time.
        // - TODO : consider some big sized image such as a UHD in the future

        // since HONEYCOMB_MR1, it supports LruCache
        private VideoBitmapLruCache _cacheDefault
            = new VideoBitmapLruCache(DEFAULT_ELEMENT_COUNT * DEFAULT_ELEMENT_WIDTH * DEFAULT_ELEMENT_HEIGHT, COLOR_CONFIG);
        
        private VideoBitmapLruCache _cacheExtra
            = new VideoBitmapLruCache(EXTRA_ELEMENT_COUNT * EXTRA_ELEMENT_WIDTH * EXTRA_ELEMENT_HEIGHT, COLOR_CONFIG);
        
        public void clearDefault(int key) {
            // TODO Auto-generated method stub
            BitmapDrawable drawable = _cacheDefault.get(key);
            if (drawable != null) {
                Bitmap bitmap = drawable.getBitmap();
                if (bitmap != null) {
                    bitmap.eraseColor(ERASE_COLOR);
                }
            }
        }
        
        public void clearExtra(int key) {
            // TODO Auto-generated method stub
            BitmapDrawable drawable = _cacheExtra.get(key);
            if (drawable != null) {
                Bitmap bitmap = drawable.getBitmap();
                if (bitmap != null) {
                    bitmap.eraseColor(ERASE_COLOR);
                }
            }
        }
        
        public BitmapDrawable getDefault(int key, int width, int height) {
            // TODO Auto-generated method stub
            return _cacheDefault.get(key, width, height);
        }
        
        public BitmapDrawable getExtra(int key, int width, int height) {
            return _cacheExtra.get(key, width, height);
        }
        
        public BitmapDrawable   findDefault(int key) {
            return _cacheDefault.get(key);
        }
        
        public BitmapDrawable   findExtra(int key) {
            return _cacheExtra.get(key);
        }
    }
    
    //***********************************************************************************
    //
    //  cache
    //
    //***********************************************************************************
    public interface OnBitmapSwapListener {
        void onSwap(int id, BitmapDrawable drawable);
    }
    
    public static VideoBitmapPool createBitmapPool(OnBitmapSwapListener l) {
        return new VideoBitmapPool(l);
    }

    private static final int NOT_PREEMTIVE_YET = -1;
    
    private int[] _extraPreemtiveKeys = new int[EXTRA_ELEMENT_COUNT];

    private CacheProxyApiDelegate _cacheDelegate = null;

    private OnBitmapSwapListener _onSwapListener = null;
    
    private VideoBitmapPool(OnBitmapSwapListener l) {

        _onSwapListener = l;
        
        if (_cacheDelegate == null) {
            if (SysInfoManager.hasHoneycomb()) {
                _cacheDelegate = new CacheProxyApiHoneycomb();
            }
            else {
                _cacheDelegate = new CacheProxyApiGingerbread();
            }
            clear();
        }
    }
    
    public void clear() {
        
        synchronized (this) {
            for (int i = 0; i < DEFAULT_ELEMENT_COUNT; ++i) {
                _cacheDelegate.clearDefault(i);
            }
            for (int i = 0; i < EXTRA_ELEMENT_COUNT; ++i) {
                _cacheDelegate.clearExtra(i);
            }
            Arrays.fill(_extraPreemtiveKeys, NOT_PREEMTIVE_YET);
        }
    }
    
    public BitmapDrawable get(int id, int width, int height) {
        
        if (DEBUG_LOG) {
            Log.i(TAG, "use new = " + id);
        }

        if (_cacheDelegate == null) {
            return null;
        }
        
        boolean useDefault = DEFAULT_ELEMENT_SIZE >= (width * height);
        
        if (useDefault) {
            int key = defaultKey(id);
            
            if (DEBUG_LOG) {
                Log.i(TAG, "use new default= " + key + " " + width + "x" + height);
            }
            
            return _cacheDelegate.getDefault(key, width, height);
        }
        else {
            int key = extraKey(id);
            
            if (DEBUG_LOG) {
                Log.i(TAG, "use new extra= " + key + key + " " + width + "x" + height);
            }
            
            if (_extraPreemtiveKeys[key] != NOT_PREEMTIVE_YET &&
                _extraPreemtiveKeys[key] != id) {
                _cacheDelegate.clearExtra(key);
                
                if (_onSwapListener != null) {
                    int previousOwner = _extraPreemtiveKeys[key];
                    _onSwapListener.onSwap(previousOwner, _cacheDelegate.findDefault(defaultKey(previousOwner)));
                }
            }
            _extraPreemtiveKeys[key] = id;
            
            return _cacheDelegate.getExtra(key, width, height);
        }
    }
    
    private static int defaultKey(int id) {
        return id >= DEFAULT_ELEMENT_COUNT ? id % DEFAULT_ELEMENT_COUNT : id;
    }
    
    private static int extraKey(int id) { 
        return id >= EXTRA_ELEMENT_COUNT ? id % EXTRA_ELEMENT_COUNT : id;
    }
    
    private static VideoBitmapPool s_instance = null;
    
    public static void initialize(OnBitmapSwapListener l) {
        s_instance = VideoBitmapPool.createBitmapPool(l);
    }
    
    public static VideoBitmapPool getInstance() {
        return s_instance;
    }
}

