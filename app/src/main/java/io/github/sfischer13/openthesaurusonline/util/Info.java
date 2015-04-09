package io.github.sfischer13.openthesaurusonline.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class Info {
    private static PackageInfo packageInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        String pn = context.getPackageName();
        try {
            return pm.getPackageInfo(pn, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private static String versionName(Context context) {
        PackageInfo pi = packageInfo(context);
        if (null == pi) {
            return "null";
        }
        return pi.versionName;
    }

    private static int versionCode(Context context) {
        PackageInfo pi = packageInfo(context);
        if (null == pi) {
            return -1;
        }
        return pi.versionCode;
    }

    public static String versionString(Context context) {
        return String.format("%s (%d)", versionName(context), versionCode(context));
    }
}
