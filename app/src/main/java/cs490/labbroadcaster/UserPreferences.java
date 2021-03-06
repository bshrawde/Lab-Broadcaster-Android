package cs490.labbroadcaster;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Nishant on 2/20/2016.
 */
public class UserPreferences extends AppCompatActivity {
    public Context context = this;
    static boolean doibroadcast = false;
    Intent intent;
    static PendingIntent refreshCapacities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //VERY FIST THING
        //new GetUserPrefs().execute();
        //new GetUserPrefs().execute();

        intent  = new Intent(context, UpdateCapacitiesService.class);
        refreshCapacities = PendingIntent.getService(context,0,intent, 0);

        setContentView(R.layout.activity_userprefs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Typeface robotoMono = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-mono-regular.ttf");

        mTitle.setTypeface(robotoMono);


        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MyPreferenceFragment()).commit();
    }


    public static class MyPreferenceFragment extends PreferenceFragment{
        @Override
        public void onCreate(final Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.userpreferences);
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//region courses need help on
/*            Set<String> selections = sharedPrefs.getStringSet("curr_classes", null);
            Log.e("current class lenght",selections.size()+"");

            String[] selected = selections.toArray(new String[] {});*/

            //Log.e("0?>??", selected[0]+"");
            //Log.e("0?>??1", selected[1]+"");
            //Log.e("0?>??2", selected[2]+"");
            /*if(selections.size() == 0){
                MultiSelectListPreference lp = (MultiSelectListPreference)findPreference("classes_help");
                lp.setEnabled(false);
            }else{
                MultiSelectListPreference lp = (MultiSelectListPreference)findPreference("classes_help");
                lp.setEnabled(true);
//                String[] selected = selections.toArray(new String[] {});
                Log.e("SELECTED LENGTH",selected.length+"");
                String[] help = new String[selected.length];
                for(int i = 0; i< selected.length; i++){
                    Log.e("in FOR",i+"");
                    Log.e("data",selected[i]+"");
                    if(selected[i].length() - 8 <= 0){
                        continue;
                    }
                    help[i] = selected[i].substring(0, selected[i].length()-8);

                    Log.e("substring ",help[i]);
                }
                lp.setEntries(help);
                lp.setEntryValues(selected);

            }*/
            //endregionb2
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
           /* MultiSelectListPreference taking_classes = (MultiSelectListPreference) findPreference("curr_classes");
            Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    SharedPreferences sharedPrefs1 = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                Set<String> selections = sharedPrefs1.getStringSet("curr_classes", null);
                    Set<String> selections = (Set<String>) newValue;

                    if(selections.size() > 0){
                        Log.e("Selected ",selections.size()+"");
                        String[] selected = selections.toArray(new String[] {});
                        String[] help = new String[selected.length];
                        for(int i = 0; i< selected.length-1; i++){
                            if(selected[i] == null){
                                continue;
                            }
                            help[i] = selected[i].substring(0, selected[i].length()-8);
                            Log.e("substring ",selected[i].substring(0, selected[i].length()-8));
                        }
                        MultiSelectListPreference lp = (MultiSelectListPreference)findPreference("classes_help");
                        lp.setEnabled(true);
                        lp.setEntries(help);
                        lp.setEntryValues(selected);
                        Log.e("SETTING SOME CLASSES", "BITCH");

                    }else{
                        Log.e("Nothing selected","");
                        MultiSelectListPreference lp = (MultiSelectListPreference)findPreference("classes_help");
                        lp.setEnabled(false);
                    }
                    return true;
                }
            };
            //endregion
            taking_classes.setOnPreferenceChangeListener(listener);*/

            SwitchPreference broadcast = (SwitchPreference) findPreference("pref_broadcast");
            final ListPreference room = (ListPreference) findPreference("pref_room");

//region broadcast status changer
            Preference.OnPreferenceChangeListener listener1 = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    boolean ischecked = (Boolean) newValue;
                    EditTextPreference status = (EditTextPreference) findPreference("pref_status");
                    SharedPreferences sharedPrefs1 = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    if(/*sharedPrefs1.getBoolean("pref_broadcast", true) == true*/ ischecked){
                        doibroadcast = true;
                        status.setEnabled(true);
                        room.setEnabled(true);
                    }else{
                        status.setEnabled(false);
                        room.setEnabled(false);
                        doibroadcast = false;
                    }
                    return true;
                }
            };
//endregion
            broadcast.setOnPreferenceChangeListener(listener1);


            ListPreference refreshrate = (ListPreference) findPreference("refresh_rate");

