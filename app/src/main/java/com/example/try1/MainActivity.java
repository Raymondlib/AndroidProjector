package com.example.try1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.cert.TrustAnchor;
import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.makeramen.roundedimageview.RoundedImageView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;

import org.eclipse.paho.client.mqttv3.MqttToken;
import org.jeromq.ZMQ;
import org.jeromq.ZMQException;
import org.json.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.MqttListener;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import static com.example.try1.MainActivity.TAG;
import static java.lang.Thread.sleep;

public class MainActivity extends Activity {
    private ActivityManager activityManager;
    private String defaultCity="西安";
    private View layout1;
    private Timer timerForWeather;
    private Timer timerForPicture;
    private TextView textView;
    private TextView textView2;
    static int posForVideo=0;
    static int posForPicture=0;
    private ArrayList<Uri> file_list;
    private ArrayList<String> video_toPlay_list;
    private ArrayList<String> picture_toPlay_list;
    private int picture_time=2;//每张图片的显示时间
    private Button button;
    private Button button2;
    private String pu_ip;
    private VideoView videoview;
    private RoundedImageView imageview;
    private RoundedImageView imageview2;
    private View layout_video_picture;
    private View layout_video_picture1;
    private View layout_video_picture2;
    private String locAddress;
    private int cutlineVideo_times=0;
    private int cutlineVideo_times2=0;
    private String weatherTypeToday;
    private String weatherTypeTomorrow;
    private String temperatureToday;
    private String temperatureTomorrow;
    private String dateToday;
    private String dateTomorrow;
    private HashMap<String,Integer> weatherMap;
    private int layoutSize =1;
    private TimerTask timerTask;
    public static final  String TAG="MainActivity";
    private String mqttIp = "tcp://39.100.88.26:1883";
    private String mqttUsername ="xupeng";
    private String mqttPassword ="000";
    public MqttClient mqttClient;
    private boolean mqttIsConnect =false;
    public static final int MQTT_STATE_CONNECTED=0;
    public static final int MQTT_STATE_FAIL=1;
    public static final int MQTT_STATE_LOST=2;
    public static final int MQTT_STATE_RECEIVE=3;
    public final String topicData="ddzl/projector/data";
    public final String topicSub="ddzl/broker/projector";
    public final String topicPub="ddzl/projector/broker";
    String topicResult = "ddzl/projector/broker/clientid/exec/result";
    String topicVolume = "ddzl/broker/projector/clientid/order/msg";
    String topicUpdateAd ="ddzl/broker/projector/clientid/update_advertisement";
    String topicStatus = "ddzl/projector/broker/clientid/device_status";
    String topicPush ="ddzl/broker/projector/clientid/advertise/push";
    public String topicPubMac;
    public String topicSubMac;
    public String mac;
    private String [] topicSubList;
    public String transportFileName;
    private MqttCallback mqttCallback;
    private int volume;
    private String light_machine="enable";
    private String current_ad_id;
    private HashMap<String,Integer> adPlayStatistic;
    //    private Runtime run = Runtime.getRuntime();//获取当前运行环境，来执行ping，相当于windows的cmd
    String url30 ;

