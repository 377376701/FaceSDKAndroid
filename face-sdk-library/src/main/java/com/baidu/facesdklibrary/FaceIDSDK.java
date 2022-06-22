package com.baidu.facesdklibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.baidu.facesdklibrary.callback.AttributeCallback;
import com.baidu.facesdklibrary.callback.DriveCallback;
import com.baidu.facesdklibrary.callback.FaceTrackCallback;
import com.baidu.facesdklibrary.callback.InitCallback;
import com.baidu.facesdklibrary.callback.LivenessCallback;
import com.baidu.facesdklibrary.model.AttributeOption;
import com.baidu.facesdklibrary.model.AttributeResult;
import com.baidu.facesdklibrary.model.DetectionErrorType;
import com.baidu.facesdklibrary.model.DriveOption;
import com.baidu.facesdklibrary.model.DriveResult;
import com.baidu.facesdklibrary.model.FaceSDKInit;
import com.baidu.facesdklibrary.model.ImageFrame;
import com.baidu.facesdklibrary.model.InitOption;
import com.baidu.facesdklibrary.model.LivenessDetectionOption;
import com.baidu.facesdklibrary.model.LivenessResult;
import com.baidu.facesdklibrary.model.SdkInfo;
import com.baidu.facesdklibrary.tools.FaceImageTool;
import com.baidu.facesdklibrary.tools.FaceModelTool;
import com.baidu.facesdklibrary.utils.BitmapUtils;
import com.baidu.facesdklibrary.utils.Logger;
import com.baidu.facesdklibrary.utils.SaveCasePicUtil;
import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.FaceCrop;
import com.baidu.idl.main.facesdk.FaceDetect;
import com.baidu.idl.main.facesdk.FaceDriverMonitor;
import com.baidu.idl.main.facesdk.FaceFeature;
import com.baidu.idl.main.facesdk.FaceGaze;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.FaceLive;
import com.baidu.idl.main.facesdk.FaceMouthMask;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceCropParam;
import com.baidu.idl.main.facesdk.model.BDFaceDetectListConf;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceInstance;
import com.baidu.idl.main.facesdk.model.BDFaceOcclusion;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.model.BDFaceSDKConfig;
import com.baidu.idl.main.facesdk.model.Feature;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.baidu.facesdklibrary.model.DetectionErrorType.INVALID_FRAME_DATA;
import static com.baidu.facesdklibrary.model.DetectionErrorType.QUALITY_SCORE_REJECT;

public class FaceIDSDK {

    private static final String TAG = "face_sdk";

    private FaceAuth mFaceAuth;
    private FaceDetect mFaceDetect;
    private FaceDetect mFaceDetect2;
    private FaceMouthMask mFaceMouthMask;
    private FaceDetect mFaceDetectNir;
    private FaceDriverMonitor mFaceDriverMonitor;
    private FaceLive mFaceLive;
    private FaceFeature mFaceFeature;
    private FaceSDKInit mFaceSDKInit;
    private BDFaceSDKConfig config;
    private FaceCrop mFaceCrop;
    private FaceGaze mFaceGaze;

    private ExecutorService es = Executors.newSingleThreadExecutor();
    private Future future;
    private ExecutorService es2 = Executors.newSingleThreadExecutor();
    private Future future2;


    private static class HolderClass {
        private static FaceIDSDK instance = new FaceIDSDK();
    }

    public static FaceIDSDK shareIns() {
        return HolderClass.instance;
    }

    /**
     * 初始化操作，如算法鉴权、加载模型等，完成后回调初始化结果;
     * <p>
     * 使用场景: 在应用启动时调用(大多数场景只做一次鉴权、模型加载，无需每次检测时重新初始化),希望鉴权失败或模型加载失败等情况下能告知用户
     * 超时时间: 15s
     *
     * @param context
     * @param initOption
     * @param initCallback
     */
    public void init(final Context context, InitOption initOption, final InitCallback initCallback) {

        if (mFaceSDKInit == null) {
            mFaceSDKInit = new FaceSDKInit();
        }

        if (mFaceSDKInit.isCommonSdkInit()) {
            Logger.i(TAG, "init has success");
            return;
        }

        if (context == null || initOption == null || initCallback == null) {
            Logger.i(TAG, "illegal params!");
            return;
        }

        if (mFaceAuth == null) {
            mFaceAuth = new FaceAuth();
            if (FaceIDDebug.isOpenIdeLog()) {
                mFaceAuth.setActiveLog(BDFaceSDKCommon.BDFaceLogInfo.BDFACE_LOG_TYPE_ALL, 1);
            }
        }

        if (!TextUtils.isEmpty(initOption.licenseKey)) {
            final long t1 = System.currentTimeMillis();
            mFaceAuth.initLicense(context, initOption.licenseKey, initOption.licenseFileName, false, new Callback() {
                @Override
                public void onResponse(int code, String response) {
                    Logger.i(TAG, "license code = " + code + ", resp = " + response);
                    long t2 = System.currentTimeMillis();
                    Logger.i(TAG, " init auth = " + (t2 - t1));
                    if (code == 0) {
                        initModel(context, initCallback);
                    } else {
                        initCallback.onError(code, response);
                    }
                }
            });
        }
    }

