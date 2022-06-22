package com.baidu.idl.main.facesdk.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.activity.BaseActivity;
import com.baidu.idl.main.facesdk.gatelibrary.R;
import com.baidu.idl.main.facesdk.manager.FaceSDKManager;
import com.baidu.idl.main.facesdk.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.utils.GateConfigUtils;
import com.baidu.idl.main.facesdk.utils.PWTextUtils;
import com.baidu.idl.main.facesdk.utils.RegisterConfigUtils;

public class LogSettingActivity extends BaseActivity {
    private Switch swLog;
    private Button tipsLog;
    private View groupLog;
    private TextView tvLog;
    private View groupFunLog;
    private String msgTag = "";
    private int showWidth;
    private int showXLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_setting);
        init();
        initListener();
    }
    private void init(){
        swLog = findViewById(R.id.sw_log);
        // log开关
        tipsLog = findViewById(R.id.tips_log);
        tvLog = findViewById(R.id.tv_log);
        groupLog = findViewById(R.id.group_log);
        groupFunLog = findViewById(R.id.group_fun_log);
        if (SingleBaseConfig.getBaseConfig().isLog()) {
            swLog.setChecked(true);
        } else {
            swLog.setChecked(false);
        }
    }
    private void initListener(){
        tipsLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_log))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_log);
                tipsLog.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(groupFunLog, tvLog, LogSettingActivity.this,
                        getString(R.string.cw_log), showWidth, showXLocation);
            }
        });
        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                tipsLog.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        findViewById(R.id.qc_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swLog.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setLog(true);
                } else {
                    SingleBaseConfig.getBaseConfig().setLog(false);
                }
                GateConfigUtils.modityJson();
                RegisterConfigUtils.modityJson();
                FaceSDKManager.getInstance().setActiveLog();
                finish();
            }
        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = groupFunLog.getWidth();
        showXLocation = (int) groupLog.getX();
    }
}
