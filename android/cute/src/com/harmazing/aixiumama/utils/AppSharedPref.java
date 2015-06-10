package com.harmazing.aixiumama.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Created by JingHuiLiu on 7-16-2014.
 */
public class AppSharedPref {

    private static AppSharedPref instance;
    private SharedPreferences mSharedPref;
    private final String APP_NAME = "com.harmazing.cute";
    private static Context mContext;

    private AppSharedPref(Context context) {
        mContext = context;
        mSharedPref =mContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
    }

    public static AppSharedPref newInstance(Context context) {
        mContext = context;
        if(instance == null) {
            instance = new AppSharedPref(context);
        }
        return instance;
    }
    public  void setCurrentState(int state){
        Editor editor = mSharedPref.edit();
        editor.putInt("state", state);
        editor.apply();
    }
    public  int getCurrentState(){
        return mSharedPref.getInt("state",6);
    }
    public void setIsQQProfile(boolean setIsQQProfile) {
        Editor editor = mSharedPref.edit();
        editor.putBoolean("setIsQQProfile", setIsQQProfile);
        editor.apply();
    }
    public void setRongToken(String token){
        Editor editor = mSharedPref.edit();
        editor.putString("rongToken",token);
        editor.apply();
    }
    public String getRongToken(){
        return mSharedPref.getString("rongToken","");
    }
    public void setSplashFlag(Boolean flag){
        Editor editor = mSharedPref.edit();
        editor.putBoolean("splash", flag);
        editor.apply();
    }
   public Boolean getSplashFlag(){
       return mSharedPref.getBoolean("splash", true);
   }
    public boolean getIsQQProfile() {
        return mSharedPref.getBoolean("setIsQQProfile", false);
    }
    public void set1WheelImage(String dir){
        Editor editor = mSharedPref.edit();
        editor.putString("first", dir);
        editor.apply();
    }
    public void set2WheelImage(String dir){
        Editor editor = mSharedPref.edit();
        editor.putString("second", dir);
        editor.apply();
    }
    public void set3WheelImage(String dir){
        Editor editor = mSharedPref.edit();
        editor.putString("third", dir);
        editor.apply();
    }
    public String get1WheelImage(){
        return     mSharedPref.getString("first","");
    }
    public String get2WheelImage(){
        return     mSharedPref.getString("second","");
    }
    public String get3WheelImage(){
        return     mSharedPref.getString("third","");
    }
    public void setToken(String token){
        Editor editor = mSharedPref.edit();
        editor.putString("Token", token);
        editor.apply();
    }
    public void setQQToken(String token){
        Editor editor = mSharedPref.edit();
        editor.putString("QQToken", token);
        editor.apply();
    }
    public void setWeiboToken(String token){
        Editor editor = mSharedPref.edit();
        editor.putString("WeiboToken", token);
        editor.apply();
    }
    public void setWeichatToken(String token){
        Editor editor = mSharedPref.edit();
        editor.putString("WeichatToken", token);
        editor.apply();
    }

    public String getToken(){
        return     mSharedPref.getString("Token",null);
    }
    public String getQQToken(){
        return  mSharedPref.getString("QQToken","");
    }
    public String getWeiboToken(){
        return  mSharedPref.getString("WeiboToken","");
    }
    public String getWeichatToken(){
        return     mSharedPref.getString("WeichatToken",null);
    }

    public void setUserId(String id){
        Editor editor = mSharedPref.edit();
        editor.putString("UserID", id);
        editor.apply();
    }
    public void setQQUserId(String id){
        Editor editor = mSharedPref.edit();
        editor.putString("QQUserID", id);
        editor.apply();
    }
    public void setWeiboUserId(String id){
        Editor editor = mSharedPref.edit();
        editor.putString("WeiboUserID", id);
        editor.apply();
    }
    public void setWeichatUserId(String id){
        Editor editor = mSharedPref.edit();
        editor.putString("WeichatUserID", id);
        editor.apply();
    }
//    public void setSavePic(Boolean isSave){
//        Editor editor = mSharedPref.edit();
//        editor.putBoolean("save",isSave);
//        editor.apply();
//    }
    public String getUserId(){
        return     mSharedPref.getString("UserID",null);
    }
    public String getQQUserId(){
        return     mSharedPref.getString("QQUserID","");
    }
    public String getWeiboUserId(){
        return     mSharedPref.getString("WeiboUserID",null);
    }
    public String getWeichatUserId(){
        return     mSharedPref.getString("WeichatUserID",null);
    }

    public void setUserName(String name){
        Editor editor = mSharedPref.edit();
        editor.putString("UserName", name);
        editor.apply();
    }

    public String getUserName(){
        return     mSharedPref.getString("UserName",null);
    }

    public void setData(String key,String value){
        Editor editor = mSharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getData(String key){
        return mSharedPref.getString(key,"");
    }


    public void setDataInt(String key,int value){
        Editor editor = mSharedPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getDataInt(String key){
        return mSharedPref.getInt(key,0);
    }
    public void clear(){
        if (mSharedPref.getAll().containsKey("WeiboToken"))
                 Log.v("有","!!!");
        mSharedPref.getAll().clear();
        Editor editor = mSharedPref.edit();
        editor.clear();
        editor.apply();

    }
    public void setBirthday(String birthday){
        Editor editor = mSharedPref.edit();
        editor.putString("birthday", birthday);
        editor.apply();
    }
    public String getBirthday(){
        return mSharedPref.getString("birthday","");
    }
    public void setSexBaby(int sex){
        Editor editor = mSharedPref.edit();
        editor.putInt("sex",sex);
        editor.apply();
    }
    public int getBabtSex(){
        return  mSharedPref.getInt("sex", 0);
    }
    public void setUserSex(int sex){
        Editor editor = mSharedPref.edit();
        editor.putInt("userSex",sex);
        editor.apply();
    }
    public int getUserSex(){
        return mSharedPref.getInt("userSex",0);
    }
    public void setDir(String dir){
        Editor editor = mSharedPref.edit();
        editor.putString("dir",dir);
        editor.apply();
    }
    public String getDir(){
        return mSharedPref.getString("dir", "");
    }
    public void setDescription(String des){
        Editor editor = mSharedPref.edit();
        editor.putString("des",des);
        editor.apply();
    }
    public String getDescreption(){
        return mSharedPref.getString("des","");
    }
    public void setPicDir(String url){
        Editor editor = mSharedPref.edit();
        editor.putString("pic",url);
        editor.apply();
    }
    public String getPicDir(){
        return  mSharedPref.getString("pic","");
    }
}
