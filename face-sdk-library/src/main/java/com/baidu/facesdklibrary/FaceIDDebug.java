package com.baidu.facesdklibrary;


import com.baidu.facesdklibrary.model.CameraSize;

/**
 * Created by yangrui on 2018/6/27.
 */

public class FaceIDDebug {
    /*true: 打开日志 false：不打开日志*/
    private static boolean openIdeLog = false;

    private static boolean saveLogFile = false;

    /* 保存 追踪失败图片*/
    private static boolean saveBadTrackPic = false;

    /*保存 活体失败原始图片*/
    private static boolean saveBadLivenessPic = false;

    /*保存 识别失败原始图片*/
    private static boolean saveBadRecognizePic = false;

    /*保存*/
    private static boolean saveGoodCasePic = false;

    /*保存badcase 图片*/
    private static boolean saveBadCasePic = false;

    private static CameraSize cameraSizesize = new CameraSize(480, 640);

    public static boolean isOpenIdeLog() {
        return openIdeLog;
    }

    public static void setOpenIdeLog(boolean openIdeLog) {
        FaceIDDebug.openIdeLog = openIdeLog;
    }

    public static boolean isSaveBadCasePic() {
        return saveBadCasePic;
    }

    public static void setSaveBadCasePic(boolean saveBadCasePic) {
        FaceIDDebug.saveBadCasePic = saveBadCasePic;
    }

    public static boolean isSaveGoodCasePic() {
        return saveGoodCasePic;
    }

    public static void setSaveGoodCasePic(boolean saveGoodCasePic) {
        FaceIDDebug.saveGoodCasePic = saveGoodCasePic;
    }

    public static boolean isSaveBadLivenessPic() {
        return saveBadLivenessPic;
    }

    public static boolean isSaveBadTrackPic() {
        return saveBadTrackPic;
    }

    public static boolean isSaveBadRecognizePic() {
        return saveBadRecognizePic;
    }

    public static boolean isSaveLogFile() {
        return saveLogFile;
    }

    public static void setSaveLogFile(boolean saveLogFile) {
        FaceIDDebug.saveLogFile = saveLogFile;
    }

    public static boolean isDebug() {
        return FaceIDDebug.isOpenIdeLog() || FaceIDDebug.isSaveLogFile()
                || FaceIDDebug.isSaveBadCasePic() || FaceIDDebug.isSaveGoodCasePic();
    }

    public static CameraSize getSize() {
        return cameraSizesize;
    }

    public static void setSize(CameraSize size) {
        cameraSizesize.setHeight(size.getHeight());
        cameraSizesize.setWidth(size.getWidth());
    }
}
