package com.admin.test1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private MarqueeText marqueeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        marqueeText = (MarqueeText) findViewById(R.id.item);
        marqueeText.setText("爱上看见的哈肯定好看");
        marqueeText.startScroll(true);
    }
}
