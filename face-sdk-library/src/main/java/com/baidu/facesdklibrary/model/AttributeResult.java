package com.baidu.facesdklibrary.model;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;

public class AttributeResult {
    public int age;
    public BDFaceSDKCommon.BDFaceGender gender;
    public BDFaceSDKCommon.BDFaceGlasses wearGlass;
    public FaceInfo faceInfo;
    public float faceMouthMaskScore;
//    public boolean wearMask;
}