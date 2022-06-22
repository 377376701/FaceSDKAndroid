package com.baidu.facesdklibrary.utils;


import android.graphics.Bitmap;

import com.baidu.facesdklibrary.FaceIDDebug;
import com.baidu.facesdklibrary.SDKConstant;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;

public class SaveCasePicUtil {
    public static final String TAG = SaveCasePicUtil.class.getSimpleName();

    public static void saveCasePic(BDFaceImageInstance imageInstance, String businessType, String caseType) {
        boolean isSave = false;
        switch (caseType) {
            case "bad_track":
                isSave = FaceIDDebug.isSaveBadTrackPic();
                break;
            case "bad_liveness":
                isSave = FaceIDDebug.isSaveBadLivenessPic();
                break;
            case "bad_recognize":
                isSave = FaceIDDebug.isSaveBadRecognizePic();
                break;
            default:
                break;
        }
        if (isSave) {
            StringBuilder type = new StringBuilder(businessType);
            type.append("_").append(DataUtils.getCurTime()).append("_").append(caseType).append("_");
            Bitmap bitmapIr = BitmapUtils.getInstaceBmp(imageInstance.getImage());
            String saveFileName = SDKConstant.SAVEPIC_PATH + type + ".png";
            Logger.i(TAG, type.append(saveFileName).toString());
            FileUtil.saveBitmapToFile(saveFileName, bitmapIr);
        }
    }
}
