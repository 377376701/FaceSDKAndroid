package com.baidu.idl.main.facesdk.gazelibrary.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.gazelibrary.BaseActivity;
import com.baidu.idl.main.facesdk.gazelibrary.R;
import com.baidu.idl.main.facesdk.gazelibrary.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.license.BDFaceLicenseAuthInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GazeSettingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView gateSetttingBack;
    private LinearLayout gateFaceDetection;
    private LinearLayout gateConfigQualtify;
    private LinearLayout gateHuotiDetection;
    private LinearLayout gateFaceRecognition;
    private LinearLayout gateLensSettings;
    private TextView tvSettingQualtify;
    private TextView tvSettingLiviness;
    private LinearLayout configVersionMessage;
    private TextView tvSettingEffectiveDate;
    private View gatePictureOptimization;
    private FaceAuth faceAuth;
    private View gateLogSettings;
    private TextView logSettingQualtify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaze_setting);
        init();
    }

    private void init() {
        faceAuth = new FaceAuth();
        // 返回
        gateSetttingBack = findViewById(R.id.gate_settting_back);
        gateSetttingBack.setOnClickListener(this);
        // 人脸检测
        gateFaceDetection = findViewById(R.id.gate_face_detection);
        gateFaceDetection.setOnClickListener(this);
        // 质量检测
        gateConfigQualtify = findViewById(R.id.gate_config_qualtify);
        gateConfigQualtify.setOnClickListener(this);
        // 活体检测
        gateHuotiDetection = findViewById(R.id.gate_huoti_detection);
        gateHuotiDetection.setOnClickListener(this);
        // 人脸识别
        gateFaceRecognition = findViewById(R.id.gate_face_recognition);
        gateFaceRecognition.setOnClickListener(this);
        // 镜头设置
        gateLensSettings = findViewById(R.id.gate_lens_settings);
        gateLensSettings.setOnClickListener(this);
        // 图像优化
        gatePictureOptimization = findViewById(R.id.gate_picture_optimization);
        gatePictureOptimization.setOnClickListener(this);
        // 日志设置
        gateLogSettings = findViewById(R.id.gate_log_settings);
        gateLogSettings.setOnClickListener(this);
        // 版本信息
        configVersionMessage = findViewById(R.id.configVersionMessage);
        configVersionMessage.setOnClickListener(this);
        tvSettingQualtify = findViewById(R.id.tvSettingQualtify);
        tvSettingLiviness = findViewById(R.id.tvSettingLiviness);
        logSettingQualtify = findViewById(R.id.logSettingQualtify);

        tvSettingEffectiveDate = findViewById(R.id.tvSettingEffectiveDate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SingleBaseConfig.getBaseConfig().isLog()) {
            logSettingQualtify.setText("开启");
        } else {
            logSettingQualtify.setText("关闭");
        }
        if (SingleBaseConfig.getBaseConfig().isQualityControl()) {
            tvSettingQualtify.setText("开启");
        } else {
            tvSettingQualtify.setText("关闭");
        }
        if (SingleBaseConfig.getBaseConfig().isLivingControl()) {
            tvSettingLiviness.setText("开启");
        } else {
            tvSettingLiviness.setText("关闭");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        BDFaceLicenseAuthInfo bdFaceLicenseAuthInfo = faceAuth.getAuthInfo(this);
        Date dateLong = new Date(bdFaceLicenseAuthInfo.expireTime * 1000L);
        String dateTime = simpleDateFormat.format(dateLong);

        tvSettingEffectiveDate.setText("有效期至" + dateTime);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.gate_settting_back) {
            finish();
        } else if (id == R.id.gate_face_detection) {
            Intent intent = new Intent(GazeSettingActivity.this, MinFaceActivity.class);
            startActivity(intent);
        } else if (id == R.id.gate_config_qualtify) {
            Intent intent = new Intent(GazeSettingActivity.this, GazeConfigQualtifyActivity.class);
            startActivity(intent);
        } else if (id == R.id.gate_huoti_detection) {
            Intent intent = new Intent(GazeSettingActivity.this, FaceLivinessTypeActivity.class);
            startActivity(intent);
        } else if (id == R.id.gate_face_recognition) {
            Intent intent = new Intent(GazeSettingActivity.this, GazeFaceDetectActivity.class);
            startActivity(intent);
        } else if (id == R.id.gate_lens_settings) {
            Intent intent = new Intent(GazeSettingActivity.this, GazeLensSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.gate_picture_optimization){
            Intent intent = new Intent(GazeSettingActivity.this, PictureOptimizationActivity.class);
            startActivity(intent);
        } else if (id == R.id.configVersionMessage) {
            Intent intent = new Intent(GazeSettingActivity.this, VersionMessageActivity.class);
            startActivity(intent);
        }else if (id == R.id.gate_log_settings) {
            Intent intent = new Intent(GazeSettingActivity.this, LogSettingActivity.class);
            startActivity(intent);
        }
    }
}