package funs.common.tools;

import android.util.Log;

/**
 * @ClassName: CLogger
 * @Description:
 */
public final class CLogger {
private static final boolean debug = true; //BuildConfig.DEBUG;

    public static void i(String tag, String format, Object... args) {
        if (debug) {
            String msg = String.format(format, args);
            i(tag, msg);
        }
    }


    public static void i(String tag, String msg) {
        if (debug) Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (debug) Log.d(tag, msg);
    }
}
