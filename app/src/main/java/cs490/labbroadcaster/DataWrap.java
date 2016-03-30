package cs490.labbroadcaster;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cs490.labbroadcaster.DBHelpler;

/**
 * Created by Purdue on 3/29/2016.
 */
public class DataWrap {
    private DBHelpler dbHelper;

    public DataWrap(Context context){
        dbHelper = new DBHelpler(context);
    }

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
