//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2015 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>
package com.example.benq4kdemo.utils;

import java.io.File;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;

import com.example.benq4kdemo.DemoApplication;

public class Tools {
    private static final String TAG = "Tools";
    public static int CURPOS = 0;
    
    /**
     * judgment file exists.
     *
     * @param path
     *            file path.
     * @return when the parameters specified file exists return true, otherwise
     *         returns false.
     */
    public static boolean isFileExist(String path) {
        return isFileExist(new File(path));
    }

    /**
     * judgment file exists.
     *
     * @param file
     *            {@link File}.
     * @return when the parameters specified file exists return true, otherwise
     *         returns false.
     */
    public static boolean isFileExist(File file) {
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    /**
     * Stop media scanning.
     */
    public static void stopMediascanner(Context context) {
        Intent intent = new Intent();
        intent.setAction("action_media_scanner_stop");
        context.sendBroadcast(intent);
        Log.d("Tools", "stopMediascanner");
    }

    /**
     * Start media scanning.
     */
    public static void startMediascanner(Context context) {
        Intent intent = new Intent();
        intent.setAction("action_media_scanner_start");
        context.sendBroadcast(intent);
        Log.d("Tools", "startMediascanner");
    }

    public static  String getHardwareName() {
        String name = DemoApplication.getInstance().getHardwareName();
        if(name == null) {
            name = parseHardwareName();
            DemoApplication.getInstance().setHardwareName(name);
        }
        return name;
    }

    private static String parseHardwareName() {
        String str = null;
        try {
             FileReader reader = new FileReader("/proc/cpuinfo");
             BufferedReader br = new BufferedReader(reader);
             while ((str = br.readLine()) != null) {
                 if (str.indexOf("Hardware") >= 0) {
                     break;
                 }
             }
             if (str != null) {
                 String s[] = str.split(":" , 2);
                 str = s[1].trim().toLowerCase();
             }
             br.close();
             reader.close();
        } catch (Exception e) {
             e.printStackTrace();
        }
        return str;
    }

    private static String getConfigName(String file, String index) {
        String str = null;
        String gConfigName = null;
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null) {
                if (str.indexOf(index) >= 0) {
                    break;
                }
            }
            //Log.i(TAG,"str:"+str);
            if (str != null) {
                int begin = str.indexOf("\"") + 1;
                int end = str.lastIndexOf("\"");
                if ((begin > 0) && (end > 0)) {
                    gConfigName = str.substring(begin,end);
                    str = null;
                }
            }
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            str = null;
            return null;
        }
        Log.i(TAG, "gConfigName:" + gConfigName + "; file: " + file + "; index: " + index);
        return gConfigName;
    }
    private static String getbEnabled(String file, String index) {
        String str = null;
        String bEnabled = null;
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null) {
                if (str.indexOf(index) >= 0 && str.indexOf(index) <9) {
                    break;
                }
            }
            Log.i(TAG,"str:"+str);
            if (str != null) {
                int begin = str.indexOf("=") + 1;
                int end = str.lastIndexOf(";");
                if ((begin > 0) && (end > 0)) {
                    bEnabled = str.substring(begin,end);
                    str = null;
                }
            }
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            str = null;
            return null;
        }
        Log.i(TAG, "bEnabled:" + bEnabled + "; file: " + file + "; index: " + index);
        return bEnabled;
    }

    private static int[] parsePanelOsdSize(String index) {
        String str = null;
        String wPanelWidth = null;
        String wPanelHeight = null;
        int[] config = {0,0};
        String platform = getHardwareName();
        if ("muji".equals(platform) || "Manhattan".equals(platform)) {
            String strbEnabled = null;
            strbEnabled = getbEnabled("/config/sys.ini", "bEnabled");
            strbEnabled = strbEnabled.trim();
            if (strbEnabled!=null && strbEnabled.equals("TRUE")) {
                return  config;
            }
        }
        try {
            FileReader reader = new FileReader(getConfigName(getConfigName("/config/sys.ini", "gModelName"), "m_pPanelName"));
            BufferedReader br = new BufferedReader(reader);
            while ((str = br.readLine()) != null) {
                if (str.indexOf(index + "Width") >= 0) {
                    Log.i(TAG, index + "Width:" + str);
                    int begin = str.indexOf("=") + 1;
                    int end = str.lastIndexOf(";");
                    if ((begin > 0) && (end > 0)) {
                        wPanelWidth = str.substring(begin, end).trim();
                        Log.i(TAG, index +"Width:" + wPanelWidth);
                        config[0] = Integer.parseInt(wPanelWidth);
                    }
                } else if (str.indexOf(index + "Height") >= 0) {
                    Log.i(TAG, index + "Height:" + str);
                    int begin = str.indexOf("=") + 1;
                    int end = str.lastIndexOf(";");
                    if ((begin > 0) && (end > 0)) {
                        wPanelHeight = str.substring(begin, end).trim();
                        Log.i(TAG, index + "Height:" + wPanelHeight);
                        config[1] = Integer.parseInt(wPanelHeight);
                    }
                }
            }
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return config;
        }
        return config;
    }

    public static int[] getPanelSize() {
        int[] config = DemoApplication.getInstance().getPanelSize();
        if(config[0] == 0 || config[1] == 0) {
            config = parsePanelOsdSize("m_wPanel");
            DemoApplication.getInstance().setPanelSize(config);
        }
        else
            Log.i(TAG,"config!=0");
        return config;
    }

    public static int[] getOsdSize() {
        int[] config = DemoApplication.getInstance().getOsdSize();
        if(config[0] == 0 || config[1] == 0) {
            config = parsePanelOsdSize("osd");
            DemoApplication.getInstance().setOsdSize(config);
        }
        return config;
    }

    public static String getFileName(String sPath) {
        int pos = sPath.lastIndexOf("/");
        String name = sPath.substring(pos+1,sPath.length());
        return name;
    }

    public static String parseUri(Uri intent) {
        if (intent != null) {
            return intent.getPath();
        }
        return null;
    }

    public static void setRotateMode(boolean flag) {
        if (flag) {
            SystemProperties.set("mstar.video.rotate", "true");
        } else {
            SystemProperties.set("mstar.video.rotate", "false");
        }

    }
    public static boolean isRotateModeOn() {
        boolean status = SystemProperties.getBoolean("mstar.video.rotate", false);
        Log.i(TAG,"isRotateModeOn:"+status);
        return  status;
    }
    
    public static void setRotateMode(String value) {
        SystemProperties.set("mstar.video.rotate", value);
        if ("0".equalsIgnoreCase(value)) {
            SystemProperties.set("mstar.video.rotate.degrees", value);
        }
    }

    public static boolean isRotateDisplayAspectRatioModeOn() {
        boolean status = SystemProperties.getBoolean("mstar.video.rotate.aspectratio", false);
        return status;
    }

    public static void setRotateDegrees(String value) {
        SystemProperties.set("mstar.video.rotate.degrees", value);
    }

    public static int getRotateDegrees() {
        return SystemProperties.getInt("mstar.video.rotate.degrees", 0);
    }

    public static boolean isVideoSWDisplayModeOn() {
        boolean status = SystemProperties.getBoolean("mstar.video.sw.display", true);
        Log.i(TAG,"isVideoSWDisplayModeOn:"+status);
        return  status;
    }

    public static boolean isRotate90OR270Degree() {
        if (!isRotateModeOn()) {
            return false;
        }
        if ("90".equalsIgnoreCase(String.valueOf(getRotateDegrees()))
            || "270".equalsIgnoreCase(String.valueOf(getRotateDegrees()))) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean unSupportTVApi() {
        String property = SystemProperties.get("mstar.build.mstartv", null);
        if (property != null) {
            if (property.equalsIgnoreCase("ddi")) {
                return true;
            } else if (property.equalsIgnoreCase("mi")
                    && !getHardwareName().equals("clippers")
                    && !getHardwareName().equals("kano")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVideoStreamlessModeOn() {
        int seamstatus = SystemProperties.getInt("mstar.video.seamless.playback", 0);
        // maxim is asked to close video seamless playback by weiping.liu and gemi.Tsai
        // mooney is asked to close video seamless playback by ocean.zhao
        String platform = getHardwareName();
        if (seamstatus == 1
            && (platform!=null && !platform.equals("maxim"))
            && (platform!=null && !platform.equals("mooney")) ) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPhotoStreamlessModeOn() {
        int seamstatus = SystemProperties.getInt("mstar.photo.seamless.playback", 1);
        if (seamstatus == 1) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isElderPlatformForStreamLessMode(){
        boolean ret = false;
        String hardwareName = getHardwareName();
        if (hardwareName.equals("edison") || hardwareName.equals("kaiser")) {
            ret = true;
        }
        return ret;
    }

    public static void copyfile(String srFile, String dtFile) {
        try {
            File f1 = new File(srFile);
            if (!f1.exists()) {
                return;
            }
            File f2 = new File(dtFile);
            if (!f2.exists()){
                f2.createNewFile();
            }
            FileInputStream in = new FileInputStream(f1);
            FileOutputStream out = new FileOutputStream(f2);
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch(FileNotFoundException ex) {
            Log.d(TAG,"the file cannot be found");
        } catch(IOException e){
            Log.i(TAG,"---IOException---");
        }
    }

    public static boolean getResumePlayState(Uri uri) {
        if (SystemProperties.getInt("mstar.bootinfo", 1) == 0) {
            if (SystemProperties.getInt("mstar.backstat", 0) == 1) {
                String lPath = SystemProperties.get("mstar.path", "");
                String FN = getFileName(uri.getPath());
                Log.i("andrew", "the file name is:" + FN);
                if (lPath.equals(FN)) {
                    return true;
                } else {
                    SystemProperties.set("mstar.path", FN);
                    return false;
                }
            } else {
                return false;
            }
        } else {
            SystemProperties.set("mstar.bootinfo", "0");
            return false;
        }
    }

    public static void setResumePlayState(int state) {
        String sState = state + "";
        SystemProperties.set("mstar.backstat", sState);
    }

    public static String fixPath(String path) {
        // lollipop5.1 corrsponding to 22, don't need to handle the sign of '#' and '%' after lollipop5.1
        // mantis:0893065,0870320
        if(Build.VERSION.SDK_INT < 22) {
            if (path.indexOf("%") != -1) {
                path = path.replaceAll("%", "%25");
            }
            if (path.indexOf("#") != -1) {
                path = path.replaceAll("#", "%23");
            }
        }
        return path;
    }

    public static int getSdkVersion(){
        int ret = Build.VERSION.SDK_INT;
        return ret;
    }

}