//region broadcast status changer
            Preference.OnPreferenceChangeListener listener2 = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    //Log.e("selected: ", newValue.toString());
                    if(newValue.toString().equals("manual-checked")){
                        Log.e("MANUAL REFRESH", "BRUH!");
                        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        manager.cancel(refreshCapacities);
                    }else{
                        Log.e("HOURLY REFRESH", "BRUH!");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        int hour = calendar.get(Calendar.HOUR_OF_DAY) + 1;
                        int min = calendar.get(Calendar.MINUTE);
                        Log.e("Refreshing in", hour+" "+min);
                        calendar.set(Calendar.HOUR_OF_DAY, hour); /* refresh in 1 hour */
                        calendar.set(Calendar.MINUTE, min);
                        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HOUR, refreshCapacities);
                    }
                    return true;
                }
            };
//endregion
            refreshrate.setOnPreferenceChangeListener(listener2);



            Preference pref = (Preference) findPreference("pref_classes");

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_userpref, menu);
        return true;
    }
    @Override
    public void onPause() {
        // your code.
        super.onPause();
//        //Toast.makeText(UserPreferences.this, "Back button pressed pause", Toast.LENGTH_SHORT).show();
        new SaveUserPrefs().execute();
        new DeleteBroadcasters().execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_save:
//                //Toast.makeText(UserPreferences.this, "Saving...", Toast.LENGTH_SHORT).show();
                recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public class SaveUserPrefs extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... params) {
            SharedPreferences logger = PreferenceManager.getDefaultSharedPreferences(context);
            Log.e("AsyncTask running","WTF");
            InputStream in;
            HttpURLConnection con;
            String found = "";
            String[] found_array1= new String[10];
            InputStream caInput = null;
            InputStream is = null;

            Certificate ca = null;
            AssetManager assManager = context.getAssets();
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");

                is = assManager.open("mc15.cs.purdue.edu.cer");
                caInput = new BufferedInputStream(is);

                ca = cf.generateCertificate(caInput);
            } catch (CertificateException e) {
                System.out.println("\n\nTHERE WAS AN ERROR1");
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                System.out.println("\n\nTHERE WAS AN ERROR2");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    caInput.close();
                } catch (IOException e) {
                    System.out.println("\n\nTHERE WAS AN ERROR3");
                    e.printStackTrace();
                } catch (NullPointerException e){
                    System.out.println("\n\nTHERE WAS AN ERROR4");
                    e.printStackTrace();
                }
            }
            int counter = 0;

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = null;


            try {
                keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);
                Log.e("BRUHHHHh", "BRUH");

                //TODO: FIX URL
                URL url = new URL("https://mc15.cs.purdue.edu:5001/user/preferences");

                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("PUT");
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                String uname = logger.getString("email","");
                String pass = logger.getString("pw","");


                //TODO: CHANGE OUT.WRITE
                //username : string, courses : string, current : string,  languages : string
                ///public SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String u_name = logger.getString("email","");
                Set<String> curr_classes = logger.getStringSet("curr_classes",null);
                Set<String> classes_taken = logger.getStringSet("pref_classes",null);
                Set<String> Lang = logger.getStringSet("pref_languages",null);
                String session = logger.getString("sessionID",null);

                String[] WTF = curr_classes.toArray(new String[0]);
                String[] OMG = Lang.toArray(new String[0]);
                String[] BBQ = classes_taken.toArray(new String[0]);
                String curr="";
                String bbqs = "";
                String omgs = "";

                boolean www = false;
                for(int w = 0;w<WTF.length;w++){
                    Log.e("WTF IS GOIN ONG",WTF[w]);
                    if(WTF[w].equals("\"\"")){

                    }else {
                        if (w==WTF.length-1) {
                            curr += WTF[w];

                        } else {
                            curr += WTF[w]+",";
                        }
                    }
                }
                boolean bull = false;
                for(int w = 0;w<BBQ.length;w++){
                    Log.e("WTF IS GOIN ONG",BBQ[w]);
                    if(BBQ[w].equals("\"\"")){
                    }else {
                        if (w==BBQ.length-1) {
                            bbqs += BBQ[w];


                        } else {
                            bbqs += BBQ[w]+",";
                        }
                    }
                }
                boolean comma = false;
                for(int w = 0;w<OMG.length;w++){
                    Log.e("WTF IS GOIN ONG",OMG[w]);
                    if(OMG[w].equals("\"\"")){

                    }else{
                        if (w==OMG.length-1) {
                            omgs += OMG[w];


                        } else {
                            omgs += OMG[w]+",";
                        }
                    }
                }
                Log.e("after",curr);
                Log.e("after",bbqs);
                Log.e("after",omgs);


                out.write("{\"username\" : "+"\""+uname+"\",  "+"\"courses\" : "+"\""+bbqs+"\", "+"\"current\" : "+"\""+curr+"\", "+"\"languages\" : "+"\""+omgs+"\" , "+"\"session\" : "+"\""+session+"\" }");
                out.close();
                in = urlConnection.getInputStream();

                int t = in.available();
                Log.e("Login available: ",t+"");
                char d = (char)in.read();
                char c='a';
                found+=d;