    private Handler mqttHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.arg1){
                case MQTT_STATE_CONNECTED:
                    break;
                case MQTT_STATE_RECEIVE:
//                    MqttObject object= (MqttObject) message.obj;
                    System.out.println();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    public void pubOrderTopic(String s,String topic){
        Message message = new Message();
        message.what = 19;
        message.obj =s+"--"+topic ;
        handler.sendMessage(message);
    }
    public void pubResult(String s){
        pubOrderTopic(s,topicResult);
        System.out.println("topicResult=");
        System.out.println(topicResult);
    }
    public void pubStatus(String s){
        pubOrderTopic(s,topicStatus);
    }

    // 接收子线程中的信息，来更新控件，因为只有主线程才能更新控件
    private Handler  handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    // 初始化连接，获取控制端ip    pair模式，端口5550
//                    pu_ip = msg.obj.toString();
                    if(msg.obj.toString().equals("start_video")){
                        videoview.start();
                    }else if(msg.obj.toString().equals("pause_video")){
                        videoview.pause();
                    }
                    break;
                case 1:
                    setVideo(msg.obj.toString());
                    System.out.println("sub模式收到信息");
                    break;
                case 2:
                    int layoutSize =Integer.parseInt(msg.obj.toString().split(",")[0]);
                    int leftMargin =Integer.parseInt(msg.obj.toString().split(",")[1]);
                    int topMargin =Integer.parseInt(msg.obj.toString().split(",")[2]);
                    setLayoutSize(layout1,layoutSize,leftMargin,topMargin);
                    System.out.println("reset layoutSize "+msg.obj.toString());
                    break;
                case 3:
                    //设置二维码图片
                    imageview2.setImageURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+msg.obj.toString()));
                    break;
                case 4:
                    videoview.resume();
                    break;
                case 5:
                    break;
                case 6:
                    pu_ip =msg.obj.toString();
                    break;
                case 7:
                    //style 1   ，图片+视频
                    setContentView(layout_video_picture);
                    //更新控件
                    videoview = (VideoView) findViewById(R.id.videoView3);
                    Bitmap bitmap = BitmapFactory.decodeFile(getExternalFilesDir(null).toString()+"/test1.jpg");
                    imageview.setImageBitmap(bitmap);
                    videoview.setVideoURI(Uri.parse(url30));
                    videoview.start();
                    break;
                case 8:
                    setSingleVideoVolume(Float.parseFloat(msg.obj.toString()),videoview);
                    break;
                case 9:
                    setSystemVolume(Float.parseFloat(msg.obj.toString())/100,MainActivity.this);
                    break;
                case 10:
                    //更新视频列表
                    ArrayList<String> video_temp_list= new ArrayList<>(Arrays.asList(msg.obj.toString().split(",")));
                    setVideoList(video_temp_list);
                    SharedPreferences.Editor editor2 = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                    editor2.putString("video_toPlay_list",video_temp_list.toString().substring(1,video_toPlay_list.toString().length()-1));
                    editor2.apply();
                    break;
                case 11:
                    //立即重复播放一个视频
                    setVideo(msg.obj.toString());
                    break;
                case 12:
                    //更换分屏样式，暂未写完，等待具体分屏样式确定
                    switch (msg.obj.toString()){
                        case "1":
                            setContentView(layout_video_picture);
//                            setContentView(layout_video_picture1);
                            break;
                        case "2":
                            setContentView(layout_video_picture);
                            break;
                        default:
                            break;
                    }
                    videoview = (VideoView) findViewById(R.id.videoView3);
                    break;
                case 13:
                    //更新生活信息，天气预报+限行
                    textView.setText(dateToday+" "+"\n"+weatherTypeToday+"\n"+temperatureToday+" ℃");
                    textView2.setText(dateTomorrow+"\n"+weatherTypeTomorrow+"\n"+temperatureTomorrow+" ℃");
                    int weatherType1 ;
                    if(weatherMap.get(weatherTypeToday)!=null){
                        weatherType1 =weatherMap.get(weatherTypeToday);
                    }else {weatherType1 =R.mipmap.a10;}
                    int weatherType2 ;
                    if(weatherMap.get(weatherTypeTomorrow)!=null){
                        weatherType2 =weatherMap.get(weatherTypeTomorrow);
                    }else {weatherType2 =R.mipmap.a10;}
                    System.out.println("weatherTypeToday"+weatherTypeToday);
                    Drawable drawable = getResources().getDrawable(weatherType1);
                    drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth()*1.4), (int)(drawable.getIntrinsicHeight()*1.4));
                    textView.setCompoundDrawables(null, null, drawable, null);
                    Drawable drawable2 = getResources().getDrawable(weatherType2);
                    drawable2.setBounds(0, 0, (int) (drawable2.getIntrinsicWidth()*1.4), (int)(drawable2.getIntrinsicHeight()*1.4));
                    textView2.setCompoundDrawables(null, null, drawable2, null);
                    break;
                case 14:
                    //设置图片播放列表
                    try {
                        ArrayList<String> picture_temp_list= new ArrayList<>(Arrays.asList(msg.obj.toString().split(",")));
                        setPictureList(picture_temp_list);
                        SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                        editor.putString("picture_toPlay_list",picture_temp_list.toString().substring(1,picture_temp_list.toString().length()-1));
                        editor.apply();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    break;
                case 15:
                    nextPicture();
                    break;
                case 16:
                    int tempPart1=msg.obj.toString().split(":").length;
                    String temp =setCutlineVideo(msg.obj.toString().split(":")[tempPart1-2],Integer.parseInt(msg.obj.toString().split(":")[tempPart1-1]));
                    if(temp.equals("fileNotFound")){
                        pubResult("fileNotFound");
                    }
                    break;
                case 17:
                    int tempPart2=msg.obj.toString().split(":").length;
                    String temp2 =setCutlineVideo2(msg.obj.toString().split(":")[tempPart2-2],Integer.parseInt(msg.obj.toString().split(":")[tempPart2-1]));
                    if(temp2.equals("fileNotFound")){
                        pubResult("fileNotFound");
                    }
                    break;
                case 18:
                    try{
                        MqttMessage mqttMsg=new MqttMessage();
                        mqttMsg.setPayload((mac+":"+msg.obj.toString()).getBytes());//设置消息内容
//        msg.setQos(2);//设置消息发送质量，可为0,1,2. 0代表至多一次
                        mqttMsg.setRetained(false);
                        mqttClient.publish(topicPubMac,mqttMsg);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    break;
                case 19:
                    try{
                        MqttMessage mqttMsg=new MqttMessage();
                        mqttMsg.setPayload(msg.obj.toString().split("--")[0].getBytes());//设置消息内容
//        msg.setQos(2);//设置消息发送质量，可为0,1,2. 0代表至多一次
                        mqttMsg.setRetained(false);
                        mqttClient.publish(msg.obj.toString().split("--")[1],mqttMsg);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //获取本机ip
    private String get_ip(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            return "0.0.0.0";
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return intToIp(ipAddress);
    }
    private String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    //关闭投影仪灯光
    public void close_light(){
        Intent intent = new Intent("com.android.sharpeye.dlp.close");
        sendBroadcast(intent);
    }
    //打开投影仪
    public void open_light(){
        Intent intent = new Intent("com.android.sharpeye.dlp.open");
        sendBroadcast(intent);
    }
    //单独设置每一个视频窗口的音量，而不是改系统音量
    public void setSingleVideoVolume(float volume,Object object) {
        try {
            Class<?> forName = Class.forName("android.widget.VideoView");
            Field field = forName.getDeclaredField("mMediaPlayer");
            field.setAccessible(true);
            MediaPlayer mMediaPlayer = (MediaPlayer) field.get(object);
            mMediaPlayer.setVolume(volume, volume);
        } catch (Exception e) {
        }
    }
    //更改系统音量
    private void setSystemVolume(float value, Context context) {
        System.out.println("why");
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//(最大值是15)
            int v;
            v = (int) (value * maxVolume);
            System.out.println(maxVolume);
            System.out.println(v);;
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //获取系统音量
    private int getSystemVolume(Context context){
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int mMaxVolume  = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//            int re = currentVolume/
            return currentVolume*100/mMaxVolume;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //读取文件列表，并返回 绝对路径+文件名
    public ArrayList<File> getFiles (String path) {
        path =getExternalFilesDir(null).toString();
        ArrayList<File> fileList = new ArrayList<>();
        File file = new File(path);
        if(file.isDirectory()){
            File [] files = file.listFiles();
            for (File f:files){
                fileList.add(f);
            }
        }
        return fileList;
    }
    // 返回总目录下 所有文件名
    public ArrayList<String> getFileNameList () {
        String path =getExternalFilesDir(null).toString();
        ArrayList<String> fileList = new ArrayList<>();
        File file = new File(path);
        if(file.isDirectory()){
            String [] files = file.list();
            for (String f:files){
                fileList.add(f);
            }
        }
        return fileList;
    }

    public void wait(int second){
        try {
            sleep(second*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void countVideoAndCurrent(){
        current_ad_id=video_toPlay_list.get(posForVideo).trim();
        if(adPlayStatistic.containsKey(current_ad_id)){
            adPlayStatistic.put(current_ad_id,adPlayStatistic.get(current_ad_id)+1);
        }else {
            adPlayStatistic.put(current_ad_id,1);
        }
    }
    private void countVideo(){
        if(adPlayStatistic.containsKey(current_ad_id)){
            adPlayStatistic.put(current_ad_id,adPlayStatistic.get(current_ad_id)+1);
        }else {
            adPlayStatistic.put(current_ad_id,1);
        }
    }
    //更改播放文件
    // 循环播放一个视频列表中的视频
    public void setVideoList( ArrayList<String> new_file_list){
        posForVideo=0;
        video_toPlay_list = new ArrayList<>(new_file_list);
        System.out.println(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(posForVideo));
        System.out.println("999999999");
        videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(posForVideo).trim()));
        System.out.println(video_toPlay_list.get(posForVideo));
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextVideo();
            }
        });
        videoview.start();
        countVideoAndCurrent();
    }
    //设备或者app重启时调用，恢复播放之前的视频
    public void setVideoListRestart( ArrayList<String> new_file_list,int pos){
        posForVideo=pos;
        video_toPlay_list = new ArrayList<>(new_file_list);
        System.out.println(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(posForVideo));
        videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(posForVideo).trim()));
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextVideo();
            }
        });
        videoview.start();
        countVideoAndCurrent();
    }

    //播放下一个视频
    private void nextVideo() {
// TODO Auto-generated method stub
        posForVideo++;
        if (posForVideo>=video_toPlay_list.size()) {
            posForVideo=0;
        }
        SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
        editor.putInt("posForVideo",posForVideo);
        editor.apply();
        System.out.println("视频");
        System.out.println(video_toPlay_list.get(posForVideo));
        videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(posForVideo).trim()));
