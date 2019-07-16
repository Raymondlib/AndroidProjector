package com.example.try1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class close_screem extends AppCompatActivity {
    final  String TAG = "close_screem";
    Button btn ;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mCompName;
//    private void setScreenBrightness(int screenBrightness){
//        try{
//            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightness);
//        }
//        catch (Exception localException){
//            localException.printStackTrace();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_screem);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        // 申请权限

        mCompName = new ComponentName(this, YNAdminReceiver.class);

        btn= (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onScreenOff(view);
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.screenBrightness = 0.05f;
                getWindow().setAttributes(params);
            }
        });

//        onScreenOff(mCompName);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setContentView(R.layout.activity_close_screem);
//
//        closeScreen();
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                openScreen();
//            }
//        }, 5000);


    }

    public void onScreenOff(View view) {

        // 判断该组件是否有系统管理员的权限

        if (!mDevicePolicyManager.isAdminActive(mCompName)) {//这一句一定要有...

            Intent intent = new Intent();

            //指定动作

            intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

            //指定给那个组件授权

            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mCompName);

            startActivity(intent);

        } else {

            //立即关闭屏幕

            mDevicePolicyManager.lockNow();

            //                    devicePolicyManager.resetPassword("123321", 0);
            System.out.println("*****************");
            System.out.println("close");
            Log.i(TAG, "具有权限,将进行锁屏....");

            Log.i(TAG, "going to shutdown screen");

        }

    }



    private void openScreen(){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.screenBrightness = 255;
                getWindow().setAttributes(params);
                closeScreen();
            }
        }, 5000);
    }

    private void closeScreen(){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                params.screenBrightness = 1f/255f;
                getWindow().setAttributes(params);
                openScreen();
            }
        }, 5000);
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setAttributestContentView(R.layout.activity_close_screem);
//        btn= (Button) findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//    }
}
