package cs490.labbroadcaster;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

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
            //region new user
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

    protected void onResume(){
        super.onResume();

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        FloatingActionButton fabstatus = (FloatingActionButton) findViewById(R.id.set_status);
        if(preferences.getBoolean("pref_broadcast",false) == false){
            fabstatus.setVisibility(View.GONE);
        }else{
            fabstatus.setVisibility(View.VISIBLE);
        }
    }

}