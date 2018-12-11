package com.eemf.sirgoingfar.bakingapp.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;

public class ArchitectureUtil {

    public static boolean isPhoneRotated(@NonNull Context context){
        switch (context.getResources().getConfiguration().orientation){
            case Configuration.ORIENTATION_LANDSCAPE:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTablet(@NonNull Context context){
        final int TAB_MIN_SCREEN_WIDTH = 1280;
        int deviceWidth = context.getResources().getDisplayMetrics().widthPixels;
       return deviceWidth >= TAB_MIN_SCREEN_WIDTH;
    }
}
