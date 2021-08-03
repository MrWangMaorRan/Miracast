package com.zizhong.mobilescreen.utils.log;


import android.util.Log;

public class LogUtils {

    public static void i(String title, String msg) {
        String str = formatString(title, msg);
        if (LogVariateUtils.getInstance().getIsShowLog())
            Log.i(LogVariateUtils.getInstance().getTag(), str);
    }

    public static void w(String title, String msg) {
        String str = formatString(title, msg);
        if (LogVariateUtils.getInstance().getIsShowLog())
            Log.w(LogVariateUtils.getInstance().getTag(), str);
    }

    public static void e(String title, String msg) {
        String str = formatString(title, msg);
        if (LogVariateUtils.getInstance().getIsShowLog())
            Log.e(LogVariateUtils.getInstance().getTag(), str);
    }

    public static String formatString(String title, String msg) {
        if (title == null) {
            return msg == null ? "" : msg;
        }
        return String.format("[%s]: %s", title, msg == null ? "" : msg);
    }

    public static void info(String title, String msg) {
        String str = formatString(title, msg);
        if (LogVariateUtils.getInstance().getIsShowLog())
            Log.i(LogVariateUtils.getInstance().getTag(), str);
        if (LogVariateUtils.getInstance().getIsWriteLog())
            LogFileUtils.writeLogFile(str);
    }

    public static void warm(String title, String msg) {
        String str = formatString(title, msg);
        if (LogVariateUtils.getInstance().getIsShowLog())
            Log.w(LogVariateUtils.getInstance().getTag(), str);
        if (LogVariateUtils.getInstance().getIsWriteLog())
            LogFileUtils.writeLogFile(str);
    }

    public static void err(String title, String msg) {
        String str = formatString(title, msg);
        if (LogVariateUtils.getInstance().getIsShowLog())
            Log.e(LogVariateUtils.getInstance().getTag(), str);
        if (LogVariateUtils.getInstance().getIsWriteLog())
            LogFileUtils.writeLogFile(str);
    }

}
