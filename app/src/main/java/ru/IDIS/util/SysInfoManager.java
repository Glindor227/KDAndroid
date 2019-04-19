package ru.IDIS.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import ru.cpc.smartflatview.MainActivity;

// This class provides informations of Android OS and Hardware features.
public class SysInfoManager {
    private static final String TAG = SysInfoManager.class.getSimpleName();

    // Debug Log    
    private static final boolean DEBUG_LOG = false;

    private static Context mContext = null;

    static {
        mContext = MainActivity.getAppContext();
    }

    public static boolean isModernArch()
    {
        /*
        // OpenGL ES will not be used,
        // until the error on Galaxy S2 will be solved.
        return false;
        */

        if (CPU.ARCH == CPU.ARMv7) {
            if (DEBUG_LOG) {
                Log.v(TAG, "This system uses ARMv7 cpu.");
            }
            
            return true;
        }
        else {
            if (DEBUG_LOG) {
                Log.v(TAG, "This system does not use ARMv7 cpu.");
            }
            
            return false;
        }
    }

    public static boolean hasFroyo() {
        return API.LEVEL >= API.FROYO;
    }
    
    public static boolean hasGingerBread() {
        return API.LEVEL >= API.GINGERBREAD;
    }
    
    public static boolean hasHoneycomb() {
        return API.LEVEL >= API.HONEYCOMB;
    }
    
    public static boolean hasHoneycombMR1() {
        return API.LEVEL >= API.HONEYCOMB_MR1;
    }

    public static boolean hasHoneycombMR2() {
        return API.LEVEL >= API.HONEYCOMB_MR2;
    }

    public static boolean hasIceCreamSandwidch() {
        return API.LEVEL >= API.ICE_CREAM_SANDWICH;
    }
    
    public static boolean hasJellyBean() {
        return API.LEVEL >= API.JELLY_BEAN;
    }
    
    public static boolean hasJellyBeanMR1() {
        return API.LEVEL >= API.JELLY_BEAN_MR1;
    }
    
    public static boolean hasKitKat() {
        return API.LEVEL >= API.KITKAT;
    }
    
    public static boolean hasLollipop() {
        return API.LEVEL >= API.LOLLIPOP;
    }

    public static boolean supportsAdditionalOnScreenSoftwareHardKey() {
        /**
         * http://stackoverflow.com/questions/7213771/how-to-get-screen-resolution-in-android-honeycomb
         * 
         * Since API 13, the height of a status bar with on-screen-software-hard keys are excluded
         * from the display height calculation, so the value of display width on Portrait and height on Landscape 
         * can be different if you get them from widthPixels or heightPixels as a member of DisplayMetrics.
         * 
         * use below example if you want to know real size of you display
         * 
         *  
         * Method mGetRawW = Display.class.getMethod("getRawWidth");
         * Method mGetRawH = Display.class.getMethod("getRawHeight");
         * int nW = (Integer)mGetRawW.invoke(dp);
         * int nH = (Integer)mGetRawH.invoke(dp);
         */
        return hasHoneycombMR2();
    }
    
    public static boolean supportsDisplayRealSize() {
        /**
         * http://stackoverflow.com/questions/7213771/how-to-get-screen-resolution-in-android-honeycomb
         * 
         * Since API 17, it supports Display.getRealSize() for it more clear
         * 
         */
        return hasJellyBeanMR1();
    }
    
    public static boolean supportsMarginsForFrameLayout() {
        return hasHoneycomb();
    }
    
    public static boolean supportsLruCache() {
        return hasHoneycombMR1();
    }

    public static boolean supportsBitmapReuse() {
        return hasIceCreamSandwidch();
    }
    
    public static boolean supportsBitmapReconfigure() {
        return hasKitKat();
    }
    
    public static boolean supportsActionBar() {
        return hasHoneycomb();
    }
    
    public static boolean supportsDetectingPermenentMenuKey() {
        return hasIceCreamSandwidch();
    }

    public static class API 
    {
        // Android OS shortcut
        // refer to Build.java in Android Source tree for further information.
        public static final int BASE                   = 1;    // 1.0
        public static final int BASE_1_1               = 2;    // 1.1
        public static final int CUPCAKE                = 3;    // 1.5
        public static final int DONUT                  = 4;    // 1.6
        public static final int ECLAIR                 = 5;    // 2.0
        public static final int ECLAIR_0_1             = 6;    // 2.0.1
        public static final int ECLAIR_MR1             = 7;    // 2.1
        public static final int FROYO                  = 8;    // 2.2
        public static final int GINGERBREAD            = 9;    // 2.3
        public static final int GINGERBREAD_MR1        = 10;   // 2.3.3~2.3.4
        public static final int HONEYCOMB              = 11;   // 3.0
        public static final int HONEYCOMB_MR1          = 12;   // 3.1
        public static final int HONEYCOMB_MR2          = 13;   // 3.2
        public static final int ICE_CREAM_SANDWICH     = 14;   // 4.0        
        public static final int ICE_CREAM_SANDWICH_MR1 = 15;   // 4.0.3
        public static final int JELLY_BEAN             = 16;   // 4.1
        public static final int JELLY_BEAN_MR1         = 17;   // 4.2
        public static final int JELLY_BEAN_MR2         = 18;   // 4.3
        public static final int KITKAT                 = 19;   // 4.4
        public static final int L                      = 20;   // 5.0 preview
        public static final int LOLLIPOP               = 21;   // 5.0