//                System.out.println("FIRST CHAR: "+d);
                while(in.available()>0){
                    c = (char)in.read();
                    found+=c;
//                    System.out.println("CHARS FROM READER: "+c);

                }
                Log.e("Login found=",found);
                if(c =='}'){
                    String temp = "";
                    in.close();
                    for(int i=0;i<found.length();i++){
                        char b = found.charAt(i);
                        temp +=b;
                        if(b=='\n'/* && counter<found_array1.length-1*/){
//                            Log.e("COUNTER=",counter+"");
                            found_array1[counter] = temp;
//                            Log.e("FOUND ARRAY AT: ",counter+": "+found_array1[counter]);
                            temp ="";
                            counter++;
                        }
                    }
                    in.close();
                    urlConnection.disconnect();
                }

            } catch (KeyStoreException e) {
                System.out.println("\n\nTHERE WAS AN ERROR5");
                e.printStackTrace();
            } catch (CertificateException e) {
                System.out.println("\n\nTHERE WAS AN ERROR6");
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                System.out.println("\n\nTHERE WAS AN ERROR7");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("\n\nTHERE WAS AN ERROR8 inside login");
                e.printStackTrace();
            } catch (KeyManagementException e) {
                System.out.println("\n\nTHERE WAS AN ERROR8 inside login 2");
                e.printStackTrace();
            }
            counter = 0;
            Log.e("found length=",found.length()+"");
            return found;
        }

        @Override
        protected void onPostExecute(String s){
            if(s.length() == 0){

            }else{

                //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class SetBroadcasters extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... params) {
            SharedPreferences logger = PreferenceManager.getDefaultSharedPreferences(context);
            Log.e("AsyncTask running","WTF");
            InputStream in;
            HttpURLConnection con;
            String found = "";
            String[] found_array1= new String[10];
            InputStream caInput = null;
            InputStream is = null;

            Certificate ca = null;
            AssetManager assManager = context.getAssets();
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");

                is = assManager.open("mc15.cs.purdue.edu.cer");
                caInput = new BufferedInputStream(is);

                ca = cf.generateCertificate(caInput);
            } catch (CertificateException e) {
                System.out.println("\n\nTHERE WAS AN ERROR1");
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                System.out.println("\n\nTHERE WAS AN ERROR2");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    caInput.close();
                } catch (IOException e) {
                    System.out.println("\n\nTHERE WAS AN ERROR3");
                    e.printStackTrace();
                } catch (NullPointerException e){
                    System.out.println("\n\nTHERE WAS AN ERROR4");
                    e.printStackTrace();
                }
            }
            int counter = 0;

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = null;


            try {
                keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);
                Log.e("BRUHHHHh", "BRUH");

                //TODO: FIX URL
                URL url = new URL("https://mc15.cs.purdue.edu:5001/broadcasters");

                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("PUT");
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                String uname = logger.getString("email","");
                String pass = logger.getString("pw","");

//                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String r = logger.getString("pref_room", "LWSN B148-checked");
                String s = logger.getString("pref_status", "I need help on CS 240");
                r = r.substring(0, r.length()-8);

                //TODO: CHANGE OUT.WRITE
                out.write("{\"username\" : "+"\""+uname+"\",  "+"\"room\" : "+"\""+r+"\", "+"\"courses\" : "+"\""+s+"\"}");
                out.close();
                in = urlConnection.getInputStream();

                int t = in.available();
                Log.e("Login available: ",t+"");
                char d = (char)in.read();
                char c='a';
                found+=d;
