package cs490.labbroadcaster;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Nishant on 2/20/2016.
 */
public class UserPreferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.userpreferences);
    }
}
