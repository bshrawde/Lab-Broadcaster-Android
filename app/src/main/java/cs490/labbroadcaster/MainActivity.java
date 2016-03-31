package cs490.labbroadcaster;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.preference.PreferenceManager;
import android.renderscript.RenderScript;
import android.renderscript.ScriptGroup;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;

import cs490.labbroadcaster.adapters.MainRecyclerAdapter;

public class MainActivity extends AppCompatActivity {
    public SharedPreferences sharedPref;
    public String email;
    public String password;
    Context context = this;
    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> cap = new ArrayList<>();
    DataWrap base = new DataWrap(this);
    private RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;
    public SharedPreferences preferences;
    String found = "";
    private boolean populate = false;
    int debug = 0; //change to 1 to enable normal function, 0 is to skip login dialog box regex checks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//region set up main activty
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Typeface robotoMono = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-mono-regular.ttf");

        mTitle.setTypeface(robotoMono);

        sharedPref  = PreferenceManager.getDefaultSharedPreferences(context);
//        sharedPref = getSharedPreferences("login", 0);
        email = sharedPref.getString("email","");
        password = sharedPref.getString("pw","");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MainRecyclerAdapter(MainActivity.this, data,cap, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(MainActivity.this, ViewLabActivity.class);
                intent.putExtra("labRoom", data.get(position).toString());
                intent.putExtra("capacity",cap.get(position).toString());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        FloatingActionButton fabstatus = (FloatingActionButton) findViewById(R.id.set_status);
        if(preferences.getBoolean("pref_broadcast",false) == false){
            fabstatus.setVisibility(View.GONE);
        }
        //endregion

        //server_request test = new server_request();
        //test.execute("http://pod4-4.cs.purdue.edu:8000");
        populateDB();
        //TODO get users from server DB and lab status
//region FAB
        fabstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentStatus = preferences.getString("pref_status", "");

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyleDark);
                builder.setTitle("Current Status");
                final EditText status = new EditText(context);
                status.setText(currentStatus);
                status.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                builder.setView(status);
                builder.setView(status, 25, 0, 25, 0);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("pref_status", status.getText().toString());
                        editor.commit();
                        //todo dont leave this here
                        serverTest();
                        System.out.println("FINAL STIRNG FOUND: "+found);
                        InputMethodManager imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(status.getWindowToken(), 0);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(status.getWindowToken(), 0);
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        //endregion
        if( email.equals("") || password.equals("")){ //If user hasn't logged in before, show dialog box
//region build dialog box
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyleDark);
            builder.setTitle("Login with Purdue Account");

            LayoutInflater inflater = this.getLayoutInflater();

            View v = inflater.inflate(R.layout.login_box, null);
            builder.setView(v);
            final EditText input = (EditText) v.findViewById(R.id.email);
            final EditText input2 = (EditText) v.findViewById(R.id.password);

            input.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            builder.setCancelable(false);
            builder.setNeutralButton("Create Account",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){

                }

            });
            builder.setPositiveButton("Login",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){

                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            //endregion

//region new use
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener(){
                @Override
                public  void onClick(View v){
                    Boolean close_login = false;
                    email = input.getText().toString();
                    password = input2.getText().toString();
                    if((!email.contains(("@purdue.edu")))){
                        Toast.makeText(context, email+" is not a vaild purdue.edu address", Toast.LENGTH_SHORT).show();
                    }else if(password.equals("")){
                        Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT).show();
                    }else{
                        //authentication check
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("email",email);
                        editor.putString("pw",password);
                        editor.commit();
                        int f = email.indexOf('@');
                        String u_name = email.substring(0,f);
                        boolean result = base.insertUser(u_name,password);
                        if(result==false) {
                            Toast.makeText(context,"User already exists",Toast.LENGTH_SHORT).show();
                        }else{
                            dialog.dismiss();
                            InputMethodManager imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(input2.getWindowToken(), 0);
                            addClassData();
                        }
                    }
                }
            });
            //endregion

