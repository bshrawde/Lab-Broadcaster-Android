package cs490.labbroadcaster;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Map;

/**
 * Created by Nishant on 2/20/2016.
 */
public class UserPreferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);

        SQLiteDatabase db = openOrCreateDatabase("pref",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS User_pref(Username varchar,Password varchar,taken varchar,current varchar,langagues varchar);");
        db.execSQL("INSERT INTO User_pref VALUES('bob','can','all','none','java');");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> allEntries = preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            //Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            System.out.println("get Key: "+entry.getKey());
            System.out.println("value: "+entry.getValue().toString());
            System.out.println("\n\n");
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
