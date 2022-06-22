package com.baidu.facesdklibrary.model;

import android.graphics.Bitmap;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.Feature;

public class LivenessResult {
    /**
     * 是否是活体(算法推荐) * true:活体
     * false:非活体
     */
    public boolean mIsLive;

    /**
     * rgb活检分数
     */
    public float livenessScore;

    /**
     * nir活检分数
     */
    public float nirlivenessScore;

    /**
     * depth活检分数
     */
    public float depthlivenessScore;

    /**
     * 活检成功那张rgb图的人脸参数
     */
    public FaceInfo faceInfo;

    /**
     * 活检成功那张nir图的人脸参数
     */
    public FaceInfo nirFaceInfo;

    /**
     * 活检成功时的rgb场景图
     * JPEG格式，24位真彩色图像 * 该图参与签名
     * 该图不裁剪
     */
    public Bitmap originBmp;

    /**
     * 活检成功时的nir场景图
     * JPEG格式, 24位灰度图像 * 该图参与签名
     * 该图不裁剪
     */
    public Bitmap nirOriginBmp;

    /**
     * 对应originBmp
     * 如果美颜开关未打开，该bmp为rgb人脸缩略图
     * 如果美颜开关打开，且算法支持美颜，该bmp为美颜后的人脸缩略图 * 该图可用于展示,图片宽高依据入参 scaleW 及 scaleH 处理
     */
    public Bitmap avatarBmp;

    /**
     * originBmp的亮度
     */
    public float rgbBrightness;

    /**
     * originBmp的清晰度
     */
    public float rgbDefinition;

//    /**
//     * 活检成功那张深度图相关信息
//     */
//    public DepthResult depthResult;
//
//    /**
//     * 活检成功那张红外图相关信息
//     */
//    public IrResult irResult;

    /**
     * 眼睛是否闭着(当不拒绝闭眼时)
     */
    public boolean isEyeClosed;

    /**
     * 非必须参数 * 签名信息
     */
    public String mSignatureData;

    /**
     * 特征提取是否成功，true 成功；false 失败
     */
    public boolean mfeatureStatus = false;

    /**
     * 特征提取结构体，包含featureID 和 feature 数组
     */
    public Feature feature;


    /**
     * 人脸库检索是否成功，true 成功；false 失败
     */
    public boolean mRecognizeStatue = false;

    /**
     * 识别成功那张红外图相关信息
     */
    public RecognizeResult recognizeResult;
}