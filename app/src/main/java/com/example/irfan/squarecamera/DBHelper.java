package com.example.irfan.squarecamera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static int DB_VERSION = 6;
    private static String DB_NAME = "database";
    private static String TABLE_TIME = "table_time";
    private static String ID = "id";
    private static String TIME = "totalTime";
    private static String STARTTIME = "startTime";
    private static String ENDTIME = "endTime";


    private SQLiteDatabase db;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_TIME + "("
                + ID + " INTEGER PRIMARY KEY,"
                + STARTTIME + " TEXT,"
                + ENDTIME + " TEXT,"
                + TIME + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TIME);
        onCreate(sqLiteDatabase);

    }

    public void addData(Calculate calculate){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, calculate.id);
        values.put(STARTTIME, calculate.startTime);
        values.put(ENDTIME, calculate.endTime);
        values.put(TIME, calculate.totalTime);
        db.insert(TABLE_TIME, null, values);
        db.close();
    }
    public ArrayList<Calculate> getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TIME;
        Cursor data = db.rawQuery(query, null);
        ArrayList<Calculate> listData = new ArrayList<>();
        //looping through all rows and adding to list
        if (data.moveToFirst()){
            do {
                String id=data.getString(0);
                String startTime=data.getString(1);
                String endTime=data.getString(2);
                String totalTime = data.getString(3);
                listData.add(new Calculate(id, startTime,endTime,totalTime));

                //Log.d("wai", waktu.getTimeStart());
            } while ( data.moveToNext());
        }
        db.close();
        Log.d("kasd", "asd" + listData.size());
        return listData;
    }
}