    /**
     * 人脸活体检测接口
     * <p>
     * 开启检测流程，如⼈人脸检测->跟踪->活检，筛选出符合活检要求的主体进⾏行行活体检测，
     * 拿到通过活检的⼈人脸相关信息与不不符合活检主体要求时的错误提示信息
     * 使⽤用场景:在预览成功的情况下开启检测，需要即时响应⽤用户的错误状态。
     * <p>
     * 注:暂无人脸跟踪能力时，调用方则可关闭人脸跟踪开关，只做活体检测，无需实现跟踪Callback;若支持跟踪，需支持只做跟踪不做活检的情况，调用方通 过开关控制。
     *
     * @param rgbFrame
     * @param nirFrame
     * @param livenessDetectionOption
     * @param faceTrackCallback
     * @param livenessCallback
     */
    public void startLivenessDetectionDetection(final ImageFrame rgbFrame,
                                                final ImageFrame nirFrame,
                                                final LivenessDetectionOption livenessDetectionOption,
                                                final FaceTrackCallback faceTrackCallback,
                                                final LivenessCallback livenessCallback) {

        if (!mFaceSDKInit.isCommonSdkInit()) {
            Logger.i(TAG, "init interface fail ");
            return;
        }

        if (future != null && !future.isDone()) {
            return;
        }

        // 如果最小检测人脸发现变化，重新配置到c++
        if (config != null && config.minFaceSize != livenessDetectionOption.mValidMinFaceSize) {
            config.minFaceSize = livenessDetectionOption.mValidMinFaceSize;
            mFaceDetect.loadConfig(config);
        }

        future = es.submit(new Runnable() {
            @Override
            public void run() {
                if (rgbFrame == null || livenessDetectionOption == null
                        || faceTrackCallback == null || livenessCallback == null) {
                    Logger.i(TAG, "illegal params!");
                    return;
                }

                final BDFaceImageInstance rgbInstance = FaceImageTool.convertYuvImage(rgbFrame);
                FaceInfo[] trackResult = null;

                // 如果需要做人脸追踪，调用onTrackCheck 方法
                trackResult = onTrackCheck(rgbInstance, livenessDetectionOption);
                if (trackResult == null) {
                    SaveCasePicUtil.saveCasePic(rgbInstance,
                            livenessDetectionOption.mBusinessType, "bad_track");
                    Logger.i(TAG, "track face is empty!");
                    // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                    rgbInstance.destory();
                    livenessCallback.onDetectionError(DetectionErrorType.NO_FACE);
                    faceTrackCallback.onTrackResult(null);
                    livenessCallback.onLivenessResult(null);
                    return;
                }
                if (livenessDetectionOption.mNeedFaceTracking) {
                    if (trackResult != null) {
                        faceTrackCallback.onTrackResult(FaceModelTool.getTrackResult(rgbFrame, trackResult));
                    }
                }

                // 如果不需要做活体检测，销毁图像并返回，使⽤用场景: 活体检测完成
                if (!livenessDetectionOption.mNeedLivenessDetection) {
                    Logger.i(TAG, "identification option is false!");
                    // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                    rgbInstance.destory();
                    return;
                }

                if (future2 != null && !future2.isDone()) {
                    // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                    rgbInstance.destory();
                    return;
                }

                // 将变量改为常量，否着Runnable 报错
                final FaceInfo[] trackInfos = trackResult;
                future2 = es2.submit(new Runnable() {
                    @Override
                    public void run() {
                        onLivenessCheck(rgbInstance, nirFrame, null,
                                livenessDetectionOption, trackInfos, livenessCallback);
                        rgbInstance.destory();
                        return;
                    }
                });
            }
        });
    }

    /**
     * 1:1人脸比对接口
     * <p>
     * 判定两张人脸图像是否属于同一个人 ，常用于身份认证如人证核验 。
     * 开启检测流程，如⼈人脸检测->活检->识别->1:1
     * 拿到通过活检的⼈人脸相关信息与不不符合活检主体要求时的错误提示信息
     * 使⽤用场景:在预览成功的情况下开启检测，需要即时响应⽤用户的错误状态。
     *
     * @param firstImageFrame         视频流
     * @param bm                      证件照
     * @param livenessDetectionOption
     * @param faceTrackCallback
     * @param livenessCallback
     */
    public void startVerification(ImageFrame firstImageFrame,
                                  Bitmap bm,
                                  final LivenessDetectionOption livenessDetectionOption,
                                  final FaceTrackCallback faceTrackCallback,
                                  final LivenessCallback livenessCallback) {
        if (!mFaceSDKInit.isCommonSdkInit()) {
            Logger.i(TAG, "init interface fail ");
            return;
        }

        if (livenessDetectionOption == null ||
                livenessCallback == null) {
            Logger.i(TAG, "illegal param");
            return;
        }

        // 如果最小检测人脸发现变化，重新配置到c++
        if (config != null && config.minFaceSize != livenessDetectionOption.mValidMinFaceSize) {
            config.minFaceSize = livenessDetectionOption.mValidMinFaceSize;
            mFaceDetect.loadConfig(config);
        }

        startFeature(firstImageFrame, null, livenessDetectionOption, faceTrackCallback, new LivenessCallback() {
            @Override
            public void onLivenessResult(LivenessResult livenessResult) {
                if (livenessResult == null || livenessResult.feature == null) {
                    return;
                }

                Feature firstFeature = livenessResult.feature;

                BDFaceImageInstance rgbInstance = new BDFaceImageInstance(bm);
                FaceInfo[] faceInfos = mFaceDetect2.detect(BDFaceSDKCommon.DetectType.DETECT_VIS, rgbInstance);
                if (faceInfos == null || faceInfos.length == 0) {
                    Logger.i(TAG, "detect face is empty!");
                    livenessCallback.onDetectionError(DetectionErrorType.NO_FACE);
                    rgbInstance.destory();
                    return;
                }
                byte[] featureArr = new byte[512];
                mFaceFeature.feature(
                        BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO,
                        rgbInstance, faceInfos[0].landmarks, featureArr);

                float featureScore = mFaceFeature.featureCompare(
                        BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO,
                        firstFeature.getFeature(),
                        featureArr,
                        true);
                livenessResult.feature.setScore(featureScore);
                livenessCallback.onLivenessResult(livenessResult);
                rgbInstance.destory();
            }

            @Override
            public void onDetectionError(DetectionErrorType detectionErrorType) {
                livenessCallback.onDetectionError(detectionErrorType);
            }
        });
    }

