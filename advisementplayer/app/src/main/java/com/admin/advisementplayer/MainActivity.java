package com.admin.advisementplayer;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    String data;
    boolean isRun = false;
    DatagramSocket ds;
    DatagramPacket dp;
    private long firstTime=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new startUdpServer().start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MainActivity.this,data,Toast.LENGTH_SHORT).show();
        }
    };

    class startUdpServer extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                if (ds == null) {
                    ds = new DatagramSocket(8899);
                    byte[] buf = new byte[1024];
                    dp = new DatagramPacket(buf, 1024);
                }
                while (!isRun) {
                    isRun = true;
                    ds.receive(dp);
                    data = new String(dp.getData(), 0, dp.getLength());
                    Log.i("miao", "###############################################" + data);
                    handler.sendEmptyMessage(1);
                    InetAddress addr = dp.getAddress();
                    int port = dp.getPort();
                    byte[] echo = "From Server:echo..........".getBytes();
                    DatagramPacket dp2 = new DatagramPacket(echo, echo.length, addr, port);
                    ds.send(dp2);
                    isRun = false;

                }

            } catch (Exception e) {
                Log.i("miao", "###############################################" + "Exception");
            }
        }
    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                long secondTime=System.currentTimeMillis();
                if(secondTime-firstTime>2000){
                    Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                    firstTime=secondTime;
                    return true;
                }else{
                    System.exit(0);
                    ds.close();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

}
