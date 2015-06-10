package com.harmazing.aixiumama.model;

import android.content.Context;

import java.util.LinkedList;

/**
 * Created by Lyn on 2014/11/10.
 */
public class Cute {

    private   LinkedList<Label> labelList ;
    int D;

    public Cute(Context context){

        labelList = new LinkedList<Label>();
    }

    public void addLabel(Label label)
    {
        labelList.add(label);
    }

    public Label getLabel(int i){
        return labelList.get(i);
    }

    public int getLabelListLength(){
        return labelList.size();
    }

    public void clear(){
        labelList.clear();
    }

}
