/**
 * Created by Brian on 2/25/2016.
 */
package cs490.labbroadcaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
public class DBHelpler extends SQLiteOpenHelper {

    public static final String DB_Name = "broadcater_db";
    public static final String Pref_Table_Name = "pref";
    public static final String Lab_Table_name = "Labs";
    public static final String Pref_Column_UName = "username";
    public static final String Pref_Column_Pass = "password";
    public static final String Pref_Column_taken="taken";
    public static final String Pref_Column_current="current";
    public static final String Pref_Column_lang="languages";
    public static final String Lab_Column_room="Lab_Room";
    public static final String Lab_Column_total="Total_capacity";
    public static final String Lab_Column_current = "current_capacity";

    public DBHelpler(Context context){
        super(context,DB_Name,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table Pref "+"(username text,password text,taken text,current text,languages text)");
        db.execSQL("create table Labs "+"(Lab_room text,Total_capacity text,current_capacity text)");
    }
    @Override
    public  void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("DROP TABLE IF EXISTS Pref");
        db.execSQL("DROP TABLE IF EXISTS Labs");
        onCreate(db);

    }
    public boolean insertUser(String username, String password,String taken,String curr,String lang){
        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("username",username);
        contentValues.put("password",password);
        contentValues.put("taken",taken);
        contentValues.put("current",curr);
        contentValues.put("languages",lang);
        db1.insert("Pref",null,contentValues);
        return true;
    }
    public boolean insertLab(String Lab_name,String Total,String current){
        SQLiteDatabase db1 = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Lab_room",Lab_name);
        contentValues.put("Total_capacity",Total);
        contentValues.put("current_capacity",current);
        db1.insert("Labs",null,contentValues);
        return true;
    }
    public Cursor getPref(String username){
        SQLiteDatabase db =this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Pref where username = "+username+"",null);
        return res;
    }
    public Cursor getLabs(String Lab){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Labs where Lab_room = "+Lab+"",null);
        return  res;
    }
    public boolean updatePref(String username,String password,String taken,String current,String lang){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username",username);
        contentValues.put("password",password);
        contentValues.put("taken",taken);
        contentValues.put("current",current);
        contentValues.put("languages",lang);

        db.update("Pref",contentValues,"username = ? ", new String[]{username}); //mabye change the ? here

        return true;
    }
    //public Integer deletePref(){
    //
    //}
    //public ArrayList<String> getAllData(){ //probs wont use
    //}
}
