package com.admin.advisementplayer;

/**
 * Created by admin on 2017/6/7.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//开机自启动广播接受
public class AutoStartBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG = "Receiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG,"开机");
//        if (intent.getAction().equals(ACTION)) {
//            Log.e(TAG,"开机启动");
//            Intent mIntent = new Intent(context, MainActivity.class);
//            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            context.startActivity(mIntent);
//        }
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.e(TAG,"开机启动");
            Intent myIntent=new Intent(context,MainActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        }
    }

}