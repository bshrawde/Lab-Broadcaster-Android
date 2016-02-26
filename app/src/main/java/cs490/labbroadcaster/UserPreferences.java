package cs490.labbroadcaster;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;

import java.util.List;
import java.util.Map;

/**
 * Created by Nishant on 2/20/2016.
 */
public class UserPreferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //}
        //this stuff is in case I cant get the UserPref done ignore it for now
    /*@Override
    public void onBuildHeaders(List<Header> target){
        loadHeadersFromResource(R.xml.preference_headers,target);
    }
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return TakenFrag.class.getName().equals(fragmentName)
                || super.isValidFragment(fragmentName);
    }


    public static class LocationFrag extends PreferenceFragment{

        @Override
        public  void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);


            //PreferenceManager.setDefaultValues(getActivity(),R.xml.advanced_preferences,false);

            addPreferencesFromResource(R.xml.fragmented_preferences);
        }
    }
    public static class TakenFrag extends  PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.class_preferences);
        }
    }*/


        addPreferencesFromResource(R.xml.userpreferences);
        Preference pref = (Preference) findPreference("pref_classes");

        SQLiteDatabase db = openOrCreateDatabase("pref", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS User_pref(Username varchar,Password varchar,taken varchar,current varchar,langagues varchar);");
        db.execSQL("INSERT INTO User_pref VALUES('bob','can','all','none','java');");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Map<String, ?> allEntries = preferences.getAll();
        //for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
        //Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        //   System.out.println("get Key: "+entry.getKey());
        // System.out.println("value: "+entry.getValue().toString());
        // System.out.println("\n\n");
        //}
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

}