//region current user
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Boolean close_login = false;
                    email = input.getText().toString();
                    password = input2.getText().toString();

                    if(debug == 1){
                        if((!email.contains("@purdue.edu"))){
                            Toast.makeText(context, email+" is not a vaild purdue.edu address", Toast.LENGTH_SHORT).show();
                        }else if(password.equals("")){
                            Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT).show();
                        } else{
                            CAS_check authent = new CAS_check(email,password);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("email", email);
                            editor.putString("pw", password);
                            editor.commit();
                            dialog.dismiss();
                        /*TODO: Keyboard not hiding automatically for some reason WTF*/
                            InputMethodManager imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(input2.getWindowToken(), 0);
                            addClassData();
                        }
                    }else{
                        CAS_check authent = new CAS_check(email,password);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("email", email);
                        editor.putString("pw", password);
                        editor.commit();
                        dialog.dismiss();
                        /*TODO: Keyboard not hiding automatically for some reason WTF*/
                        InputMethodManager imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input2.getWindowToken(), 0);
                        addClassData();
                    }

                    //TODO make an area to check both username and password with CAS
                }
            });
            //endregion
        }else{
            addClassData();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings){

        }else if(id == R.id.action_logout){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }else if(id == R.id.action_user_preferences){
//            Toast.makeText(MainActivity.this,  "Todo profile settings page", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(MainActivity.this, UserPreferences.class);
            startActivity(i);

        }

        return true;
    }
    public void populateDB(){
        if(populate==false) {
            base.insertLab("LWSN B160", "25", "X");
            base.insertLab("LWSN B158", "24", "X");
            base.insertLab("LWSN B148", "25", "X");
            base.insertLab("LWSN B146", "24", "X");
            base.insertLab("LWSN B131", "?", "X");
            base.insertLab("HAAS G56", "24", "X");
            base.insertLab("HAAS G40", "24", "X");
            base.insertLab("HAAS 257", "21", "X");
        }else{
            //update
        }

    }
    public void addClassData(){
        Toast.makeText(context, email, Toast.LENGTH_SHORT).show();
        data.add(new String("LWSN B160"));
            cap.add(new String("X/25 Computers"));
        data.add(new String("LWSN B158"));
            cap.add(new String("X/24 Computers"));
        data.add(new String("LWSN B148"));
            cap.add(new String("X/25 Computers"));
        data.add(new String("LWSN B146"));
            cap.add(new String("X/24 Computers"));
        data.add(new String("LWSN B131"));
            cap.add(new String("X/?? Computers"));
        data.add(new String("HAAS G56"));
            cap.add(new String("X/24 Computers"));
        data.add(new String("HAAS G40"));
            cap.add(new String("X/24 Computers"));
        data.add(new String("HAAS 257"));
            cap.add(new String("X/21 Computers"));
        adapter.notifyDataSetChanged();
    }
    private void serverTest() {
        //final String url = "http://www.google.com";
        final String url = "http://mc15.cs.purdue.edu:5000";

        new Thread() {
            public void run() {
                InputStream in= null;
                HttpURLConnection con = null;


                Message msg = Message.obtain();
                msg.what = 1;

                try {

                    URL tt = new URL(url);
                    con = (HttpURLConnection) new URL("http://mc15.cs.purdue.edu:5000").openConnection();

                    int status = con.getResponseCode();
                        System.out.println("URL RESPONSE CODE: "+status);

                    in = new BufferedInputStream(con.getInputStream());
                    //InputStream in = new BufferedReader(con.getInputStream());

                    int t = in.available();
                        System.out.println("avaliable: "+t);
                        char d = (char)in.read();
                    char c='a';
                    found+=d;
                    System.out.println("FIRST CHAR: "+d);
                    while(in.available()>0){
                        c = (char)in.read();
                        found+=c;
                        System.out.println("CHARS FROM REDER: "+c);

                    }
                    if(c =='}'){
                        in.close();
                        con.disconnect();
                        System.out.println("FINAL SGTRING: "+found);
                    }

                    //in.close();
                    //con.disconnect();
                }
                catch (IOException e1) {
                    System.out.println("\n\nTHERE WAS AN ERROR");
                    e1.printStackTrace();
                }

            }

        }.start();

    }
    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||

                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            Toast.makeText(this, " Not Connected ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
    private InputStream openHttpConnection(String urlStr) {
        InputStream in = null;
        int resCode = -1;

        try {
            URL url = new URL(urlStr);
            URLConnection urlConn = url.openConnection();

            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            HttpURLConnection httpConn = (HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }

        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }
    protected void onResume(){
        super.onResume();
        FloatingActionButton fabstatus = (FloatingActionButton) findViewById(R.id.set_status);
        if(preferences.getBoolean("pref_broadcast",false) == false){
            fabstatus.setVisibility(View.GONE);
        }else{
            fabstatus.setVisibility(View.VISIBLE);
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Map<String,?> all = preferences.getAll();

        for(Map.Entry<String,?> entry : all.entrySet()){
            Log.d("values:",entry.getKey() + ": "+entry.getValue().toString());
        }

        //new server_request().execute("username","password");
        //test.tester();

        Set<String> curr_classes = preferences.getStringSet("curr_classes",null); //current classes
        Set<String> classes_taken = preferences.getStringSet("pref_classes",null);//taken
        Set<String> TA = preferences.getStringSet("TA",null);                       //classes TA for
        Set<String> Lang = preferences.getStringSet("pref_languages",null);     //languages
        Set<String> class_help = preferences.getStringSet("classes_help",null); //classes need help on
        String helper = preferences.getString("pref_status",null);              // what you can help with
        Boolean broadcast = preferences.getBoolean("pref_broadcast",false);

        //KEYS
        //classes_help      what classes you need help with
        //pref_status       what your willing to help on
        //pw                password
        //email             email/username
        //pref_languages    languages you know
        //curr_classes      current classes
        //pref_classes      older classes
        //TA                classes TA for
    }

}