package com.baidu.facesdklibrary.model;

public class SdkInfo {
    /**
     * 按照标准接⼊入指引定义填充
     * 活检算法信息
     * 算法名称、版本，应与算法检测时相关信息⼀一致，不不⾜足位以空格填充 * 数据类型:ans20
     */
    private String sdkStandardInfo;
    /**
     * 按照标准接⼊入指引定义填充 * 过检算法⼚厂商标识码
     * 数据类型:ans3
     */
    private String checkedId;
    /**
     * 按照标准接⼊入指引定义填充，参考如下(截⽌止到2019.09.05) * 活检算法能⼒力力
     * 数据类型:ans1
     * A:基本级
     * B:增强级
     */
    private String algAbility;
    /**
     * 按照标准接⼊入指引定义填充，参考如下(截⽌止到2019.09.05) * 活检⽅方式
     * 数据类型:n2
     * 01: 3D
     * 02: 双⽬目
     * 03: TOF
     */
    private String livenessDetectType;
    /**
     * 活体通过阈值
     * 数据类型:n2 * 取值范围00-99
     */
    private String threshold;
}
