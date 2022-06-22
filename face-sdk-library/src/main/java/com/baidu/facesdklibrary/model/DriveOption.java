package com.baidu.facesdklibrary.model;

public class DriveOption {

    /**
     * 最小人脸
     */
    public int minFaceSize;

    /**
     * 非必须参数 , 活体相关参数
     */
    public LivenessDetectionOption livenessDetectionOption=new LivenessDetectionOption();
}
