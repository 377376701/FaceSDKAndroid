package com.baidu.facesdklibrary.model;

public enum DetectionErrorType {

    /**
     * NO_FACE
     */
    NO_FACE,
    /**
     * ⼈人脸⼤大⼩小不不合适(不不符合最⼤大或最⼩小⼈人脸的限制)
     */
    SIZE_REJECT,
    /**
     * ⼈人脸⻆角度不不合适(不不符合⻆角度限制)
     */
    ANGLE_REJECT,
    /**
     * 质量量分不不合适(低于质量量分阈值)
     */
    QUALITY_SCORE_REJECT,
    /**
     * 闭眼
     */
    EYE_CLOSE,
    /**
     * 脸部遮挡
     * 参考信标委关于⼈人脸遮挡的标准
     */
    Face_COVER,
    /**
     * ⾮必须
     * 多⼈人时⽆无法确定活检主体
     * ⼈人脸之间⼤大⼩小⽐比例例不不合适(maxFace*rate ~ maxFace 之间存在多个⼈人脸) * 设置mValidFaceSizeRate后必须回调
     */
    SIZE_RATE_REJECT,
    /**
     * ⾮必须
     * ⼈人脸过多，超出最⼤大⽀支持⼈人脸数量量
     */
    TOO_MANY_FACES,
    /**
     * ⾮必须
     * ⽆无效的帧数据
     */
    INVALID_FRAME_DATA,
    /**
     * ⾮必须
     * 未知错误
     */
    UNKNOWN_ERROR,
    /**
     * 扣图错误
     */
    FACE_CROP_REJECT
}
