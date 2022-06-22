package com.baidu.facesdklibrary.callback;


import com.baidu.facesdklibrary.model.AttributeResult;
import com.baidu.facesdklibrary.model.DetectionErrorType;

public interface AttributeCallback {
    /**
     * 属性结果更更新
     * 当算法完成属性时回调 *
     *
     * @param attributeResult
     */
    void onLivenessResult(AttributeResult attributeResult);

    /**
     * 属性过程出错
     * ⼈人脸不不满⾜足属性主体筛选条件时回调 *
     *
     * @param detectionErrorType 失败信息
     */
    void onDetectionError(DetectionErrorType detectionErrorType);
}