package com.example.try1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.alibaba.fastjson.JSON;
import com.makeramen.roundedimageview.RoundedImageView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;

import org.eclipse.paho.client.mqttv3.MqttToken;
import org.jeromq.ZMQ;
import org.jeromq.ZMQException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.MqttListener;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import static android.view.KeyEvent.KEYCODE_1;
import static android.view.KeyEvent.KEYCODE_2;
import static com.example.try1.MyTool.connectWifi;
import static com.example.try1.MyTool.getTime;
import static java.lang.Thread.sleep;
public class MainActivity extends Activity {
    private String defaultCity="西安";
    private LogError mylog=new LogError();
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
    private ArrayList<Integer> picture_toPlay_list_time;
    private int picture_time=2;//每张图片的显示时间
    private VideoView videoview;
    private RoundedImageView imageview;
    private RoundedImageView imageview2;
    private View layout_video_picture;
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
    public String topicSub="ddzl/broker/projector";
    public String topicPub="ddzl/projector/broker";
    String topicResult = "ddzl/projector/broker/clientid/exec/result";
    String topicError = "ddzl/projector/broker/clientid/exec/error";
    String topicVolume = "ddzl/broker/projector/clientid/order/msg";
    String topicUpdateAd ="ddzl/broker/projector/clientid/update_advertisement";
    String topicStatus = "ddzl/projector/broker/clientid/device_status";
    String topicPush ="ddzl/broker/projector/clientid/advertise/push";
    String topicTest = "ddzl/broker/projector/test";
    private String defaultWifiSSID ="ddzl";
    private String defaultWifiPWD ="ddzl2019";
    private String defaultWifiTYPE ="WPA2";
//    private String defaultWifiSSID ="dasen_wifi_test";
//    private String defaultWifiPWD ="wifi@mima";
//    private String defaultWifiTYPE ="WPA2";
    public String topicPubMac;
    public String topicSubMac;
    public String mac;
    public String macColon;
    public String ip;
    private String [] topicSubList;
    public String transportFileName;
    private MqttCallback mqttCallback;
    private int volume;
    private String light_machine ;
    private String current_video_ad_id;
    private String current_picture_ad_id;
    private String wordAd;
    private HashMap<String,Integer> adPlayStatistic;
    private int picCutInTimes= 0;
    private String picCutInName;
    long time1;
    long time2;
    private int systemVolume;
    private int triggerForWifiReconnect=0;
    private String packageName ;
    private String rawDir;
    private String externalFilesDir ;
    private final static int TOPIC_RESULT_ARG1=0;
    private final static int TOPIC_ERROR_ARG1=1;
    private final static int TOPIC_STATUS_ARG1=2;
    private long totalMemory =0;
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
//    public void pubOrderTopic(String s,String topic){
//        Message message = new Message();
//        message.what = 19;
//        message.obj =s+"--"+topic ;
//        handler.sendMessage(message);
//    }
    private void pubOrderTopic(String order ,String topic){
        Message message;
        if(topic.equals(topicError)) {  message = Message.obtain(handler,5,TOPIC_ERROR_ARG1,0,order);}
        else if(topic.equals(topicResult)) {  message = Message.obtain(handler,5,TOPIC_RESULT_ARG1,0,order);}
        else if(topic.equals(topicStatus)) {  message = Message.obtain(handler,5,TOPIC_STATUS_ARG1,0,order);}
        else {message = Message.obtain(handler,5,TOPIC_STATUS_ARG1,0,order);}
        message.sendToTarget();
    }

    public void pubResult(String s){
        pubOrderTopic(s,topicResult);
        System.out.println("topicResult=");
        System.out.println(topicResult);
    }
    public void pubError(String s){
        pubOrderTopic(s,topicError);
    }
    public void pubStatus(String s){
        pubOrderTopic(s,topicStatus);
    }

