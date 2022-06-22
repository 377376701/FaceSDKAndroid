package com.baidu.facesdklibrary.model;

public class CameraSize {
    private int width = 480;
    private int height = 640;

    public CameraSize(int w, int h) {
        width = w;
        height = h;
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
}
