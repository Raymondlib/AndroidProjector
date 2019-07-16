package com.example.try1;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenOffAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        Log.d("1234", "设备管理器使用");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Log.d("1234", "设备管理器没有使用");
    }
}