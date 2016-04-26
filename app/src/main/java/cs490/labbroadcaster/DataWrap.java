package cs490.labbroadcaster;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cs490.labbroadcaster.DBHelpler;

/**
 * Created by Brian Shrawder on 3/29/2016.
 * used to insert update and delete the database
 */
public class DataWrap {
    private DBHelpler dbHelper;

    public DataWrap(Context context){
        dbHelper = new DBHelpler(context);
    }
//region users
    public boolean insertUser(String username,String password){
        boolean status = false;
        boolean exists = false;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            contentValues.put("username", username);
            contentValues.put("password", password);
            String check_user = "SELECT username FROM User";
            Cursor res = db.rawQuery(check_user,null);

            int num = res.getCount();
            if(res.moveToFirst()){
                do{
                    String out = res.getString(0);
                    System.out.println("\n\n\n\n\n CURREN TUSER"+out);
                    if(out.equals(username)){
                        exists = true;
                    }
                }while(res.moveToNext());
            }
            if(exists){
                status = false;
            }else {
                db.insert("User", null, contentValues);
                status = true;
            }
            res.close();
            db.close();
        }catch(Exception e){
            System.err.println("INsert eror");
        }
        return status;
    }
    public boolean updateUser(String username,String password,String new_pass){
        return true;
    }
    public boolean getUser(String username){
        return true;
    }
//endregion
//region TA
    public boolean insertTA(String username,String classes){
        return true;
    }
    public boolean updateTA(String username,String classes){
        return true;
    }
    public boolean getTA(String username,String classes){
        return true;
    }
//endregion
//region Labs
    public boolean insertLab(String name,String total, String current){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("Lab_room",name);
        contentValues.put("Total_capacity",total);
        contentValues.put("current_capacity",current);
        db.insert("Labs",null,contentValues);

        db.close();
        return true;
    }
    public boolean updateLab(String name,String current){
     return  true;
    }
    public boolean getLab(String name){
        return true;
    }
//endregion
//region prefrences
    public boolean insertPref(String username,String taken,String current,String need_help,String lang){
        //in db names
        //username , taken,current,need_help,languages
        return true;
    }
    public boolean updatePref(){
        return true;
    }
    public boolean getPref(){
        return true;
    }
//endregion
    public void delete(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db,1,2);
        db.close();

    }
    public boolean getIt(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String temp = "";
        System.out.println("\n\n\n\n\n INTO RETRUEVE");
        //temp = db.toString();
        String select_querey = "SELECT username FROM Pref";
        Cursor res = db.rawQuery(select_querey,null);
        int num = res.getCount();
        temp = res.getColumnName(0);

        if(res.moveToFirst()){
            do{
                String out = res.getString(0);
            }while(res.moveToNext());
        }
        res.close();
        db.close();
        return  true;
    }
    public void update(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.close();


    }

}
