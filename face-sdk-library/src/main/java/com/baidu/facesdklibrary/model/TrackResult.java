package com.baidu.facesdklibrary.model;

import com.baidu.idl.main.facesdk.FaceInfo;

public class TrackResult {
    /**
     * 实时⼈人脸参数，将最⼤大⽀支持⼈人脸检测个数以内的⼈人脸全部返回
     */
    public FaceInfo[] faceInfos;

    /**
     * 实时rgb width
     */
    public int width;

    /**
     * 实时rgb height
     */
    public int height;

    /**
     * 实时rgb原图
     */
    public byte[] rgbData;

    /**
     * 实时深度图
     * 如果摄像头带深度数据，必须返回
     */
    public byte[] depthData;

    /**
     * 实时红外图
     * 如果摄像头带红外数据，必须返回
     */
    public byte[] irData;
}
