package com.baidu.idl.main.facesdk.identifylibrary.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.identifylibrary.BaseActivity;
import com.baidu.idl.main.facesdk.identifylibrary.R;
import com.baidu.idl.main.facesdk.identifylibrary.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.identifylibrary.utils.IdentifyConfigUtils;
import com.baidu.idl.main.facesdk.identifylibrary.utils.PWTextUtils;
import com.baidu.idl.main.facesdk.identifylibrary.utils.RegisterConfigUtils;

public class IdentifyPictureOptimizationActivity extends BaseActivity {
    private Switch swDarkEnhance;
    private Switch swBestImage;
    private Button tipsDarkEnhance;
    private Button tipsBestImage;
    private View groupDarkEnhance;
    private View groupBestImage;
    private TextView tvDarkEnhance;
    private TextView tvBestImage;
    private View groupFunDarkEnhance;
    private String msgTag = "";
    private int showWidth;
    private int showXLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_picture_optimization);
        init();
        initListener();
    }
    private void init(){
        swDarkEnhance = findViewById(R.id.sw_dark_enhance);
        swBestImage = findViewById(R.id.sw_best_image);
        // 暗光恢复开关
        tipsDarkEnhance = findViewById(R.id.tips_dark_enhance);
        tvDarkEnhance = findViewById(R.id.tv_dark_enhance);
        groupDarkEnhance = findViewById(R.id.group_dark_enhance);
        groupFunDarkEnhance = findViewById(R.id.group_fun_dark_enhance);
        // best image开关
        tipsBestImage = findViewById(R.id.tips_best_image);
        groupBestImage = findViewById(R.id.group_best_image);
        tvBestImage = findViewById(R.id.tv_best_image);
        if (SingleBaseConfig.getBaseConfig().isDarkEnhance()) {
            swDarkEnhance.setChecked(true);
        } else {
            swDarkEnhance.setChecked(false);
        }
        if (SingleBaseConfig.getBaseConfig().isBestImage()) {
            swBestImage.setChecked(true);
        } else {
            swBestImage.setChecked(false);
        }
    }
    private void initListener(){
        tipsDarkEnhance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_darkEnhance))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_darkEnhance);
                tipsDarkEnhance.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(groupFunDarkEnhance, tvDarkEnhance,
                        IdentifyPictureOptimizationActivity.this,
                        getString(R.string.cw_darkEnhance), showWidth, showXLocation);
            }
        });
        tipsBestImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_bestimage))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_bestimage);
                tipsBestImage.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(groupBestImage, tvBestImage, IdentifyPictureOptimizationActivity.this,
                        getString(R.string.cw_bestimage), showWidth, showXLocation);
            }
        });
        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                tipsBestImage.setBackground(getDrawable(R.mipmap.icon_setting_question));
                tipsDarkEnhance.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        findViewById(R.id.qc_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swDarkEnhance.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setDarkEnhance(true);
                } else {
                    SingleBaseConfig.getBaseConfig().setDarkEnhance(false);
                }
                if (swBestImage.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setBestImage(true);
                } else {
                    SingleBaseConfig.getBaseConfig().setBestImage(false);
                }
                IdentifyConfigUtils.modityJson();
                RegisterConfigUtils.modityJson();
                finish();
            }
        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = groupFunDarkEnhance.getWidth();
        showXLocation = (int) groupDarkEnhance.getX();
    }
}
