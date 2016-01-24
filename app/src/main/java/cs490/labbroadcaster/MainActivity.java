package cs490.labbroadcaster;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.Inflater;

import cs490.labbroadcaster.adapters.MainRecyclerAdapter;

public class MainActivity extends ActionBarActivity {
    public SharedPreferences sharedPref;
    public String email;
    public String password;
    Context context = this;
    ArrayList<String> data = new ArrayList<>();
    private RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getSharedPreferences("login", 0);
        email = sharedPref.getString("email","");
        password = sharedPref.getString("pw","");
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MainRecyclerAdapter(MainActivity.this, data, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(MainActivity.this,  "Clicked on "+ data.get(position).toString(), Toast.LENGTH_SHORT).show();

                //TODO: START ANOTHER ACTIVITY FOR DISPLAYING SELECTED LAB'S INFO
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

            builder.setPositiveButton("Login", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    email = input.getText().toString();
                    password = input2.getText().toString();

                    //TODO: ADD CHECK FOR VALID LOGIN

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("email", email);
                    editor.putString("pw", password);
                    editor.commit(); //saves username and password

                    InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input2.getWindowToken(), 0);
                    dialog.cancel();
                    addClassData();
                }
            });
            builder.show();
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
        }

        return true;
    }

    public void addClassData(){
        data.add(new String("LWSN B158"));
        data.add(new String("LWSN B148"));
        data.add(new String("LWSN B146"));
        data.add(new String("HAAS 257"));

        adapter.notifyDataSetChanged();
    }

}
