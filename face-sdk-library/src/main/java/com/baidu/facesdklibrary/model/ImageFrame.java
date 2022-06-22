package com.baidu.facesdklibrary.model;

import java.io.Serializable;

/**
 *
 */
public class ImageFrame implements Serializable {

    public byte[] imageData;
    public int width;
    public int height;
    public int angle;
    public int isMirror;   // 是否镜像
    public int format;

    public ImageFrame() {

    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getIsMirror() {
        return isMirror;
    }

    public void setIsMirror(int isMirror) {
        this.isMirror = isMirror;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }
}
