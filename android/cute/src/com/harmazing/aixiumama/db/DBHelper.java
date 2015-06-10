package com.harmazing.aixiumama.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by Lyn on 2014/11/11.
 */
public class DBHelper {
     SQLiteDatabase db;
    Context mContext;
    public DBHelper(Context context){
        mContext = context;
        db = mContext.openOrCreateDatabase("cute.db", Context.MODE_PRIVATE, null);
    }

    public  void CreateDB() throws IOException {
        db.execSQL("CREATE TABLE IF NOT EXISTS Cute (_id INTEGER PRIMARY KEY AUTOINCREMENT,items VARCHAR)");
    }

    public void insertLabelItems(String items){
        db.execSQL("INSERT INTO Cute VALUES (NULL, ?)", new Object[]{items});
    }

    public LinkedList<String> queryData(){
        LinkedList<String> list = new LinkedList<String>();
        Cursor c = db.rawQuery("SELECT * FROM Cute ", null);
        while (c.moveToNext()) {
            String item = c.getString(c.getColumnIndex("items"));
            list.add(item);
        }
        c.close();
        return list;
    }



    public void deleteData(){
        db.execSQL("drop table Cute");
    }
}
