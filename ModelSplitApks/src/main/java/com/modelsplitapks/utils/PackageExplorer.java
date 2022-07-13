/*
 * Copyright (C) 2021-2022 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Package Manager, a simple, yet powerful application
 * to manage other application installed on an android device.
 *
 */

package com.modelsplitapks.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import net.dongliu.apk.parser.ApkFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import in.sunilpaulmathew.sCommon.Utils.sExecutor;
import in.sunilpaulmathew.sCommon.Utils.sPermissionUtils;
import in.sunilpaulmathew.sCommon.Utils.sUtils;


public class PackageExplorer {

    public static boolean isTextFile(String path) {
        return path.endsWith(".txt") || path.endsWith(".xml") || path.endsWith(".json") || path.endsWith(".properties")
                || path.endsWith(".version") || path.endsWith(".sh") || path.endsWith(".MF") || path.endsWith(".SF")
                || path.endsWith(".RSA") || path.endsWith(".html") || path.endsWith(".ini");
    }

    public static boolean isImageFile(String path) {
        return path.endsWith(".bmp") || path.endsWith(".png") || path.endsWith(".jpg");
    }

    public static boolean isBinaryXML(String path) {
        return path.endsWith(".xml") && (new File(path).getName().equals("AndroidManifest.xml") || path.contains("/apk/res"));
    }

    public static int getSpanCount(Activity activity) {
        return sUtils.getOrientation(activity) == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1;
    }

    public static String readManifest(String apk) {
        try (ApkFile apkFile = new ApkFile(new File(apk))) {
            String manifest = apkFile.getManifestXml();
            apkFile.close();
            return manifest;
        } catch (IOException ignored) {
        }
        return null;
    }

    public static String readXMLFromAPK(String apk, String path) {
        try (ApkFile apkFile = new ApkFile(new File(apk))) {
            String xnkData = apkFile.transBinaryXml(path);
            apkFile.close();
            return xnkData;
        } catch (IOException ignored) {
        }
        return null;
    }

    public static Uri getIconFromPath(String path) {
        File mFile = new File(path);
        if (mFile.exists()) {
            return Uri.fromFile(mFile);
        }
        return null;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }



}