package com.admin.advisementplayer.preference;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MyPreference {
	private SharedPreferences preference ;
	private Editor preferenceEditor;
	String PREFERENCE_FILE = "mysharePreference";

	public MyPreference(Context context) {
		preference = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
		preferenceEditor = preference.edit();
	}

	public int getIntValue(String preferenceKey) {
		//preference里存放的上一次保存的最大的number值，所以在取出这个值之后，将要再将此值自增1,然后再添加
		return preference.getInt(preferenceKey, 0);
	}


	public String getStringValue(String preferenceKey) {
		return preference.getString(preferenceKey, "");
	}
	public boolean getBooleanValue(String preferenceKey) {
		return preference.getBoolean(preferenceKey, false);
	}

	public void commitIntValue(String preferenceKey, int buttonNumber) {
		preferenceEditor.putInt(preferenceKey, buttonNumber);
		preferenceEditor.commit();
	}

	public void commitStringValue(String preferenceKey, String valueString) {
		preferenceEditor.putString(preferenceKey, valueString);
		preferenceEditor.commit();
	}
	public void commitBooleanValue(String preferenceKey, Boolean valueBoolean) {
		preferenceEditor.putBoolean(preferenceKey, valueBoolean);
		preferenceEditor.commit();
	}
}
