package cs490.labbroadcaster;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.Inflater;

import cs490.labbroadcaster.adapters.MainRecyclerAdapter;

public class MainActivity extends AppCompatActivity {
    public SharedPreferences sharedPref;
    public String email;
    public String password;
    Context context = this;
    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> cap = new ArrayList<>();
    private RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Typeface robotoMono = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-mono-regular.ttf");

        mTitle.setTypeface(robotoMono);

        sharedPref = getSharedPreferences("login", 0);
        email = sharedPref.getString("email","");
        password = sharedPref.getString("pw","");
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MainRecyclerAdapter(MainActivity.this, data,cap, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                Toast.makeText(MainActivity.this,  "Clicked on "+ data.get(position).toString(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, ViewLabActivity.class);
                intent.putExtra("labRoom", data.get(position).toString());
                intent.putExtra("capacity",cap.get(position).toString());

                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        if( email.equals("") || password.equals("")){ //If user hasn't logged in before, show dialog box
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyleDark);
            builder.setTitle("Login with Purdue Account");

            LayoutInflater inflater = this.getLayoutInflater();

            View v = inflater.inflate(R.layout.login_box, null);
            builder.setView(v);
            final EditText input = (EditText) v.findViewById(R.id.email);
            final EditText input2 = (EditText) v.findViewById(R.id.password);

            InputMethodManager imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            builder.setCancelable(false);
            builder.setPositiveButton("Login",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){

                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            //override handler
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Boolean close_login = false;
                    email = input.getText().toString();
                    password = input2.getText().toString();

                    if((!email.contains("@purdue.edu"))||password.equals("")){
                        //make an alert for invalid email
                    }else{

                        dialog.dismiss();
                        addClassData();
                    }
                    //TODO make an area to check both username and password with CAS
                }
            });
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
            sharedPref = getSharedPreferences("login", 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("email", "");
            editor.putString("pw", "");
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

    public void addClassData(){
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

}