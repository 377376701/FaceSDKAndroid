package com.baidu.facesdklibrary.callback;


import com.baidu.facesdklibrary.model.TrackResult;

public interface FaceTrackCallback {
    /**
     * ⼈人脸跟踪回调，将最⼤大⽀支持⼈人脸检测个数以内的⼈人脸全部返回 *
     *
     * @param trackResult
     */
    void onTrackResult(TrackResult trackResult);
}
