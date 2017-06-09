package com.admin.advisementplayer;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.admin.advisementplayer.preference.MyPreference;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    String data;
    boolean isRun = false;
    DatagramSocket ds;
    DatagramPacket dp;
    private long firstTime = 0;
    WebView my_webView;
    private String url = "http://192.168.1.124/192.168.1.100.html";//http://192.168.1.124/pay1920.html
    //    TextView my_textView;
    MyPreference myPreference;
    private TextView my_textView;
    private MarqueeView myMarqueeView;
//    InetAddress addr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new startUdpServer().start();

        initView();
    }

    private void initView() {
        myPreference = new MyPreference(this);

        my_webView = (WebView) findViewById(R.id.my_webView);
        my_textView = (TextView) findViewById(R.id.my_textView);
//        my_textView.setText("撒大家看了多久爱离开大家安利建档立卡几点啦肯定");
        myMarqueeView = (MarqueeView) findViewById(R.id.marqueeView);
        TextView textView1 = new TextView(this);
        textView1.setText("撒大家看了多久爱离开大家安利建档立卡几点啦肯定");
        myMarqueeView.addView(textView1);
        setTextViewAttribute();
        //启用支持javascript
        WebSettings settings = my_webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        my_webView.loadUrl(url);
        AnimationSet set = new AnimationSet(true);

        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, 0);

        set.addAnimation(translate);

        set.setFillAfter(true);


        my_textView.startAnimation(set);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (data.contains("url")) {
                data = data.replace("url", "");

                if (url.equals(data)) {
                    my_webView.reload();
                } else {
                    url = data;
                    my_webView.loadUrl(url);
                }
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                my_webView.loadUrl(data);
            } else if (data.contains("msg")) {//后台传过来的显示信息
                data = data.replace("msg", "");
                my_textView.setText(data.replace("|", "\n"));
                my_textView.setVisibility(View.VISIBLE);

            } else if (data.contains("tcl")) {//显示文字的颜色
                data = data.replace("tcl", "");
                myPreference.commitStringValue("tcl", data);
                setTextViewAttribute();
            } else if (data.contains("tsz")) {//文字显示大小
                data = data.replace("tsz", "");
                myPreference.commitIntValue("tsz", Integer.parseInt(data));
                setTextViewAttribute();
            } else if (data.contains("bcl")) {//文字背景色
                data = data.replace("bcl", "");
                myPreference.commitStringValue("bcl", data);
                setTextViewAttribute();
            } else if (data.contains("loc")) {//文字相对顶部的距离
                data = data.replace("loc", "");
                myPreference.commitIntValue("loc", Integer.parseInt(data));
                setTextViewAttribute();
            } else if (data.contains("msc")) {//清除显示信息
                my_textView.setVisibility(View.GONE);
            }


        }
    };

    private void setTextViewAttribute() {

        int loc = myPreference.getIntValue("loc");
        if (loc != 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myMarqueeView.getLayoutParams();
            params.setMargins(0, loc, 0, 0);// 通过自定义坐标来放置你的控件left, top, right, bottom
            myMarqueeView.setLayoutParams(params);//
        }
        int tsz = myPreference.getIntValue("tsz");
        if (tsz != 0) {
            my_textView.setTextSize(tsz);
        }

        String textColor = myPreference.getStringValue("tcl");

        String[] tcl = textColor.split(",");
        if (tcl.length > 2) {
            my_textView.setTextColor(Color.rgb(Integer.parseInt(tcl[0]), Integer.parseInt(tcl[1]), Integer.parseInt(tcl[2])));
        }
        String bgColor = myPreference.getStringValue("bcl");
        String[] bcl = bgColor.split(",");
        if (tcl.length > 2) {
            my_textView.setBackgroundColor(Color.rgb(Integer.parseInt(bcl[0]), Integer.parseInt(bcl[1]), Integer.parseInt(bcl[2])));
        }

    }

    class startUdpServer extends Thread {
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
                    udpClient(addr, port);

//                    byte[] echo = "success".getBytes();
//                    DatagramPacket dp2 = new DatagramPacket(echo, echo.length, addr, port);
//                    ds.send(dp2);
                    isRun = false;

                }

            } catch (Exception e) {
                Log.i("miao", "###############################################" + "Exception");
            }
        }
    }

    private void udpClient(final InetAddress addr, final int port) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.i("miao", "###############################################" + "prepared");
                    DatagramSocket ds = new DatagramSocket();
//                    InetAddress addr = InetAddress.getByName("192.168.1.130");
                    String reBack;
                    if (data.contains("src")) {
                        // 获取屏幕分辨率
//                        DisplayMetrics dm = new DisplayMetrics();
//                        getWindowManager().getDefaultDisplay().getMetrics(dm);
//                        int width= (int) (dm.widthPixels*dm.density);
//                        int height= (int) (dm.heightPixels*dm.density);
                        //  Android获得屏幕的宽和高
                        WindowManager windowManager = getWindowManager();
                        Display display = windowManager.getDefaultDisplay();
                        int screenWidth = display.getWidth();
                        int screenHeight = display.getHeight();
                        reBack = screenWidth + "," + screenHeight;
                    } else {
                        reBack = "success";
                    }

                    DatagramPacket dp = new DatagramPacket(reBack.getBytes(), reBack.length(), addr, 8899);
                    ds.send(dp);

//                    byte[] buf = new byte[1024];
//                    DatagramPacket dp2 = new DatagramPacket(buf, 1024);
//                    ds.receive(dp2);
//                    String echo = new String(dp2.getData(), 0, dp2.getLength());
//                    Log.i("miao", "##########################################" + echo);

                    ds.close();
                } catch (Exception e) {
                    Log.i("miao", "###############################################" + "Exception");
                }
            }
        }.start();
    }

    public void udpClient2(View view) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.i("miao", "###############################################" + "prepared");
                    DatagramSocket ds = new DatagramSocket();
                    InetAddress addr = InetAddress.getByName("192.168.1.130");
                    String data = "http://192.168.1.124/pay1920.html";
                    DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length(), addr, 8899);
                    ds.send(dp);

                    byte[] buf = new byte[1024];
                    DatagramPacket dp2 = new DatagramPacket(buf, 1024);
                    ds.receive(dp2);
                    String echo = new String(dp2.getData(), 0, dp2.getLength());
                    Log.i("miao", "##########################################" + echo);

                    ds.close();
                } catch (Exception e) {
                    Log.i("miao", "###############################################" + "Exception");
                }
            }
        }.start();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    firstTime = secondTime;
                    return true;
                } else {
                    System.exit(0);
                    ds.close();
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

}
