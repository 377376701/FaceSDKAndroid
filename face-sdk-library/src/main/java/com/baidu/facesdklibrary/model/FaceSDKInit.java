package com.baidu.facesdklibrary.model;

/**
 * Created by v_liujialu01 on 2019/4/23.
 */

public class FaceSDKInit {
    private boolean isCommonSdkInit = false;
    private boolean isCropInitSuccess = false;
    private boolean isDetectInitSuccess = false;
    private boolean isDetectFastInitSuccess = false;
    private boolean isDetectNirInitSuccess = false;
    private boolean isDriverMonitorInitSuccess = false;
    private boolean isFaceMouthMaskInitSuccess = false;
    private boolean isQualityInitSuccess = false;
    private boolean isLivenessInitSuccess = false;
    private boolean isBestImageInitSuccess = false;
    private boolean isFaceGazeInitSuccess = false;

    public boolean isCommonSdkInit() {
        return isCommonSdkInit;
    }

    public void setCommonSdkInit(boolean commonSdkInit) {
        isCommonSdkInit = commonSdkInit;
    }

    public boolean isDetectInitSuccess() {
        return isDetectInitSuccess;
    }

    public void setDetectInitSuccess(boolean detectInitSuccess) {
        isDetectInitSuccess = detectInitSuccess;
    }

    public boolean isDetectFastInitSuccess() {
        return isDetectFastInitSuccess;
    }

    public void setDetectFastInitSuccess(boolean detectFastInitSuccess) {
        isDetectFastInitSuccess = detectFastInitSuccess;
    }

    public boolean isDetectNirInitSuccess() {
        return isDetectNirInitSuccess;
    }

    public void setDetectNirInitSuccess(boolean detectNirInitSuccess) {
        isDetectNirInitSuccess = detectNirInitSuccess;
    }

    public void setDriverMonitorInitSuccess(boolean driverMonitorInitSuccess) {
        isDriverMonitorInitSuccess = driverMonitorInitSuccess;
    }

    public boolean isQualityInitSuccess() {
        return isQualityInitSuccess;
    }

    public void setQualityInitSuccess(boolean qualityInitSuccess) {
        isQualityInitSuccess = qualityInitSuccess;
    }

    public boolean isLivenessInitSuccess() {
        return isLivenessInitSuccess;
    }

    public void setLivenessInitSuccess(boolean livenessInitSuccess) {
        isLivenessInitSuccess = livenessInitSuccess;
    }

    public boolean isCropInitSuccess() {
        return isCropInitSuccess;
    }

    public void setCropInitSuccess(boolean cropInitSuccess) {
        isCropInitSuccess = cropInitSuccess;
    }

    public boolean isBestImageInitSuccess() {
        return isBestImageInitSuccess;
    }

    public void setBestImageInitSuccess(boolean bestImageInitSuccess) {
        isBestImageInitSuccess = bestImageInitSuccess;
    }

    public void setFaceMouthMaskInitSuccess(boolean faceMouthMaskInitSuccess) {
        isFaceMouthMaskInitSuccess = faceMouthMaskInitSuccess;
    }

    public boolean isFaceMouthMaskInitSuccess(){
        return isFaceMouthMaskInitSuccess;
    }

    public void setFaceGazeInitSuccess(boolean faceGazeInitSuccess) {
        isFaceGazeInitSuccess = faceGazeInitSuccess;
    }

    public boolean isFaceGazeInitSuccess(){
        return isFaceGazeInitSuccess;
    }

}