    /**
     * 识别接口
     * 拿到通过活检的⼈人脸相关信息和返回人脸识别ID，与不不符合活检主体要求时的错误提示信息
     * 使⽤用场景:在预览成功的情况下开启检测，需要即时响应⽤用户的错误状态。
     * <p>
     * 注:暂无人脸跟踪能力时，调用方则可关闭人脸跟踪开关，只做识别检测，无需实现跟踪Callback；若支持跟踪，需支持只做跟踪不做活检的情况，调用方通 过开关控制。
     *
     * @param rgbFrame
     * @param nirFrame
     * @param livenessDetectionOption
     * @param faceTrackCallback
     * @param livenessCallback
     */
    public void startFeature(final ImageFrame rgbFrame,
                             final ImageFrame nirFrame,
                             final LivenessDetectionOption livenessDetectionOption,
                             final FaceTrackCallback faceTrackCallback,
                             final LivenessCallback livenessCallback) {

        if (!mFaceSDKInit.isCommonSdkInit()) {
            Logger.i(TAG, "init interface fail ");
            return;
        }

        if (future != null && !future.isDone()) {
            return;
        }
        future = es.submit(new Runnable() {
            @Override
            public void run() {
                if (rgbFrame == null || livenessDetectionOption == null
                        || faceTrackCallback == null || livenessCallback == null) {
                    Logger.i(TAG, "illegal params!");
                    return;
                }

                final BDFaceImageInstance rgbInstance = FaceImageTool.convertYuvImage(rgbFrame);
                FaceInfo[] trackResult = null;

                // 如果需要做人脸追踪，调用onTrackCheck 方法
                trackResult = onTrackCheck(rgbInstance, livenessDetectionOption);
                if (trackResult == null) {
                    SaveCasePicUtil.saveCasePic(rgbInstance,
                            livenessDetectionOption.mBusinessType, "bad_track");
                    Logger.i(TAG, "track face is empty!");
                    // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                    rgbInstance.destory();
                    livenessCallback.onDetectionError(DetectionErrorType.NO_FACE);
                    faceTrackCallback.onTrackResult(null);
                    livenessCallback.onLivenessResult(null);
                    return;
                }
                if (livenessDetectionOption.mNeedFaceTracking) {
                    if (trackResult != null) {
                        faceTrackCallback.onTrackResult(FaceModelTool.getTrackResult(rgbFrame, trackResult));
                    }
                }

                // 如果不需要做人脸识别，销毁图像并返回，使⽤用场景: 识别成功
                if (!livenessDetectionOption.mNeedIdentification) {
                    Logger.i(TAG, "identification option is false!");
                    // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                    rgbInstance.destory();
                    return;
                }

                if (future2 != null && !future2.isDone()) {
                    // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                    rgbInstance.destory();
                    return;
                }

                // 将变量改为常量，否着Runnable 报错
                final FaceInfo[] trackInfos = trackResult;

                future2 = es2.submit(() -> {
                    onLivenessCheck(rgbInstance, nirFrame, null, livenessDetectionOption, trackInfos,
                            new LivenessCallback() {
                                @Override
                                public void onLivenessResult(LivenessResult livenessResult) {
                                    if (!livenessResult.mIsLive) {
                                        Logger.i(TAG, String.format("liveness rgb %f nir %f depth %f",
                                                livenessResult.livenessScore,
                                                livenessResult.nirlivenessScore,
                                                livenessResult.depthlivenessScore));
                                        SaveCasePicUtil.saveCasePic(rgbInstance, livenessDetectionOption.mBusinessType,
                                                "bad_liveness");
                                        livenessCallback.onLivenessResult(livenessResult);
                                        rgbInstance.destory();
                                        return;
                                    }

                                    // 获取特征值
                                    getLivePhoneFeature(rgbInstance, livenessResult);
                                    if (!livenessResult.mfeatureStatus) {
                                        Logger.i(TAG, "feature get error!");
                                        livenessCallback.onLivenessResult(livenessResult);
                                        rgbInstance.destory();
                                        return;
                                    }
                                    // 获取扣图和原图
                                    getCropImage(rgbInstance, livenessResult, livenessCallback);

                                    // 人脸库检索
//                                        getSearchPerson(livenessDetectionOption.recognizeOption,
//                                                livenessResult);

                                    if (!livenessResult.mRecognizeStatue) {
                                        Logger.i(TAG, String.format("recognize score low threshold %f",
                                                livenessDetectionOption.recognizeOption.threshold));
                                        SaveCasePicUtil.saveCasePic(rgbInstance,
                                                livenessDetectionOption.mBusinessType, "bad_recognize");
                                    }
                                    // 回调返回结果
                                    livenessCallback.onLivenessResult(livenessResult);
                                    rgbInstance.destory();
                                }

                                @Override
                                public void onDetectionError(DetectionErrorType detectionErrorType) {
                                    livenessCallback.onDetectionError(detectionErrorType);
                                }
                            });
                });
            }
        });
    }


