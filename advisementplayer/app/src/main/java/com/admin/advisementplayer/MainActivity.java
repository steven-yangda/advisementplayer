package com.admin.advisementplayer;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {
    private WebView my_webView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            my_webView.reload();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        System.out.print("--------------");
        my_webView = (WebView) findViewById(R.id.my_webView);
        //启用支持javascript
        WebSettings settings = my_webView.getSettings();
        settings.setJavaScriptEnabled(true);
        my_webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        my_webView.loadUrl("http:///www.baidu.com");
//        new Thread(new UDPThread()).start();
    }

    class UDPThread implements Runnable {

        byte[] buf = new byte[1024];
        private int UDP_PORT = 8899;

        public void run() {
            //代码上传测试
            DatagramSocket ds = null;
            try {
                ds = new DatagramSocket(UDP_PORT);
            } catch (BindException e) {
                System.out.println("UDP端口使用中...请重关闭程序启服务器");
            } catch (SocketException e) {
                e.printStackTrace();
            }
        /*用这个来处理循环接收数据包比较合理，服务器接收数据包一般都是转发至客户端，而客户端接收数据包一般处理后再发送到服务器，很复杂。不会
            close()掉，下面只是简单的System.out.println("收到数据包!")*/
            while (ds != null) {
                DatagramPacket dp = new DatagramPacket(buf, buf.length);

                try {
                    ds.receive(dp);
                    System.out.println("收到数据包!");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }
}
