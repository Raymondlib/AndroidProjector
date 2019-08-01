package com.example.try1;

import java.io.BufferedReader;
import java.io.File;
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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.makeramen.roundedimageview.RoundedImageView;

import org.jeromq.ZMQ;
import org.jeromq.ZMQException;
import org.json.JSONObject;

import static java.lang.Thread.sleep;

public class MainActivity extends Activity {
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
    private String weatherTypeToday;
    private String weatherTypeTomorrow;
    private String temperatureToday;
    private String temperatureTomorrow;
    private String dateToday;
    private String dateTomorrow;
    private HashMap<String,Integer> weatherMap;
    private int layoutSize =1;

    //    private Runtime run = Runtime.getRuntime();//获取当前运行环境，来执行ping，相当于windows的cmd
    String url30 ;

    // 接收子线程中的信息，来更新控件，因为只有主线程才能更新控件
    private Handler  handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    // 初始化连接，获取控制端ip    pair模式，端口5550
                    pu_ip = msg.obj.toString();
                    break;
                case 1:
                    setVideo(msg.obj.toString());
                    System.out.println("sub模式收到信息");
                    break;
                case 2:
                    setLayout(layout1,Integer.parseInt(msg.obj.toString()));
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

//                    videoview.setVideoPath();
//                    videoview.setVideoURI(Uri.parse(videoURI));
                    break;
                case 6:

                    pu_ip =msg.obj.toString();
//                    videoview.setVideoPath();
//                    videoview.setVideoURI(Uri.parse(videoURI));
                    break;
                case 7:
                    //style 1   ，图片+视频
                    setContentView(layout_video_picture);
                    //更新控件
                    videoview = (VideoView) findViewById(R.id.videoView3);


//                    String video_path =msg.obj.toString().split("|")[0];
//                    String picture_path =msg.obj.toString().split("|")[1];
//                    imageview.setImageResource();
//                    videoview.stopPlayback();
//                    Bitmap bitmap = BitmapFactory.decodeFile(getExternalFilesDir(null).toString()+"/"+msg.obj.toString());
                    Bitmap bitmap = BitmapFactory.decodeFile(getExternalFilesDir(null).toString()+"/test1.jpg");
                    imageview.setImageBitmap(bitmap);
                    videoview.setVideoURI(Uri.parse(url30));
                    videoview.start();
                    break;
                case 8:
                    //更改单个视频音量
                    setSingleVideoVolume(Float.parseFloat(msg.obj.toString()),videoview);
                    break;
                case 9:
                    setSystemVolume(Float.parseFloat(msg.obj.toString()),MainActivity.this);
                    break;
                case 10:
                    //更新视频列表
                    ArrayList<String> video_temp_list= new ArrayList<>(Arrays.asList(msg.obj.toString().split(",")));
                    setVideoList(video_temp_list);
                    SharedPreferences.Editor editor2 = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                    editor2.putString("video_toPlay_list",video_temp_list.toString().substring(1,video_temp_list.toString().length()-1));
                    editor2.apply();
                    SharedPreferences pref2 = getSharedPreferences("data_try1",MODE_PRIVATE);
                    String video_toPlay_list = pref2.getString("video_toPlay_list","why");
                    System.out.println("&&&&&&&&&");
                    System.out.println(video_toPlay_list);
                    System.out.println(video_toPlay_list.toString().substring(1,video_toPlay_list.toString().length()-1));
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
                    //更新控件
                    videoview = (VideoView) findViewById(R.id.videoView3);