    /**
     * 驾驶员行为分析接口
     * <p>
     * 使用场景:获取图象中人脸的驾驶员相关属性：抽烟、进食、饮水、使用手机、注意力、安全带等
     *
     * @param rgbFrame
     * @param nirFrame
     * @param driveOption
     * @param faceTrackCallback
     * @param driveCallback
     */
    public void startDrive(final ImageFrame rgbFrame,
                           final ImageFrame nirFrame,
                           DriveOption driveOption,
                           FaceTrackCallback faceTrackCallback,
                           DriveCallback driveCallback) {
        if (!mFaceSDKInit.isCommonSdkInit()) {
            Logger.i(TAG, "init interface fail ");
            return;
        }
        if (rgbFrame == null || driveCallback == null || driveOption == null) {
            Logger.i(TAG, "illegal param");
            return;
        }
        // 如果最小检测人脸发现变化，重新配置到c++
        if (config != null && config.minFaceSize != driveOption.minFaceSize) {
            config.minFaceSize = driveOption.minFaceSize;
            config.isAttribute = true;
            mFaceDetect.loadConfig(config);
        }
        final BDFaceImageInstance rgbInstance = FaceImageTool.convertYuvImage(rgbFrame);

        final FaceInfo[] faceInfo = new FaceInfo[1];
        FaceInfo[] trackResult = onTrackCheck(rgbInstance, driveOption.livenessDetectionOption);
        if (trackResult == null) {
            SaveCasePicUtil.saveCasePic(rgbInstance,
                    driveOption.livenessDetectionOption.mBusinessType, "bad_track");
            Logger.i(TAG, "track face is empty!");
            // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
            rgbInstance.destory();
            faceTrackCallback.onTrackResult(null);
            return;
        }
        if (driveOption.livenessDetectionOption.mNeedFaceTracking) {
            if (trackResult != null) {
                faceTrackCallback.onTrackResult(FaceModelTool.getTrackResult(rgbFrame, trackResult));
            }
        }
        boolean liveRst = onLivenessCheck(rgbInstance, nirFrame, null, driveOption.livenessDetectionOption,
                trackResult, new LivenessCallback() {
                    @Override
                    public void onLivenessResult(LivenessResult livenessResult) {
                        faceInfo[0] = livenessResult.faceInfo;

                        DriveResult driveResult = new DriveResult();
                        driveResult.bdFaceDriverMonitorInfo =
                                mFaceDriverMonitor.driverMonitor(rgbInstance, faceInfo[0]);
                        if (driveResult.bdFaceDriverMonitorInfo == null) {
                            driveCallback.onDetectionError(DetectionErrorType.Face_COVER);
                            rgbInstance.destory();
                            return;
                        }

                        driveResult.bdFaceGazeInfo = mFaceGaze.gaze(rgbInstance, faceInfo[0].landmarks);

                        driveCallback.onSuccess(driveResult);
                        rgbInstance.destory();
                    }

                    @Override
                    public void onDetectionError(DetectionErrorType detectionErrorType) {
                        driveCallback.onDetectionError(detectionErrorType);
                        rgbInstance.destory();
                    }
                });

        if (!liveRst) {
            return;
        }
    }