    // 接收子线程中的信息，来更新控件，因为只有主线程才能更新控件
    private Handler  handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
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
                    break;
                case 4:
                    videoview.resume();
                    break;
                case 5:
                    MqttMessage mqttMsg0=new MqttMessage();
                    try{
                        switch (msg.arg1){
                            case TOPIC_ERROR_ARG1:mqttMsg0.setPayload(msg.obj.toString().getBytes());mqttMsg0.setQos(2);mqttClient.publish(topicError,mqttMsg0);break;
                            case TOPIC_STATUS_ARG1:mqttMsg0.setPayload(msg.obj.toString().getBytes());mqttMsg0.setQos(0);mqttClient.publish(topicStatus,mqttMsg0);break;
                            case TOPIC_RESULT_ARG1:mqttMsg0.setPayload(msg.obj.toString().getBytes());mqttMsg0.setQos(1);mqttClient.publish(topicResult,mqttMsg0);break;
                            default:mqttMsg0.setPayload("topic wrong".getBytes());break;
                        }
//                          msg.setQos(2);//设置消息发送质量，可为0,1,2. 0代表至多一次,2代表仅1次
//                        mqttMsg.setRetained(false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    try {
                        msg.recycle(); //it can work in some situations
                    } catch (IllegalStateException e) {
                        handler.removeMessages(5); //if recycle doesnt work we do it manually
                    }
                    break;
                case 6:
                    break;
                case 7:
//                    //style 1   ，图片+视频
//                    setContentView(layout_video_picture);
//                    videoview = (VideoView) findViewById(R.id.videoView3);
//                    Bitmap bitmap = BitmapFactory.decodeFile(getExternalFilesDir(null).toString()+"/test1.jpg");
//                    imageview.setImageBitmap(bitmap);
//                    videoview.setVideoURI(Uri.parse(url30));
//                    videoview.start();
                    break;
                case 8:
                    setSingleVideoVolume(Float.parseFloat(msg.obj.toString()),videoview);
                    break;
                case 9:
                    systemVolume = Integer.parseInt(msg.obj.toString());
                    setSystemVolume(Float.parseFloat(msg.obj.toString())/100,MainActivity.this);
                    break;
                case 10:
                    //更新视频列表
                    ArrayList<String> video_temp_list= new ArrayList<>(Arrays.asList(msg.obj.toString().split(",")));
                    setVideoList2(video_temp_list);
                    SharedPreferences.Editor editor2 = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                    editor2.putString("video_toPlay_list",video_temp_list.toString().substring(1,video_toPlay_list.toString().length()-1));
                    editor2.apply();
                    System.out.println("视频播放列表为"+video_toPlay_list);
                    break;
                case 11:
                    //立即重复播放一个视频
                    setVideo(msg.obj.toString());
                    break;
                case 12:
                    //更换分屏样式，暂未写完，等待具体分屏样式确定
//                    switch (msg.obj.toString()){
//                        case "1":
//                            setContentView(layout_video_picture);
////                            setContentView(layout_video_picture1);
//                            break;
//                        case "2":
//                            setContentView(layout_video_picture);
//                            break;
//                        default:
//                            break;
//                    }//更新videoview获取
                    break;
                case 13:
                    //更新生活信息，天气预报+限行
//                    textView.setText(dateToday+" "+"\n"+weatherTypeToday+"\n"+temperatureToday+" ℃");
//                    textView2.setText(dateTomorrow+"\n"+weatherTypeTomorrow+"\n"+temperatureTomorrow+" ℃");
                    int weatherType1 ;
                    if(weatherMap.get(weatherTypeToday)!=null){
                        weatherType1 =weatherMap.get(weatherTypeToday);
                    }else {weatherType1 =R.mipmap.a10;}
                    int weatherType2 ;
                    if(weatherMap.get(weatherTypeTomorrow)!=null){
                        weatherType2 =weatherMap.get(weatherTypeTomorrow);
                    }else {weatherType2 =R.mipmap.a10;}
//                    System.out.println("weatherTypeToday"+weatherTypeToday);
//                    Drawable drawable = getResources().getDrawable(weatherType1);
//                    drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth()*1.4), (int)(drawable.getIntrinsicHeight()*1.4));
//                    textView.setCompoundDrawables(null, null, drawable, null);
//                    Drawable drawable2 = getResources().getDrawable(weatherType2);
//                    drawable2.setBounds(0, 0, (int) (drawable2.getIntrinsicWidth()*1.4), (int)(drawable2.getIntrinsicHeight()*1.4));
//                    textView2.setCompoundDrawables(null, null, drawable2, null);
                    break;
                case 14:
                    //设置图片播放列表
                    break;
                case 15:
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
                case 20:
                    //{"png1.png":"4","p":3}
                    ArrayList <String>tempPicList = new ArrayList();
                    ArrayList <Integer>tempPicTimeList = new ArrayList();
                    for(String s:msg.obj.toString().split(",")){
//                        System.out.println(s);
                        tempPicList.add(s.split(":")[0]);
                        tempPicTimeList.add(Integer.parseInt(s.split(":")[s.split(":").length-1]));
                    }
                    System.out.println("why");
                    System.out.println(tempPicList);
                    System.out.println(tempPicTimeList);
                    SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                    editor.putString("picture_toPlay_list",tempPicList.toString().substring(1,tempPicList.toString().length()-1));
                    editor.putString("picture_toPlay_list_time",tempPicTimeList.toString().substring(1,tempPicTimeList.toString().length()-1));
                    editor.apply();
                    setPictureListAndTime(tempPicList,tempPicTimeList);
//                    switch (msg.arg1){
////                        case 2: setPictureListAndTime(tempPicList,tempPicTimeList);break;
////                        case 3: setPictureListAndTime3(tempPicList,tempPicTimeList);break;
////                    }
                    break;
                case 21:
                    final String url = msg.obj.toString().split("@@")[0];
                    final String fileName = msg.toString().split("@@")[msg.toString().split("@@").length-1].split(" ")[0];
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DOWNLOAD(url,fileName);
                        }
                    }).start();
                    break;
                case 22:
                    if(msg.obj.toString().split(":").length==3){
                        String wifiName= msg.obj.toString().split(":")[0];
                        String wifiPwd= msg.obj.toString().split(":")[1];
                        String wifiType =  msg.obj.toString().split(":")[ msg.obj.toString().split(":").length-1];
                        String wifiConnectResult=connectWifi(MainActivity.this,wifiName,wifiPwd,wifiType);
                        pubResult(wifiConnectResult);
                    }else {pubResult("命令格式错误");}
                    break;
                case 23:
                    wordAd = msg.obj.toString();
                    SharedPreferences.Editor editor3 = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                    editor3.putString("wordAd",wordAd);
                    editor3.apply();
                    textView.setText(msg.obj.toString());
                    System.out.println();
                    break;
                case 24:
                    String tempOrder =  msg.obj.toString();
                    switch (tempOrder){
                        case "testFunction":
                            testFunction();
                            break;
                        case "getAllParas":
                            getAllParas();
                            break;
                        case "getErrorLog":
                            pubError(getErrorLog());
                            break;
                        default:
                            if(tempOrder.startsWith("setAnyParas")){
                                String Separator = "::";
                                if(tempOrder.split(Separator).length==3){
                                    setAnyParas(tempOrder.split(Separator)[1],tempOrder.split(Separator)[2]);
                                }
                            }
                            break;
                    }

                    break;
                case 25:
                    //图片不打断插入
                    String Separator = ":";
                    if(msg.obj.toString().split(Separator).length==3){
                        String tempPicName = msg.obj.toString().split(Separator)[1];
                        String tempPicNum = msg.obj.toString().split(Separator)[2];
                        setCutlinePictureNotInterrupt(tempPicName,tempPicNum);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void DOWNLOAD( String path, String fileName) {
        String dirName =externalFilesDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            System.out.println("如果相等的话表示当前的sdcard挂载在手机上并且是可用的");
        }
        final String fileName1 = dirName +"/"+ fileName;
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(fileName1);
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            pubResult(fileName+"下载完成");
//            Toast.makeText(MainActivity.this,fileName+"下载完成", LENGTH_LONG).show();
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
    //打开投影仪光机
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
            return currentVolume*100/mMaxVolume;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //读取文件列表，并返回 绝对路径+文件名
    public ArrayList<File> getFiles (String path) {
        path =externalFilesDir;
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
        String path =externalFilesDir;
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
    private void countVideoAndCurrent(){
        current_video_ad_id=video_toPlay_list.get(posForVideo).trim();
        countVideo();
    }
    private void countVideo(){
        if(adPlayStatistic.containsKey(current_video_ad_id)){
            adPlayStatistic.put(current_video_ad_id,adPlayStatistic.get(current_video_ad_id)+1);
        }else {
            adPlayStatistic.put(current_video_ad_id,1);
        }
    }
    private void countPicture(){
        if(adPlayStatistic.containsKey(current_picture_ad_id)){
            adPlayStatistic.put(current_picture_ad_id,adPlayStatistic.get(current_picture_ad_id)+1);
        }else {
            adPlayStatistic.put(current_picture_ad_id,1);
        }
    }
    private void countPictureAndCurrent(){
        current_picture_ad_id = picture_toPlay_list.get(posForPicture).trim();
        countPicture();
    }
    //更改播放文件
    // 循环播放一个视频列表中的视频
    public void setVideoList( ArrayList<String> new_file_list){
        posForVideo=0;
        video_toPlay_list = new ArrayList<>(new_file_list);
        System.out.println(externalFilesDir+video_toPlay_list.get(posForVideo));
        System.out.println("999999999");
        videoview.setVideoURI(Uri.parse(externalFilesDir+video_toPlay_list.get(posForVideo).trim()));
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
    //播放 磁盘上的文件，没找到就播放 apk自带文件，或者sd卡文件
    public void setVideoList2( ArrayList<String> filenames){
        posForVideo=0;
        video_toPlay_list = new ArrayList<>(filenames);
        File file = new File(externalFilesDir+"/"+video_toPlay_list.get(posForVideo).trim());
        if (!file.exists()) {
            videoview.setVideoURI(Uri.parse(rawDir+video_toPlay_list.get(posForVideo).trim().split("\\.")[0]));
            System.out.println("播放视频----------"+video_toPlay_list.get(posForVideo));
        }else {
            videoview.setVideoURI(Uri.parse(externalFilesDir+video_toPlay_list.get(posForVideo).trim()));
            System.out.println("开始播放视频:"+video_toPlay_list.get(posForVideo).trim());
        }
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextVideo2();
            }
        });
        videoview.start();
        countVideoAndCurrent();
    }
    //设备或者app重启时调用，恢复播放之前的视频
    public void setVideoListRestart( ArrayList<String> new_file_list,int pos){
        posForVideo=pos;
        video_toPlay_list = new ArrayList<>(new_file_list);
        if(posForVideo == video_toPlay_list.size()){
            posForVideo=0;
        }
        File file = new File(externalFilesDir+video_toPlay_list.get(posForVideo).trim());
        if (!file.exists()) {
            videoview.setVideoURI(Uri.parse(rawDir+video_toPlay_list.get(posForVideo).trim().split("\\.")[0]));
            System.out.println("播放视频----------"+video_toPlay_list.get(posForVideo));
        }else {
            videoview.setVideoURI(Uri.parse(externalFilesDir+video_toPlay_list.get(posForVideo).trim()));
        }
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextVideo2();
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
        videoview.setVideoURI(Uri.parse(externalFilesDir+video_toPlay_list.get(posForVideo).trim()));
        videoview.start();
        countVideoAndCurrent();
    }
    private void nextVideo2() {
// TODO Auto-generated method stub
        posForVideo++;
        if (posForVideo>=video_toPlay_list.size()) {
            posForVideo=0;
        }
//        SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
//        editor.putInt("posForVideo",posForVideo);
//        editor.apply();
        File file = new File(externalFilesDir+video_toPlay_list.get(posForVideo).trim());
        if (!file.exists()) {
            videoview.setVideoURI(Uri.parse(rawDir+video_toPlay_list.get(posForVideo).trim().split("\\.")[0]));
            System.out.println("播放视频----------"+video_toPlay_list.get(posForVideo));
        }else {
            videoview.setVideoURI(Uri.parse(externalFilesDir+video_toPlay_list.get(posForVideo).trim()));
        }
        videoview.start();
        countVideoAndCurrent();
    }
    // 循环播放一个视频
    public void setVideo(String fileName){
        videoview.stopPlayback();
        checkVideoFileWithName(fileName);
       current_video_ad_id=fileName;
        countVideo();
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
                countVideo();
            }
        });
    }
    //检查文件目录，使得file目录和raw目录，以及之后的外置sd卡目录可以混用
    private void checkVideoFile(){
        if(posForVideo==video_toPlay_list.size()){posForVideo=0;}
        File file = new File(externalFilesDir+video_toPlay_list.get(posForVideo).trim());
        if (!file.exists()) {
            videoview.setVideoURI(Uri.parse(rawDir+video_toPlay_list.get(posForVideo).trim().split("\\.")[0]));
            pubResult(video_toPlay_list.get(posForVideo)+":not_exists");
        }else {
            videoview.setVideoURI(Uri.parse(externalFilesDir+video_toPlay_list.get(posForVideo).trim()));
        }
        videoview.start();
    }
    private void checkVideoFileWithName(String fileName){
        if(posForVideo==video_toPlay_list.size()){posForVideo=0;}
        File file = new File(externalFilesDir+fileName);
        if (!file.exists()) {
            System.out.println("开始播放文件2"+fileName);
            videoview.setVideoURI(Uri.parse(rawDir+fileName.split("\\.")[0]));
//            if(new File("android.resource://"+getPackageName()+"/raw/"+fileName).exists()){
//                videoview.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/raw/"+fileName.split("\\.")[0]));
//            }else {pubResult(video_toPlay_list.get(posForVideo)+":not_exists");}
        }else {
            videoview.setVideoURI(Uri.parse(externalFilesDir+fileName));
        }
        videoview.start();
    }
    private void checkPictureFile(){
        if(posForPicture == picture_toPlay_list.size()){posForPicture=0;}
        File file = new File(externalFilesDir+picture_toPlay_list.get(posForPicture).trim());
        if (!file.exists()) {
            imageview.setImageURI(Uri.parse(rawDir+picture_toPlay_list.get(posForPicture).trim().split("\\.")[0]));
        }else {
            imageview.setImageURI(Uri.parse(externalFilesDir+picture_toPlay_list.get(posForPicture).trim()));
        }
    }
    //插队播放一个视频, n次,打断当前播放视频
    public String setCutlineVideo(String file_name,int times){
        cutlineVideo_times =times;
        if(!getFileNameList().contains(file_name)){
            return "fileNotFound";
        }
        videoview.setVideoURI(Uri.parse(externalFilesDir+file_name));
        videoview.start();
        current_video_ad_id=file_name;
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
                    checkVideoFile();
                    countVideoAndCurrent();
                }
            }
        });
        return "ok";
    }
    //插队播放一个视频, n次,不打断当前播放视频
    public String setCutlineVideo2(final String file_name,int times){
        cutlineVideo_times2 =times;
//        if(!getFileNameList().contains(file_name)){
//            return file_name+"fileNotFound";
//        }
        System.out.println("cutlineVideo_times2"+cutlineVideo_times2);
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                checkVideoFileWithName(file_name);
                current_video_ad_id=file_name;
                countVideo();
                videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        cutlineVideo_times2--;
//                        System.out.println("剩余播放次数"+cutlineVideo_times2);
                        if(cutlineVideo_times2>0){
                            videoview.start();
                            countVideo();
                        }else {
                            checkVideoFile();
                            countVideoAndCurrent();
                        }
                    }
                });
            }
        });
        return "ok";
    }

    //循环播放图片

    Handler pictureHandler = new Handler();

    private String getAllParas(){
        String re="";
        re+= "\"mac\":"+"\""+mac+"\",";
        re+= "\"picture_toPlay_list\":"+"\""+picture_toPlay_list.toString()+"\",";
        re+= "\"picture_toPlay_list_time\":"+"\""+picture_toPlay_list_time.toString()+"\",";
        re+= "\"mqttIp\":"+"\""+mqttIp+"\",";
        re+= "\"mqttUsername\":"+"\""+mqttUsername+"\",";
        re+= "\"mqttPassword\":"+"\""+mqttPassword+"\",";
        re+= "\"topicResult\":"+"\""+topicResult+"\",";
        re+= "\"topicError\":"+"\""+topicError+"\",";
        re+= "\"defaultWifiSSID\":"+"\""+defaultWifiSSID+"\",";
        re+= "\"defaultWifiPWD\":"+"\""+defaultWifiPWD+"\",";
        re+= "\"video_toPlay_list\":"+"\""+video_toPlay_list.toString()+"\",";
        re+= "\"defaultCity\":"+"\""+defaultCity+"\",";
        pubResult(re);
        return re;
    }

    private void setAnyParas(String paraName,String newValue){
        SharedPreferences.Editor editor2 = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
        switch (paraName){
            case "mqttIp": mqttIp = newValue;editor2.putString(paraName,mqttIp);break;
            case "mqttUsername": mqttUsername = newValue;editor2.putString(paraName,mqttUsername);break;
            case "mqttPassword": mqttPassword = newValue;editor2.putString(paraName,mqttPassword);break;
            case "topicResult": topicResult = newValue;editor2.putString(paraName,topicResult);break;
            case "topicError": topicError = newValue;editor2.putString(paraName,topicError);break;
            case "topicVolume": topicVolume = newValue;editor2.putString(paraName,topicVolume);break;
            case "topicPush": topicPush = newValue;editor2.putString(paraName,topicPush);break;
            case "topicUpdateAd": topicUpdateAd = newValue;editor2.putString(paraName,topicUpdateAd);break;
            case "topicStatus": topicStatus = newValue;editor2.putString(paraName,topicStatus);break;
            case "defaultCity": defaultCity = newValue;editor2.putString(paraName,defaultCity);break;
            case "defaultWifiSSID": defaultWifiSSID = newValue;editor2.putString(paraName,defaultWifiSSID);break;
            case "defaultWifiPWD": defaultWifiPWD = newValue;editor2.putString(paraName,defaultWifiPWD);break;
            case "defaultWifiTYPE": defaultWifiTYPE = newValue;editor2.putString(paraName,defaultWifiTYPE);break;
            default: break;
        }
        editor2.apply();
    }

    public void reloadParas(){
        SharedPreferences pref = getSharedPreferences("data_try1",MODE_PRIVATE);
        mqttIp = pref.getString("mqttIp","tcp://39.100.88.26:1883");
        mqttUsername = pref.getString("mqttUsername","xupeng");
        mqttPassword = pref.getString("mqttPassword","000");
        topicResult = pref.getString("topicResult","ddzl/projector/broker/"+macColon+"/exec/result");
        topicError = pref.getString("topicError","ddzl/projector/broker/"+macColon+"/exec/error");
        topicVolume = pref.getString("topicVolume","ddzl/broker/projector/"+macColon+"/order/msg");
        topicUpdateAd = pref.getString("topicUpdateAd","ddzl/broker/projector/"+macColon+"/update_advertisement");
        topicStatus = pref.getString("topicStatus","ddzl/projector/broker/"+macColon+"/device_status");
        topicPush = pref.getString("topicPush","ddzl/broker/projector/"+macColon+"/advertise/push");
        defaultCity = pref.getString("defaultCity","西安");
        defaultWifiSSID = pref.getString("defaultWifiSSID","ddzl");
        defaultWifiPWD = pref.getString("defaultWifiPWD","ddzl2019");
        defaultWifiTYPE = pref.getString("defaultWifiTYPE","WPA2");
        wordAd = pref.getString("wordAd","垃圾分类  从我做起");
        textView.setText(wordAd);
    }
    //播放两个目录中的图片文件，优先在app file=目录中找，没找到就去找raw目录，再没找到就上报异常
    private  void setPictureListAndTime(ArrayList<String> picture_toPlay_list1,ArrayList<Integer> picture_times){
        posForPicture=0;
        picture_toPlay_list = new ArrayList<>(picture_toPlay_list1);
        picture_toPlay_list_time = new ArrayList<>(picture_times);
        if (picture_toPlay_list.size()<1){
            System.out.println("文件列表中无文件");
            return;
        }
        nextPic();
    }
    private void nextPic(){
        if(picCutInTimes>0){
            File file = new File(externalFilesDir+picCutInName.trim());
            if (!file.exists()) {
                pubResult(picCutInName+" not exist");
                imageview.setImageURI(Uri.parse(rawDir+picCutInName.trim().split("\\.")[0]));
            }else {
                imageview.setImageURI(Uri.parse(externalFilesDir+picCutInName.trim()));
            }
            pictureHandler.removeCallbacks(runnable);
//            pictureHandler.postDelayed(runnable, picture_toPlay_list_time.get(picture_toPlay_list.indexOf(picCutInName))*1000*picCutInTimes);
            pictureHandler.postDelayed(runnable, 1000*picCutInTimes);
            picCutInTimes =0;
        }else {
            if (posForPicture == picture_toPlay_list.size()) {
                posForPicture = 0;
            }
            time1=System.currentTimeMillis();
            countPictureAndCurrent();
//            SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
//            editor.putInt("posForPicture",posForPicture);
//            editor.apply();
            Drawable d = (Drawable)imageview.getDrawable();
            if (d != null && d instanceof BitmapDrawable) {
                Bitmap bmp = ((BitmapDrawable)d).getBitmap();
                if(bmp !=null && !bmp.isRecycled()){
                    imageview.setImageBitmap(null);
                    bmp.recycle();
                    bmp = null;
                }
            }
            File file = new File(externalFilesDir+picture_toPlay_list.get(posForPicture).trim());
            if (!file.exists()) {
                imageview.setImageURI(Uri.parse(rawDir+picture_toPlay_list.get(posForPicture).trim().split("\\.")[0]));
            }else {
                imageview.setImageURI(Uri.parse(externalFilesDir+picture_toPlay_list.get(posForPicture).trim()));
            }
            pictureHandler.removeCallbacks(runnable);
            pictureHandler.postDelayed(runnable, picture_toPlay_list_time.get(posForPicture)*1000);
            posForPicture++;
        }

    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            nextPic();
        }
    };
    public void setPictureListRestart( ArrayList<String> picture_toPlay_list1,int pos,ArrayList<Integer> picture_toPlay_list_time){
        picture_toPlay_list = new ArrayList<>(picture_toPlay_list1);
        posForPicture =pos;
        if(posForPicture == picture_toPlay_list.size()){
            posForPicture=0;
        }
        if (picture_toPlay_list.size()<1){
            System.out.println("文件列表中无文件");
            return;
        }else {
            System.out.println("文件列表");
            setPictureListAndTime(picture_toPlay_list1,picture_toPlay_list_time);
        }
    }
    private void setCutlinePictureNotInterrupt(String filename,String times){//次数
        picCutInName = filename;
        picCutInTimes = Integer.parseInt(times);
    }

    public String get_mac(Context context) {
        String mac_s = "";
        try {
            byte[] mac;
            System.out.println("ip地址");
            System.out.println(get_ip(MainActivity.this));
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
    //获取cpu利用率，目前在小米手机上测试有问题,但是在投影仪上测试正常
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
        //左右移动被取消了，现在只能设置大小
        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) layout.getLayoutParams();
//        layoutParams1.gravity= Gravity.CENTER_HORIZONTAL;
//        layoutParams1.gravity= Gravity.CENTER_VERTICAL;
        layoutParams1.width =11*layoutSize;
        layoutParams1.height=5*layoutSize;
//        layoutParams1.leftMargin = left;
//        layoutParams1.topMargin = -1*top;
        layout.setLayoutParams(layoutParams1);
//        setContentView(layout,new ConstraintLayout.LayoutParams(layout.getWidth(), layout.getHeight()));
        textView =(TextView)findViewById(R.id.textView);
//        textView2 =(TextView)findViewById(R.id.textView2);
        videoview = (VideoView) findViewById(R.id.videoView);
        imageview = (RoundedImageView) findViewById(R.id.imageView);
//        imageview2 = (RoundedImageView) findViewById(R.id.imageView2);
        SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
        editor.putInt("layoutSize",layoutSize);
        editor.putInt("left_margin",left);
        editor.putInt("top_margin",top);
        editor.apply();
    }
    private void setLayoutSize2(View layout,int layoutSize,int left,int top){
        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) layout.getLayoutParams();
