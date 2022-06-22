package com.baidu.facesdklibrary.model;

import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;

public class RecognizeOption {
    /**
     * 特征类型
     * 模型生活照模型
     */
    public BDFaceSDKCommon.FeatureType featureType =
            BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO;

    /**
     *
     */
    public float threshold = 0.9f;

    /**
     *
     */
    public int topNum = -1;

    /**
     *
     */
    public boolean isPercent = true;
}