//                System.out.println("FIRST CHAR: "+d);
                while(in.available()>0){
                    c = (char)in.read();
                    found+=c;
                    System.out.println("CHARS FROM READER: "+c);

                }
                Log.e("Login found=",found);
                if(c =='}'){
                    String temp = "";
                    in.close();
                    for(int i=0;i<found.length();i++){
                        char b = found.charAt(i);
                        temp +=b;
                        if(b=='\n'/* && counter<found_array1.length-1*/){
//                            Log.e("COUNTER=",counter+"");
                            found_array1[counter] = temp;
//                            Log.e("FOUND ARRAY AT: ",counter+": "+found_array1[counter]);
                            temp ="";
                            counter++;
                        }
                    }
                    in.close();
                    urlConnection.disconnect();
                }

            } catch (KeyStoreException e) {
                System.out.println("\n\nTHERE WAS AN ERROR5");
                e.printStackTrace();
            } catch (CertificateException e) {
                System.out.println("\n\nTHERE WAS AN ERROR6");
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                System.out.println("\n\nTHERE WAS AN ERROR7");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("\n\nTHERE WAS AN ERROR8 inside login");
                e.printStackTrace();
            } catch (KeyManagementException e) {
                System.out.println("\n\nTHERE WAS AN ERROR8 inside login 2");
                e.printStackTrace();
            }
            counter = 0;
            Log.e("found length=",found.length()+"");
            return found;
        }

        @Override
        protected void onPostExecute(String s){
            if(s.length() == 0){

            }else{

                //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class DeleteBroadcasters extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... params) {
            SharedPreferences logger = PreferenceManager.getDefaultSharedPreferences(context);
            Log.e("AsyncTask running","WTF");
            InputStream in;
            HttpURLConnection con;
            String found = "";
            String[] found_array1= new String[10];
            InputStream caInput = null;
            InputStream is = null;

            Certificate ca = null;
            AssetManager assManager = context.getAssets();
            try {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");

                is = assManager.open("mc15.cs.purdue.edu.cer");
                caInput = new BufferedInputStream(is);

                ca = cf.generateCertificate(caInput);
            } catch (CertificateException e) {
                System.out.println("\n\nTHERE WAS AN ERROR1");
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                System.out.println("\n\nTHERE WAS AN ERROR2");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    caInput.close();
                } catch (IOException e) {
                    System.out.println("\n\nTHERE WAS AN ERROR3");
                    e.printStackTrace();
                } catch (NullPointerException e){
                    System.out.println("\n\nTHERE WAS AN ERROR4");
                    e.printStackTrace();
                }
            }
            int counter = 0;

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = null;


            try {
                keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, tmf.getTrustManagers(), null);
                Log.e("BRUHHHHh", "BRUH");

                //TODO: FIX URL
                URL url = new URL("https://mc15.cs.purdue.edu:5001/broadcasters");

                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("DELETE");
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                String uname = logger.getString("email","");
                String pass = logger.getString("pw","");



                String r = logger.getString("pref_room", "LWSN B148-checked");
                String s = logger.getString("pref_status", "I need help on CS 240");
                r = r.substring(0, r.length()-8);

                //TODO: CHANGE OUT.WRITE
                out.write("{\"username\" : "+"\""+uname+"\"}");
                out.close();
                in = urlConnection.getInputStream();

                int t = in.available();
                Log.e("Login available: ",t+"");
                char d = (char)in.read();
                char c='a';
                found+=d;
//                System.out.println("FIRST CHAR: "+d);
                while(in.available()>0){
                    c = (char)in.read();
                    found+=c;
                    System.out.println("CHARS FROM READER: "+c);

                }
                Log.e("Login found=",found);
                if(c =='}'){
                    String temp = "";
                    in.close();
                    for(int i=0;i<found.length();i++){
                        char b = found.charAt(i);
                        temp +=b;
                        if(b=='\n'/* && counter<found_array1.length-1*/){
//                            Log.e("COUNTER=",counter+"");
                            found_array1[counter] = temp;
//                            Log.e("FOUND ARRAY AT: ",counter+": "+found_array1[counter]);
                            temp ="";
                            counter++;
                        }
                    }
                    in.close();
                    urlConnection.disconnect();
                }

            } catch (KeyStoreException e) {
                System.out.println("\n\nTHERE WAS AN ERROR5");
                e.printStackTrace();
            } catch (CertificateException e) {
                System.out.println("\n\nTHERE WAS AN ERROR6");
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                System.out.println("\n\nTHERE WAS AN ERROR7");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("\n\nTHERE WAS AN ERROR8 inside login");
                e.printStackTrace();
            } catch (KeyManagementException e) {
                System.out.println("\n\nTHERE WAS AN ERROR8 inside login 2");
                e.printStackTrace();
            }
            counter = 0;
            Log.e("found length=",found.length()+"");
            return found;
        }

        @Override
        protected void onPostExecute(String s){
            if(s.length() == 0){

            }else{
                if(doibroadcast == true){
                    new SetBroadcasters().execute();
                }

                //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void manual(){
        Log.e("MANUAL REFRESH", "BRUH!");
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(refreshCapacities);
    }

    public void hourly(){
        Log.e("HOURLY REFRESH", "BRUH!");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int hour = calendar.get(Calendar.HOUR_OF_DAY) + 1;
        int min = calendar.get(Calendar.MINUTE);
        Log.e("Refreshing in", hour+" "+min);
        calendar.set(Calendar.HOUR_OF_DAY, hour); /* refresh in 1 hour */
        calendar.set(Calendar.MINUTE, min);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HOUR, refreshCapacities);
    }
}
