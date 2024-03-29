package com.rede.indicanetapp.Firebase;

import android.util.Log;

import com.rede.indicanet.BuildConfig;


/**
 * Logger showing class name,method name,line number
 *
 * @author Tom Misawa (riversun.org@gmail.com)
 */
public class Logg {

    private static final String TAG = "MyAppDebug";
    private static final boolean debuggable = BuildConfig.DEBUG;

    public static void d() {
        if (debuggable) {
            Log.d(TAG, getCallFrom());
        }
    }

    public static void d(String msg) {
        if (debuggable) {
            Log.d(TAG, getCallFrom() + " " + msg);
        }
    }


    private static String getCallFrom() {

        final StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        final String fqClassName = ste.getClassName();
        final String className = fqClassName.substring(fqClassName.lastIndexOf(".") + 1);
        final String methodName = ste.getMethodName();
        final int lineNumber = ste.getLineNumber();
        final String callerInfo = className + "." + methodName + ": L" + lineNumber;

        return callerInfo;

    }
}
