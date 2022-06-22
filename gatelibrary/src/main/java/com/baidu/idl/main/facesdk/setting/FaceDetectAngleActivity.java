package com.baidu.idl.main.facesdk.setting;

import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.activity.BaseActivity;
import com.baidu.idl.main.facesdk.gatecamera.AutoTexturePreviewView;
import com.baidu.idl.main.facesdk.gatecamera.CameraPreviewManager;
import com.baidu.idl.main.facesdk.gatelibrary.R;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.utils.BitmapUtils;
import com.baidu.idl.main.facesdk.utils.GateConfigUtils;
import com.baidu.idl.main.facesdk.utils.RegisterConfigUtils;
import com.baidu.idl.main.facesdk.view.PreviewTexture;


/**
 * author : shangrog
 * date : 2019/5/27 6:37 PM
 * description :人脸检测角度设置
 */
public class FaceDetectAngleActivity extends BaseActivity {
    private static final int PREFER_WIDTH = 640;
    private static final int PREFER_HEIGHT = 480;
    // RGB+IR 控件
    private PreviewTexture[] mPreview;
    private Camera[] mCamera;

    private AutoTexturePreviewView rgbFaceView;
    private View rgbRotate;
    private ImageView rgbRotateImg;
    private View rgbMirror;
    private TextView rgbMirrorTx;
    private ImageView rgbMirrorImg;
    private ImageView rgbImageView;
    private View rgbGroup;
    private View rgbFaceGroup;

