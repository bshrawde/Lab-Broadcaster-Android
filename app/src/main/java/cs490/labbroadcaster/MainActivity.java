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
import android.os.AsyncTask;
import android.os.Message;
import android.preference.PreferenceManager;
import android.renderscript.RenderScript;
import android.renderscript.ScriptGroup;
import android.support.v4.net.ConnectivityManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Arrays;
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
    SwipeRefreshLayout mSwipeRefreshLayout;
    private MainRecyclerAdapter adapter;
    public SharedPreferences preferences;
    String found = "";
    String[] found_array= new String[15];
    private boolean populate = false;
    int debug = 0; //change to 1 to enable normal function, 0 is to skip login dialog box regex checks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//region set up main activty
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if(id == R.id.action_refresh){
////            server_request test = new server_request();
////            test.LabData(found_array1);
//            mSwipeRefreshLayout.setEnabled(true);
//            mSwipeRefreshLayout.setRefreshing(true);
//            new RefreshRoomData().execute();
//
//        }else if(id == R.id.action_logout){
//            SharedPreferences.Editor editor = sharedPref.edit();
//            editor.clear();
//            editor.commit();
//            Intent i = new Intent(MainActivity.this, MainActivity.class);
//            startActivity(i);
//            finish();
//        }else if(id == R.id.action_user_preferences){
////            Toast.makeText(MainActivity.this,  "Todo profile settings page", Toast.LENGTH_SHORT).show();
//            Intent i = new Intent(MainActivity.this, UserPreferences.class);
//            startActivity(i);
//
//        }
//
//        return true;
//    }


//    protected void onResume(){
//        super.onResume();
//        FloatingActionButton fabstatus = (FloatingActionButton) findViewById(R.id.set_status);
//        if(preferences.getBoolean("pref_broadcast",false) == false){
//            fabstatus.setVisibility(View.GONE);
//        }else{
//            fabstatus.setVisibility(View.VISIBLE);
//        }
////        System.out.println("FINAL STIRNG FOUND: "+found);
//        preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        Map<String,?> all = preferences.getAll();
//
//        for(Map.Entry<String,?> entry : all.entrySet()){
//            Log.d("values:",entry.getKey() + ": "+entry.getValue().toString());
//        }
//
//        //new server_request().execute("username","password");
//        //test.tester();
//
//        Set<String> curr_classes = preferences.getStringSet("curr_classes",null); //current classes
//        Set<String> classes_taken = preferences.getStringSet("pref_classes",null);//taken
//        Set<String> TA = preferences.getStringSet("TA",null);                       //classes TA for
//        Set<String> Lang = preferences.getStringSet("pref_languages",null);     //languages
//        Set<String> class_help = preferences.getStringSet("classes_help",null); //classes need help on
//        String helper = preferences.getString("pref_status",null);              // what you can help with
//        Boolean broadcast = preferences.getBoolean("pref_broadcast",false);
//
//        //KEYS
//        //classes_help      what classes you need help with
//        //pref_status       what your willing to help on
//        //pw                password
//        //email             email/username
//        //pref_languages    languages you know
//        //curr_classes      current classes
//        //pref_classes      older classes
//        //TA                classes TA for
//    }



}