    /**
     * 属性识别接口
     *
     * @param imageFrame
     * @param attributeOption
     * @param attributeCallback
     */
    public void startAttribute(ImageFrame imageFrame,
                               AttributeOption attributeOption,
                               AttributeCallback attributeCallback) {
        if (future != null && !future.isDone()) {
            return;
        }
        future = es.submit(new Runnable() {
            @Override
            public void run() {
                boolean lastIsAttribute = config.isAttribute;
                if (!mFaceSDKInit.isCommonSdkInit()) {
                    Logger.i(TAG, "init interface fail ");
                    return;
                }
                if (attributeOption == null || attributeCallback == null) {
                    Logger.i(TAG, "illegal param");
                    return;
                }
                // 如果最小检测人脸发现变化，重新配置到c++
                if (config != null && config.minFaceSize != attributeOption.minFaceSize) {
                    config.minFaceSize = attributeOption.minFaceSize;
                    mFaceDetect.loadConfig(config);
                }
                BDFaceDetectListConf rgbDetectListConf = FaceModelTool.getRgbDetectListConf();
                rgbDetectListConf.usingAttribute = true;
                rgbDetectListConf.usingAlign = true;
                rgbDetectListConf.usingDetect = true;

                BDFaceImageInstance rgbInstance = FaceImageTool.convertYuvImage(imageFrame);
                FaceInfo[] faceInfos = mFaceDetect.detect(BDFaceSDKCommon.DetectType.DETECT_VIS,
                        BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE, rgbInstance,
                        null, rgbDetectListConf);

                if (faceInfos == null || faceInfos.length == 0) {
                    rgbInstance.destory();
                    attributeCallback.onDetectionError(DetectionErrorType.NO_FACE);
                    return;
                }


                AttributeResult attributeResult = new AttributeResult();
                attributeResult.age = faceInfos[0].age;
                attributeResult.gender = faceInfos[0].gender;
                attributeResult.wearGlass = faceInfos[0].glasses;
                attributeResult.faceInfo = faceInfos[0];

                if (attributeOption.needDetectMask) {
                    float[] scores = mFaceMouthMask.checkMask(rgbInstance, faceInfos);
                    if (scores != null && scores.length > 0) {
                        attributeResult.faceMouthMaskScore = scores[0];
                    }
                }

                attributeCallback.onLivenessResult(attributeResult);

                if (config != null) {
                    config.isAttribute = lastIsAttribute;
                }
                rgbInstance.destory();
            }
        });
    }

    /**
     * 获取SDK 信息接口
     *
     * @return
     */
    public SdkInfo getSdkInfo() {
        return null;
    }


    /**
     * 释放接口
     */
    public void release() {
        mFaceSDKInit.setCommonSdkInit(false);
        mFaceSDKInit = null;
    }

    /**
     * 情况内存中加载的id 和  feature
     * 使用场景：数据库删除
     *
     * @return
     */
    private int clearPerson() {
        int result = -1;
        return result;
    }

