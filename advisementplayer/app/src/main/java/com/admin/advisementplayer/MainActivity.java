package com.admin.advisementplayer;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.admin.advisementplayer.preference.MyPreference;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String data, myMsg = "";
    boolean isRun = false;
    DatagramSocket ds;
    DatagramPacket dp;
    private long firstTime = 0;
    WebView my_webView;
    private String url = "http://192.168.1.218/192.168.1.120.html";//http://192.168.1.124/pay1920.html
    MyPreference myPreference;
    private TextView my_textView;
    InetAddress addr;
    private List<MarqueeText> marqueeTexts = new ArrayList<>();
    private List<Boolean> ftps = new ArrayList<>();
    int port;
    LinearLayout my_layout;
    private LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new startUdpServer().start();

        initView();
    }

    private void initView() {
        inflater = getLayoutInflater();
        myPreference = new MyPreference(this);

        my_webView = (WebView) findViewById(R.id.my_webView);
        my_textView = (TextView) findViewById(R.id.my_textView);
        my_layout = (LinearLayout) findViewById(R.id.my_layout);
        getFtp();
        addTextView();
        setTextViewAttribute();
        //启用支持javascript
        WebSettings settings = my_webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        my_webView.loadUrl(url);
    }

    private void addTextView() {
        my_layout.removeAllViews();
        marqueeTexts.clear();

        for (int i = 0; i < 5; i++) {
            View view = inflater.inflate(R.layout.item_martextview, null);
            MarqueeText marqueeText = (MarqueeText) view.findViewById(R.id.item);
            marqueeTexts.add(marqueeText);
            if (ftps.size() > 4) {
                if (!ftps.get(i)) {
                    ViewGroup.LayoutParams params = marqueeText.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    marqueeText.setLayoutParams(params);
                } else {
                    ViewGroup.LayoutParams params = marqueeText.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    marqueeText.setLayoutParams(params);
                }
                marqueeText.startScroll(ftps.get(i));
            } else {

                ViewGroup.LayoutParams params = marqueeText.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                marqueeText.setLayoutParams(params);
                marqueeText.startScroll(false);
            }
            my_layout.addView(view);

        }


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
                my_webView.loadUrl(data);
                udpClient(addr, port);
            } else if (data.contains("msg")) {//后台传过来的显示信息
                data = data.replace("msg", "");
                myMsg = data;
                updateMsg();
                udpClient(addr, port);
            } else if (data.contains("tcl")) {//显示文字的颜色
                data = data.replace("tcl", "");
                myPreference.commitStringValue("tcl", data);
                setTextViewAttribute();
                udpClient(addr, port);
            } else if (data.contains("tsz")) {//文字显示大小
                data = data.replace("tsz", "");
                String[] myTsz = data.split("\\|");
                if (myTsz.length != 5) {
                    Toast.makeText(MainActivity.this, "设置字幕大小格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                myPreference.commitStringValue("tsz", data);
                setTextViewAttribute();
                udpClient(addr, port);
            } else if (data.contains("bcl")) {//文字背景色
                data = data.replace("bcl", "");
                myPreference.commitStringValue("bcl", data);
                setTextViewAttribute();
                udpClient(addr, port);
            } else if (data.contains("loc")) {//文字相对顶部的距离
                data = data.replace("loc", "");
                myPreference.commitIntValue("loc", Integer.parseInt(data));
                setTextViewAttribute();
                udpClient(addr, port);
            } else if (data.contains("msc")) {//清除显示信息
                my_textView.setVisibility(View.GONE);
                udpClient(addr, port);
            } else if (data.contains("ftp")) {//字幕显示模式分0,1
                data = data.replace("ftp", "");
                String[] myFtps = data.split("\\|");
                if (myFtps.length != 5) {
                    Toast.makeText(MainActivity.this, "设置字幕模式格式不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                myPreference.commitStringValue("ftp", data);
                getFtp();
                addTextView();
                setTextViewAttribute();
                updateMsg();
                udpClient(addr, port);
            }else if (data.contains("src")){

                udpClient(addr, port);
            }


        }
    };

    /**
     * 获取字幕滚动属性
     */
    String ftp;
    String[] myFtps;
    private void getFtp(){
        //设置显示字幕属性（滚动与非滚动）
        ftp = myPreference.getStringValue("ftp");
        myFtps = ftp.split("\\|");
        ftps.clear();
        if (myFtps.length > 4) {
            for (int i = 0; i < myFtps.length; i++) {
                if (myFtps[i].equals("0")) {
                    ftps.add(false);
                } else {
                    ftps.add(true);
                }
            }
        } else {
            for (int i = 0; i < 5; i++) {
                ftps.add(false);
            }
        }
    }

    /**
     * 设置字幕属性
     */
    private void setTextViewAttribute() {


//        if (!myMsg.equals("")) {
//            String[] updateStr = myMsg.split("\\|");
//            for (int i = 0; i < 5; i++) {
//                if (updateStr[i].equals("")) {
//                    MarqueeText marqueeText = marqueeTexts.get(i);
//                    marqueeText.setVisibility(View.GONE);
//                    marqueeText.setText("");
//                } else {
//                    MarqueeText marqueeText = marqueeTexts.get(i);
//                    marqueeText.setText(updateStr[i]);
//                    marqueeText.setVisibility(View.VISIBLE);
//                    marqueeText.startScroll(ftps.get(i));
//                }
//            }
//        }
        //获取字幕颜色
        String textColor = myPreference.getStringValue("tcl");

        String[] tcl = textColor.trim().split(",");
        if (tcl.length > 2) {
            for (int i = 0; i < marqueeTexts.size(); i++) {
                MarqueeText marqueeText = marqueeTexts.get(i);
                marqueeText.setTextColor(Color.rgb(Integer.parseInt(tcl[0]), Integer.parseInt(tcl[1]), Integer.parseInt(tcl[2])));
            }
        }
        //获取设置位置
        int loc = myPreference.getIntValue("loc");
        if (loc != 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) my_layout.getLayoutParams();
            params.setMargins(0, loc, 0, 0);// 通过自定义坐标来放置你的控件left, top, right, bottom
            my_layout.setLayoutParams(params);//
        }

        //获取字幕尺寸
        String textSize = myPreference.getStringValue("tsz");
        String[] myTsz = textSize.split("\\|");

        for (int i = 0; i < myTsz.length; i++) {
            if (!myTsz[i].equals("")) {
                MarqueeText marqueeText = marqueeTexts.get(i);
                marqueeText.setTextSize(Integer.parseInt(myTsz[i]));
            }
        }
        String bgColor = myPreference.getStringValue("bcl");
        String[] bcl = bgColor.split(",");
        if (bcl.length > 2) {
            my_layout.setBackgroundColor(Color.rgb(Integer.parseInt(bcl[0]), Integer.parseInt(bcl[1]), Integer.parseInt(bcl[2])));
        }

    }


    private void updateMsg() {
        addTextView();
        setTextViewAttribute();
        String[] updateStr = myMsg.split("\\|");
        if (updateStr.length == 5) {
            for (int i = 0; i < 5; i++) {
                if (updateStr[i].equals("")) {
                    MarqueeText marqueeText = marqueeTexts.get(i);
                    marqueeText.setVisibility(View.GONE);
                    marqueeText.setText("");
                } else {
                    MarqueeText marqueeText = marqueeTexts.get(i);
                    marqueeText.setText(updateStr[i]);
                    marqueeText.setVisibility(View.VISIBLE);
                    marqueeText.startScroll(ftps.get(i));
                }
            }
        } else {
            Toast.makeText(MainActivity.this, "字幕格式不正确", Toast.LENGTH_SHORT).show();
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
                    Log.i("miao", "-----------------------------------------------" + data);
                    handler.sendEmptyMessage(1);
                    addr = dp.getAddress();
                    port = dp.getPort();

//                    byte[] echo = "success".getBytes();
//                    DatagramPacket dp2 = new DatagramPacket(echo, echo.length, addr, port);
//                    ds.send(dp2);
                    isRun = false;

                }

            } catch (Exception e) {
                Log.i("miao", "----------------------------------------------" + "Exception");
            }
        }
    }

    private void udpClient(final InetAddress addr, final int port) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.i("miao", "------------------------------------------" + "prepared");
                    DatagramSocket ds = new DatagramSocket();
//                    InetAddress addr = InetAddress.getByName("192.168.1.130");
                    String reBack;
                    if (data.contains("src")) {
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