                    break;
                case 13:
                    //更新生活信息，天气预报+限行
                    textView.setText(dateToday+" "+"\n"+weatherTypeToday+"  "+temperatureToday+" ℃");
                    textView2.setText(dateTomorrow+"\n"+weatherTypeTomorrow+"  "+temperatureTomorrow+" ℃");
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

//                        String picture_toPlay_list = pref.getString("picture_toPlay_list","why");
//                        System.out.println("&&&&&&&&&");
//                        System.out.println(picture_toPlay_list);
//                        System.out.println(picture_temp_list.toString().substring(1,picture_temp_list.toString().length()-1));
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    break;
                case 15:
                    nextPicture();
                    break;
                case 16:
                    String temp =setCutlineVideo(msg.obj.toString().split("_")[msg.obj.toString().split("_").length-2],Integer.parseInt(msg.obj.toString().split("_")[msg.obj.toString().split("_").length-1]));
                    if(temp.equals("fileNotFound")){
                        pair.send("fileNotFound");
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
            return currentVolume;
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
    //初始化连接，获取控制端ip，设定此投影仪信息
    public void init_client(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //作为 服务端，监听控制器连接
                pair.bind("tcp://*:5550");
                String pu_ip ;
                boolean wait = true;
                while (wait) {//
                    byte[] request;
                    try {
                        request = pair.recv(0);//接收的客户端数据
                        String getData=new String(request);
                        Log.d("pair_string_continue_t",getData);
                        System.out.println(getData);
                        if (getData.equals("pu_online")) {
                            pair.send("ok".toString(),1);
                            pu_ip =pair.recvStr();

                            SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                            editor.putString("pu_ip",pu_ip);
                            editor.apply();

                            Message message = new Message();
                            message.what = 6;
                            message.obj = pu_ip;
                            handler.sendMessage(message);
                            test_sub();
//                            socket.close();
//                            context.term();
//                            wait= false;
                        }else {
                            switch (getData){
                                case "start_video":
                                    Message msg = new Message();
                                    msg.what=3;
                                    handler.sendMessage(msg);
//                                    videoview.start();
                                    break;
                                case "pause_video":

                                    videoview.pause();
                                    break;
                                case "get_file_list":
//                                    pair.send(getFileNameList().toString());
                                    pair.send(getFileNameList().toString()+"["+getFreeSize()+"]");
                                    break;
                                case "get_video_list":
                                    //视频播放列表
//                                    pair.send(video_toPlay_list.toString());
                                    if(video_toPlay_list==null){
                                        pair.send("video_toPlay_list is null");
                                    }else {
                                        pair.send(video_toPlay_list.toString());
                                    }
                                    break;
                                case "update_video_list":
//                                video_toPlay_list= ArrayList.asList(sub.recvStr());
                                    Message message4 = new Message();
                                    message4.what = 10;
                                    message4.obj =pair.recvStr() ;
                                    handler.sendMessage(message4);
                                    break;

                                case "get_picture_list":
                                    if(picture_toPlay_list==null){
                                        pair.send("picture_toPlay_list is null");
                                    }else {
                                        pair.send(picture_toPlay_list.toString());
                                    }
                                    break;
                                case "update_picture_list":
//                                video_toPlay_list= ArrayList.asList(sub.recvStr());
                                    Message message12 = new Message();
                                    message12.what = 14;
                                    message12.obj =pair.recvStr() ;
                                    if(message12.obj!=null){
                                        handler.sendMessage(message12);
                                    }

                                    break;

                                case "change_layout_video_picture":
                                    Message message = new Message();
                                    message.what = 7;
//                                        message.obj = pu_ip;
                                    handler.sendMessage(message);
                                    break;
                                case "transport_video":
                                    pair.send("ready_to_download");
//                                        String recv = socket.recvStr();
                                    byte[] video_byte=pair.recv(0);
                                    try{
                                        OutputStream os = new FileOutputStream(getExternalFilesDir(null).toString()+ "/30_saved_1.mp4");
                                        os.write(request, 0, request.length);
                                        os.flush();
                                        Log.w("传输视频","video传输完成");
                                        os.close();
                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }
                                    break;
                                case "transport_file":
                                    String transport_file_name =pair.recvStr(0);
                                    if (transport_file_name.length()>100){
                                        Log.d("error","文件名过长");
                                    }
                                    byte[] file_byte=pair.recv(0);
                                    try{
                                        OutputStream os = new FileOutputStream(getExternalFilesDir(null).toString()+ "/"+transport_file_name);
                                        os.write(file_byte, 0, file_byte.length);
                                        os.flush();
                                        Log.w("传输文件",transport_file_name+"传输完成");
                                        //pair.send(transport_file_name+"传输完成");
                                        os.close();
                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }
                                    break;
                                case "close_light":
                                    close_light();
                                    break;
                                case "open_light":
                                    open_light();
                                    break;
                                case "1":


                                    pair.send( getSDAvailableSize()+ getRomAvailableSize()+getAvailableInternalMemorySize());
                                    break;
                                case "are_you_ok":
                                    pair.send("i_am_ok");
                                    break;
                                case "delete_file_list":
                                    String fileListToDelete = pair.recvStr();
                                    for(String s : fileListToDelete.split(",")){
                                        deleteSingleFile(getExternalFilesDir(null).toString()+"/"+s);
                                    }
                                    break;
                                default :
                                    if (getData.startsWith("change_volume")){
                                        Message message2 = new Message();
                                        message2.what = 8;
                                        message2.obj = getData.split("_")[getData.split("_").length-1];
                                        handler.sendMessage(message2);
                                    }else if (getData.startsWith("change_system_volume")){
                                        Message message3 = new Message();
                                        message3.what = 9;
                                        message3.obj = getData.split("_")[getData.split("_").length-1];
                                        handler.sendMessage(message3);
                                    }else if (getData.startsWith("set_single_video")){
                                        pair.send(getExternalFilesDir(null).toString());
                                        Message message5 = new Message();
                                        message5.what = 11;
                                        message5.obj = getData.split("_")[getData.split("_").length-1];
                                        handler.sendMessage(message5);

                                    }else if(getData.startsWith("change_layout")){
                                        Message message6 = new Message();
                                        message6.what = 12;
                                        message6.obj = getData.split("_")[getData.split("_").length-1];
                                        handler.sendMessage(message6);
                                    }else if(getData.startsWith("set_cut_line_video")){
                                        Message message6 = new Message();
                                        message6.what = 16;
                                        message6.obj = getData;
                                        handler.sendMessage(message6);

                                    }else if(getData.startsWith("change_city_")){
                                        defaultCity=getData.split("_")[getData.split("_").length-1];
                                        SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                                        editor.putString("defaultCity",defaultCity);
                                        editor.apply();
                                        updateWeather(defaultCity);
                                    }else if(getData.startsWith("set_picture_time_")){
                                        picture_time = Integer.parseInt(getData.split("_")[getData.split("_").length-1]);
                                        setPictureTimer();
                                    }else if(getData.startsWith("set_layoutsize")){
                                        layoutSize = Integer.parseInt(getData.split("_")[getData.split("_").length-1]);
                                        Message message6 = new Message();
                                        message6.what = 2;
                                        message6.obj = layoutSize;
                                        handler.sendMessage(message6);
                                    }else if(getData.startsWith("set_qrcode")){
                                        Message message6 = new Message();
                                        message6.what = 3;
                                        message6.obj = getData.split("_")[getData.split("_").length-1];
                                        handler.sendMessage(message6);
                                    }
                                    break;
                            }
                        }
                    } catch (ZMQException e) {
                        throw e;
                    }
                }
            }
        }).start();
    }
    public void test_sub(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ZMQ.Context context = ZMQ.context(1);
                ZMQ.Socket sub = context.socket(ZMQ.SUB);
                System.out.println(pu_ip);
                System.out.println("sub连接结果");
                System.out.println("tcp://"+pu_ip);
                System.out.println( sub.connect("tcp://"+pu_ip));
                sub.subscribe("");
                byte[] request;
                boolean wait = true;
                while (wait) {//
                    try {
                        request = sub.recv(0);//接收的客户端数据
                        // 处理命令
                        String getData=new String(request);
                        Log.d("sub模式",getData);
                        System.out.println(getData);
                        switch (getData){
                            case "start_video":
                                Message msg = new Message();
                                msg.what=3;
                                handler.sendMessage(msg);
//                                videoview.start();
                                break;
                            case "pause_video":
                                Message msg1 = new Message();
                                msg1.what=2;
                                handler.sendMessage(msg1);
//                                videoview.pause();
                                break;
                            case "get_file_list":
                                pair.send(getFileNameList().toString()+"["+getFreeSize()+"]");
//                                JSONObject object = new JSONObject();
                                break;
                            case "get_video_list":
                                //视频播放列表
                                if(video_toPlay_list==null){
                                    pair.send("video_toPlay_list is null");
                                }else {
                                    pair.send(video_toPlay_list.toString());
                                }
                                break;
                            case "update_video_list":
//                                video_toPlay_list= ArrayList.asList(sub.recvStr());
                                Message message4 = new Message();
                                message4.what = 10;
                                message4.obj =sub.recvStr() ;
                                handler.sendMessage(message4);
                                break;

                            case "get_picture_list":
                                if(picture_toPlay_list==null){
                                    pair.send("picture_toPlay_list is null");
                                }else {
                                    pair.send(picture_toPlay_list.toString());
                                }
                                break;
                            case "update_picture_list":
//                                video_toPlay_list= ArrayList.asList(sub.recvStr());
                                Message message12 = new Message();
                                message12.what = 14;
                                message12.obj =sub.recvStr() ;
                                handler.sendMessage(message12);
                                break;

                            case "change_layout_video_picture":
                                Message message = new Message();
                                message.what = 7;
                                handler.sendMessage(message);
                                break;
                            case "transport_file":
                                String transport_file_name =sub.recvStr(0);
                                if (transport_file_name.length()>100){
                                    Log.d("error","文件名过长");
                                }
                                byte[] file_byte=sub.recv(0);
                                try{
                                    OutputStream os = new FileOutputStream(getExternalFilesDir(null).toString()+ "/"+transport_file_name);
                                    os.write(file_byte, 0, file_byte.length);
                                    os.flush();
                                    Log.w("传输文件",transport_file_name+"传输完成");
                                    //pair.send(transport_file_name+"传输完成");
                                    os.close();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                                break;
                            case "close_light":
                                close_light();
                                break;
                            case "open_light":
                                open_light();
                                break;
                            case "are_you_ok":
                                pair.send("i_am_ok");
                                break;
                            case "delete_file_list":
                                String fileListToDelete = sub.recvStr();
                                for(String s : fileListToDelete.split(",")){
                                    deleteSingleFile(getExternalFilesDir(null).toString()+"/"+s);
                                }
                                break;
                            default :
                                if (getData.startsWith("change_volume")){

                                    Message message2 = new Message();
                                    message2.what = 8;
                                    message2.obj = getData.split("_")[getData.split("_").length-1];
                                    handler.sendMessage(message2);
                                }else if (getData.startsWith("change_system_volume")){
                                    Message message3 = new Message();
                                    message3.what = 9;
                                    message3.obj = getData.split("_")[getData.split("_").length-1];
                                    handler.sendMessage(message3);
                                }else if (getData.startsWith("set_single_video")){
                                    Message message5 = new Message();
                                    message5.what = 11;
                                    message5.obj = getData.split("_")[getData.split("_").length-1];
                                    handler.sendMessage(message5);
                                }else if(getData.startsWith("change_layout")){
                                    Message message6 = new Message();
                                    message6.what = 12;
                                    message6.obj = getData.split("_")[getData.split("_").length-1];
                                    handler.sendMessage(message6);
                                }else if(getData.startsWith("set_cut_line_video")){
                                    Message message6 = new Message();
                                    message6.what = 16;
                                    message6.obj = getData;
                                    handler.sendMessage(message6);

                                }else if(getData.startsWith("change_city_")){
                                    defaultCity=getData.split("_")[getData.split("_").length-1];

                                    SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
                                    editor.putString("defaultCity",defaultCity);
                                    editor.apply();

                                    updateWeather(defaultCity);
                                }else if(getData.startsWith("set_picture_time_")){
                                    picture_time = Integer.parseInt(getData.split("_")[getData.split("_").length-1]);
                                    setPictureTimer();
                                }else if(getData.startsWith("set_layoutsize")){
                                    layoutSize = Integer.parseInt(getData.split("_")[getData.split("_").length-1]);
                                    Message message6 = new Message();
                                    message6.what = 2;
                                    message6.obj = layoutSize;
                                    handler.sendMessage(message6);
                                }else if(getData.startsWith("set_qrcode")){
                                    Message message6 = new Message();
                                    message6.what = 3;
                                    message6.obj = getData.split("_")[getData.split("_").length-1];
                                    handler.sendMessage(message6);
                                }
                                break;
                        }
                    } catch (ZMQException e) {
                        throw e;
                    }
                }
            }}).start();
    }
    //更改播放文件
    // 循环播放一个视频列表中的视频
    public void setVideoList( ArrayList<String> new_file_list){
//        videoview.onMeasure(100,100);
//        videoview.onMeasure(10,20); 没有用
        posForVideo=0;
        video_toPlay_list = new ArrayList<>(new_file_list);

        System.out.println(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(posForVideo));
        System.out.println("999999999");
        videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(posForVideo).trim()));
