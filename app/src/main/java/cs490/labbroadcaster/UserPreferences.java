package cs490.labbroadcaster;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

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
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}