package com.admin.advisementplayer;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
    WebView my_webView;
    private String url = "http://192.168.1.124/192.168.1.100.html";//http://192.168.1.124/pay1920.html


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new startUdpServer().start();
        initView();
    }
    private void initView(){
        my_webView = (WebView) findViewById(R.id.my_webView);
        //启用支持javascript
        WebSettings settings = my_webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        my_webView.loadUrl(url);
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (url.equals(data)){
                my_webView.reload();
            }else {
                url = data;
                my_webView.loadUrl(url);
            }
            Toast.makeText(MainActivity.this,data,Toast.LENGTH_SHORT).show();
            my_webView.loadUrl(data);
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
                    byte[] echo = "success".getBytes();
                    DatagramPacket dp2 = new DatagramPacket(echo, echo.length, addr, port);
                    ds.send(dp2);
                    isRun = false;

                }

            } catch (Exception e) {
                Log.i("miao", "###############################################" + "Exception");
            }
        }
    }

    public void udpClient(View view) {
        new Thread(){
            @Override
            public void run() {
                try{
                    Log.i("miao","###############################################"+"prepared");
                    DatagramSocket ds = new DatagramSocket();
                    InetAddress addr = InetAddress.getByName("192.168.1.130");
                    String data = "http://192.168.1.124/192.168.1.100.html";
                    DatagramPacket dp = new DatagramPacket(data.getBytes(),data.length(), addr,8899);
                    ds.send(dp);

                    byte[] buf = new byte[1024];
                    DatagramPacket dp2 = new DatagramPacket(buf,1024);
                    ds.receive(dp2);
                    String echo = new String(dp2.getData(),0,dp2.getLength());
                    Log.i("miao","##########################################"+echo);

                    ds.close();
                }catch (Exception e){
                    Log.i("miao","###############################################"+"Exception");
                }
            }
        }.start();
    }
    public void udpClient2(View view) {
        new Thread(){
            @Override
            public void run() {
                try{
                    Log.i("miao","###############################################"+"prepared");
                    DatagramSocket ds = new DatagramSocket();
                    InetAddress addr = InetAddress.getByName("192.168.1.130");
                    String data = "http://192.168.1.124/pay1920.html";
                    DatagramPacket dp = new DatagramPacket(data.getBytes(),data.length(), addr,8899);
                    ds.send(dp);

                    byte[] buf = new byte[1024];
                    DatagramPacket dp2 = new DatagramPacket(buf,1024);
                    ds.receive(dp2);
                    String echo = new String(dp2.getData(),0,dp2.getLength());
                    Log.i("miao","##########################################"+echo);

                    ds.close();
                }catch (Exception e){
                    Log.i("miao","###############################################"+"Exception");
                }
            }
        }.start();
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
