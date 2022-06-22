package com.baidu.facesdklibrary.tools;

import com.baidu.facesdklibrary.model.ImageFrame;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;

public class FaceImageTool {
    /**
     * 图片转换
     *
     * @param imageFrame
     * @return
     */
    public static BDFaceImageInstance convertYuvImage(ImageFrame imageFrame) {
        if (imageFrame == null || imageFrame.height <= 0 || imageFrame.width <= 0) {
            return null;
        }
        return new BDFaceImageInstance(imageFrame.imageData, imageFrame.height,
                imageFrame.width, BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21,
                imageFrame.angle, imageFrame.isMirror);
    }




}
