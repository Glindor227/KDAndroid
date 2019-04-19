package ru.IDIS.view;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;
import android.util.Log;

import ru.IDIS.util.SysInfoManager;
import ru.cpc.smartflatview.MainActivity;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@TargetApi(SysInfoManager.API.HONEYCOMB_MR1)
public final class VideoBitmapLruCache {
    
    private static final boolean DEBUG_LOG = false;
    private static final String TAG = VideoBitmapLruCache.class.getSimpleName(); 

    private Set<SoftReference<Bitmap>> _reusableBitmaps 
    = Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>()); 

    private LruCache<Integer, BitmapDrawable> _cache = null;
            
    private Config _config = null;

    public VideoBitmapLruCache(int maxSize, Config config) {
        
        _config = config;
        
        initialize(maxSize, config);
    }
    
    private void initialize(int maxSize, Config config) {
        
        _cache = new LruCache<Integer, BitmapDrawable>(maxSize) {
            
            @Override
            protected void entryRemoved(boolean evicted, Integer key,
                    BitmapDrawable oldValue, BitmapDrawable newValue) {
                // TODO Auto-generated method stub
                
                if (RecyclingBitmapDrawable.class.isInstance(oldValue)) {
                    // for Gingerbread
                    // The removed entry is a recycling drawable, so notify it
                    // that it has been removed from the memory cache
                    
                    if (DEBUG_LOG) {
                        Log.v(TAG, "uncached key = " + key);
                    }
                    ((RecyclingBitmapDrawable)oldValue).setIsCached(false);
                }
                else {
                    // for upper versions
                    if (DEBUG_LOG) {
                        Log.v(TAG, "_reusableBitmaps added key = " + key);
                    }
                    _reusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
                }
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
            
            @Override
            protected int sizeOf(Integer key, BitmapDrawable value) {
                final int bitmapSize = getBitmapSize(value);
                return bitmapSize == 0 ? 1 : bitmapSize;
            };
        };
    }
    
    public BitmapDrawable get(int key, int width, int height) {
        
        Bitmap bitmap = null;
        BitmapDrawable drawable = _cache.get(key);
        
        boolean newEntry = false;
        
        if (drawable != null) {
            bitmap = drawable.getBitmap();
            
            if (bitmap == null) {
                newEntry = true;
                bitmap = getBitmapFromReusableSet(width, height);
            }
        }
        if (!isReuseableIfConfiguredAs(bitmap, width, height, _config, true)) {
            newEntry = true;
            bitmap = Bitmap.createBitmap(width, height, _config);
        }

        if (SysInfoManager.hasHoneycomb()) {
            // cache bitmap only, Drawable must be created every time to update ImageView 
            drawable = new BitmapDrawable(MainActivity.getAppContext().getResources(), bitmap);
        }
        else {
            // reuse Drawable
            if (newEntry) {
                drawable = new RecyclingBitmapDrawable(MainActivity.getAppContext().getResources(), bitmap);
                ((RecyclingBitmapDrawable)drawable).setIsCached(true);
            }
        }
        
        _cache.put(key, drawable);
        
        return drawable;
    }
    
    public BitmapDrawable get(int key) {
        return _cache.get(key);
    }
    
    /**
     * @param options - BitmapFactory.Options with out* options populated
     * @return Bitmap that case be used for inBitmap
     */
    protected Bitmap getBitmapFromReusableSet(int width, int height) {
        //BEGIN_INCLUDE(get_bitmap_from_reusable_set)
        Bitmap bitmap = null;

        if (!_reusableBitmaps.isEmpty()) {
            synchronized (_reusableBitmaps) {
                final Iterator<SoftReference<Bitmap>> iterator = _reusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && item.isMutable()) {
                        // Check to see it the item can be used for inBitmap
                        
                        if (isReuseableIfConfiguredAs(item, width, height, _config, true)) {
                            bitmap = item;
                            // Remove from reusable set so it can't be used again
                            iterator.remove();
                            break;
                        }
                    } 
                    else {
                        // Remove from the set if the reference has been cleared.
                        
                        if (DEBUG_LOG) {
                            Log.v(TAG, "bitmap removed");
                        }
                        iterator.remove();
                    }
                }
            }
        }
        return bitmap;
    }

    public static int getBitmapSize(BitmapDrawable value) {
        return getBitmapSize(value.getBitmap());
    }
    
    @TargetApi(SysInfoManager.API.KITKAT)
    public static int getBitmapSize(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        // From KitKat onward use getAllocationByteCount() as allocated bytes can potentially be
        // larger than bitmap byte count.
        if (SysInfoManager.hasKitKat()) {
            return bitmap.getAllocationByteCount();
        }

        if (SysInfoManager.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }

        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
    
    @TargetApi(SysInfoManager.API.KITKAT)
    public static boolean isReuseableIfConfiguredAs(Bitmap bitmap, int width, int height, Config config, boolean reconfigure) {
        if (bitmap == null) {
            return false;
        }
        
        if (SysInfoManager.hasKitKat()) {
            boolean ret = bitmap.getAllocationByteCount() >= width * height * getBytesPerPixel(config);
            if (ret && reconfigure && (bitmap.getWidth() != width || bitmap.getHeight() != height)) {
                bitmap.reconfigure(width, height, config);
            }
            return ret;
        }

        // Lower than Kitkat, bitmap should be always recreated.
        
        return false;
    }
    
    public static int getBytesPerPixel(Config config) {
        if (config == Config.ARGB_8888) {
            return 4;
        }
        else if (config == Config.RGB_565) {
            return 2;
        }
        else if (config == Config.ARGB_4444) {
            return 2;
        }
        else if (config == Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }
}