//            videoview.setVideoURI(Uri.parse("/storage/emulated/0/Android/data/com.example.try1/files/10.mp4"));
        System.out.println(video_toPlay_list.get(posForVideo));
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                videoview.setVideoPath(file_list.get(pos++).getPath());
                nextVideo();
            }
        });
        videoview.start();
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

        videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(posForVideo)));
//        videoview.setMediaController(mc);
//        videoview.requestFocus();
        videoview.start();
    }
    // 循环播放一个视频
    public void setVideo(String fileName){
        videoview.stopPlayback();
        videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+fileName));
        videoview.start();
//        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.start();
//                mp.setLooping(true);
//            }
//        });
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                System.out.println("***视频结束***");
//                mediaPlayer.setLooping(true);
                videoview.start();
            }
        });
    }
    //插队播放一个视频, n次
    public String setCutlineVideo(String file_name,int times){
        cutlineVideo_times =times;
        if(!getFileNameList().contains(file_name)){
            return "fileNotFound";
        }
        videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+file_name));
        videoview.start();
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                videoview.setVideoPath(file_list.get(pos++).getPath());
                System.out.println(cutlineVideo_times);
                if(cutlineVideo_times-->1){
                    videoview.start();
                }else {
                    videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(posForVideo)));
                    videoview.start();
                }

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
    //播放下一个图片
    private void nextPicture() {
        if (posForPicture == picture_toPlay_list.size()) {
            posForPicture = 0;
        }
        if(picture_time>5){
            SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
            editor.putInt("posForPicture",posForPicture);
            editor.apply();
        }
        imageview.setImageURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+picture_toPlay_list.get(posForPicture).trim()));
