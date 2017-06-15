package com.project.parking.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.project.parking.data.LoginData;
import com.project.parking.data.SettingData;

import java.io.IOException;

/**
 * Created by Yohanes on 14/06/2017.
 */

public class SharedPreferencesUtils {
    private static final String TAG = "Share Preferences Utils";
    private static SharedPreferences prefs;

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(com.project.parking.data.Constants.PARKING_PREFERENCE,
                Context.MODE_MULTI_PROCESS); //4
    }

    public static void saveLoginData(String loginData,Context ctx){
        SharedPreferences.Editor editor = getPreferences(ctx).edit();
        editor.putString(com.project.parking.data.Constants.LOGIN_DATA_PREF, loginData);
        editor.commit();
    }

    public static void saveSettingData(String settingDataJson,Context ctx,String email){
        SharedPreferences.Editor editor = getPreferences(ctx).edit();
        editor.putString(com.project.parking.data.Constants.SETTING_DATA_PREF+email, settingDataJson);
        editor.commit();
    }

    public static SettingData getSettingData(Context ctx,String email) {
        SettingData settingData = null;
        try {
            String ld = getPreferences(ctx).getString(com.project.parking.data.Constants.SETTING_DATA_PREF+email, "");
            settingData = HttpClientUtil.getObjectMapper(ctx).readValue(ld, new TypeReference<SettingData>(){});
        } catch (JsonGenerationException e) {
            Log.e(TAG, "JsonGenerationException  getSettingData: " + e);
        } catch (JsonMappingException e) {
            Log.e(TAG, "JsonMappingException getSettingData: " + e);
        } catch (IOException e) {
            Log.e(TAG, "IOException getSettingData: " + e);
        }
        return settingData;
    }

    public static LoginData getLoginData(Context ctx) {
        LoginData loginData = null;
        try {
            String ld = getPreferences(ctx).getString(com.project.parking.data.Constants.LOGIN_DATA_PREF, "");
            loginData = HttpClientUtil.getObjectMapper(ctx).readValue(ld, new TypeReference<LoginData>(){});
        } catch (JsonGenerationException e) {
            Log.e(TAG, "JsonGenerationException  getLoginData: " + e);
        } catch (JsonMappingException e) {
            Log.e(TAG, "JsonMappingException getLoginData: " + e);
        } catch (IOException e) {
            Log.e(TAG, "IOException getLoginData: " + e);
        }
        return loginData;
    }
}