//        videoview.setMediaController(mc);
//        videoview.requestFocus();
        videoview.start();
        countVideoAndCurrent();
    }
    // 循环播放一个视频
    public void setVideo(String fileName){
        videoview.stopPlayback();
        videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+fileName));
        videoview.start();
        current_ad_id=fileName;
        countVideo();
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                mediaPlayer.setLooping(true);
                videoview.start();
                countVideo();
            }
        });
    }
    //插队播放一个视频, n次,打断当前播放视频
    public String setCutlineVideo(String file_name,int times){
        cutlineVideo_times =times;
        if(!getFileNameList().contains(file_name)){
            return "fileNotFound";
        }
        videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+file_name));
        videoview.start();
        current_ad_id=file_name;
        countVideo();
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                videoview.setVideoPath(file_list.get(pos++).getPath());
                System.out.println(cutlineVideo_times);
                if(cutlineVideo_times-->1){
                    videoview.start();
                    countVideo();
                }else {
                    videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(posForVideo).trim()));
                    videoview.start();
                    countVideoAndCurrent();
                }
            }
        });
        return "ok";
    }
    //插队播放一个视频, n次,不打断当前播放视频
    public String setCutlineVideo2(final String file_name,int times){
        cutlineVideo_times2 =times;
        if(!getFileNameList().contains(file_name)){
            return "fileNotFound";
        }
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+file_name));
                videoview.start();
                current_ad_id=file_name;
                countVideo();
                videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
//                videoview.setVideoPath(file_list.get(pos++).getPath());
                        if(cutlineVideo_times2-->1){
                            videoview.start();
                            countVideo();
                        }else {
                            videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(posForVideo).trim()));
                            videoview.start();
                            countVideoAndCurrent();
                        }
                    }
                });
            }
        });
        return "ok";
    }

    //循环播放图片
