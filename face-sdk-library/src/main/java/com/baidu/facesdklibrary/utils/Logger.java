package com.baidu.facesdklibrary.utils;

import android.util.Log;

import com.baidu.facesdklibrary.FaceIDDebug;
import com.baidu.facesdklibrary.SDKConstant;


public class Logger {
    private static final String TAG = "nighthawk";
    private static StringBuffer sBuffer = new StringBuffer();

    /**
     * 输出log.i日志
     *
     * @param tag
     * @param msg
     * @return
     */
    public static int i(String tag, String msg) {
        // 打开日志
        if (FaceIDDebug.isOpenIdeLog()) {
            if (FaceIDDebug.isSaveLogFile()) {
                sBuffer.delete(0, sBuffer.length());
                sBuffer.append("\n");
                sBuffer.append(DataUtils.getCurTime());
                sBuffer.append(":");
                sBuffer.append(TAG);
                sBuffer.append(":");
                sBuffer.append(tag);
                sBuffer.append("=");
                sBuffer.append(msg);
                FileUtil.appendStrToFile(SDKConstant.SAVELOG_PATH, sBuffer.toString());
                sBuffer.delete(0, sBuffer.length());
            }
            return Log.i(TAG + ":" + tag, msg);
        } else {
            return -1;
        }
    }
}