    /**
     * 初始化模型
     *
     * @param context      当前上下文
     * @param initCallback 结果回调
     */
    private void initModel(final Context context, final InitCallback initCallback) {

        if (context != null && initCallback != null) {
            if (mFaceDetect == null) {
                mFaceDetect = new FaceDetect();
            }
            if (mFaceLive == null) {
                mFaceLive = new FaceLive();
            }
            if (mFaceMouthMask == null) {
                mFaceMouthMask = new FaceMouthMask();
            }
            if (mFaceFeature == null) {
                mFaceFeature = new FaceFeature();
            }
            if (mFaceGaze == null) {
                mFaceGaze = new FaceGaze();
            }
            if (mFaceDetectNir == null) {
                BDFaceInstance irBdFaceInstance = new BDFaceInstance();
                irBdFaceInstance.creatInstance();
                mFaceDetectNir = new FaceDetect(irBdFaceInstance);
            }
            if (mFaceCrop == null) {
                mFaceCrop = new FaceCrop();
            }
            if (mFaceDriverMonitor == null) {
                mFaceDriverMonitor = new FaceDriverMonitor();
            }

            final long startTime = System.currentTimeMillis();

            config = new BDFaceSDKConfig();
            config.minFaceSize = 0;
            config.maxDetectNum = 1;
            config.detectInterval = 0;
            config.trackInterval = 1500;
            // 如果开启质量检测
            if (SDKConfig.faceQuality().isOpenQuality()) {
                config.isCheckBlur = true;
                config.isIllumination = true;
                // 判断是否开启遮挡检测
                if (SDKConfig.registOcc().isOpenOcclusion()) {
                    config.isOcclusion = true;
                } else {
                    config.isOcclusion = false;
                }
            }
            config.isHeadPose = true;
            mFaceDetect.loadConfig(config);
            // 检测和对齐

            mFaceCrop.initFaceCrop(new Callback() {
                @Override
                public void onResponse(int code, String response) {
                    Logger.i(TAG, "detect code = " + code + ", msg = " + response);
                    if (code == 0) {
                        mFaceSDKInit.setCropInitSuccess(true);
                    } else {
                        mFaceSDKInit.setCropInitSuccess(false);
                    }
                }
            });

            mFaceGaze.initModel(context, SDKConstant.GAZE_MODEL, new Callback() {
                @Override
                public void onResponse(int code, String response) {
                    if (code == 0) {
                        mFaceSDKInit.setFaceGazeInitSuccess(true);
                    } else {
                        mFaceSDKInit.setFaceGazeInitSuccess(false);
                    }
                }
            });

            mFaceDetect.initModel(context,
                    SDKConstant.DETECT_VIS_MODEL,
                    SDKConstant.ALIGN_TRACK_MODEL,
                    BDFaceSDKCommon.DetectType.DETECT_VIS,
                    BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_FAST,
                    new Callback() {
                        @Override
                        public void onResponse(int code, String response) {
                            Logger.i(TAG, "detect code = " + code + ", msg = " + response);
                            if (code == 0) {
                                mFaceSDKInit.setDetectFastInitSuccess(true);
                            } else {
                                mFaceSDKInit.setDetectFastInitSuccess(false);
                            }
                        }
                    });

            mFaceDetect.initAttrEmo(context, SDKConstant.ATTRIBUTE_MODEL, SDKConstant.EMOTION_MODEL, new Callback() {
                @Override
                public void onResponse(int code, String response) {
                    if (code == 0) {
                        mFaceSDKInit.setDetectInitSuccess(true);
                    } else {
                        mFaceSDKInit.setDetectInitSuccess(false);
                    }
                }
            });

            mFaceDetect.initModel(context,
                    SDKConstant.DETECT_VIS_MODEL,
                    SDKConstant.ALIGN_RGB_MODEL,
                    BDFaceSDKCommon.DetectType.DETECT_VIS,
                    BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE,
                    new Callback() {
                        @Override
                        public void onResponse(final int code, final String response) {
                            Logger.i(TAG, "detect code = " + code + ", msg = " + response);
                            if (code == 0) {
                                mFaceSDKInit.setDetectInitSuccess(true);
                            } else {
                                mFaceSDKInit.setDetectInitSuccess(false);
                            }
                        }
                    });

            mFaceDetectNir.initModel(context,
                    SDKConstant.DETECT_NIR_MODE,
                    SDKConstant.ALIGN_NIR_MODEL,
                    BDFaceSDKCommon.DetectType.DETECT_NIR,
                    BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_NIR_ACCURATE, new Callback() {
                        @Override
                        public void onResponse(final int code, final String response) {
                            mFaceSDKInit.setDetectNirInitSuccess(code == 0);
                        }
                    }
            );

            mFaceDriverMonitor.initDriverMonitor(context,
                    SDKConstant.DRIVEMONITOR_MODEL, new Callback() {
                        @Override
                        public void onResponse(int code, String response) {
                            mFaceSDKInit.setDriverMonitorInitSuccess(code == 0);
                        }
                    });

            // 质量检测
            mFaceDetect.initQuality(context,
                    SDKConstant.BLUR_MODEL,
                    SDKConstant.OCCLUSION_MODEL,
                    new Callback() {
                        @Override
                        public void onResponse(final int code, final String response) {
                            Logger.i(TAG, "quality code = " + code + ", msg = " + response);
                            if (code == 0) {
                                mFaceSDKInit.setQualityInitSuccess(true);
                            }
                        }
                    });

            // 最优人脸检测
            mFaceDetect.initBestImage(context, SDKConstant.BEST_IMAGE, new Callback() {
                @Override
                public void onResponse(int code, String response) {
                    if (code == 0) {
                        mFaceSDKInit.setBestImageInitSuccess(true);
                    }
                }
            });

            mFaceMouthMask.initModel(context, SDKConstant.MOUTH_MASK, new Callback() {
                @Override
                public void onResponse(int code, String response) {
                    if (code == 0) {
                        mFaceSDKInit.setFaceMouthMaskInitSuccess(true);
                    }
                }
            });

            if (mFaceDetect2 == null) {
                BDFaceInstance instance = new BDFaceInstance();
                instance.creatInstance();
                mFaceDetect2 = new FaceDetect(instance);
            }

            mFaceDetect2.initModel(context,
                    SDKConstant.DETECT_VIS_MODEL,
                    SDKConstant.ALIGN_RGB_MODEL,
                    BDFaceSDKCommon.DetectType.DETECT_VIS,
                    BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE,
                    new Callback() {
                        @Override
                        public void onResponse(final int code, final String response) {
                            Logger.i(TAG, "detect code = " + code + ", msg = " + response);
                            if (code == 0) {
                                mFaceSDKInit.setDetectInitSuccess(true);
                            } else {
                                mFaceSDKInit.setDetectInitSuccess(false);
                            }
                        }
                    });

            // 活体检测
            mFaceLive.initModel(context,
                    SDKConstant.LIVE_VIS_MODEL,
                    SDKConstant.LIVE_NIR_MODEL,
                    "",
                    new Callback() {
                        @Override
                        public void onResponse(int code, String response) {
                            Logger.i(TAG, "liveness code = " + code + ", msg = " + response);
                            if (code == 0) {
                                mFaceSDKInit.setLivenessInitSuccess(true);
                            }
                        }
                    });

            // 特征提取
            mFaceFeature.initModel(context,
                    SDKConstant.RECOGNIZE_IDPHOTO_MODEL,
                    SDKConstant.RECOGNIZE_VIS_MODEL,
                    "",
                    new Callback() {
                        @Override
                        public void onResponse(int code, String response) {
                            Logger.i(TAG, "feature code = " + code + ", msg = " + response);
                            if (code == 0
                                    && mFaceSDKInit.isDetectFastInitSuccess()
                                    && mFaceSDKInit.isDetectInitSuccess()
                                    && mFaceSDKInit.isDetectNirInitSuccess()
                                    && mFaceSDKInit.isQualityInitSuccess()
                                    && mFaceSDKInit.isLivenessInitSuccess()
                                    && mFaceSDKInit.isCropInitSuccess()
                                    && mFaceSDKInit.isBestImageInitSuccess()
                                    && mFaceSDKInit.isFaceMouthMaskInitSuccess()
                                    && mFaceSDKInit.isFaceGazeInitSuccess()
                            ) {
                                Logger.i(TAG, "init model = " + (System.currentTimeMillis() - startTime));
                                mFaceSDKInit.setCommonSdkInit(true);
                                initCallback.onSucces(0, "initSuccess");
                            } else {
                                initCallback.onError(-1, "initFailure");
                            }
                        }
                    });
        }
    }

