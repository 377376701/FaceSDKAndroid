/**
 * FileUtil.java
 * <p>
 * Created by yangrui on 2018-4-22
 * Copyright (c) 1998-2014 273.cn. All rights reserved.
 */

package com.baidu.facesdklibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.baidu.facesdklibrary.SDKConstant;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * 与文件相关工具类
 *
 * @author yangrui09
 */
public class FileUtil {
    private FileUtil() {
    }

    public static File getFeatureDir(Context mContext) {
        File featureFile = mContext.getDir(SDKConstant.FEATURE_DIR, Context.MODE_PRIVATE);
        return featureFile;
    }

    /**
     * 获取一个目录下所有的文件
     *
     * @param path           要遍历的目录
     * @param isRecurrence   true:递归遍历子目录 false:不递归遍历子目录
     * @param includeEndName
     * @return 所有的文件路径
     */
    public static ArrayList<File> getFiles(String path, boolean isRecurrence, String includeEndName) {
        ArrayList<File> fileList = new ArrayList<File>();
        File[] allFiles = new File(path).listFiles();
        for (int i = 0; i < allFiles.length; i++) {
            File file = allFiles[i];
            if (file.isFile()) {
                if (!TextUtils.isEmpty(includeEndName)) {
                    if (file.getName().endsWith(includeEndName)) {
                        fileList.add(file);
                    }
                } else if (TextUtils.isEmpty(includeEndName)) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    /**
     * 获取一个目录下所有的文件夹
     *
     * @param path         要遍历的目录
     * @param isRecurrence true:递归遍历子目录 false:不递归遍历子目录
     * @return 所有的文件夹路径
     */
    public static ArrayList<String> getSubDirectories(String path, boolean isRecurrence) {
        ArrayList<String> fileList = new ArrayList<String>();
        File[] allFiles = new File(path).listFiles();
        for (int i = 0; i < allFiles.length; i++) {
            File file = allFiles[i];
            if (file.isDirectory()) {
                fileList.add(file.getAbsolutePath());
            }
        }
        return fileList;
    }

    /**
     * 判断一个文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 得到文件的二进制数据
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public static byte[] file2byte(String filePath) throws Exception {
        byte[] buffer = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            buffer = bos.toByteArray();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer;
    }

    /**
     * 删除目录
     *
     * @param file
     * @throws IOException
     */
    public static void removeDirectory(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                String name = f.getName();
                if ((".").equals(name) || ("..").equals(name)) {
                    continue;
                }
                removeDirectory(f);
            }
        }
        if (file.exists() && !file.delete()) {
            throw new IOException("Failed to delete directory");
        }
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     *
     * @param value 要转换的int值
     * @return byte数组
     */
    private static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    private static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    public static boolean saveInts(String savePath, int[] ints) {
        if (TextUtils.isEmpty(savePath) || ints == null) {
            return false;
        } else {
            File newfile = new File(savePath);
            File newFileDir = new File(newfile.getPath().replace(newfile.getName(), ""));
            if (!newFileDir.exists()) {
                newFileDir.mkdirs();
            }
            boolean isOk = false;
            ObjectOutputStream outputStream = null;
            try {
                outputStream = new ObjectOutputStream(new FileOutputStream(savePath));
                outputStream.writeObject(ints);
                isOk = true;
            } catch (IOException e) {
                e.printStackTrace();
            }


            return isOk;
        }
    }

    public static int[] getInsFile(String savePath) {
        int[] saveIns = null;
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(savePath));
            saveIns = (int[]) inputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveIns;
    }

    public static boolean saveObjects(String savePath, Object obj) {
        if (TextUtils.isEmpty(savePath) || obj == null) {
            return false;
        } else {
            File newfile = new File(savePath);
            File newFileDir = new File(newfile.getPath().replace(newfile.getName(), ""));
            if (!newFileDir.exists()) {
                newFileDir.mkdirs();
            }
            boolean isOk = false;
            ObjectOutputStream outputStream = null;
            try {
                outputStream = new ObjectOutputStream(new FileOutputStream(savePath));
                outputStream.writeObject(obj);
                isOk = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return isOk;
        }
    }

    public static Object getObjectFile(String savePath) {
        Object saveIns = null;
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(savePath));
            saveIns = inputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saveIns;
    }

    /**
     * 根据byte数组生成文件
     *
     * @param bytes 生成文件用到的byte数组
     */
    public static boolean saveBytes(String savePath, byte[] bytes) {
        File f = new File(savePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            File file = new File(savePath);
            FileOutputStream fop = new FileOutputStream(file);

            if (!file.exists()) {
                file.createNewFile();
            }

            fop.write(bytes);
            fop.flush();
            fop.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * @param savePath
     * @param bitmap
     * @return
     */
    public static boolean saveBitmapToFile(String savePath, Bitmap bitmap) {
        Logger.i("robot", "保存图片");
        boolean result = false;
        FileOutputStream out = null;
        File f = new File(savePath);
        if (f.exists()) {
            f.delete();
        }
        File newFileDir = new File(f.getPath().replace(f.getName(), ""));
        if (!newFileDir.exists()) {
            newFileDir.mkdirs();
        }
        try {
            out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            Logger.i("robot", "已经保存");
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * 追加文件：使用FileOutputStream，在构造FileOutputStream时，把第二个参数设为true
     *
     * @param file
     * @param conent
     */
    public static void appendStrToFile(String file, String conent) {
        if (file == null || conent == null) {
            return;
        }

        File newfile = new File(file);
        File newFileDir = new File(newfile.getPath().replace(newfile.getName(), ""));
        if (!newFileDir.exists()) {
            newFileDir.mkdirs();
        }

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}