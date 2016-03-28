package cs490.labbroadcaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Button;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Nishant on 2/20/2016.
 */
public class UserPreferences extends PreferenceActivity {
    Context context = this;
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
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);


        Set<String> selections = sharedPrefs.getStringSet("curr_classes", null);
        if(selections.size() == 0){
            MultiSelectListPreference lp = (MultiSelectListPreference)findPreference("classes_help");
            lp.setEnabled(false);
        }else{
            MultiSelectListPreference lp = (MultiSelectListPreference)findPreference("classes_help");
            lp.setEnabled(true);
            String[] selected = selections.toArray(new String[] {});
            String[] help = new String[selected.length];
            for(int i = 0; i< selected.length; i++){
                help[i] = selected[i]+"checked";
            }
            lp.setEntries(selected);
            lp.setEntryValues(help);

        }

        if(sharedPrefs.getBoolean("pref_broadcast", true) == true){
            EditTextPreference status = (EditTextPreference) findPreference("pref_status");
            status.setEnabled(true);
        }else{
            EditTextPreference status = (EditTextPreference) findPreference("pref_status");
            status.setEnabled(false);
        }

        MultiSelectListPreference taking_classes = (MultiSelectListPreference) findPreference("curr_classes");
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPrefs1 = PreferenceManager.getDefaultSharedPreferences(context);
//                Set<String> selections = sharedPrefs1.getStringSet("curr_classes", null);
                Set<String> selections = (Set<String>) newValue;

                if(selections.size() > 0){
                    Log.e("Selected ",selections.size()+"");
                    String[] selected = selections.toArray(new String[] {});
                    String[] help = new String[selected.length];
                    for(int i = 0; i< selected.length; i++){
                        help[i] = selected[i]+"checked";
                    }
                    MultiSelectListPreference lp = (MultiSelectListPreference)findPreference("classes_help");
                    lp.setEnabled(true);
                    lp.setEntries(selected);
                    lp.setEntryValues(help);

                }else{
                    Log.e("Nothing selected","");
                    MultiSelectListPreference lp = (MultiSelectListPreference)findPreference("classes_help");
                    lp.setEnabled(false);
                }
                return true;
            }
        };
        taking_classes.setOnPreferenceChangeListener(listener);

        SwitchPreference broadcast = (SwitchPreference) findPreference("pref_broadcast");


        Preference.OnPreferenceChangeListener listener1 = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean ischecked = (Boolean) newValue;
                EditTextPreference status = (EditTextPreference) findPreference("pref_status");
                SharedPreferences sharedPrefs1 = PreferenceManager.getDefaultSharedPreferences(context);
                if(/*sharedPrefs1.getBoolean("pref_broadcast", true) == true*/ ischecked){
                    status.setEnabled(true);
                }else{
                    status.setEnabled(false);
                }
                return true;
            }
        };

        broadcast.setOnPreferenceChangeListener(listener1);





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
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.clear();
//        editor.commit();
    }

}
