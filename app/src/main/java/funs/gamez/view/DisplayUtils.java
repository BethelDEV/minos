package funs.gamez.view;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import funs.common.tools.CLogger;

public class DisplayUtils {

    private static final String TAG = "DisplayUtils";

    public static Display getDisplay(Context context) {
        return getWindowManager(context).getDefaultDisplay();
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        Display display = getDisplay(context);
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics;
    }

    @SuppressWarnings("deprecation") // due to target api level
    public static Point getDisplaySize(Context context) {
        Display display = getDisplay(context);
        return new Point(display.getWidth(), display.getHeight());
    }

    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public static int getCurrentScreenOrientation(Context context) {
        Display display = getDisplay(context);
        Point displaySize = getDisplaySize(context);
        int displayRotation = display.getRotation();
        int screenOrientation = 0;
        if ((displaySize.x > displaySize.y && (displayRotation == Surface.ROTATION_0 || displayRotation == Surface.ROTATION_180)) ||
                (displaySize.x <= displaySize.y && (displayRotation == Surface.ROTATION_90 || displayRotation == Surface.ROTATION_270))) {
            // 平板
            switch (displayRotation) {
                case Surface.ROTATION_0:
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        } else {
            // 手机
            switch (displayRotation) {
                case Surface.ROTATION_0:
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
            }
        }
        CLogger.i(TAG, "=> screenOrientation: " + screenOrientation);
        return screenOrientation;
    }

}
