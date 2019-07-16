package com.example.try1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import static android.widget.Toast.LENGTH_LONG;

public class learn extends AppCompatActivity {
    public static final  String TAG="MainActivity";
    private VideoView videoview;
//    String path = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"; //  1min
//    String path = "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312083533415853.mp4"; // 30s
    String path = "http://www.w3school.com.cn/example/html5/mov_bbb.mp4"; // 10s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
//        downMp4();
        Log.e(TAG, "e.getMessage() --- ");
//        videoview =(VideoView) findViewById(R.id.videoView2);

//        Toast.makeText(learn.this,"fda ",LENGTH_LONG);

        // 创建文件夹，在存储卡下
//        String dirName = Environment.getExternalStorageDirectory() + "/" + mContext.getPackageName();
//        String dirName = Environment.getExternalStorageDirectory() + "/" + this.getPackageName();
        String dirName =getExternalFilesDir(null).toString();

        File file = new File(dirName);
        // 文件夹不存在时创建
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            System.out.println("如果相等的话表示当前的sdcard挂载在手机上并且是可用的");
        }
        if (!file.exists()) {
            System.out.println(file.mkdir());
            System.out.println("创建失败");
        }

        // 下载后的文件名
        int i = path.lastIndexOf("/"); // 取的最后一个斜杠后的字符串为名
//        final String fileName = dirName + path.substring(i, path.length());
        final String fileName = dirName + "/10.mp4";
        File file1 = new File(fileName);
        if (file1.exists()) {
            // 如果已经存在, 就不下载了, 去播放
            Toast.makeText(learn.this,"文件已存在 ", LENGTH_LONG);
            System.out.println("文件已存在,测试覆写");
            new Thread(new Runnable() {
                @Override
                public void run() {

                    DOWNLOAD(path,fileName);

                }
            }).start();
            Log.d("df","文件存在");
            startVideo(fileName);
        } else {
            System.out.println("***************");
            System.out.println();
            System.out.println("***************");
            new Thread(new Runnable() {
                @Override
                public void run() {

                    DOWNLOAD(path,fileName);

                }
            }).start();
        }
    }

    private void f(){

    }

     void DOWNLOAD(String path,String fileName) {
        try {
//            path = "http://www.w3school.com.cn/example/html5/mov_bbb.mp4";
            URL url = new URL(path);
            // 打开连接
            URLConnection conn = url.openConnection();
            System.out.println("-------------");
            System.out.println(path);
            System.out.println("-------------");
            // 打开输入流
            InputStream is = conn.getInputStream();
            // 创建字节流
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(fileName);
            // 写数据
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完成后关闭流
            Log.e(TAG, "download-finish");
            os.close();
            is.close();
            //            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "e.getMessage() --- " + e.getMessage());
        }
    }
    private void startVideo(String videoURI) {
        videoview.setVisibility(View.VISIBLE);
//        videoview.setLayoutParams(new RelativeLayout.LayoutParams(UtilsTools.getCurScreenWidth(mContext), UtilsTools.getCurScreenWidth(mContext) / 3 * 4)); // 此行代码是设置视频的宽高比是3/4,不需要就注释掉即可
        // 设置播放加载路径
        //        videoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.aaa));
        videoview.setVideoURI(Uri.parse(videoURI));
        // 播放
        videoview.start();
        // 循环播放
        //        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        //            @Override
        //            public void onCompletion(MediaPlayer mediaPlayer) {
        //                videoview.start();
        //            }
        //        });
    }

    private void downMp4() {
        final ProgressDialog pd; // 进度条对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //正在下载更新
        pd.setMessage("下载中...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    String url1 ="https://media.w3.org/2010/05/sintel/trailer.mp4";
                    String url2 ="http://img.tukuppt.com/video_show/3987418/00/02/84/5b9556857f5d9.mp4";
                    File file = getFileFromServer(url1, pd);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
                    //sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(String.valueOf(file))));
//                    //获取ContentResolve对象，来操作插入视频
//                    ContentResolver localContentResolver = getContentResolver();
//                    //ContentValues：用于储存一些基本类型的键值对
//                    ContentValues localContentValues = getVideoContentValues(MainActivity.this, file, System.currentTimeMillis());
//                    //insert语句负责插入一条新的纪录，如果插入成功则会返回这条记录的id，如果插入失败会返回-1。
//                    Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
                    sleep(3);
                    pd.dismiss(); // 结束掉进度条对话框
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 视频存在本地
     *
     * @param paramContext
     * @param paramFile
     * @param paramLong
     * @return
     */
    public static ContentValues getVideoContentValues(Context paramContext, File paramFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "video/3gp");
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }

    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception {
        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            // 获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File sd1 = Environment.getExternalStorageDirectory();
            String path1 = sd1.getPath() + "/lfmf";
            File myfile1 = new File(path1);
            if (!myfile1.exists()) {
                myfile1.mkdir();
            }
            File file = new File(myfile1, "lfmf.mp4");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                // 获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }
}