//        layoutParams1.gravity= Gravity.CENTER_HORIZONTAL;
//        layoutParams1.gravity= Gravity.CENTER_VERTICAL;
        layoutParams1.width =11*layoutSize;
        layoutParams1.height=4*layoutSize;
//        layoutParams1.leftMargin = left;
//        layoutParams1.topMargin = -1*top;
        layout.setLayoutParams(layoutParams1);
//        setContentView(layout,new ConstraintLayout.LayoutParams(layout.getWidth(), layout.getHeight()));
        textView =(TextView)findViewById(R.id.textView);
//        textView2 =(TextView)findViewById(R.id.textView2);
        videoview = (VideoView) findViewById(R.id.videoView);
        imageview = (RoundedImageView) findViewById(R.id.imageView);
//        imageview2 = (RoundedImageView) findViewById(R.id.imageView2);
        SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
        editor.putInt("layoutSize",layoutSize);
        editor.putInt("left_margin",left);
        editor.putInt("top_margin",top);
        editor.apply();
    }
    //重启
    private void reboot(){
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //  https://blog.csdn.net/Luck_mw/article/details/71085697
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name))
                    return getApplicationContext().getSystemService(name);
                return super.getSystemService(name);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏和状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_projector3);
        //LayoutInflater inflater = getLayoutInflater();