//        if(posForPicture%2==0){
//            videoview.pause();
//        }else {
//            videoview.start();
//        }

        posForPicture++;
    }
    private void setPictureTimer(){
        if(timerForPicture!=null){
            timerForPicture.cancel();
        }
        timerForPicture=new Timer();
//        timerForPicture.purge();
        posForPicture=0;
        timerForPicture.schedule(new TimerTask(){
            public void run(){
//                nextPicture();
                Message msg = new Message();
                msg.what =15;
                handler.sendMessage(msg);
//                timer.cancel();
            }
        }, 0,picture_time*1000);
    }

    ZMQ.Context context = ZMQ.context(1);
    ZMQ.Socket pair = context.socket(ZMQ.PAIR);
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
        return String.format("cpu:%.2f",rate);
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

    private void setLayout(View v,int layoutSize){
        setContentView(layout1,new LinearLayout.LayoutParams(11*layoutSize, 4*layoutSize));
        textView =(TextView)findViewById(R.id.textView);
        textView2 =(TextView)findViewById(R.id.textView2);
        videoview = (VideoView) findViewById(R.id.videoView);
        imageview = (RoundedImageView) findViewById(R.id.imageView);
        imageview2 = (RoundedImageView) findViewById(R.id.imageView2);
        SharedPreferences.Editor editor = getSharedPreferences("data_try1",MODE_PRIVATE).edit();
        editor.putInt("layoutSize",layoutSize);
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

        //LayoutInflater inflater = getLayoutInflater();
        LayoutInflater inflater = LayoutInflater.from(this);
        layout1 = inflater.inflate(R.layout.activity_main_projector, null);
        layout1.setMinimumWidth(407);
        layout1.setMinimumHeight(148);
        setContentView(layout1);
        //以上两行功能一样
        setContentView(layout1,new LinearLayout.LayoutParams(407*2, 148*2));

        System.out.println(getSharedPreferences("data_try1",MODE_PRIVATE).getInt("layoutSize",80));
        restart();
        System.out.println(getSharedPreferences("data_try1",MODE_PRIVATE).getInt("layoutSize",80));
//        setContentView(layout1);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//手机上横屏
        //        send_udp_broadcast(MainActivity.this);
        timerForPicture = new Timer();
        textView =(TextView)findViewById(R.id.textView);
        textView2 =(TextView)findViewById(R.id.textView2);
        videoview = (VideoView) findViewById(R.id.videoView);
        imageview = (RoundedImageView) findViewById(R.id.imageView);
//        imageview.setImageURI(Uri.parse(getExternalFilesDir(null).toString()+ "/new2.jpg"));

        weatherMap = initWeatherMap();




//        videoview.setVideoURI(Uri.parse("android.resource://"+getPackageName() +"/"+R.raw.pic1));

        video_toPlay_list = new ArrayList<>();
        video_toPlay_list.add("a2.mp4");
        video_toPlay_list.add("c.mp4");
        setVideoList(video_toPlay_list);
        picture_toPlay_list = new ArrayList<>();
        picture_toPlay_list.add("png1.png");
        picture_toPlay_list.add("png2.png");
        picture_toPlay_list.add("png3.png");
        setPictureList(picture_toPlay_list);

        System.out.println(get_mac(MainActivity.this));
        System.out.println(android.os.Build.MANUFACTURER);
        setUpdateWeatherTimer();

        init_client();
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
        }, 0,30*60*1000);
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
        pu_ip = pref.getString("pu_ip","not_set");
        defaultCity = pref.getString("defaultCity","西安");
        updateWeather(defaultCity);
        video_toPlay_list = new ArrayList<>(Arrays.asList(pref.getString("video_toPlay_list","not_set").split(",")));
        picture_toPlay_list = new ArrayList<>(Arrays.asList(pref.getString("picture_toPlay_list","not_set").split(",")));
        if(!pu_ip.equals("not_set")){
            test_sub();
        }
        if(video_toPlay_list!=null){
            if(video_toPlay_list.get(0).equals("not_set")){
                System.out.println("video_toPlay_list"+video_toPlay_list);
//                    video_toPlay_list.set(0,"10.mp4");
//                    setVideoList(video_toPlay_list);
            }else {
                System.out.println("00000000");
                System.out.println(video_toPlay_list);
                setVideoList(video_toPlay_list);
            }
        }else {
            System.out.println("88888");
            System.out.println("video_toPlay_list = null");
        }
        if(picture_toPlay_list.get(0).equals("not_set")){
            System.out.println("picture_toPlay_list"+picture_toPlay_list);
        }else {
            System.out.println(picture_toPlay_list);
//                    setPictureList(picture_toPlay_list);
        }
        setLayout(layout1,pref.getInt("layoutSize",90));
//            posForPicture = pref.getInt("posForPicture",0);
//            posForVideo = pref.getInt("posForVideo",0);

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
    public HashMap<String, Integer> initWeatherMap() {
//        ResourceBundle rb = ResourceBundle.getBundle(propertiesFile);
//
//        Enumeration enu = rb.getKeys();
//        while (enu.hasMoreElements()) {
//            Object obj = enu.nextElement();
//            Object objv = rb.getObject(obj.toString());
//            weatherMap.put(obj.toString(), (Integer)objv );
//        }
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
//            Toast.makeText(getApplicationContext(), "删除单个文件失败：" + filePath$Name + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
}