    private FaceInfo[] onTrackCheck(BDFaceImageInstance rgbInstance, LivenessDetectionOption livenessDetectionOption) {
        // 如果最小检测人脸发现变化，重新配置到c++
        if (config != null && config.minFaceSize != livenessDetectionOption.mValidMinFaceSize) {
            config.minFaceSize = livenessDetectionOption.mValidMinFaceSize;
            mFaceDetect.loadConfig(config);
        }

        // 快速检测获取人脸信息，仅用于绘制人脸框，详细人脸数据后续获取
        FaceInfo[] faceInfos = mFaceDetect
                .track(BDFaceSDKCommon.DetectType.DETECT_VIS,
                        BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_FAST, rgbInstance);
        return faceInfos;
    }

    private boolean onLivenessCheck(BDFaceImageInstance rgbInstance,
                                    ImageFrame nirFrame,
                                    ImageFrame depthFrame,
                                    LivenessDetectionOption livenessDetectionOption,
                                    FaceInfo[] fastFaceInfos,
                                    LivenessCallback livenessCallback) {

        // 通过facebox 进行人脸检测细粒度检测，
        FaceInfo[] faceInfos = mFaceDetect2.detect(BDFaceSDKCommon.DetectType.DETECT_VIS,
                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE, rgbInstance,
                fastFaceInfos, FaceModelTool.getRgbDetectListConf());

        // 人脸数据数据判断
        if (faceInfos == null || faceInfos.length == 0) {
            Logger.i(TAG, "detect face is empty!");
            // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
            rgbInstance.destory();
            if (livenessCallback != null) {
                livenessCallback.onDetectionError(DetectionErrorType.NO_FACE);
            }
            return false;
        }

        // track追踪id赋值检测id
        faceInfos[0].faceID = fastFaceInfos[0].faceID;

        // 最优人脸控制
        if (!onBestImageCheck(faceInfos[0])) {
            rgbInstance.destory();
            if (livenessCallback != null) {
                livenessCallback.onDetectionError(INVALID_FRAME_DATA);
            }
            return false;
        }

        // 质量检测未通过,销毁BDFaceImageInstance，结束函数
        if (!onQualityCheck(livenessDetectionOption, faceInfos[0])) {
            rgbInstance.destory();
            if (livenessCallback != null) {
                livenessCallback.onDetectionError(QUALITY_SCORE_REJECT);
            }
            return false;
        }

        LivenessResult result = FaceModelTool.getLivenessResult(faceInfos);

        // 如果不需要做活体检测，活体结果设置为true，使⽤用场景: 设置关闭活体检测直接识别
        if (livenessDetectionOption.mNeedLivenessDetection) {
            // rgb 活体检测
            FaceInfo faceInfo = faceInfos[0];
            float rgbScore = mFaceLive.silentLive(
                    BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_RGB,
                    rgbInstance, faceInfo.landmarks);
            result.livenessScore = rgbScore;

            // 红外检测
            if (livenessDetectionOption.nirOption.mNeedLivenessDetection && nirFrame != null) {
                // nir 活体检测
                BDFaceImageInstance nirInstance = FaceImageTool.convertYuvImage(nirFrame);
                FaceInfo[] nirFaceInfos = mFaceDetectNir.detect(BDFaceSDKCommon.DetectType.DETECT_NIR,
                        BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_NIR_ACCURATE,
                        nirInstance, null, FaceModelTool.getNirDetectListConf());

                // 人脸数据数据判断
                if (nirFaceInfos == null || nirFaceInfos.length == 0) {
                    Logger.i(TAG, "detect nir face is empty!");
                    // 此处不return
                } else {
                    FaceInfo nirFaceInfo = nirFaceInfos[0];
                    float nirScore = mFaceLive.silentLive(
                            BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_NIR,
                            nirInstance, nirFaceInfo.landmarks);
                    // 增加红外原始图获取，耗时
                    result.nirOriginBmp = BitmapUtils.getInstaceBmp(nirInstance.getImage());
                    result.nirlivenessScore = nirScore;
                    result.nirFaceInfo = nirFaceInfo;
                }
                // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                nirInstance.destory();
            }

            // 深度检测 TODO 待开发
            if (livenessDetectionOption.depthOption.mNeedLivenessDetection && depthFrame != null) {
                result.depthlivenessScore = -1f;
            }

            // 如果不需要红外活体检测，判断 livenessScore 大于 mThreshold
            result.mIsLive = result.livenessScore >= livenessDetectionOption.mThreshold;

            // 如果需要红外活体检测，判断 nirlivenessScore 大于 mThreshold
            if (livenessDetectionOption.nirOption.mNeedLivenessDetection) {
                result.mIsLive = result.nirlivenessScore >= livenessDetectionOption.nirOption.mThreshold;
            }

            // 如果需要深度活体检测，判断 depthlivenessScore 大于 mThreshold
            if (livenessDetectionOption.depthOption.mNeedLivenessDetection) {
                result.mIsLive = result.depthlivenessScore >= livenessDetectionOption.depthOption.mThreshold;
            }

        } else {
            result.mIsLive = true;
        }
        if (livenessCallback != null) {
            livenessCallback.onLivenessResult(result);
        }
        return true;
    }

