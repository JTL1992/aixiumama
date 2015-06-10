package com.harmazing.aixiumama.model;

import android.content.Context;

import com.harmazing.aixiumama.application.CuteApplication;

/**
 * Created by Lyn on 2014/11/10.
 */
public class Label {
    Double X ,Y;
    int direction,id;
    String name;
    int screenWidth;
    public Label(Context context){
        screenWidth = CuteApplication.getScreenHW(context)[0];
    //    imageHeight = BitmapUtil.dip2px(context,350);       //因为首页里imageview 的height为350dp

    }


    public void setX(Double x) {
        X = x * screenWidth;

    }

    public void setXByAdd(Double x) {
        X = x ;

    }

    public void setYByAdd(Double y) {
        Y = y ;
    }
    public void setY(Double y) {
        Y = y * screenWidth;
    }

    public Double getX() {
        return X;
    }

    public Double getY() {
        return Y;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    // 1左边   2 右边
    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDirection() {
        return direction;
    }

    public String getName() {
        return name;
    }

    public void clear(){
        setName("");
        X = 0d;
        Y = 0d;
    }
}
