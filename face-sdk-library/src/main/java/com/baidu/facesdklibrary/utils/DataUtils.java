package com.baidu.facesdklibrary.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtils {
    public static String getCurTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss:SSS");
        return dateFormat.format(date);
    }
}
