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
     * ????????????????????????????????????????????????????????????????????????????????????;
     * <p>
     * ????????????: ????????????????????????(???????????????????????????????????????????????????????????????????????????????????????),??????????????????????????????????????????????????????????????????
     * ????????????: 15s
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
     * ????????????????????????
     * <p>
     * ???????????????????????????????????????->??????->????????????????????????????????????????????????????????????????????????
     * ???????????????????????????????????????????????????????????????????????????????????????????????????
     * ???????????????:???????????????????????????????????????????????????????????????????????????????????????
     * <p>
     * ???:???????????????????????????????????????????????????????????????????????????????????????????????????????????????Callback;??????????????????????????????????????????????????????????????????????????? ??????????????????
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

        // ??????????????????????????????????????????????????????c++
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

                // ????????????????????????????????????onTrackCheck ??????
                trackResult = onTrackCheck(rgbInstance, livenessDetectionOption);
                if (trackResult == null) {
                    SaveCasePicUtil.saveCasePic(rgbInstance,
                            livenessDetectionOption.mBusinessType, "bad_track");
                    Logger.i(TAG, "track face is empty!");
                    // ???????????????????????????????????????????????????????????????????????????
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

                // ????????????????????????????????????????????????????????????????????????: ??????????????????
                if (!livenessDetectionOption.mNeedLivenessDetection) {
                    Logger.i(TAG, "identification option is false!");
                    // ???????????????????????????????????????????????????????????????????????????
                    rgbInstance.destory();
                    return;
                }

                if (future2 != null && !future2.isDone()) {
                    // ???????????????????????????????????????????????????????????????????????????
                    rgbInstance.destory();
                    return;
                }

                // ??????????????????????????????Runnable ??????
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
     * 1:1??????????????????
     * <p>
     * ???????????????????????????????????????????????? ??????????????????????????????????????? ???
     * ???????????????????????????????????????->??????->??????->1:1
     * ???????????????????????????????????????????????????????????????????????????????????????????????????
     * ???????????????:???????????????????????????????????????????????????????????????????????????????????????
     *
     * @param firstImageFrame         ?????????
     * @param bm                      ?????????
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

        // ??????????????????????????????????????????????????????c++
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
     * ????????????
     * ???????????????????????????????????????????????????????????????ID????????????????????????????????????????????????????????????
     * ???????????????:???????????????????????????????????????????????????????????????????????????????????????
     * <p>
     * ???:???????????????????????????????????????????????????????????????????????????????????????????????????????????????Callback?????????????????????????????????????????????????????????????????????????????? ??????????????????
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

                // ????????????????????????????????????onTrackCheck ??????
                trackResult = onTrackCheck(rgbInstance, livenessDetectionOption);
                if (trackResult == null) {
                    SaveCasePicUtil.saveCasePic(rgbInstance,
                            livenessDetectionOption.mBusinessType, "bad_track");
                    Logger.i(TAG, "track face is empty!");
                    // ???????????????????????????????????????????????????????????????????????????
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

                // ????????????????????????????????????????????????????????????????????????: ????????????
                if (!livenessDetectionOption.mNeedIdentification) {
                    Logger.i(TAG, "identification option is false!");
                    // ???????????????????????????????????????????????????????????????????????????
                    rgbInstance.destory();
                    return;
                }

                if (future2 != null && !future2.isDone()) {
                    // ???????????????????????????????????????????????????????????????????????????
                    rgbInstance.destory();
                    return;
                }

                // ??????????????????????????????Runnable ??????
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

                                    // ???????????????
                                    getLivePhoneFeature(rgbInstance, livenessResult);
                                    if (!livenessResult.mfeatureStatus) {
                                        Logger.i(TAG, "feature get error!");
                                        livenessCallback.onLivenessResult(livenessResult);
                                        rgbInstance.destory();
                                        return;
                                    }
                                    // ?????????????????????
                                    getCropImage(rgbInstance, livenessResult, livenessCallback);

                                    // ???????????????
//                                        getSearchPerson(livenessDetectionOption.recognizeOption,
//                                                livenessResult);

                                    if (!livenessResult.mRecognizeStatue) {
                                        Logger.i(TAG, String.format("recognize score low threshold %f",
                                                livenessDetectionOption.recognizeOption.threshold));
                                        SaveCasePicUtil.saveCasePic(rgbInstance,
                                                livenessDetectionOption.mBusinessType, "bad_recognize");
                                    }
                                    // ??????????????????
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
     * ???????????????????????????
     * <p>
     * ????????????:??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
        // ??????????????????????????????????????????????????????c++
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
            // ???????????????????????????????????????????????????????????????????????????
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
     * ??????????????????
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
                // ??????????????????????????????????????????????????????c++
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
     * ??????SDK ????????????
     *
     * @return
     */
    public SdkInfo getSdkInfo() {
        return null;
    }


    /**
     * ????????????
     */
    public void release() {
        mFaceSDKInit.setCommonSdkInit(false);
        mFaceSDKInit = null;
    }

    /**
     * ????????????????????????id ???  feature
     * ??????????????????????????????
     *
     * @return
     */
    private int clearPerson() {
        int result = -1;
        return result;
    }

    /**
     * ???????????????
     *
     * @param context      ???????????????
     * @param initCallback ????????????
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
            // ????????????????????????
            if (SDKConfig.faceQuality().isOpenQuality()) {
                config.isCheckBlur = true;
                config.isIllumination = true;
                // ??????????????????????????????
                if (SDKConfig.registOcc().isOpenOcclusion()) {
                    config.isOcclusion = true;
                } else {
                    config.isOcclusion = false;
                }
            }
            config.isHeadPose = true;
            mFaceDetect.loadConfig(config);
            // ???????????????

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

            // ????????????
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

            // ??????????????????
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

            // ????????????
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

            // ????????????
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
        // ??????????????????????????????????????????????????????c++
        if (config != null && config.minFaceSize != livenessDetectionOption.mValidMinFaceSize) {
            config.minFaceSize = livenessDetectionOption.mValidMinFaceSize;
            mFaceDetect.loadConfig(config);
        }

        // ??????????????????????????????????????????????????????????????????????????????????????????
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

        // ??????facebox ????????????????????????????????????
        FaceInfo[] faceInfos = mFaceDetect2.detect(BDFaceSDKCommon.DetectType.DETECT_VIS,
                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE, rgbInstance,
                fastFaceInfos, FaceModelTool.getRgbDetectListConf());

        // ????????????????????????
        if (faceInfos == null || faceInfos.length == 0) {
            Logger.i(TAG, "detect face is empty!");
            // ???????????????????????????????????????????????????????????????????????????
            rgbInstance.destory();
            if (livenessCallback != null) {
                livenessCallback.onDetectionError(DetectionErrorType.NO_FACE);
            }
            return false;
        }

        // track??????id????????????id
        faceInfos[0].faceID = fastFaceInfos[0].faceID;

        // ??????????????????
        if (!onBestImageCheck(faceInfos[0])) {
            rgbInstance.destory();
            if (livenessCallback != null) {
                livenessCallback.onDetectionError(INVALID_FRAME_DATA);
            }
            return false;
        }

        // ?????????????????????,??????BDFaceImageInstance???????????????
        if (!onQualityCheck(livenessDetectionOption, faceInfos[0])) {
            rgbInstance.destory();
            if (livenessCallback != null) {
                livenessCallback.onDetectionError(QUALITY_SCORE_REJECT);
            }
            return false;
        }

        LivenessResult result = FaceModelTool.getLivenessResult(faceInfos);

        // ??????????????????????????????????????????????????????true??????????????????: ????????????????????????????????????
        if (livenessDetectionOption.mNeedLivenessDetection) {
            // rgb ????????????
            FaceInfo faceInfo = faceInfos[0];
            float rgbScore = mFaceLive.silentLive(
                    BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_RGB,
                    rgbInstance, faceInfo.landmarks);
            result.livenessScore = rgbScore;

            // ????????????
            if (livenessDetectionOption.nirOption.mNeedLivenessDetection && nirFrame != null) {
                // nir ????????????
                BDFaceImageInstance nirInstance = FaceImageTool.convertYuvImage(nirFrame);
                FaceInfo[] nirFaceInfos = mFaceDetectNir.detect(BDFaceSDKCommon.DetectType.DETECT_NIR,
                        BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_NIR_ACCURATE,
                        nirInstance, null, FaceModelTool.getNirDetectListConf());

                // ????????????????????????
                if (nirFaceInfos == null || nirFaceInfos.length == 0) {
                    Logger.i(TAG, "detect nir face is empty!");
                    // ?????????return
                } else {
                    FaceInfo nirFaceInfo = nirFaceInfos[0];
                    float nirScore = mFaceLive.silentLive(
                            BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_NIR,
                            nirInstance, nirFaceInfo.landmarks);
                    // ????????????????????????????????????
                    result.nirOriginBmp = BitmapUtils.getInstaceBmp(nirInstance.getImage());
                    result.nirlivenessScore = nirScore;
                    result.nirFaceInfo = nirFaceInfo;
                }
                // ???????????????????????????????????????????????????????????????????????????
                nirInstance.destory();
            }

            // ???????????? TODO ?????????
            if (livenessDetectionOption.depthOption.mNeedLivenessDetection && depthFrame != null) {
                result.depthlivenessScore = -1f;
            }

            // ?????????????????????????????????????????? livenessScore ?????? mThreshold
            result.mIsLive = result.livenessScore >= livenessDetectionOption.mThreshold;

            // ??????????????????????????????????????? nirlivenessScore ?????? mThreshold
            if (livenessDetectionOption.nirOption.mNeedLivenessDetection) {
                result.mIsLive = result.nirlivenessScore >= livenessDetectionOption.nirOption.mThreshold;
            }

            // ??????????????????????????????????????? depthlivenessScore ?????? mThreshold
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
     * ??????????????????????????????????????????????????????
     * ???????????? SingleBaseConfig.getBaseConfig().setQualityControl(true);?????????true???
     * ?????????  FaceSDKManager.getInstance().initConfig() ???????????????????????????
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
            // ????????????
            if (Math.abs(faceInfo.yaw) > livenessDetectionOption.mValidYaw) {
                return false;
            } else if (Math.abs(faceInfo.roll) > livenessDetectionOption.mValidRoll) {
                return false;
            } else if (Math.abs(faceInfo.pitch) > livenessDetectionOption.mValidPitch) {
                return false;
            }
            // ??????????????????
            if (faceInfo.bluriness > SDKConfig.faceQuality().getBluriness()) {
                return false;
            }
            // ??????????????????
            if (faceInfo.illum < SDKConfig.faceQuality().getIllum()) {
                return false;
            }
            // ??????????????????
            if (!SDKConfig.registOcc().isOpenOcclusion()) {
                return true;
            }

            if (faceInfo.occlusion != null) {
                BDFaceOcclusion occlusion = faceInfo.occlusion;
                if (occlusion.leftEye > SDKConfig.registOcc().toArrays()[0]) {
                    // ?????????????????????
                    return false;
                } else if (occlusion.rightEye > SDKConfig.registOcc().toArrays()[1]) {
                    // ?????????????????????
                    return false;
                } else if (occlusion.nose > SDKConfig.registOcc().toArrays()[2]) {
                    // ?????????????????????
                    return false;
                } else if (occlusion.mouth > SDKConfig.registOcc().toArrays()[3]) {
                    // ?????????????????????
                    return false;
                } else if (occlusion.leftCheek > SDKConfig.registOcc().toArrays()[4]) {
                    // ?????????????????????
                    return false;
                } else if (occlusion.rightCheek > SDKConfig.registOcc().toArrays()[5]) {
                    // ?????????????????????
                    return false;
                } else if (occlusion.chin > SDKConfig.registOcc().toArrays()[6]) {
                    // ?????????????????????
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ??????????????????
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
