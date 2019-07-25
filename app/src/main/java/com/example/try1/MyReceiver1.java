package com.example.try1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
/**
 实现功能：开机启动
 **/
public class MyReceiver1 extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Toast.makeText(context,"fdas ",Toast.LENGTH_LONG).show();
//        throw new UnsupportedOperationException("Not yet implemented");
        Log.e("broadCastReceiver","onReceiver...");
        try {
            Intent mBootIntent = new Intent(context, MainActivity.class);
            // 下面这句话必须加上才能开机自动运行app的界面
            mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mBootIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