    private AutoTexturePreviewView nirFaceView;
    private View nirRotate;
    private ImageView nirRotateImg;
    private View nirMirror;
    private TextView nirMirrorTx;
    private ImageView nirMirrorImg;
    private View nirFaceGroup;
    private View nirGroup;
    private ImageView nirImageView;
    int rgbDetectDirection;
    int mirrorDetectRGB;
    int nirDetectDirection;
    int mirrorDetectNIR;
    // 摄像头个数
    private int mCameraNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_facedetectangle);
        initMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void initMode(){
        // rgb view
        rgbFaceView = findViewById(R.id.rbg_face_view);
        rgbRotate = findViewById(R.id.rgb_rotate);
        rgbRotateImg = findViewById(R.id.rgb_rotate_image);
        rgbMirror = findViewById(R.id.rgb_mirror);
        rgbMirrorTx = findViewById(R.id.rgb_mirror_tx);
        rgbMirrorImg = findViewById(R.id.rgb_mirror_image);
        rgbImageView = findViewById(R.id.rbg_image_view);
        rgbGroup = findViewById(R.id.rgb_group);
        rgbFaceGroup = findViewById(R.id.rbg_face_group);
        // nir view
        nirFaceView = findViewById(R.id.nir_face_view);
        nirRotate = findViewById(R.id.nir_rotate);
        nirRotateImg = findViewById(R.id.nir_rotate_image);
        nirMirror = findViewById(R.id.nir_mirror);
        nirMirrorTx = findViewById(R.id.nir_mirror_tx);
        nirMirrorImg = findViewById(R.id.nir_mirror_image);
        nirFaceGroup = findViewById(R.id.nir_face_group);
        nirGroup = findViewById(R.id.nir_group);
        nirImageView = findViewById(R.id.nir_image_view);
        rgbDetectDirection =  SingleBaseConfig.getBaseConfig().getRgbDetectDirection();
        mirrorDetectRGB = SingleBaseConfig.getBaseConfig().getMirrorDetectRGB();
        nirDetectDirection =  SingleBaseConfig.getBaseConfig().getNirDetectDirection();
        mirrorDetectNIR = SingleBaseConfig.getBaseConfig().getMirrorDetectNIR();
    }
    private void init(){

        int rgbVideoDirection =  SingleBaseConfig.getBaseConfig().getRgbVideoDirection();
        int mirrorRGB = SingleBaseConfig.getBaseConfig().getMirrorVideoRGB();
        int nirVideoDirection =  SingleBaseConfig.getBaseConfig().getNirVideoDirection();
        int mirrorNIR = SingleBaseConfig.getBaseConfig().getMirrorVideoNIR();
        mCameraNum = Camera.getNumberOfCameras();
        mPreview = new PreviewTexture[mCameraNum];
        mCamera = new Camera[mCameraNum];
        // 打开摄像头
        if (mCameraNum < 2) {
            setNirView(0.3f , View.GONE , R.mipmap.texture_default);
            openCan(0, 0, rgbFaceView.textureView, new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    BDFaceImageInstance rgbInstance = new BDFaceImageInstance(data, PREFER_HEIGHT,
                            PREFER_WIDTH, BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21,
                            rgbDetectDirection,
                            mirrorDetectRGB);
                    showRgbImage(rgbInstance);
                }
            });
            setDisplayOrientation(mCamera[0] , rgbVideoDirection , rgbFaceView);
            setRotationY(rgbVideoDirection , mirrorRGB , rgbFaceView);
            setRgbRotate();
            setRbgMirror();
        } else {
            int rbg = CameraPreviewManager.CAMERA_USB;
            int nir = 1;
            if (SingleBaseConfig.getBaseConfig().getRBGCameraId() != -1){
                rbg = SingleBaseConfig.getBaseConfig().getRBGCameraId();
                nir = Math.abs(SingleBaseConfig.getBaseConfig().getRBGCameraId() - 1);
            }
            setNirView(1 , View.VISIBLE , R.drawable.sr_texture_rectangle);
            boolean rbgCameraReady = openCan(0, rbg , rgbFaceView.textureView, new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    try {
                        BDFaceImageInstance rgbInstance = new BDFaceImageInstance(data, PREFER_HEIGHT,
                                PREFER_WIDTH, BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21,
                                rgbDetectDirection,
                                mirrorDetectRGB);
                        showRgbImage(rgbInstance);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            if (rbgCameraReady){
                setDisplayOrientation(mCamera[0] , rgbVideoDirection , rgbFaceView);
                setRotationY(rgbVideoDirection , mirrorRGB , rgbFaceView);
                setRgbRotate();
                setRbgMirror();
            }else {
                setRgbView(0.3f , R.mipmap.texture_default);
            }
            boolean nirCameraReady = openCan(1, nir , nirFaceView.textureView, new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    try {
                        BDFaceImageInstance nirInstance = new BDFaceImageInstance(data, PREFER_HEIGHT,
                                PREFER_WIDTH, BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21,
                                nirDetectDirection,
                                mirrorDetectNIR);
                        showNirImage(nirInstance);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            if (nirCameraReady){
                setDisplayOrientation(mCamera[1] , nirVideoDirection , nirFaceView);
                setRotationY(nirVideoDirection , mirrorNIR , nirFaceView);
                setNirRotate();
                setNirMirror();
                // nir旋转
                nirRotate.setOnClickListener(onClickListener);
                // nir镜像
                nirMirror.setOnClickListener(onClickListener);
            }else {
                setNirView(0.3f , View.GONE , R.mipmap.texture_default);
            }
        }
        // RGB旋转
        rgbRotate.setOnClickListener(onClickListener);
        // rgb镜像
        rgbMirror.setOnClickListener(onClickListener);
        ImageView cdaSave = findViewById(R.id.cda_save);
        cdaSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleBaseConfig.getBaseConfig().setRgbDetectDirection(rgbDetectDirection);
                SingleBaseConfig.getBaseConfig().setMirrorDetectRGB(mirrorDetectRGB);
                SingleBaseConfig.getBaseConfig().setNirDetectDirection(nirDetectDirection);
                SingleBaseConfig.getBaseConfig().setMirrorDetectNIR(mirrorDetectNIR);
                GateConfigUtils.modityJson();
                RegisterConfigUtils.modityJson();
                finish();
            }
        });
    }

    private void setNirView(float alpha , int visibility , int bgResid){
        View nirTx = findViewById(R.id.nir_tx);
        nirGroup.setAlpha(alpha);
        nirTx.setVisibility(visibility);
        nirRotateImg.setImageResource(R.mipmap.rotate_0);
        nirFaceGroup.setBackgroundResource(bgResid);
    }

    private void setRgbView(float alpha , int bgResid){
        rgbGroup.setAlpha(alpha);
        rgbFaceGroup.setBackgroundResource(bgResid);
    }

    private void showNirImage(final BDFaceImageInstance nirInstance){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nirImageView.setImageBitmap(BitmapUtils.getInstaceBmp(nirInstance.getImage()));
            }
        });
        nirInstance.destory();
    }

    private void showRgbImage(final BDFaceImageInstance rgbInstance){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                rgbImageView.setImageBitmap(BitmapUtils.getInstaceBmp(rgbInstance.getImage()));
            }
        });
        rgbInstance.destory();
    }


    private boolean openCan(int displayIndex, int index, TextureView faceView, Camera.PreviewCallback cb){
        try {
            mCamera[displayIndex] = Camera.open(index);
            mPreview[displayIndex] = new PreviewTexture(this, faceView);
            mPreview[displayIndex].setCamera(mCamera[displayIndex], PREFER_WIDTH, PREFER_HEIGHT);
            mCamera[displayIndex].setPreviewCallback(cb);
            return true;
        } catch (Exception e) {
            e.getLocalizedMessage();
        }
        return false;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.rgb_rotate){
                setVideoDirection(0);
                setRgbRotate();
            }else if (id == R.id.rgb_mirror){
                setMirror(0);
                setRbgMirror();
            }else if (id == R.id.nir_rotate){
                setVideoDirection(1);
                setNirRotate();
            }else if (id == R.id.nir_mirror){
                setMirror(1);
                setNirMirror();
            }
        }
    };
    private void setRgbRotate(){
        if (rgbDetectDirection == 0){
            rgbRotateImg.setImageResource(R.mipmap.rotate_0);
        }else if (rgbDetectDirection == 90){
            rgbRotateImg.setImageResource(R.mipmap.rotate_90);
        }else if (rgbDetectDirection == 180){
            rgbRotateImg.setImageResource(R.mipmap.rotate_180);
        }else if (rgbDetectDirection == 270){
            rgbRotateImg.setImageResource(R.mipmap.rotate_270);
        }
    }

    private void setRbgMirror(){
        if (mirrorDetectRGB == 0){
            rgbMirrorImg.setImageResource(R.mipmap.mirror_close);
            rgbMirrorTx.setTextColor(Color.parseColor("#ffffff"));
        }else {
            rgbMirrorImg.setImageResource(R.mipmap.mirror_oppen);
            rgbMirrorTx.setTextColor(Color.parseColor("#00BAF2"));
        }
    }
    private void setNirMirror(){
        if (mirrorDetectNIR == 0){
            nirMirrorImg.setImageResource(R.mipmap.mirror_close);
            nirMirrorTx.setTextColor(Color.parseColor("#ffffff"));
        }else {
            nirMirrorImg.setImageResource(R.mipmap.mirror_oppen);
            nirMirrorTx.setTextColor(Color.parseColor("#00BAF2"));
        }
    }
    private void setNirRotate(){
        if (nirDetectDirection == 0){
            nirRotateImg.setImageResource(R.mipmap.rotate_0);
        }else if (nirDetectDirection == 90){
            nirRotateImg.setImageResource(R.mipmap.rotate_90);
        }else if (nirDetectDirection == 180){
            nirRotateImg.setImageResource(R.mipmap.rotate_180);
        }else if (nirDetectDirection == 270){
            nirRotateImg.setImageResource(R.mipmap.rotate_270);
        }
    }
    // 旋转角度
    private void setVideoDirection(int index){
        if (mCamera == null || mCamera[index] == null) {
            return;
        }
        if (index == 0){
            rgbDetectDirection += 90;
            if (rgbDetectDirection > 270) {
                rgbDetectDirection = 0;
            }
        }else {
            nirDetectDirection += 90;
            if (nirDetectDirection > 270) {
                nirDetectDirection = 0;
            }
        }
    }
    public void setDisplayOrientation(Camera camera , int videoDirection , AutoTexturePreviewView mTextureView){
        camera.setDisplayOrientation(videoDirection);
        ViewGroup.LayoutParams layoutParams = mTextureView.getLayoutParams();
        int h = layoutParams.height;
        int w = layoutParams.width;
        if (videoDirection == 90 || videoDirection == 270) {
            if (h > w){

                layoutParams.height = w;
                layoutParams.width = h;
            }
            // 旋转90度或者270，需要调整宽高
            mTextureView.setPreviewSize(PREFER_HEIGHT, PREFER_WIDTH);
        } else {
            if (w > h){

                layoutParams.height = w;
                layoutParams.width = h;
            }
            mTextureView.setPreviewSize(PREFER_WIDTH, PREFER_HEIGHT);
        }
        mTextureView.setLayoutParams(layoutParams);
    }
    // 旋转角度
    private void setMirror(int index){
        if (mCamera == null || mCamera[index] == null) {
            return;
        }
        if (index == 0){
            mirrorDetectRGB = Math.abs(1 - mirrorDetectRGB);
        }else {
            mirrorDetectNIR = Math.abs(1 - mirrorDetectNIR);
        }
    }
    // 镜像
    public void setRotationY(int videoDirection , int isRgbRevert , AutoTexturePreviewView mTextureView){
        if (videoDirection == 90 || videoDirection == 270) {
            if (isRgbRevert == 1) {
                mTextureView.setRotationY(180);
            } else {
                mTextureView.setRotationY(0);
            }
            // 旋转90度或者270，需要调整宽高
        } else {
            if (isRgbRevert == 1) {
                mTextureView.setRotationY(180);
            } else {
                mTextureView.setRotationY(0);
            }
        }
    }

    @Override
    protected void onPause() {
        for (int i = 0; i < mCameraNum; i++) {
            if (mCamera[i] != null) {
                mCamera[i].setPreviewCallback(null);
                mCamera[i].stopPreview();
                mPreview[i].release();
                mCamera[i].release();
                mCamera[i] = null;
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
