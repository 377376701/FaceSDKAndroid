package com.baidu.facesdklibrary;

public class SDKConfig {
    /**
     * 质量检测开关
     */
    public static class FaceQuality {
        private final float defaultScore = 2.0f;
        private boolean isOpenQuality;     // 是否开启质量检测
        private float bluriness = defaultScore;
        private float illum = defaultScore;

        public boolean isOpenQuality() {
            return isOpenQuality;
        }

        public void setOpenQuality(boolean openQuality) {
            isOpenQuality = openQuality;
        }

        public float getBluriness() {
            return bluriness;
        }

        public void setBluriness(float bluriness) {
            this.bluriness = bluriness;
        }

        public float getIllum() {
            return illum;
        }

        public void setIllum(float illum) {
            this.illum = illum;
        }
    }

    /**
     * 质量检测开关
     */
    public static class FaceBestImage {
        private boolean isOpenBestImage;     // 是否开启质量检测
        private float bestImageThreshold;

        public boolean isOpenBestImage() {
            return isOpenBestImage;
        }

        public float getBestImageThreshold() {
            return bestImageThreshold;
        }

        public void setBestImageThreshold(float bestImageThreshold) {
            this.bestImageThreshold = bestImageThreshold;
        }

        public void setOpenBestImage(boolean openBestImage) {
            isOpenBestImage = openBestImage;
        }
    }

    /**
     * 遮挡的配置类
     */
    public static class FaceOcclusion {
        private boolean isOpenOcclusion;
        private final float defaultScore = 2.0f;
        private float leftEye = defaultScore;
        private float rightEye = defaultScore;
        private float nose = defaultScore;
        private float mouth = defaultScore;
        private float leftContour = defaultScore;
        private float rightContour = defaultScore;
        private float chinContour = defaultScore;

        public boolean isOpenOcclusion() {
            return isOpenOcclusion;
        }

        public void setOpenOcclusion(boolean openOcclusion) {
            isOpenOcclusion = openOcclusion;
        }

        /**
         * 设置左眼遮挡阈值，当当前实际值大与设置值时，则认为当前实际被遮挡了
         * 建议设置为0.5
         *
         * @param leftEye 取值范围为[0,1]
         */
        public void setLeftEye(float leftEye) {
            this.leftEye = leftEye;
        }

        /**
         * 设置右眼遮挡阈值，当当前实际值大与设置值时，则认为当前实际被遮挡了
         * 建议设置为0.5
         *
         * @param rightEye 取值范围为[0,1]
         */
        public void setRightEye(float rightEye) {
            this.rightEye = rightEye;
        }

        /**
         * 设置鼻子遮挡阈值，当当前实际值大与设置值时，则认为当前实际被遮挡了
         * 建议设置为0.5
         *
         * @param nose 取值范围为[0,1]
         */
        public void setNose(float nose) {
            this.nose = nose;
        }

        /**
         * 设置嘴挡阈值，当当前实际值大与设置值时，则认为当前实际被遮挡了
         * 建议设置为0.5
         *
         * @param mouth 取值范围为[0,1]
         */
        public void setMouth(float mouth) {
            this.mouth = mouth;
        }

        /**
         * 设置左脸遮挡阈值，当当前实际值大与设置值时，则认为当前实际被遮挡了
         * 建议设置为0.5
         *
         * @param leftContour 取值范围为[0,1]
         */
        public void setLeftContour(float leftContour) {
            this.leftContour = leftContour;
        }

        /**
         * 设置右脸遮挡阈值，当当前实际值大与设置值时，则认为当前实际被遮挡了
         * 建议设置为0.5
         *
         * @param rightContour 取值范围为[0,1]
         */
        public void setRightContour(float rightContour) {
            this.rightContour = rightContour;
        }

        /**
         * 设置下巴遮挡阈值，当当前实际值大与设置值时，则认为当前实际被遮挡了
         * 建议设置为0.5
         *
         * @param chinContour 取值范围为[0,1]
         */
        public void setChinContour(float chinContour) {
            this.chinContour = chinContour;
        }

        public float[] toArrays() {
            return new float[]{leftEye, rightEye, nose, mouth, leftContour,
                    rightContour, chinContour};
        }
    }

    /**
     * 人脸框大小配置类
     */
    public static class FaceSize {
        private int[] registFaceSize;
        private int[] unlockFaceSize;

        public FaceSize() {
            registFaceSize = new int[]{-1, -1};
            unlockFaceSize = new int[]{-1, -1};
        }

