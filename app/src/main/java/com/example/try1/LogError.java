package com.example.try1;

import android.app.Activity;
import android.content.Context;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class LogError extends Activity {
    public LogError(){

    };
    public void errorLog(String in){
        //异常日志
        saveString("errorLog.txt",in);
    }
    public void countLog(String in){
        //统计日志
        saveString("countLog.txt",in);
    }
    public void saveString(String filename,String inputText){
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try {
            //getExternalFilesDir(null).toString()+"/"+
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
}
