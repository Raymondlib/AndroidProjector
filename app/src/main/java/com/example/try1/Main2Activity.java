package com.example.try1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/*
 尝试截屏，但是没有效果；暂时没有业务需求，先不管了
*/

public class Main2Activity extends Activity {
    private TextView textView;
    private VideoView videoview;
    private GifImageView imageview;
//    private  imageView2;
public static Bitmap capture(Activity activity) {
    activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
    Bitmap bmp = activity.getWindow().getDecorView().getDrawingCache();
    return bmp;
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_projector2);
//        imageview = (GifImageView) findViewById(R.id.imageView);
//        imageview.setImageURI(Uri.parse("android.resource://"+getPackageName()+"/raw/pic1"));




        new Thread(new Runnable() {
            @Override
            public void run() {
//                /getExternalFilesDir
                File file = new File(getExternalFilesDir(null).toString()+"/", System.currentTimeMillis()+".jpg");
                FileOutputStream out;
                try {
                    out = new FileOutputStream(file);
                    Bitmap b = capture(Main2Activity.this);
                    if(b!= null){
                        capture(Main2Activity.this).compress(Bitmap.CompressFormat.JPEG, 90, out);
                        System.out.println("开始保存");
                        out.flush();
                        out.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("保存已经至"+Environment.getExternalStorageDirectory());
//                Toast.makeText(Main2Activity.this,"保存已经至"+Environment.getExternalStorageDirectory()+"下", Toast.LENGTH_SHORT).show();
            }
        }).start();

    }

}

