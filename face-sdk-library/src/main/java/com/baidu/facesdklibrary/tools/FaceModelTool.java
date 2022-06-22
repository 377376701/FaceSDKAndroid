package com.baidu.facesdklibrary.tools;

import com.baidu.facesdklibrary.SDKConfig;
import com.baidu.facesdklibrary.model.ImageFrame;
import com.baidu.facesdklibrary.model.LivenessResult;
import com.baidu.facesdklibrary.model.TrackResult;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceDetectListConf;

public class FaceModelTool {

    public static TrackResult getTrackResult(ImageFrame imageFrame, FaceInfo[] faceInfos) {
        if (faceInfos == null || faceInfos.length == 0) {
            return null;
        }
        TrackResult trackResult = new TrackResult();
        trackResult.faceInfos = faceInfos;

        if (imageFrame != null) {
            trackResult.width = (imageFrame.angle / 90) % 2 == 0 ? imageFrame.width : imageFrame.height;
            trackResult.height = (imageFrame.angle / 90) % 2 == 0 ? imageFrame.height : imageFrame.width;
        }

        return trackResult;
    }


    public static LivenessResult getLivenessResult(FaceInfo[] faceInfos) {
        if (faceInfos == null || faceInfos.length == 0) {
            return null;
        }

        LivenessResult livenessResult = new LivenessResult();
        livenessResult.faceInfo = faceInfos[0];
        return livenessResult;
    }

    public static BDFaceDetectListConf getRgbDetectListConf() {
        BDFaceDetectListConf conf = null;
        if (conf == null) {
            conf = new BDFaceDetectListConf();
        }
        if (SDKConfig.faceQuality().isOpenQuality()) {
            conf.usingHeadPose = true;
            conf.usingQuality = true;
        }
        if (SDKConfig.faceBestImage().isOpenBestImage()) {
            conf.usingBestImage = true;
        }
        return conf;
    }

    public static BDFaceDetectListConf getNirDetectListConf() {
        BDFaceDetectListConf conf = null;
        if (conf == null) {
            conf = new BDFaceDetectListConf();
        }
        conf.usingDetect = true;
        if (SDKConfig.faceQuality().isOpenQuality()) {
            conf.usingHeadPose = true;
            conf.usingQuality = true;
        }
        if (SDKConfig.faceBestImage().isOpenBestImage()) {
            conf.usingBestImage = true;
        }
        return conf;
    }
}
