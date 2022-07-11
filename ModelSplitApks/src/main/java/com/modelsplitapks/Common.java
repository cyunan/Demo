/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Package Manager, a simple, yet powerful application
 * to manage other application installed on an android device.
 *
 */

package com.modelsplitapks;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on May 03, 2021
 */
public class Common {

    private static boolean mReloadPage = false, mRunning = false,  mSystemApp = false, mUninstall = false, mUpdating = false;

    private static final List<String> mAPKList = new ArrayList<>();

    private static String mApplicationID, mPath;



    public static String getApplicationID() {
        return mApplicationID;
    }



    public static String getPath() {
        return mPath;
    }



    public static List<String> getAppList() {
        return mAPKList;
    }



    public static void isUpdating(boolean b) {
        mUpdating = b;
    }


    public static void setApplicationID(String packageID) {
        mApplicationID = packageID;
    }


    public static void setPath(String path) {
        mPath = path;
    }

}