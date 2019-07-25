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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import org.jeromq.ZMQ;
import org.jeromq.ZMQException;
import org.json.JSONObject;

import static java.lang.Thread.sleep;

public class MainActivity extends Activity {
    private TextView textView;
    private TextView textView2;
    static int pos=0;
    private ArrayList<Uri> file_list;
    private ArrayList<String> video_toPlay_list;
    private ArrayList<String> picture_toPlay_list;
    private int picture_time;//每张图片的显示时间
    private Button button;
    private Button button2;
    private String pu_ip;
    private CustomVideoView videoview;
    private ImageView imageview;
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


//    private Runtime run = Runtime.getRuntime();//获取当前运行环境，来执行ping，相当于windows的cmd
    String url30 ;

    // 接收子线程中的信息，来更新控件，因为只有主线程才能更新控件
    private Handler  handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    // 初始化连接，获取控制端ip    pair模式，端口5550

                    pu_ip = msg.obj.toString();
                    System.out.println(pu_ip);
                    break;
                case 1:

                    setVideo(msg.obj.toString());

                    System.out.println("sub模式收到信息");
                    break;
                case 2:

                    videoview.pause();
                    System.out.println("pair模式收到信息");
                    break;
                case 3:

                    videoview.start();
                    break;
                case 4:

                    videoview.resume();
                    break;
                case 5:

//                    videoview.setVideoPath();
//                    videoview.setVideoURI(Uri.parse(videoURI));
                    break;
                case 6:
                    textView.setText(msg.obj.toString()+new Date().toLocaleString());
                    pu_ip =msg.obj.toString();
//                    videoview.setVideoPath();
//                    videoview.setVideoURI(Uri.parse(videoURI));
                    break;
                case 7:
                    //style 1   ，图片+视频
                    setContentView(layout_video_picture);
                    //更新控件
                    videoview = (CustomVideoView) findViewById(R.id.videoView3);
                    imageview = (ImageView) findViewById(R.id.imageView3);
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
                case 10:
                    //更新视频列表
                    ArrayList<String> video_temp_list= new ArrayList<>(Arrays.asList(msg.obj.toString().split(",")));
                    setVideoList(video_temp_list);
                    break;
                case 11:
                    //立即重复播放一个视频
                    setVideo((getExternalFilesDir(null).toString()+"/"+msg.obj.toString()));
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
                    videoview = (CustomVideoView) findViewById(R.id.videoView3);
                    imageview = (ImageView) findViewById(R.id.imageView3);
                    break;
                case 13:
                    //更新生活信息，天气预报+限行
                    textView.setText(dateToday+" "+"\n"+weatherTypeToday+"  "+temperatureToday+" ℃");
                    textView2.setText(dateTomorrow+"\n"+weatherTypeTomorrow+"  "+temperatureTomorrow+" ℃");
//                    private String weatherTypeToday;
//                    private String weatherTypeTomorrow;
//                    private String dateToday;
//                    private String dateTomorrow;temperatureTomorrow
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
                                    videoview.start();
                                    break;
                                case "pause_video":
                                    videoview.pause();
                                    break;
                                case "get_file_list":
//                                    pair.send(getFileNameList().toString());
                                    pair.send(getFileNameList().toString()+"["+getAvailableInternalMemorySize()+"]");
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
                                        String temp =setCutlineVideo(getData.split("_")[getData.split("_").length-2],Integer.parseInt(getData.split("_")[getData.split("_").length-1]));
                                        if(temp.equals("fileNotFound")){
                                            pair.send("fileNotFound");
                                        }
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
                                videoview.start();
                                break;
                            case "pause_video":
                                videoview.pause();
                                break;
                            case "get_file_list":
                                pair.send(getFileNameList().toString()+"["+getAvailableInternalMemorySize()+"]");
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
                                    pair.send(transport_file_name+"传输完成");
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
                                    String temp =setCutlineVideo(getData.split("_")[getData.split("_").length-2],Integer.parseInt(getData.split("_")[getData.split("_").length-1]));
                                    if(temp.equals("fileNotFound")){
                                        pair.send("fileNotFound");
                                    }
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
        pos=0;
        video_toPlay_list = new ArrayList<>(new_file_list);
        if (file_list.size()<1){
            System.out.println("文件列表中无文件");
            return;
        }
        System.out.println(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(pos));
        videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(pos)));
        videoview.start();
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
//                videoview.setVideoPath(file_list.get(pos++).getPath());
                nextVideo();
            }
        });
    }
    //播放下一个视频
    private void nextVideo() {
// TODO Auto-generated method stub
        pos++;
        if (pos==video_toPlay_list.size()) {
            pos=0;
        }
        videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(pos)));