        /**
         * 设置注册时最小，最大人脸，当当前检测出实际的人脸小于最小值，或者大于最大值
         * 则当前注册不成功
         *
         * @param minSize 最小人脸宽度
         * @param maxSize 最小人脸宽度
         */
        public void setRegistSize(int minSize, int maxSize) {
            registFaceSize[0] = minSize;
            registFaceSize[1] = maxSize;
        }

        /**
         * 设置解锁时最小，最大人脸，当当前检测出实际的人脸小于最小值，或者大于最大值
         * 则当前解锁不成功
         *
         * @param minSize 最小人脸宽度
         * @param maxSize 最小人脸宽度
         */
        public void setUnlockSize(int minSize, int maxSize) {
            unlockFaceSize[0] = minSize;
            unlockFaceSize[1] = maxSize;
        }

        public int[] getRegistFaceSize() {
            return (isRegistDefault() ? null : registFaceSize);
        }

        public int[] getUnlockFaceSize() {
            return (isUnlockDefault() ? null : unlockFaceSize);
        }

        private boolean isRegistDefault() {
            return (registFaceSize[0] == -1 && registFaceSize[1] == -1);
        }

        private boolean isUnlockDefault() {
            return (unlockFaceSize[0] == -1 && unlockFaceSize[1] == -1);
        }
    }

    /**
     * 人眼状态配置
     */
    public static class EyeStatus {
        private final float defaultScore = 2.0f;
        private float[] registEyeStatus = new float[]{defaultScore, defaultScore};
        private float[] unlockEyeStatus = new float[]{defaultScore, defaultScore};

        /**
         * 设置注册时眼睛状态控制
         *
         * @param leftEyeClose  左眼闭合程度 [0,1] 其中1为完毕闭合
         * @param rightEyeClose 右眼闭合程度 [0,1] 其中1为完毕闭合
         */
        public void setRegistEyesClose(float leftEyeClose, float rightEyeClose) {
            registEyeStatus[0] = leftEyeClose;
            registEyeStatus[1] = rightEyeClose;
        }

        /**
         * 设置解锁时眼睛状态控制
         *
         * @param leftEyeClose  左眼闭合程度 [0,1] 其中1为完毕闭合
         * @param rightEyeClose 右眼闭合程度 [0,1] 其中1为完毕闭合
         */
        public void setUnlcokEyesClose(float leftEyeClose, float rightEyeClose) {
            unlockEyeStatus[0] = leftEyeClose;
            unlockEyeStatus[1] = rightEyeClose;
        }

        private boolean isUnlockDefault() {
            return (unlockEyeStatus[0] == defaultScore && unlockEyeStatus[1] == defaultScore);
        }

        private boolean isRegistDefault() {
            return (registEyeStatus[0] == defaultScore && registEyeStatus[1] == defaultScore);
        }

        public float[] registToArrays() {
            return (isRegistDefault() ? null : registEyeStatus);
        }

        public float[] unlockToArrays() {
            return (isUnlockDefault() ? null : unlockEyeStatus);
        }
    }

    private static FaceOcclusion registOcclusion;
    private static FaceOcclusion unlockOcclusion;
    private static FaceSize faceSize;
    private static EyeStatus registEyeStatus;
    private static FaceQuality configQuality;
    private static FaceBestImage configBestImage;

    static {
        registOcclusion = new FaceOcclusion();
        unlockOcclusion = new FaceOcclusion();
        faceSize = new FaceSize();
        registEyeStatus = new EyeStatus();
        configQuality = new FaceQuality();
        configBestImage = new FaceBestImage();
    }

    /**
     * 设置注册时遮挡的参数
     *
     * @return
     */
    public static FaceOcclusion registOcc() {
        return registOcclusion;
    }

    /**
     * 设置解锁时遮挡的参数
     *
     * @return
     */
    public static FaceOcclusion unlockOcc() {
        return unlockOcclusion;
    }

    /**
     * 设置人脸尺寸
     *
     * @return
     */
    public static FaceSize faceSize() {
        return faceSize;
    }

    /**
     * 人眼控制
     *
     * @return
     */
    public static EyeStatus eyeStatus() {
        return registEyeStatus;
    }

    /**
     * 是否开启质量检测
     */
    public static FaceQuality faceQuality() {
        return configQuality;
    }

    /**
     * 是否开启质量检测
     */
    public static FaceBestImage faceBestImage() {
        return configBestImage;
    }
}
