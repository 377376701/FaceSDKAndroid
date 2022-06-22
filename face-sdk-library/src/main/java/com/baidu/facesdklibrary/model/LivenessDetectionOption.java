package com.baidu.facesdklibrary.model;

public class LivenessDetectionOption {
    /**
     * 是否需要人脸追踪
     * true: 需要人脸跟踪
     * false: 不需要人脸跟踪，此时不回调FaceTrackCallback,活检结果集中可不包含faceId
     */
    public boolean mNeedFaceTracking;

    /**
     * 是否需要活体检测
     * true: 需要活检
     * false:不需要活检，无LivenessCallback，将跟踪的实时人脸参数回调即可
     */
    public boolean mNeedLivenessDetection;

    /**
     * 是否需要识别
     * true: 需要识别
     * false:不需要识别，无LivenessCallback，将跟踪的实时人脸参数回调即可
     */
    public boolean mNeedIdentification;

    /**
     * 是否需要人脸库检索
     * true: 需要检索
     * false:不需要检索，mNeedFeatureDetection，将跟踪的实时人脸参数回调即可
     */
    public boolean mNeedFeatureDetection;

    /**
     * 活检阈值分数，范围0-1。
     * 设置该字段时，仅当超过该阈值时返回活检成功。若mThreshold=-1，使用算法默认阈值
     */
    public float mThreshold;

    /**
     * 上下阈值，过滤超过该⻆度的人脸
     * 范围0-45
     */
    public float mValidPitch;

    /**
     * 旋转阈值，过滤超过该⻆度的人脸
     * 范围0-45
     */
    public float mValidRoll;

    /**
     * 左右阈值，过滤超过该⻆度的人脸
     * 范围0-45
     */
    public float mValidYaw;

    /**
     * 眼间距阈值，过滤超过该眼间距的人脸
     * 范围0-480
     */
    public float mValidEyeDistance;

    /**
     * 人脸质量分阈值，过滤低于该质量分的人脸
     * 范围0-1
     */
    public float mValidQualityScore;

    /**
     * 最小有效人脸大小(width*height)，过滤小于该人脸大小的人脸
     */
    public int mValidMinFaceSize;

    /**
     * 最大有效人脸大小(width*height)，过滤大于该人脸大小的人脸
     */
    public int mValidMaxFaceSize;

    /**
     * 非必须参数
     * 支持人脸检测的最多人脸数
     * 注: 并非支持多人活检的人脸数，而是人脸检测、跟踪的最大人脸数
     */
    public int mMaxFaceNumSupport;

    /**
     * 非必须参数
     * 多人脸阈值，当前帧中有多个人脸的大小 > 最大人脸大小 * validFaceSizeRate 时，判断该帧的最大人脸不适合做活检(活检主体无法确认),可过滤 * 推荐阈值0.6
     * 1代表不做该种情况的过滤，直接返回最大人脸
     * 范围0-1
     */
    public float mValidFaceSizeRate;

    /**
     * 非必须参数
     * 是否拒绝闭眼
     * true: 如果闭眼，则认为活检失败:EYE_CLOSE * false:不过滤闭眼的情况
     */
    public boolean isRejectEyeClose;

    /**
     * 主体人脸筛选范围，以预览区域中心为ROI中心，以该值为半径圈的圆的外切正方形圈出ROI区域，当人脸区域超过ROI时给出状态信息POSITION_REJECT(人脸位置太偏)
     * 范围0-240
     */
    public float mValidRoiRadius = 240f;

    /**
     * 非必须参数
     * 人脸裁剪图(活检成功时rgb场景图对应的人脸图)的宽度扩大倍数
     * 例:scaleW = f,人脸原宽度为w,则横向以w为基础，左右分别拓宽(f/2)*w进行扩大。裁剪后图片宽度为w+f*w
     * 注:scaleW = 0表示裁剪,但不放大;
     * scaleW <0表示返回场景图。裁剪人脸时，若裁剪范围超过图片边框，则裁剪到边框即可
     */
    public float scaleW;

    /**
     * 非必须参数
     * 人脸裁剪图(活检成功时rgb场景图对应的人脸图)的高度扩大倍数
     * 例:scaleH = f，人脸原宽度为h,则纵向以 h 为基础，上下分别拓⻓ (f/2)*h 进行扩大，裁剪后图片⻓度为 h + f*h
     * 注:scaleH = 0 表示裁剪,但不放大;
     * scaleH < 0 表示返回场景图。裁剪人脸时，若裁剪范围超过图片边框，则裁剪到边框即可
     */
    public float scaleH;

    /**
     * 非必须参数
     * 活检成功时rgb场景图JPEG压缩比,取值范围[0-100]
     */
    public int compressRate;

    /**
     * 非必须参数
     * 红外图相关阈值
     */
    public NirOption nirOption = new NirOption();

    /**
     * 非必须参数
     * 深度图相关阈值
     */
    public DepthOption depthOption = new DepthOption();

    /**
     * 非必须参数
     * 识别相关阈值
     */
    public RecognizeOption recognizeOption = new RecognizeOption();

    /**
     * 业务场景，例如活检，注册，打卡，签到等
     */
    public String mBusinessType = "liveness_detect";
}