//        LayoutInflater inflater = LayoutInflater.from(this);
//        layout1 = inflater.inflate(R.layout.activity_main_projector, null);
        packageName = getPackageName();
        rawDir = "android.resource://"+packageName+"/raw/";
        externalFilesDir= getExternalFilesDir(null).toString()+"/";
        totalMemory = getTotalMemory();
        layout1 = findViewById(R.id.constraintlayout1);
        SharedPreferences pref = getSharedPreferences("data_try1",MODE_PRIVATE);
        mac = pref.getString("mac",get_mac(MainActivity.this));
        if(mac!=null){
            macColon= mac.replaceAll(".{2}(?=.)", "$0:");
            ip = get_ip(MainActivity.this);
        }
        System.out.println("第一步mac："+mac);
        System.out.println(mac.replaceAll(".{2}(?=.)", "$0:"));
//        Toast.makeText(MainActivity.this,loadString("adPlayStatistic.txt"),Toast.LENGTH_LONG).show();
        if(mac!=null){
            SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
            editor.putString("mac",mac);
            editor.apply();
            topicSubMac = topicSub+"/"+macColon;
            topicSubList = new String[]{topicSubMac,topicSub,topicVolume,topicUpdateAd,topicPush,topicTest};
            for(int i =0;i<topicSubList.length;i++){
                topicSubList[i]=topicSubList[i].replaceFirst("clientid",macColon);
                System.out.println(i);
                System.out.println(topicSubList[i]);
            }
            topicResult=topicResult.replaceFirst("clientid",macColon);
            topicError=topicError.replaceFirst("clientid",macColon);
            topicStatus= topicStatus.replaceFirst("clientid",macColon);
        }
        timerForPicture = new Timer();
        textView =(TextView)findViewById(R.id.textView);
        textView2 =(TextView)findViewById(R.id.textView2);
        videoview = (VideoView) findViewById(R.id.videoView);
        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoview.stopPlayback(); //播放异常，则停止播放，防止弹窗使界面阻塞
                Log.d("视频","视频无法播放");
                nextVideo2();//跳过当前视频，播放下一个
