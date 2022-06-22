package com.baidu.facesdklibrary.callback;


import com.baidu.facesdklibrary.model.DetectionErrorType;
import com.baidu.facesdklibrary.model.LivenessResult;

public interface LivenessCallback {
    /**
     * 活检结果更更新
     * 当算法完成活检时回调 *
     *
     * @param livenessResult
     */
    void onLivenessResult(LivenessResult livenessResult);

    /**
     * 活检过程出错
     * ⼈人脸不不满⾜足活检主体筛选条件时回调 *
     *
     * @param detectionErrorType 失败信息
     */
    void onDetectionError(DetectionErrorType detectionErrorType);
}