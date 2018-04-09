package com.flo354.xposed.applocale;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

public class AppItem {

    private final PackageInfo packageInfo;

    private final String appLabel;

    public AppItem(PackageInfo packageInfo, String appLabel) {
        this.packageInfo = packageInfo;
        this.appLabel = appLabel;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public ApplicationInfo getApplicationInfo() {
        return packageInfo.applicationInfo;
    }

    public String getAppLabel() {
        return appLabel;
    }
}