//                pubError("video can't play");
                saveErrorLog("视频无法播放");
                return true;
            }
        });
        imageview = (RoundedImageView) findViewById(R.id.imageView);
        weatherMap = initWeatherMap();
        adPlayStatistic = new HashMap<>();
        mqttCallback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                mqttIsConnect=false;
                saveErrorLog("mqtt断线");
            }
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                Message msg = new Message();
                msg.arg1 = MQTT_STATE_RECEIVE;
//                { "mac":"XX:XX","order":"",}
                Log.e("接收到消息",new String(mqttMessage.getPayload()));
                String content0 = new String(mqttMessage.getPayload(),"GB2312");//GB2312
                String taskMac="" ;
                String order ="";
                String taskId ="";
                System.out.println("topic:"+s+"\n"+content0);
                boolean macMatch =false;
                final  String finalTopicSub= topicSub;
                if(s.equals(topicSub)||s.equals(topicTest)){ //统一命令
                    try {
                        JSONObject result = new JSONObject(content0);
                        order = result.getString("order");
                        taskId = result.getString("taskId");
                    }catch (JSONException e){
                        saveErrorLog("mqtt接收内容非json格式");
                        e.printStackTrace();
                    }
                    macMatch =true;
                } else {
                    try {
                        JSONObject result = new JSONObject(content0);
                        taskMac = result.getString("mac");
                        order = result.getString("order");
                        taskId = result.getString("taskId");
                    }catch (JSONException e){
                        saveErrorLog("mqtt接收内容非json格式");
                        e.printStackTrace();
                    }
                    System.out.println("topic=" + s + "  接收mqtt：" + content0);
                    switch (taskMac.length()){
                        case 12:
                            if(taskMac.equals(mac)){
                                macMatch=true;
                            }
                            break;
                        case 17:
                            if(taskMac.replace(":","").equals(mac)){
                                macMatch=true;
                            }else if(taskMac.replace("-","").equals(mac)) {
                                macMatch=true;
                            }
                            break;
                    }
                    System.out.println(mac);
                    System.out.println(order);
                    System.out.println(macMatch);
                }
                if(macMatch){
                    if(s.equals(topicUpdateAd)){
                        ArrayList<UpdateVideo> updateVideos = new ArrayList<>();

                        try {
                            JSONObject result = new JSONObject(content0);
                            taskMac = result.getString("mac");
                            order = result.getString("order");
                            taskId = result.getString("taskId");
                            for(int i =0;i<result.getJSONArray("videos").length();i++){
                                String adId= result.getJSONArray("videos").getJSONObject(i).getString("adId");
                                String type= result.getJSONArray("videos").getJSONObject(i).getString("type");
                                String url= result.getJSONArray("videos").getJSONObject(i).getString("url");
                                String playNum= result.getJSONArray("videos").getJSONObject(i).getString("playNum");
                                UpdateVideo updateVideo = new UpdateVideo(adId,type,url,Integer.parseInt(playNum));
                                updateVideos.add(updateVideo);
                            }
                        }catch (JSONException e){
                            saveErrorLog("mqtt接收内容非json格式");
                            e.printStackTrace();
                        }


                    }
                    for(String content:order.split("\\n")){
                        System.out.println("order1"+content);
                        switch (content) {
                            case "getErrorLog":
                                Message message7 =new Message();
                                message7.what =24;
                                message7.obj= content;
                                handler.sendMessage(message7);
                                break;
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
                            case "get_file_list":
                                pubResult(getFileNameList().toString()+"["+getFreeSize()+"] "+mac+" ip "+ip);
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
                                reboot();
                                break;
                            default:
                                if (content.startsWith("update_video_list")) {
                                    Message message3 = new Message();
                                    message3.what = 10;
                                    message3.obj =  content.split(":")[content.split(":").length-1];
                                    handler.sendMessage(message3);
                                    break;
                                }
                                else if(content.startsWith("update_picture_list")) {
                                    Message message3 = new Message();
                                    message3.what = 20;
//                                    message3.obj = content.split(":")[content.split(":").length - 1];
                                    String tempPictureList = content.substring(19);//{a.jpg:4,b.jpg:5}
                                    System.out.println("tempPictureList"+tempPictureList);
                                    message3.obj =content.substring(20,content.length()-1);//{a.jpg:4,b.jpg:5}
                                    handler.sendMessage(message3);
                                    break;
                                }
                                else if(content.startsWith("delete_file_list")){
                                    String deleteFileList = content.split(":")[content.split(":").length-1];
                                    for(String fileTodelete : deleteFileList.split(",")){
                                        deleteSingleFile(externalFilesDir+fileTodelete);
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
                                }else if(content.startsWith("set_cut_line_pictureNotInterrupt")){
                                    Message message6 = new Message();
                                    message6.what = 25;
                                    message6.obj = content;
                                    handler.sendMessage(message6);
                                } else if(content.startsWith("set_cut_line_picture")){
                                    Message message6 = new Message();
                                    message6.what = 26;
                                    message6.obj = content;
                                    handler.sendMessage(message6);
                                }
                                else if(content.startsWith("change_city_")){
                                    defaultCity=content.split("_")[content.split("_").length-1];
                                    SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                                    editor.putString("defaultCity",defaultCity);
                                    editor.apply();
                                    updateWeather(defaultCity);
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
                                }else if(content.startsWith("download")){
                                    Message message6 =new Message();
                                    message6.what =21;
                                    message6.obj= content.substring(8);
                                    handler.sendMessage(message6);
                                }else if(content.startsWith("connectWifi")){
                                    Message message6 =new Message();
                                    message6.what =22;
                                    message6.obj= content.substring(11);
                                    handler.sendMessage(message6);
                                }else if(content.startsWith("set_word")){
                                    Message message6 =new Message();
                                    message6.what =23;
                                    message6.obj= content.substring(8);
                                    handler.sendMessage(message6);
                                    break;
                                }else if(content.startsWith("callFun")){
                                    Message message6 =new Message();
                                    message6.what =24;
                                    message6.obj= content.substring(7);
                                    handler.sendMessage(message6);
                                    break;
                                }
                                pubResult(content+":success");
                                break;
                        }
                    }
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        };
//        setUpdateWeatherTimer();
//        updateWeather(defaultCity);
        System.out.println("先试试1");
        newMqttClient();
        testFunction();
        restart();
//        testFunction();
        System.out.println("mac地址2");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoview!=null){
            videoview.suspend();
        }
    }
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

    public  String loadString(String filename){
        FileInputStream in =null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        try {
            in=openFileInput(filename);
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while ((line=reader.readLine())!=null){
                content.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }
    private void restart(){
        reloadParas();
        systemVolume = getSystemVolume(MainActivity.this);
        String errorLogString=loadString("errorLog.txt");
        if(errorLogString!=null&&errorLogString.length()>1){
            System.out.println("重启加载errorlog="+errorLogString);
            System.out.println(errorLogString);
        } else {
            System.out.println("errorlog not exists");
        }

//        Timer timerForWifi =new Timer();
//        timerForWifi.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                checkWifi();
//            }
//        },0,1000*30);

        String mapString=loadString("adPlayStatistic.txt");
        if(mapString!=null&&mapString.length()>1){
            System.out.println("重启加载map="+mapString);
            mapString=mapString.substring(1, mapString.length()-1);
            String[] strs=mapString.split(",");
            adPlayStatistic= new HashMap<String, Integer>();
            for (String string : strs) {
                String key=string.split("=")[0].trim();
                int value=Integer.parseInt(string.split("=")[1]);
                adPlayStatistic.put(key, value);
            }
        } else {
            System.out.println("adPlayStatistic文件不存在");
        }
        SharedPreferences pref = getSharedPreferences("data_try1",MODE_PRIVATE);
//        setLayoutSize(layout1,pref.getInt("layoutSize",120),pref.getInt("left_margin",0),pref.getInt("top_margin",0));
        System.out.println("重新安装");
        System.out.println(pref.getString("mac","不存在"));
        defaultCity = pref.getString("defaultCity","西安");
//        updateWeather(defaultCity);
        video_toPlay_list = new ArrayList<>(Arrays.asList(pref.getString("video_toPlay_list","not_set").split(",")));
        picture_toPlay_list = new ArrayList<>(Arrays.asList(pref.getString("picture_toPlay_list","not_set").split(",")));
        ArrayList <String>temp_picture_toPlay_list_time = new ArrayList<>(Arrays.asList(pref.getString("picture_toPlay_list_time","not_set").split(",")));
        picture_toPlay_list_time = new ArrayList<>();
        posForPicture = pref.getInt("posForPicture",0);
        posForVideo = pref.getInt("posForVideo",0);
        if(temp_picture_toPlay_list_time!=null){
            if(temp_picture_toPlay_list_time.get(0).equals("not_set")){
                System.out.println("picture_toPlay_list"+picture_toPlay_list);
            }else {
                System.out.println(temp_picture_toPlay_list_time.get(0));
                for (int i=0;i<temp_picture_toPlay_list_time.size();i++){
                    picture_toPlay_list_time.add(Integer.parseInt(temp_picture_toPlay_list_time.get(i).trim()));
                }
                setPictureListRestart(picture_toPlay_list,pref.getInt("posForPicture",0),picture_toPlay_list_time);
            }
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
    public void checkWifi(){
        ConnectivityManager connectivityManager = (ConnectivityManager)MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String wifiStatus;
        if(wifiNetworkInfo.isConnected())
        {
            wifiStatus= "wifiSuccess";
            System.out.println(wifiStatus);
        }else {
            saveErrorLog("wifi失效");
            connectWifi(MainActivity.this,defaultWifiSSID,defaultWifiPWD,defaultWifiTYPE);
            System.out.println("开始连接wifi");
        }
    }
    private void connectDefaultWifi(){
        connectWifi(MainActivity.this,defaultWifiSSID,defaultWifiPWD,defaultWifiTYPE);
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
            StringBuilder sb = new StringBuilder();
            for (char c : memTotal.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            //为了方便格式化 所以乘以1024
            return Long.parseLong(sb.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    private String getRamUseRate(){
//        String.format("%.2f",1-getFreeMemory(MainActivity.this).doubleValue/getTotalMemory().doubleValue)
        if(totalMemory!=0){
            return String.format("%.3f",1-getFreeMemory(MainActivity.this)*1.0/totalMemory);
        }else{
            return String.format("%.3f",1-getFreeMemory(MainActivity.this)*1.0/getTotalMemory());
        }

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
        StringBuilder result = new StringBuilder();
//        String re ="{\"projector_mac\":\""+macColon+"\",\"cpu_usage_rate\":\""
//                +"小米"+"\",\"memory_usage_rate\":\""
//                +getRamUseRate()+"\",\"disk_usage_rate\":\""
//                +getRomUseRate()+"\",\"inner_ip\":\""+"\","
//                +"\"volume\":\""+systemVolume+"\","
//                +"\"light_machine\":\""+light_machine+"\","
//                +"\"current_video_ad_id\":\""+current_video_ad_id+"\","
//                +"\"current_picture_ad_id\":\""+current_picture_ad_id+"\","
//                +"\"ad_play_statistic\":[";
        result.append("{\"projector_mac\":\"");
        result.append(macColon);
        result.append("\",\"cpu_usage_rate\":\"");
        result.append("0.30");
        result.append("\",\"memory_usage_rate\":\"");
        result.append("");
        result.append("\",\"disk_usage_rate\":\"");
        result.append("");
        result.append("\",\"inner_ip\":\"");
        result.append(ip);
        result.append("\"volume\":\"");
        result.append(systemVolume);
        result.append("\"light_machine\":\"");
        result.append(light_machine);
        result.append("\",\"current_video_ad_id\":\"");
        result.append(current_video_ad_id);
        result.append("\",\"current_picture_ad_id\":\"");
        result.append(current_picture_ad_id);
        result.append("\",\"ad_play_statistic\":[");
        for(String k:adPlayStatistic.keySet()){
//            re+= "{\"ad_id\":\""+k+"\",\"play_num\":\""+adPlayStatistic.get(k)+"\"},";
//            result.append("{\"ad_id\":\""+k+"\",\"play_num\":\""+adPlayStatistic.get(k)+"\"},");
            result.append("{\"ad_id\":\"");
            result.append(k);
            result.append("\",\"play_num\":\"");
            result.append(adPlayStatistic.get(k));
            result.append("\"},");
        }
//        re= re.substring(0,re.length()-1);
//        re+="]}";
        result.deleteCharAt(result.length()-1);
        result.append("]}");
        return result.toString();
    }
    private String addColonToMac(String mac){
        String re = "";

        return re;
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
                String clientId = macColon;
                try {
                    MemoryPersistence persistence = new MemoryPersistence();
                    mqttClient=new MqttClient(host,clientId,persistence);
                    mqttClient.setCallback(mqttCallback);//设置回调函数
                    mqttClient.connect(options);//连接broker
                    mqttClient.subscribe(topicTest);
                    mqttClient.subscribe(topicSubList);
                    System.out.println("订阅的topic为");
                    for(int i=0;i<topicSubList.length;i++){
                        System.out.println(topicSubList[i]);
                    }
                    System.out.println("心跳信息");
                    System.out.println(getProjectorInfo());
                    heartBeat();
                    System.out.println("怎么不打印心跳");
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
//                System.out.println(getProjectorInfo());
                pubStatus(getProjectorInfo());
            }
        };
        timerForHeartBeat.schedule(timerTask, 0,1*1000);
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
                String clientId = "s1234";
                String subscribeTopics=topicTest;
                try {
                    MemoryPersistence persistence = new MemoryPersistence();
                    MqttClient mqttClient=new MqttClient(host,clientId,persistence);
                    mqttClient.setCallback(mqttCallback);//设置回调函数
                    mqttClient.connect(options);//连接broker
                    mqttClient.subscribe(subscribeTopics);//设置监听的topic
                    ArrayList<String> publist=  new ArrayList<>();
                    MqttMessage msg=new MqttMessage();
                        msg.setPayload(("testM").getBytes());//设置消息内容
                        msg.setQos(2);//设置消息发送质量，可为0,1,2.
                        msg.setRetained(false);//服务器是否保存最后一条消息，若保存，client再次上线时，将再次受到上次发送的最后一条消息。
                        Log.e("topic",topicTest);
                        mqttClient.publish(topicTest,msg);//设置消息的topic，并发送。
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private void testFunction(){
        System.out.println("初始化");
        ArrayList<String> list1= new ArrayList<>();
        list1.add("v1");
        list1.add("v2");
        list1.add("v3");
        list1.add("v4");
        list1.add("v5");
        list1.add("v6");
        ArrayList<String> list2= new ArrayList<>();
        list2.add("p1");
        list2.add("p2");
        list2.add("p3");
        ArrayList<Integer> list3= new ArrayList<>();
        list3.add(8);
        list3.add(8);
        list3.add(8);
        setPictureListAndTime(list2,list3);
        setVideoList2(list1);
        SharedPreferences.Editor editor2 = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
        editor2.putString("video_toPlay_list",list1.toString().substring(1,video_toPlay_list.toString().length()-1));
        editor2.putString("picture_toPlay_list",list2.toString().substring(1,list2.toString().length()-1));
        editor2.putString("picture_toPlay_list_time",list3.toString().substring(1,list3.toString().length()-1));
        editor2.apply();
        Timer timerForWifiTest =new Timer();
        TimerTask timerTaskForWifiTest = new TimerTask() {
            @Override
            public void run() {
//                test_mqtt();
                if(testWifi()){
//                    Log.e("wifi测试","正常连接");
                    if(mac==null){
                        //说明没有初始化,也就是mac和ip还没有保存
                        mac = get_mac(MainActivity.this);
                        ip = get_ip(MainActivity.this);
                        macColon = mac.replaceAll(".{2}(?=.)", "$0:");
                        SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                        editor.putString("mac",mac);
                        editor.putString("macColon",macColon);
                        editor.putString("ip",ip);
                        editor.apply();
                        if(!topicSubMac.contains(macColon)){
                            topicSubMac = topicSub+"/"+macColon;
                            topicSubList = new String[]{topicSubMac,topicSub,topicVolume,topicUpdateAd,topicPush,topicTest};
                            for(int i =0;i<topicSubList.length;i++){
                                topicSubList[i]=topicSubList[i].replaceFirst("clientid",macColon);
                            }
                            topicResult=topicResult.replaceFirst("clientid",macColon);
                            topicError=topicError.replaceFirst("clientid",macColon);
                            topicStatus= topicStatus.replaceFirst("clientid",macColon);
                            try {
                                mqttClient.subscribe(topicSubList);
                            }catch (MqttException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    if(triggerForWifiReconnect==2){
                        System.out.println("reboot:系统重启");
                        triggerForWifiReconnect= 0;//复位
                        reboot();
                    }
                }else {
//                    Log.e("wifi测试","不能正常通信");
                    saveErrorLog("wifi不能正常通信");
                    triggerForWifiReconnect= 2;// wifi 刚断开
                    connectDefaultWifi();
                }
            }
        };

        TimerTask timerTaskForSaveAdMap = new TimerTask() {
            @Override
            public void run() {
                saveAdMap();
            }
        };
        timerForWifiTest.schedule(timerTaskForWifiTest, 0,5*1000);
        timerForWifiTest.schedule(timerTaskForSaveAdMap, 0,3600*1000);
    }
//    private void printWifi(){
//        WifiManager wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
////        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        Log.d("wifiInfo", wifiInfo.toString());
//        Log.d("SSID",wifiInfo.getSSID());
//    }
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
    public static String getSdCardPath() {
        boolean exist = isSdCardExist();
        String sdpath = "";
        if (exist) {
            sdpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            sdpath = "不适用";
        }
        return sdpath;
    }

     //周期性保存统计map
    private void saveAdMap(){
        saveString("adPlayStatistic.txt",adPlayStatistic.toString());
    }
    public void saveString(String filename,String inputText){
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try {
            out=openFileOutput(filename, Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void saveErrorLog(String inputText){
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try {
            out=openFileOutput("errorLog.txt", Context.MODE_APPEND);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write("\""+getTime()+"\":"+"\""+inputText+"\",");
            FileInputStream in = openFileInput("errorLog.txt");
            if(in.available()>1024*50){
                //大于50k ，清空重写
                out=openFileOutput("errorLog.txt", Context.MODE_PRIVATE);
                writer=new BufferedWriter(new OutputStreamWriter(out));
                writer.write("\""+getTime()+"\":"+"\""+inputText+"\",");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void saveCountLog(String inputText){
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try {
            out=openFileOutput("countLog.txt", Context.MODE_APPEND);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean onKeyDown = super.onKeyDown(keyCode, event);
//        tv_keycode.setText("des = " + KeyEvent.keyCodeToString(keyCode) + " : code = " + keyCode);
        System.out.println("按键###########");
        System.out.println(event);
        int tempPosForPicture=0;
        switch (event.getKeyCode()){
            case KEYCODE_1:
                light_machine = "disable";
                videoview.pause();
                time2 = System.currentTimeMillis();
                pictureHandler.removeCallbacks(runnable);
                break;
            case KEYCODE_2:
                light_machine = "enable";
                videoview.start();
                posForPicture--;
//                pictureHandler.removeCallbacks(runnable);
                if(posForPicture>=0){
                    checkPictureFile();
                    pictureHandler.postDelayed(runnable,picture_toPlay_list_time.get(posForPicture)*1000-(time2-time1));
                }
                break;
        }
        return onKeyDown;
    }
    private String getErrorLog(){
        String errorLogString=loadString("errorLog.txt");
        if(errorLogString!=null && errorLogString.length()>1){
            return errorLogString;
        }else return "errorLog not exist";
    }
    private boolean testWifi(){
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p = runtime.exec("ping -c 3 www.baidu.com");
            int ret = p.waitFor();
//            Log.i("Avalible", "Process:"+ret);
            if(ret==0){
                return true;
            }else return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}