//    setPictureList
    public void setPictureList( ArrayList<String> new_file_list){
//        videoview.onMeasure(100,100);
//        videoview.onMeasure(10,20); 没有用
        posForPicture=0;
        picture_toPlay_list = new ArrayList<>(new_file_list);
        System.out.println("$$$$$$");
        System.out.println(picture_toPlay_list);
        if (picture_toPlay_list.size()<1){
            System.out.println("文件列表中无文件");
            return;
        }else {
            System.out.println("文件列表");
            setPictureTimer();
        }
    }
    public void setPictureListRestart( ArrayList<String> new_file_list,int pos,int picture_time){
        posForPicture=0;
        picture_toPlay_list = new ArrayList<>(new_file_list);
        System.out.println("$$$$$$");
        System.out.println(picture_toPlay_list);
        if (picture_toPlay_list.size()<1){
            System.out.println("文件列表中无文件");
            return;
        }else {
            System.out.println("文件列表");
            setPictureTimer2(pos,picture_time);
        }
    }
    //播放下一个图片
    private void nextPicture() {
        if (posForPicture == picture_toPlay_list.size()) {
            posForPicture = 0;
        }
//        System.out.println(picture_toPlay_list);
//        System.out.println(picture_toPlay_list.get(posForPicture));
//        System.out.println(posForPicture);
        if(picture_time>5){
            SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
            editor.putInt("posForPicture",posForPicture);
            editor.apply();
        }
        imageview.setImageURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+picture_toPlay_list.get(posForPicture).trim()));
        posForPicture++;
    }
    private void setPictureTimer(){
        System.out.println("时间间隔"+picture_time);
        if(timerForPicture!=null){
            timerForPicture.cancel();
        }
        timerForPicture=new Timer();
//        timerForPicture.purge();
        posForPicture=0;
        if(timerTask!=null){timerTask.cancel();}
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what =15;
                handler.sendMessage(msg);
            }
        };
        System.out.println("图片时间间隔="+picture_time);
        timerForPicture.schedule(timerTask, 0,picture_time*1000);
    }
    private void setPictureTimer2(int pos,int picture_time){
        System.out.println("时间间隔"+picture_time);
        if(timerForPicture!=null){
            timerForPicture.cancel();
        }
        timerForPicture=new Timer();
        posForPicture=pos;
        if(timerTask!=null){timerTask.cancel();}
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what =15;
                handler.sendMessage(msg);
            }
        };
        timerForPicture.schedule(timerTask, 0,picture_time*1000);
    }
    //发送udp广播,通知控制端本机 ip mac 设备名
    private void send_udp_broadcast(Context context){
        String mac = get_mac(context);
        String local_ip = get_ip(context);
//        String broadcast_address = local_ip.split("\\.")[0]+"."+local_ip.split("\\.")[1]+"."+local_ip.split("\\.")[2]+".255"; //直接广播
        String broadcast_address = "255.255.255.255"; //本地广播
        String msg = local_ip+"|"+mac+"|projector";
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket();
            datagramSocket.setBroadcast(true);
            InetAddress address = InetAddress.getByName(broadcast_address);
            DatagramPacket datagramPacket = new DatagramPacket(msg.getBytes(), msg.length(),     address, 5500);
            datagramSocket.send(datagramPacket);
        } catch (Exception e) {
//            LogUtil.d(TAG,e.toString());
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }
    }
    public String get_mac(Context context) {
        String mac_s = "";
        try {
            byte[] mac;
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(get_ip(MainActivity.this)));
            mac = ne.getHardwareAddress();
            mac_s = byte2hex(mac);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mac_s;
    }
    public String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1) {
                hs = hs.append("0").append(stmp);
            } else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }
    //获取cpu利用率，目前在小米手机上测试有问题
    public static String getCPURateDesc_All(){
        String path = "/proc/stat";// 系统CPU信息文件
        long totalJiffies[]=new long[2];
        long totalIdle[]=new long[2];
        int firstCPUNum=0;//设置这个参数，这要是防止两次读取文件获知的CPU数量不同，导致不能计算。这里统一以第一次的CPU数量为基准
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        Pattern pattern= Pattern.compile(" [0-9]+");
        for(int i=0;i<2;i++) {
            totalJiffies[i]=0;
            totalIdle[i]=0;
            try {
                fileReader = new FileReader(path);
                bufferedReader = new BufferedReader(fileReader, 8192);
                int currentCPUNum=0;
                String str;
                while ((str = bufferedReader.readLine()) != null&&(i==0||currentCPUNum<firstCPUNum)) {
                    if (str.toLowerCase().startsWith("cpu")) {
                        currentCPUNum++;
                        int index = 0;
                        Matcher matcher = pattern.matcher(str);
                        while (matcher.find()) {
                            try {
                                long tempJiffies = Long.parseLong(matcher.group(0).trim());
                                totalJiffies[i] += tempJiffies;
                                if (index == 3) {//空闲时间为该行第4条栏目
                                    totalIdle[i] += tempJiffies;
                                }
                                index++;
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if(i==0){
                        firstCPUNum=currentCPUNum;
                        try {//暂停50毫秒，等待系统更新信息。
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        double rate=-1;
        if (totalJiffies[0]>0&&totalJiffies[1]>0&&totalJiffies[0]!=totalJiffies[1]){
            rate=1.0*((totalJiffies[1]-totalIdle[1])-(totalJiffies[0]-totalIdle[0]))/(totalJiffies[1]-totalJiffies[0]);
        }

        Log.d("CpuUtils","zrx---- cpu_rate:"+rate);
        return String.format("%.2f",rate);
    }

    static public long getAvailableInternalMemorySize() {
        //剩余内部存储大小
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        //获取可用区块数量
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize/1000000;
    }

    private void setLayoutSize(View layout,int layoutSize,int left,int top){
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) layout.getLayoutParams();
//        layoutParams1.gravity= Gravity.CENTER_HORIZONTAL;
//        layoutParams1.gravity= Gravity.CENTER_VERTICAL;
        layoutParams1.width =11*layoutSize;
        layoutParams1.height=4*layoutSize;
        layoutParams1.leftMargin = left;
        layoutParams1.topMargin = -1*top;
        layout.setLayoutParams(layoutParams1);
//        setContentView(layout,new ConstraintLayout.LayoutParams(layout.getWidth(), layout.getHeight()));
        textView =(TextView)findViewById(R.id.textView);
        textView2 =(TextView)findViewById(R.id.textView2);
        videoview = (VideoView) findViewById(R.id.videoView);
        imageview = (RoundedImageView) findViewById(R.id.imageView);
        imageview2 = (RoundedImageView) findViewById(R.id.imageView2);
        SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
        editor.putInt("layoutSize",layoutSize);
        editor.putInt("left_margin",left);
        editor.putInt("top_margin",top);
        editor.apply();
    }
    //重启
    private void reboot(){
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        //与正常页面跳转一样可传递序列化数据,在Launch页面内获得
        intent.putExtra("REBOOT","reboot");
        PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_projector);
        //LayoutInflater inflater = getLayoutInflater();
//        LayoutInflater inflater = LayoutInflater.from(this);
//        layout1 = inflater.inflate(R.layout.activity_main_projector, null);
        layout1 = findViewById(R.id.constraintlayout1);
        System.out.println(getSharedPreferences("data_try1",MODE_PRIVATE).getInt("layoutSize",80));
        mac = get_mac(MainActivity.this);
        SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
        editor.putString("mac",mac);
        editor.apply();
        topicSubMac = topicSub+"/"+mac;
        topicSubList = new String[]{topicSubMac,topicSub,topicVolume,topicUpdateAd,topicPush};
        for(int i =0;i<topicSubList.length;i++){
            topicSubList[i]=topicSubList[i].replaceFirst("clientid",mac);
            System.out.println(i);
            System.out.println(topicSubList[i]);
        }
        topicResult=topicResult.replaceFirst("clientid",mac);
        topicStatus= topicStatus.replaceFirst("clientid",mac);

        mqttCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                mqttIsConnect=false;
                Message msg = new Message();
                msg.arg1 = MQTT_STATE_LOST;
            }
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                Message msg = new Message();
                msg.arg1 = MQTT_STATE_RECEIVE;
                String content = new String(mqttMessage.getPayload());
                System.out.println("topic=" + s + "  接收mqtt：" + content);
//                final String topicSubFinal = (topicSub1) + get_mac(MainActivity.this);
                switch (content) {
                    case "start_video":
                        Message message1 = new Message();
                        message1.what = 0;
                        message1.obj = "start_video";
                        handler.sendMessage(message1);
                        break;
                    case "pause_video":
                        Message message2 = new Message();
                        message2.what = 0;
                        message2.obj = "pause_video";
                        handler.sendMessage(message2);
                        break;
                    case "close_light":
                        close_light();
                        light_machine= "disable";
                        pubResult("关闭光机成功");
                        break;
                    case "open_light":
                        open_light();
                        light_machine= "enable";
                        pubResult("打开光机成功");
                        break;
                    case "updt":

                        break;
                    case "get_file_list":
                        pubResult(getFileNameList().toString()+"["+getFreeSize()+"]");
                        break;
                    case "get_video_list":
                        //视频播放列表
                        if (video_toPlay_list == null) {
                            pubResult("video_toPlay_list is null");
                        } else {
                            pubResult(video_toPlay_list.toString());
                        }
                        break;
                    case "get_picture_list":
                        if(picture_toPlay_list==null){
                            pubResult("picture_toPlay_list is null");
                        }else {
                            pubResult(picture_toPlay_list.toString());
                        }
                        break;
                    case "reboot":

                        break;
                    default:
                        if (content.startsWith("update_video_list")) {
                            Message message3 = new Message();
                            message3.what = 10;
                            message3.obj =  content.split(":")[content.split(":").length-1];
                            handler.sendMessage(message3);
                        }else if(content.startsWith("update_picture_list")){
                            Message message3 = new Message();
                            message3.what = 14;
                            message3.obj = content.split(":")[content.split(":").length-1];
                            handler.sendMessage(message3);
                            break;
                        }else if(content.startsWith("delete_file_list")){
                            String deleteFileList = content.split(":")[content.split(":").length-1];
                            for(String fileTodelete : deleteFileList.split(",")){
                                deleteSingleFile(getExternalFilesDir(null).toString()+"/"+fileTodelete);
                            }
                        }else if(content.startsWith("change_system_volume")){
                            Message message3 = new Message();
                            message3.what = 9;
                            message3.obj = content.split(":")[content.split(":").length-1];
                            handler.sendMessage(message3);
                        }else if (content.startsWith("set_single_video")){
                            Message message5 = new Message();
                            message5.what = 11;
                            message5.obj = content.split(":")[content.split(":").length-1];
                            handler.sendMessage(message5);
                        }else if(content.startsWith("change_layout")){
                            Message message6 = new Message();
                            message6.what = 12;
                            message6.obj = content.split(":")[content.split(":").length-1];
                            handler.sendMessage(message6);
                        }
                        else if(content.startsWith("set_cut_line_videoNotInterrupt")){
                            Message message6 = new Message();
                            message6.what = 17;
                            message6.obj = content;
                            handler.sendMessage(message6);
                        }
                        else if(content.startsWith("set_cut_line_video")){
                            Message message6 = new Message();
                            message6.what = 16;
                            message6.obj = content;
                            handler.sendMessage(message6);
                        }
                        else if(content.startsWith("change_city_")){
                            defaultCity=content.split("_")[content.split("_").length-1];
                            SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                            editor.putString("defaultCity",defaultCity);
                            editor.apply();
                            updateWeather(defaultCity);
                        }else if(content.startsWith("set_picture_time_")){
                            picture_time = Integer.parseInt(content.split("_")[content.split("_").length-1]);
                            SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                            editor.putInt("picture_time",picture_time);
                            editor.apply();
                            setPictureTimer();
                        }else if(content.startsWith("set_layoutsize")){
                            Message message6 = new Message();
                            message6.what = 2;
                            message6.obj = (content.split("_")[content.split("_").length-1]);
                            handler.sendMessage(message6);
                        }else if(content.startsWith("set_qrcode")){
                            //二维码图片
                            Message message6 = new Message();
                            message6.what = 3;
                            message6.obj = content.split(":")[content.split(":").length-1];
                            handler.sendMessage(message6);
                        }
                        pubResult(content+":success");
                        break;
//                    case topicData:
//                        byte[] file_byte = mqttMessage.getPayload();
//                        String fileName = transportFileName;
//                        try{
//                            OutputStream os = new FileOutputStream(getExternalFilesDir(null).toString()+ "/"+fileName);
//                            os.write(file_byte, 0, file_byte.length-10);
//                            os.flush();
//                            Log.w("传输文件","传输完成");
//                            //pair.send(transport_file_name+"传输完成");
//                            os.close();
//                        }catch (IOException e){
//                            e.printStackTrace();
//                        }
//                        break;
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        };

        timerForPicture = new Timer();
        textView =(TextView)findViewById(R.id.textView);
        textView2 =(TextView)findViewById(R.id.textView2);
        videoview = (VideoView) findViewById(R.id.videoView);
        imageview = (RoundedImageView) findViewById(R.id.imageView);
//        imageview.setImageURI(Uri.parse(getExternalFilesDir(null).toString()+ "/new2.jpg"));
        weatherMap = initWeatherMap();
        adPlayStatistic = new HashMap<>();
        restart();
        newMqttClient();
//        for(int i=0;i<10;i++){
//            pubResult(getCPURateDesc_All());
//            wait(1);
//        }
//        pubOrderTopic("cpu","testMqtt");
        System.out.println(get_mac(MainActivity.this));
        System.out.println(android.os.Build.MANUFACTURER);
        setUpdateWeatherTimer();
        Toast.makeText(MainActivity.this,get_ip(MainActivity.this).split("\\.")[3],Toast.LENGTH_LONG);
        updateWeather(defaultCity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoview!=null){
            videoview.suspend();
        }
    }
//
//    class TestSensorListener implements SensorEventListener {
//
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            // 读取加速度传感器数值，values数组0,1,2分别对应x,y,z轴的加速度
//            Log.i(TAG, "onSensorChanged: " + event.values[0] + ", " + event.values[1] + ", " + event.values[2]);
////            mSensorInfoA.setText(event.values[0] + ", " + event.values[1] + ", " + event.values[2]);
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//            Log.i(TAG, "onAccuracyChanged");
//        }
//
//    }
    //定时刷新天气信息
    private void setUpdateWeatherTimer(){
        timerForWeather = new Timer();
        timerForWeather.schedule(new TimerTask(){
            public void run(){
                updateWeather(defaultCity);
//                timer.cancel();
            }
        }, 12,30*60*1000);
    }
    //获取天气
    private void updateWeather(String city)
    {
        //    final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        final String address = "http://wthrcdn.etouch.cn/weather_mini?city="+city;
        Log.d("Address:",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(address);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(8000);
                    urlConnection.setReadTimeout(8000);
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer sb = new StringBuffer();
                    String str;
                    while((str=reader.readLine())!=null)
                    {
                        sb.append(str);
                        System.out.println(defaultCity);
                        Log.d("date from url",str);
                        JSONObject result = new JSONObject(str);
                        JSONObject temp0 =result.getJSONObject("data").getJSONArray("forecast").getJSONObject(0);
                        JSONObject temp1 =result.getJSONObject("data").getJSONArray("forecast").getJSONObject(1);
                        weatherTypeToday=temp0.getString("type");
                        dateToday=temp0.getString("date");
                        weatherTypeTomorrow=temp1.getString("type");
                        dateTomorrow=temp1.getString("date");
                        temperatureToday=result.getJSONObject("data").getString("wendu");
                        temperatureTomorrow =temp1.getString("low").substring(2,5)+ "~"+temp1.getString("high").substring(2,5);
                        Message msg =new Message();
                        msg.what=13;
                        handler.sendMessage(msg);
                    }
                    String response = sb.toString();
                    Log.d("response",response);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void restart(){

        SharedPreferences pref = getSharedPreferences("data_try1",MODE_PRIVATE);
        setLayoutSize(layout1,pref.getInt("layoutSize",90),pref.getInt("left_margin",0),pref.getInt("top_margin",0));
        pu_ip = pref.getString("pu_ip","not_set");
//        mac = pref.getString("mac",get_mac(MainActivity.this));
        defaultCity = pref.getString("defaultCity","西安");
        updateWeather(defaultCity);
        video_toPlay_list = new ArrayList<>(Arrays.asList(pref.getString("video_toPlay_list","not_set").split(",")));
        picture_toPlay_list = new ArrayList<>(Arrays.asList(pref.getString("picture_toPlay_list","not_set").split(",")));
        if(!pu_ip.equals("not_set")){
//            test_sub();
        }
        if(video_toPlay_list!=null){
            if(video_toPlay_list.get(0).equals("not_set")){
                System.out.println("video_toPlay_list"+video_toPlay_list);
            }else {
                System.out.println("*****");
                System.out.println(video_toPlay_list);
                System.out.println(pref.getInt("posForVideo",0));
                videoview = findViewById(R.id.videoView);
                setVideoListRestart(video_toPlay_list,pref.getInt("posForVideo",0));
            }
        }else {
            System.out.println("video_toPlay_list = null");
        }
        if(picture_toPlay_list.get(0).equals("not_set")){
            System.out.println("picture_toPlay_list"+picture_toPlay_list);
        }else {
            picture_time = pref.getInt("picture_time",2);
            setPictureListRestart(picture_toPlay_list,pref.getInt("posForPicture",0),pref.getInt("picture_time",2));
//                    setPictureList(picture_toPlay_list);
        }
//        setLayoutPosition(layout1);

//            posForPicture = pref.getInt("posForPicture",0);


//            ArrayList<String> video_toPlay_list_t = new ArrayList<>();
//            video_toPlay_list_t.add("10.mp4");
//            setVideoList(video_toPlay_list_t);
    }
    private String getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(MainActivity.this, blockSize * availableBlocks);
    }
    private String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(MainActivity.this, blockSize * availableBlocks);
    }
    // 内部存储+外部存储。
    private String getFreeSize(){
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        File path2 = Environment.getDataDirectory();
        StatFs stat2 = new StatFs(path2.getPath());
        long blockSize2 = stat2.getBlockSize();
        long availableBlocks2 = stat2.getAvailableBlocks();
        return Formatter.formatFileSize(MainActivity.this, blockSize * availableBlocks +blockSize2 * availableBlocks2 );
    }
    //获取磁盘占用率,精度3
    private String getRomUseRate(){
        final StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        final StatFs statFs2 = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long totalCounts = statFs.getBlockCount()+statFs2.getBlockCount();//总共的block数
        long availableCounts = statFs.getAvailableBlocks()+statFs2.getAvailableBlocks() ;
        return String.format("%.3f",1-availableCounts*1.0/totalCounts);
    }



    public static long getFreeMemory(Context context) {
        //运存
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        // 单位Byte
        return info.availMem;
    }
    private long getTotalMemory() {
        try {
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));
            //包装一个一行行读取的流
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
            //取到所有的内存信息
            String memTotal = bufferedReader.readLine();
            StringBuffer sb = new StringBuffer();
            for (char c : memTotal.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            //为了方便格式化 所以乘以1024
            long totalMemory = Long.parseLong(sb.toString()) * 1024;
            return totalMemory;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    private String getRamUseRate(){
//        String.format("%.2f",1-getFreeMemory(MainActivity.this).doubleValue/getTotalMemory().doubleValue)
        return String.format("%.3f",1-getFreeMemory(MainActivity.this)*1.0/getTotalMemory());
    }
    public HashMap<String, Integer> initWeatherMap() {
        HashMap<String,Integer> map  = new HashMap<>();
        map.put("多云",R.mipmap.a1);
        map.put("多云转晴",R.mipmap.a2);
        map.put("小雨",R.mipmap.a3);
        map.put("中雨",R.mipmap.a4);
        map.put("大雨",R.mipmap.a5);
        map.put("中雨转晴",R.mipmap.a6);
        map.put("小雪",R.mipmap.a7);
        map.put("中雪",R.mipmap.a8);
        map.put("雨夹雪",R.mipmap.a9);
        map.put("夜晚",R.mipmap.a10);
        map.put("夜晚多云",R.mipmap.a11);
        map.put("冰雹",R.mipmap.a12);
        map.put("沙尘暴",R.mipmap.a13);
        map.put("雾霾",R.mipmap.a14);
        map.put("雨夹冰雹",R.mipmap.a15);
        map.put("霜降",R.mipmap.a16);
        map.put("雷阵雨伴有冰雹",R.mipmap.a17);
        map.put("雷阵雨转晴",R.mipmap.a18);
        map.put("晴",R.mipmap.a19);
        map.put("雷阵雨",R.mipmap.a20);
        map.put("浮尘",R.mipmap.a21);
        map.put("暴雪",R.mipmap.a22);
        map.put("大雪",R.mipmap.a23);
        map.put("阵雪",R.mipmap.a24);
        map.put("暴雨",R.mipmap.a25);
        map.put("大暴雨",R.mipmap.a26);
        map.put("特大暴雨",R.mipmap.a27);
        map.put("雾",R.mipmap.a28);
        map.put("大暴雨-特大暴雨",R.mipmap.a29);
        map.put("暴雨-大暴雨",R.mipmap.a30);
        map.put("大雨-暴雨",R.mipmap.a31);
        map.put("中雨-大雨",R.mipmap.a32);
        map.put("小雨-中雨",R.mipmap.a33);
        map.put("大雪-暴雪",R.mipmap.a34);
        map.put("中雪-大雪",R.mipmap.a35);
        map.put("小雪-中雪",R.mipmap.a36);
        map.put("阴",R.mipmap.a37);
        return map;
    }
    private boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
//                Toast.makeText(getApplicationContext(), "删除单个文件" + filePath$Name + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return false;
        }

    }
    private String getProjectorInfo(){
        String re="{\"projector_mac\"";
        String result ="{\"projector_mac\":\""+mac+"\",\"cpu_usage_rate\":\""+getCPURateDesc_All()+"\",\"memory_usage_rate\":\""+getRamUseRate()+"\",\"disk_usage_rate\":\""+getRomUseRate()+"\",\"inner_ip\":\""+get_ip(MainActivity.this)+"\"," +
                "\"volume\":\""+getSystemVolume(MainActivity.this)+"\"," +
                "\"light_machine\":\""+light_machine+"\"," +
                "\"current_ad_id\":\""+current_ad_id+"\"," +
                "\"ad_play_statistic\":[" +
                "{" +
                "\"ad_id\":\"xxxx\"," +
                "\"play_num\":\"xxx\"," +
                "\"type\":\"xxx\"" +
                "}," +
                "{" +
                "\"ad_id\":\"xxxx\"," +
                "\"play_num\":\"xxx\"," +
                "\"type\":\"xxx\"" +
                "}," +
                "{" +
                "\"ad_id\":\"xxxx\"," +
                "\"play_num\":\"xxx\",\n" +
                "\"type\":\"xxx\"\n" +
                "}\n" +
                "]\n" +
                "}"+adPlayStatistic.toString();
        return result;
    }
    private void newMqttClient(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String host = mqttIp;
                MqttConnectOptions options =new MqttConnectOptions();
                options.setUserName(mqttUsername);
                options.setPassword(mqttPassword.toCharArray());
                options.setCleanSession(false);
                String clientId = "projector-"+get_mac(MainActivity.this);
                try {
                    MemoryPersistence persistence = new MemoryPersistence();
                    mqttClient=new MqttClient(host,clientId,persistence);
                    mqttClient.setCallback(mqttCallback);//设置回调函数
                    mqttClient.connect(options);//连接broker
                    mqttClient.subscribe(topicSubList);
//                    MqttMessage msg=new MqttMessage();
//                    String msgStr="Hello World";
//                    msg.setPayload(msgStr.getBytes());//设置消息内容
////                    msg.setQos(2);//设置消息发送质量，可为0,1,2.
//                    msg.setRetained(false);//服务器是否保存最后一条消息，若保存，client再次上线时，将再次受到上次发送的最后一条消息。
//                    mqttClient.publish("topic1",msg);//设置消息的topic，并发送。

//                    MqttMessage msg2=new MqttMessage();
//                    msgStr = "close_light";
//                    msg2.setPayload(msgStr.getBytes());
//                    mqttClient.publish("order1",msg2);
                    pubOrderTopic(mac,"ddzl/projector/broker/initMac");
                    pubOrderTopic(String.valueOf(getTotalMemory()),topicResult);
                    pubOrderTopic(String.valueOf(getFreeMemory(MainActivity.this)),topicResult);
                    pubOrderTopic(getRamUseRate(),topicResult);
                    pubOrderTopic(getRomUseRate(),topicResult);
                    System.out.println(getProjectorInfo());
                    heartBeat();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }}).start();



    }

    private void heartBeat(){
        Timer timerForHeartBeat =new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                pubStatus(getProjectorInfo());
            }
        };
        timerForHeartBeat.schedule(timerTask, 0,30*1000);
    }
    private void newMqttClient2(){
        String host = mqttIp;
        MqttConnectOptions options =new MqttConnectOptions();
        options.setUserName(mqttUsername);
        options.setPassword(mqttPassword.toCharArray());
        options.setCleanSession(false);
        String clientId = get_mac(MainActivity.this);
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient=new MqttClient(host,clientId,persistence);
            mqttClient.setCallback(mqttCallback);//设置回调函数
            mqttClient.connect(options);//连接broker
            mqttClient.subscribe(topicSubList);
//            MqttMessage msg=new MqttMessage();
//            String msgStr="Hello World";
//            msg.setPayload(msgStr.getBytes());//设置消息内容
////                    msg.setQos(2);//设置消息发送质量，可为0,1,2.
//            msg.setRetained(false);//服务器是否保存最后一条消息，若保存，client再次上线时，将再次受到上次发送的最后一条消息。
//            mqttClient.publish("topic1",msg);//设置消息的topic，并发送。
//                    MqttMessage msg2=new MqttMessage();
//                    msgStr = "close_light";
//                    msg2.setPayload(msgStr.getBytes());
//                    mqttClient.publish("order1",msg2);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private void test_mqtt(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String host = "tcp://39.100.88.26:1883";

                MqttConnectOptions options =new MqttConnectOptions();
                options.setUserName("xupeng");
                options.setPassword("000".toCharArray());
                options.setCleanSession(true);
                String clientId = "s123";
                String subscribeTopics="topic1";
                try {
                    MemoryPersistence persistence = new MemoryPersistence();
                    MqttClient mqttClient=new MqttClient(host,clientId,persistence);
                    mqttClient.setCallback(mqttCallback);//设置回调函数
                    mqttClient.connect(options);//连接broker
                    mqttClient.subscribe(subscribeTopics);//设置监听的topic

                    MqttMessage msg=new MqttMessage();
                    String msgStr="Hello World";
                    msg.setPayload(msgStr.getBytes());//设置消息内容
                    msg.setQos(2);//设置消息发送质量，可为0,1,2.
                    msg.setRetained(false);//服务器是否保存最后一条消息，若保存，client再次上线时，将再次受到上次发送的最后一条消息。
                    mqttClient.publish("topic1",msg);//设置消息的topic，并发送。
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }
}



