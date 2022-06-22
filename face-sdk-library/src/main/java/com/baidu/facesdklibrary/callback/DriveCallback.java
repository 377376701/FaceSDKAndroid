package com.baidu.facesdklibrary.callback;

import com.baidu.facesdklibrary.model.DetectionErrorType;
import com.baidu.facesdklibrary.model.DriveResult;

public interface DriveCallback {
    void onDetectionError(DetectionErrorType detectionErrorType);
    void onSuccess(DriveResult driveResult);
}
