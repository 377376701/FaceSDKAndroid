package com.baidu.facesdklibrary.model;

public class DepthOption {
    /**
     * 是否需要深度活体检测
     * true: 需要活检
     * false:不需要活检，无LivenessCallback，将跟踪的实时人脸参数回调即可
     */
    public boolean mNeedLivenessDetection;

    /**
     * 活检阈值分数，范围0-1。
     * 设置该字段时，仅当超过该阈值时返回活检成功。若mThreshold=-1，使用算法默认阈值
     */
    public float mThreshold;
}
