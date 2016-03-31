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
        addPreferencesFromResource(R.xml.userpreferences);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
//region courses need help on
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
                help[i] = selected[i].substring(0, selected[i].length()-8);
                Log.e("substring ",selected[i].substring(0, selected[i].length()-8));
            }
            lp.setEntries(help);
            lp.setEntryValues(selected);

        }
        //endregion
//region brodcast status
        if(sharedPrefs.getBoolean("pref_broadcast", true) == true){
            EditTextPreference status = (EditTextPreference) findPreference("pref_status");
            status.setEnabled(true);
        }else{
            EditTextPreference status = (EditTextPreference) findPreference("pref_status");
            status.setEnabled(false);
        }
//endregion
        //region update course_help local
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
                        help[i] = selected[i].substring(0, selected[i].length()-8);
                        Log.e("substring ",selected[i].substring(0, selected[i].length()-8));
                    }
                    MultiSelectListPreference lp = (MultiSelectListPreference)findPreference("classes_help");
                    lp.setEnabled(true);
                    lp.setEntries(help);
                    lp.setEntryValues(selected);

                }else{
                    Log.e("Nothing selected","");
                    MultiSelectListPreference lp = (MultiSelectListPreference)findPreference("classes_help");
                    lp.setEnabled(false);
                }
                return true;
            }
        };
        //endregion
        taking_classes.setOnPreferenceChangeListener(listener);

        SwitchPreference broadcast = (SwitchPreference) findPreference("pref_broadcast");

//region broadcast status changer
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
//endregion
        broadcast.setOnPreferenceChangeListener(listener1);

        Preference pref = (Preference) findPreference("pref_classes");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

}
