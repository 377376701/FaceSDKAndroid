package com.baidu.idl.face.main.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.baidu.facesdklibrary.FaceIDSDK;
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
import com.baidu.facesdklibrary.model.ImageFrame;
import com.baidu.facesdklibrary.model.InitOption;
import com.baidu.facesdklibrary.model.LivenessDetectionOption;
import com.baidu.facesdklibrary.model.LivenessResult;
import com.baidu.facesdklibrary.model.TrackResult;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class SdTest {

    public static void testAttribute() throws Exception {
        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();
        BufferedInputStream bis =
                new BufferedInputStream(new FileInputStream(new File("/sdcard/tmp/mn.nv21")));

        Bitmap cnBm = BitmapFactory.decodeFile("/sdcard/tmp/114.jpg");
        byte[] cnBytes = ImageUtil.bitmapToNv21(cnBm, cnBm.getWidth(), cnBm.getHeight());
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = bis.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }

        ImageFrame imageFrame = new ImageFrame();
        imageFrame.imageData = cnBytes;
        imageFrame.width = cnBm.getWidth();
        imageFrame.height = cnBm.getHeight();
        imageFrame.angle = 0;
        imageFrame.isMirror = 0;
        LivenessDetectionOption livenessDetectionOption = new LivenessDetectionOption();
//        livenessDetectionOption.nirOption.mNeedLivenessDetection=true;
        livenessDetectionOption.mValidMinFaceSize = 0;
        livenessDetectionOption.mNeedFaceTracking = false;
        livenessDetectionOption.mNeedLivenessDetection = true;
        livenessDetectionOption.mNeedIdentification = true;
        livenessDetectionOption.mThreshold = 0.3f;

        AttributeOption attributeOption = new AttributeOption();
        attributeOption.minFaceSize = 0;

        FaceIDSDK.shareIns().startAttribute(imageFrame, attributeOption,
                new AttributeCallback() {
                    @Override
                    public void onLivenessResult(AttributeResult attributeResult) {
                        Log.e("huwwds", "== attributeResult" + attributeResult);
                    }

                    @Override
                    public void onDetectionError(DetectionErrorType detectionErrorType) {
                        Log.e("huwwds", "== detectionErrorType" + detectionErrorType);
                    }
                }
        );
    }

    public static void testDrive() throws Exception {
        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();
        BufferedInputStream bis =
                new BufferedInputStream(new FileInputStream(new File("/sdcard/tmp/mn.nv21")));
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = bis.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }

        ImageFrame imageFrame = new ImageFrame();
        imageFrame.imageData = baos.toByteArray();
        imageFrame.width = 720;
        imageFrame.height = 1280;
        imageFrame.angle = 0;
        imageFrame.isMirror = 0;
        LivenessDetectionOption livenessDetectionOption = new LivenessDetectionOption();
        livenessDetectionOption.mValidMinFaceSize = 0;
        livenessDetectionOption.mNeedFaceTracking = false;
        livenessDetectionOption.mNeedLivenessDetection = true;
        livenessDetectionOption.mNeedIdentification = true;
        livenessDetectionOption.mThreshold = 0.3f;

        DriveOption driveOption = new DriveOption();
        driveOption.minFaceSize = 0;
        driveOption.livenessDetectionOption = livenessDetectionOption;
        FaceIDSDK.shareIns().startDrive(imageFrame, null, driveOption,
                new FaceTrackCallback() {
                    @Override
                    public void onTrackResult(TrackResult trackResult) {
                        Log.e("huwwds", "== onTrackResult" + trackResult);
                    }
                },
                new DriveCallback() {
                    @Override
                    public void onDetectionError(DetectionErrorType detectionErrorType) {
                        Log.e("huwwds", "== detectionErrorType" + detectionErrorType);

                    }

                    @Override
                    public void onSuccess(DriveResult driveResult) {
                        Log.e("huwwds", "== driveResult" + driveResult);
                    }
                });
    }

    public static void testVerification() throws Exception {
        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();
        BufferedInputStream bis =
                new BufferedInputStream(new FileInputStream(new File("/sdcard/tmp/ttt.nv21")));
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = bis.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        bis.close();

        ImageFrame imageFrame = new ImageFrame();
        imageFrame.imageData = baos.toByteArray();
        baos.close();
        imageFrame.width = 720;
        imageFrame.height = 1280;
        imageFrame.angle = 0;
        imageFrame.isMirror = 0;
        LivenessDetectionOption livenessDetectionOption = new LivenessDetectionOption();
        livenessDetectionOption.mValidMinFaceSize = 0;
        livenessDetectionOption.mNeedFaceTracking = false;
        livenessDetectionOption.mNeedLivenessDetection = true;
        livenessDetectionOption.mNeedIdentification = true;
        livenessDetectionOption.mThreshold = 0.3f;

        FaceIDSDK.shareIns().startVerification(imageFrame,
                BitmapFactory.decodeFile("/sdcard/tmp/mn.jpeg"), livenessDetectionOption,
                new FaceTrackCallback() {
                    @Override
                    public void onTrackResult(TrackResult trackResult) {
                        Log.e("huwwds", "== onTrackResult" + trackResult);
                    }
                }, new LivenessCallback() {
                    @Override
                    public void onLivenessResult(LivenessResult livenessResult) {
                        Log.e("huwwds", "== onLivenessResult" + livenessResult);
                    }

                    @Override
                    public void onDetectionError(DetectionErrorType detectionErrorType) {
                        Log.e("huwwds", "== detectionErrorType" + detectionErrorType);
                    }
                });
    }

    public static void testLivenessDetectionDetection() throws Exception {
        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();
        BufferedInputStream bis =
                new BufferedInputStream(new FileInputStream(new File("/sdcard/tmp/mn.nv21")));
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = bis.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }

        Bitmap firstBitmap = BitmapFactory.decodeFile("/sdcard/tmp/fff.jpg");
        Bitmap resizedBm = null;
        if ((firstBitmap.getWidth() & 3) != 0 || (firstBitmap.getHeight() & 3) != 0) {
            resizedBm = Bitmap.createBitmap(firstBitmap, 0, 0, firstBitmap.getWidth() / 2 * 2, firstBitmap.getHeight() / 2 * 2);

            if ((resizedBm.getWidth() & 2) != 0) {
                resizedBm = Bitmap.createBitmap(resizedBm, 0, 0, resizedBm.getWidth() - 2, resizedBm.getHeight());
            }
            if ((resizedBm.getHeight() & 2) != 0) {
                resizedBm = Bitmap.createBitmap(resizedBm, 0, 0, resizedBm.getWidth(), resizedBm.getHeight() - 2);
            }
        }
        resizedBm = resizedBm == null ? firstBitmap : resizedBm;
        byte[] bmBytes = ImageUtil.bitmapToNv21(resizedBm, resizedBm.getWidth(), resizedBm.getHeight());

        ImageFrame imageFrame = new ImageFrame();
        imageFrame.imageData = bmBytes;
        imageFrame.width = resizedBm.getWidth();
        imageFrame.height = resizedBm.getHeight();
        imageFrame.angle = 0;
        imageFrame.isMirror = 0;
        LivenessDetectionOption livenessDetectionOption = new LivenessDetectionOption();
        livenessDetectionOption.mValidMinFaceSize = 0;
        livenessDetectionOption.mNeedFaceTracking = false;
        livenessDetectionOption.mNeedLivenessDetection = true;
        livenessDetectionOption.mNeedIdentification = true;
        livenessDetectionOption.mThreshold = 0.3f;

        FaceIDSDK.shareIns().startLivenessDetectionDetection(imageFrame,
                null, livenessDetectionOption, new FaceTrackCallback() {
                    @Override
                    public void onTrackResult(TrackResult trackResult) {
                        Log.e("huwwds", "== onTrackResult" + trackResult);
                    }
                }, new LivenessCallback() {
                    @Override
                    public void onLivenessResult(LivenessResult livenessResult) {
                        Log.e("huwwds", "== onLivenessResult" + livenessResult);
                    }

                    @Override
                    public void onDetectionError(DetectionErrorType detectionErrorType) {
                        Log.e("huwwds", "onDetectionError" + detectionErrorType);
                    }
                }
        );
    }

    public static void test(Context context) {
        InitOption initOption = new InitOption();
        initOption.licenseKey = "facesdk-21-0810";
//        initOption.licenseKey = "facesdk-21-0726";
        initOption.licenseFileName = "license.ini";

        FaceIDSDK.shareIns().init(context, initOption, new InitCallback() {
            @Override
            public void onSucces(int code, String desc) {
                Log.e("huwwds", "===== success" + code);

                try {
//                    while (1 == 1) {
//                    testLivenessDetectionDetection();
//                        testVerification();
//                    testAttribute();
                        testDrive();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String desc) {
                Log.e("huwwds", "===== error" + code + " ==" + desc);
            }
        });
    }
}