//        videoview.setMediaController(mc);
//        videoview.requestFocus();
        videoview.start();
    }
    // 循环播放一个视频
    public void setVideo(String url){
        videoview.stopPlayback();
        videoview.setVideoURI(Uri.parse(url));
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
                    videoview.setVideoURI(Uri.parse(getExternalFilesDir(null).toString()+"/"+video_toPlay_list.get(pos)));
                    videoview.start();
                }

            }
        });
        return "ok";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send_udp_broadcast(MainActivity.this);

        textView =(TextView)findViewById(R.id.textView);
        textView2 =(TextView)findViewById(R.id.textView2);
        videoview = (CustomVideoView) findViewById(R.id.videoView);
//        button = (Button) findViewById(R.id.button1);
//        button2 = (Button) findViewById(R.id.button2);
        file_list =new ArrayList<>();
        video_toPlay_list =new ArrayList<>();
        String url10 =getExternalFilesDir(null).toString()+ "/10.mp4";
        url30=getExternalFilesDir(null).toString()+ "/30_saved_1.mp4";
//        File f = new File(urla);
//        System.out.println(getFiles(getExternalFilesDir(null).toString()).toString());
//        String system_file_path =getExternalFilesDir(null).toString();
        System.out.println(getFileNameList().toString());
        //LayoutInflater inflater = getLayoutInflater();
        LayoutInflater inflater = LayoutInflater.from(this);
        file_list.add(Uri.parse(url10));
        file_list.add(Uri.parse(url30));
        //以上两行功能一样
        layout_video_picture = inflater.inflate(R.layout.style1, null);
//        videoview.setVideoURI(Uri.parse("android.resource://"+getPackageName() +"/"+R.raw.pic1));
        videoview.setVideoURI(Uri.parse("android.resource://"+getPackageName() +"/"+R.raw.nine_second));
        videoview.start();
        System.out.println(get_mac(MainActivity.this));
        System.out.println(android.os.Build.MANUFACTURER);
//        setVideo(url30);

        getWeatherDatafromNet("北京");
//        video_toPlay_list.add("10.mp4");
        video_toPlay_list.add("30_saved_1.mp4");
//        setVideoList(video_toPlay_list);

        init_client();
//        test_sub();
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                init_client();
////                Intent i = new Intent(MainActivity.this,learn.class);
////                startActivity(i);
////                setVideo(urla);
//            }
//        });
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
private void getWeatherDatafromNet(String city)
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
                    Log.d("date from url",str);
                    JSONObject result = new JSONObject(str);
                    JSONObject temp0 =result.getJSONObject("data").getJSONArray("forecast").getJSONObject(0);
                    JSONObject temp1 =result.getJSONObject("data").getJSONArray("forecast").getJSONObject(1);
                    weatherTypeToday=temp0.getString("type");
                    dateToday=temp0.getString("date");
                    weatherTypeTomorrow=temp1.getString("type");
                    dateTomorrow=temp1.getString("date");
                    temperatureToday=result.getJSONObject("data").getString("wendu");
                    temperatureTomorrow = temp1.getString("high").substring(2,5);

                    Message msg =new Message();
                    msg.what=13;
                    handler.sendMessage(msg);
//                    System.out.println("weatherType"+weatherType);
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
}