    private void getLivePhoneFeature(BDFaceImageInstance rgbInstance, LivenessResult livenessResult) {
        byte[] featureArr = new byte[512];
        float featureSize = mFaceFeature.feature(
                BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO,
                rgbInstance, livenessResult.faceInfo.landmarks, featureArr);

        if (featureSize != -1) {
            Feature feature = new Feature();
            feature.setFeature(featureArr);
            livenessResult.mfeatureStatus = true;
            livenessResult.feature = feature;
        }
    }

    private void getIDPhoneFeature(BDFaceImageInstance rgbInstance, LivenessResult livenessResult) {
        byte[] featureArr = new byte[512];
        float featureSize = mFaceFeature.feature(
                BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_ID_PHOTO,
                rgbInstance, livenessResult.faceInfo.landmarks, featureArr);

        if (featureSize != -1) {
            Feature feature = new Feature();
            feature.setFeature(featureArr);
            livenessResult.mfeatureStatus = true;
            livenessResult.feature = feature;
        }
    }

    private void getCropImage(BDFaceImageInstance rgbInstance,
                              LivenessResult livenessResult,
                              LivenessCallback livenessCallback) {
        BDFaceCropParam cropParam = new BDFaceCropParam();
        cropParam.foreheadExtend = 2.0f / 9;
        cropParam.chinExtend = 1.0f / 9;
        cropParam.enlargeRatio = 1.5f;
        cropParam.height = 640;
        cropParam.width = 480;
        BDFaceImageInstance cropInstance = mFaceCrop
                .cropFaceByLandmarkParam(rgbInstance, livenessResult.faceInfo.landmarks, cropParam);
        if (cropInstance == null) {
            Logger.i(TAG, "face crop reject!");
//            livenessCallback.onDetectionError(FACE_CROP_REJECT);
            return;
        }

        livenessResult.avatarBmp = BitmapUtils.getInstaceBmp(cropInstance);
        livenessResult.originBmp = BitmapUtils.getInstaceBmp(rgbInstance.getImage());
    }

    /**
     * 质量检测结果过滤，如果需要质量检测，
     * 需要调用 SingleBaseConfig.getBaseConfig().setQualityControl(true);设置为true，
     * 再调用  FaceSDKManager.getInstance().initConfig() 加载到底层配置项中
     *
     * @param livenessDetectionOption
     * @param faceInfo
     * @return
     */
    private boolean onQualityCheck(
            LivenessDetectionOption livenessDetectionOption,
            FaceInfo faceInfo) {
        if (!SDKConfig.faceQuality().isOpenQuality()) {
            return true;
        }
        if (faceInfo != null) {
            // 角度过滤
            if (Math.abs(faceInfo.yaw) > livenessDetectionOption.mValidYaw) {
                return false;
            } else if (Math.abs(faceInfo.roll) > livenessDetectionOption.mValidRoll) {
                return false;
            } else if (Math.abs(faceInfo.pitch) > livenessDetectionOption.mValidPitch) {
                return false;
            }
            // 模糊结果过滤
            if (faceInfo.bluriness > SDKConfig.faceQuality().getBluriness()) {
                return false;
            }
            // 光照结果过滤
            if (faceInfo.illum < SDKConfig.faceQuality().getIllum()) {
                return false;
            }
            // 遮挡结果过滤
            if (!SDKConfig.registOcc().isOpenOcclusion()) {
                return true;
            }

            if (faceInfo.occlusion != null) {
                BDFaceOcclusion occlusion = faceInfo.occlusion;
                if (occlusion.leftEye > SDKConfig.registOcc().toArrays()[0]) {
                    // 左眼遮挡置信度
                    return false;
                } else if (occlusion.rightEye > SDKConfig.registOcc().toArrays()[1]) {
                    // 右眼遮挡置信度
                    return false;
                } else if (occlusion.nose > SDKConfig.registOcc().toArrays()[2]) {
                    // 鼻子遮挡置信度
                    return false;
                } else if (occlusion.mouth > SDKConfig.registOcc().toArrays()[3]) {
                    // 嘴巴遮挡置信度
                    return false;
                } else if (occlusion.leftCheek > SDKConfig.registOcc().toArrays()[4]) {
                    // 左脸遮挡置信度
                    return false;
                } else if (occlusion.rightCheek > SDKConfig.registOcc().toArrays()[5]) {
                    // 右脸遮挡置信度
                    return false;
                } else if (occlusion.chin > SDKConfig.registOcc().toArrays()[6]) {
                    // 下巴遮挡置信度
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 最优人脸控制
     *
     * @param faceInfo
     * @return
     */
    public boolean onBestImageCheck(FaceInfo faceInfo) {
        if (!SDKConfig.faceBestImage().isOpenBestImage()) {
            return true;
        }
        if (faceInfo != null) {
            float bestImageScore = faceInfo.bestImageScore;
            if (bestImageScore < SDKConfig.faceBestImage().getBestImageThreshold()) {
                return false;
            }
        }
        return true;
    }


}
