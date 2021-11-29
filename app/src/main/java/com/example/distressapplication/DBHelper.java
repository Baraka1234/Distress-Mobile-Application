package com.example.distressapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper  extends SQLiteOpenHelper {
    public DBHelper(Context context){
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Userdetails(id integer PRIMARY KEY AUTOINCREMENT,users_name TEXT,users_number TEXT, em_name TEXT, em_number TEXT )");
}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists Userdetails");

        onCreate(db);
    }

    public Boolean insertuserdata(String Users_name, String Users_number, String Em_name, String Em_number){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("users_name", Users_name);
        contentValues.put("users_number", Users_number);
        contentValues.put("em_name", Em_name);
        contentValues.put("em_number", Em_number);
        long result=db.insert("Userdetails", null, contentValues);
        if(result == -1) {
            return false;
        }else{
            return true;
        }
    }

    public Boolean deletedata(String Users_name){
        SQLiteDatabase db= this.getWritableDatabase();
        //Cursor cursor = db.rawQuery ( "Select * from Userdetails where Users_name=?", new String[] {Users_name} );
        //if (cursor.getCount() > 0) {
            long result = db.delete("Userdetails", null, null);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        /*}else {
            return false;
        }*/
    }

    public Cursor getdata (){

        SQLiteDatabase db= this.getWritableDatabase();
        Cursor cursor = db.rawQuery ( "Select users_name, users_number, em_name, em_number from Userdetails ", null );
        return cursor;
    }

}