        // You can find Android Version on runtime by checking this LEVEL value.
        public static final int LEVEL = android.os.Build.VERSION.SDK_INT;
    }
    
    public static class Device {
        public static String name() {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                return capitalize(model);
            }
            else {
                return capitalize(manufacturer) + " " + model;
            }
        }
        
        private static String capitalize(String s) {
            if (s == null || s.length() == 0) {
                return "";
            }
            char first = s.charAt(0);
            if (Character.isUpperCase(first)) {
                return s;
            }
            else {
                return Character.toUpperCase(first) + s.substring(1);
            }
        }
    }

    public static class CPU
    {
        public static final int ARMunknown  = -1;
        public static final int ARMv5       =  0;
        public static final int ARMv6       =  1;
        public static final int ARMv7       =  2;

        private static String cpuinfo_Processor;
        
        @SuppressWarnings("unused")
        private static float cpuinfo_BogoMIPS;          // currently not used
        @SuppressWarnings("unused")
        private static String cpuinfo_Features;         // currently not used
        @SuppressWarnings("unused")
        private static int cpuinfo_CPU_implementer;     // currently not used
        
        @SuppressWarnings("unused")
        private static int cpuinfo_CPU_architecture;    // currently not used
        
        @SuppressWarnings("unused")
        private static int cpuinfo_CPU_variant;         // currently not used
        
        @SuppressWarnings("unused")
        private static int cpuinfo_CPU_part;            // currently not used
        
        @SuppressWarnings("unused")
        private static int cpuinfo_CPU_revision;        // currently not used
        @SuppressWarnings("unused")
        private static String cpuinfo_Hardware;         // currently not used
        @SuppressWarnings("unused")
        private static int cpuinfo_Revision;            // currently not used

        static {
            // all cpuinfo_* values are retrieved here.
            getCPUInfo();
        }

        public static final int ARCH = getCPUArch(cpuinfo_Processor);
        
        private static void getCPUInfo()
        {
            ProcessBuilder cmd;
            String cpuinfo_string = "";
        
            try {
                String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
                cmd = new ProcessBuilder(args);

                Process process = cmd.start();
                InputStream in = process.getInputStream();
                byte[] re = new byte[1024];
                while (in.read(re) != -1) {
                    cpuinfo_string = new String(re);

                    if (DEBUG_LOG) {
                        Log.v(TAG, "cpuinfo_string = " + cpuinfo_string);
                    } 

                    // Processor
                    if (cpuinfo_string.contains("Processor") == true) {
                        // first, remove preceding "Processor :" characters
                        int start_idx = 0;
                        int end_idx = 0;
                        try {
                            start_idx = cpuinfo_string.indexOf(":");
                            end_idx = cpuinfo_string.indexOf("\n");

                            if (DEBUG_LOG) {
                                Log.v(TAG, "start_idx = " + start_idx);
                                Log.v(TAG, "end_idx = " + end_idx);
                            }
                        }
                        catch (NullPointerException e) {
                            e.printStackTrace();
                            if (DEBUG_LOG) {
                                Log.e(TAG, "Oops, is null");
                            }
                        }

                        start_idx = start_idx + 2;  // removing ": "
                        cpuinfo_Processor = cpuinfo_string.substring(start_idx, end_idx);
                        if (DEBUG_LOG) {
                            Log.v(TAG, "Processor = " + cpuinfo_Processor);
                        }

                        cpuinfo_string = cpuinfo_string.substring(end_idx);
                    }

                    // remaining informations are ignored, for a while.
                }
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Android officially supports ARMv5te and ARMv7
        private static int getCPUArch(String processor)
        {
            //3 dirty fix
            if (processor.length() == 0) {
                if (DEBUG_LOG) {
                    Log.e(TAG, "Oops, processor string is empty.");
                    Log.v(TAG, "Architecture : ARMunknown");
                }
                
                return ARMunknown;
            }
            else {  
                // cautious : specific ARM Core strings SHOULD be used,
                // since some "ARM9" cpus are kind of ARMv4T
                if (processor.contains("ARMv5") == true ||
                     processor.contains("ARM926") == true ||
                     processor.contains("ARM946") == true ||
                     processor.contains("ARM966") == true ||
                     processor.contains("ARM968") == true ||
                     processor.contains("ARM996") == true ||
                     processor.contains("ARM1020") == true ||
                     processor.contains("ARM1022") == true ||
                     processor.contains("ARM1026") == true) {
                    if (DEBUG_LOG) {
                        Log.v(TAG, "Architecture : ARMv5");
                    }

                    return ARMv5;
                }
                else if (processor.contains("ARMv6") == true ||
                            processor.contains("ARM11") == true) {
                    if (DEBUG_LOG) {
                        Log.v(TAG, "Architecture : ARMv6");
                    }
                    
                    return ARMv6;
                }
                else if (processor.contains("ARMv7") == true) {
                    if (DEBUG_LOG) {
                        Log.v(TAG, "Architecture : ARMv7");
                    }
                    
                    return ARMv7;
                }
                else {
                    if (DEBUG_LOG) {
                        Log.v(TAG, "Architecture : ARMunknown");
                    }
                    
                    return ARMunknown;
                }
            }
        }
    }
}

