package cs490.labbroadcaster;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

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
import cs490.labbroadcaster.adapters.ViewLabsRecyclerAdapter;

public class MainFragment extends Fragment {
    public SharedPreferences sharedPref;
    public String email;
    public String password;
    Context context = getActivity();
    ArrayList<String> data = new ArrayList<>();

    ArrayList<String> cap = new ArrayList<>();
    DataWrap base = new DataWrap(getActivity());
    private RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private MainRecyclerAdapter adapter;
    public SharedPreferences preferences;

    String found = "";
    String[] found_array= new String[15];
    private boolean populate = false;
    int debug = 0; //change to 1 to enable normal function, 0 is to skip login dialog box regex checks

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_fragment, container, false);
        Toolbar toolbar =  (Toolbar) view.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        setHasOptionsMenu(true);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Typeface robotoMono = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto-mono-regular.ttf");

        mTitle.setTypeface(robotoMono);

        sharedPref  = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        sharedPref = getSharedPreferences("login", 0);
        email = sharedPref.getString("email","");
        password = sharedPref.getString("pw","");

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MainRecyclerAdapter(getActivity(), data,cap, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                View labView = getActivity().findViewById(R.id.viewlab);
                boolean isDual = labView != null && labView.getVisibility() == View.VISIBLE;
                if(isDual){
//                    ViewLabFragment f = new ViewLabFragment();
//                    f.changeText(data.get(position).toString(), cap.get(position).toString());
                    TextView title = (TextView) getFragmentManager().findFragmentById(R.id.viewlab).getView().findViewById(R.id.toolbar_title);
                    TextView currentusers = (TextView) getFragmentManager().findFragmentById(R.id.viewlab).getView().findViewById(R.id.currentusers);
                    ImageView groupimage = (ImageView) getFragmentManager().findFragmentById(R.id.viewlab).getView().findViewById(R.id.groupimage);
                    RecyclerView rv = (RecyclerView) getFragmentManager().findFragmentById(R.id.viewlab).getView().findViewById(R.id.recycler_view);

                    FloatingActionsMenu f = (FloatingActionsMenu) getFragmentManager().findFragmentById(R.id.viewlab).getView().findViewById(R.id.fab);


                    title.setText(data.get(position).toString());
                    f.setVisibility(View.VISIBLE);


                    if(cap.get(position).toString().charAt(0) == '0'){
                        groupimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_people_outline_white_24dp));
                        currentusers.setText(cap.get(position).toString().substring(0,cap.get(position).toString().indexOf('C'))+ "Current Users");
                    }else if(cap.get(position).toString().charAt(0) == '1' && cap.get(position).toString().charAt(1) == '/'){
                        groupimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_person_white_24dp));
                        currentusers.setText(cap.get(position).toString().substring(0,cap.get(position).toString().indexOf('C'))+ "Current User");
                    }else{
                        groupimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_group_white_24dp));
                        currentusers.setText(cap.get(position).toString().substring(0,cap.get(position).toString().indexOf('C'))+ "Current Users");
                    }
                    String t = "";
                    if(cap.get(position).toString().charAt(1) != '/'){ //double digit number
                        t = cap.get(position).toString().substring(0,2);
                    }else{
                        t = cap.get(position).toString().substring(0,1);
                    }
                    int currentcap = Integer.parseInt(t);
                    ArrayList<String> username = new ArrayList<>();
                    ArrayList<String> status = new ArrayList<>();
                    for(int i = 0; i<currentcap; i++){
                        username.add("nmoorthy");
                        status.add("I need help with eating cookies");
                    }

                    rv.setVisibility(View.VISIBLE);

                    final LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
                    rv.setLayoutManager(layoutManager1);
                    ViewLabsRecyclerAdapter adapter2 = new ViewLabsRecyclerAdapter(getActivity(), username, status, new CustomItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Toast.makeText(getActivity(), "View Profile TODO", Toast.LENGTH_SHORT).show();
                            View labView = getActivity().findViewById(R.id.viewlab);
                            boolean isDual = labView != null && labView.getVisibility() == View.VISIBLE;
                            if(isDual){

                            }else{

                            }
                        }
                    });
                    rv.setAdapter(adapter2);


                }else{
                    Intent intent = new Intent(getActivity(), ViewLabActivity.class);
                    intent.putExtra("labRoom", data.get(position).toString());
                    intent.putExtra("capacity",cap.get(position).toString());
                    startActivity(intent);
                }

            }
        });
        recyclerView.setAdapter(adapter);



        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.accent_color);
        mSwipeRefreshLayout.setEnabled(false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        FloatingActionButton fabstatus = (FloatingActionButton) view.findViewById(R.id.set_status);
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
                        new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyleDark);
                builder.setTitle("Current Status");
                final EditText status = new EditText(getActivity());
                status.setText(currentStatus);
                status.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(context.INPUT_METHOD_SERVICE);
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
                        //serverTest();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(status.getWindowToken(), 0);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(context.INPUT_METHOD_SERVICE);
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
                    new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyleDark);
            builder.setTitle("Login with Purdue Account");

            LayoutInflater inflater1 = getActivity().getLayoutInflater();

            View v = inflater1.inflate(R.layout.login_box, null);
            builder.setView(v);
            final EditText input = (EditText) v.findViewById(R.id.email);
            final EditText input2 = (EditText) v.findViewById(R.id.password);

            input.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(context.INPUT_METHOD_SERVICE);
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
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
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
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
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
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input2.getWindowToken(), 0);
                        addClassData();
                    }

                    //TODO make an area to check both username and password with CAS
                }
            });
            //endregion
        }else{
//            addClassData();
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setRefreshing(true);
            new RefreshRoomData().execute();
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void populateDB(){
        if(populate==false) {

            /* UNCOMMENT TO CAUSE CRASH */
//            base.insertLab("LWSN B160", "25", "X");
//            base.insertLab("LWSN B158", "24", "X");
//            base.insertLab("LWSN B148", "25", "X");
//            base.insertLab("LWSN B146", "24", "X");
//            base.insertLab("LWSN B131", "?", "X");
//            base.insertLab("HAAS G56", "24", "X");
//            base.insertLab("HAAS G40", "24", "X");
//            base.insertLab("HAAS 257", "21", "X");
        }else{
            //update
        }

    }

    public class RefreshRoomData extends AsyncTask<String[], Void, String[]> {
        @Override
        protected String[] doInBackground(String[]... params) {
            Log.e("Thread running","");
            InputStream in;
            HttpURLConnection con;

            found = "";
            Message msg = Message.obtain();
            msg.what = 1;
            String[] found_array1= new String[10];
            int counter = 0;
            try {

//                URL tt = new URL(url);
                con = (HttpURLConnection) new URL("http://mc15.cs.purdue.edu:5000").openConnection();

                int status = con.getResponseCode();
                System.out.println("URL RESPONSE CODE: "+status);

                in = new BufferedInputStream(con.getInputStream());
                //InputStream in = new BufferedReader(con.getInputStream());

                int t = in.available();
//                Log.e("avaliable: ",t+"");
                char d = (char)in.read();
                char c='a';
                found+=d;
                //System.out.println("FIRST CHAR: "+d);
                while(in.available()>0){
                    c = (char)in.read();
                    found+=c;
                    //System.out.println("CHARS FROM REDER: "+c);

                }
                Log.e("found=",found);
                if(c =='}'){
                    String temp = "";
                    in.close();
                    con.disconnect();
                    for(int i=0;i<found.length();i++){
                        char b = found.charAt(i);
                        temp +=b;
                        if(b=='\n'/* && counter<found_array1.length-1*/){
                            Log.e("COUNTER=",counter+"");
                            found_array1[counter] = temp;
                            Log.e("FOUND ARRAY AT: ",counter+": "+found_array1[counter]);
                            temp ="";
                            counter++;
                        }
                    }

                    in.close();
                    con.disconnect();
                }

                //in.close();
                //con.disconnect();
            }
            catch (IOException e1) {
                System.out.println("\n\nTHERE WAS AN ERROR");

                e1.printStackTrace();
            }
            counter = 0;
            Log.e("found length=",found.length()+"");
            return found_array1;
        }

        @Override
        protected void onPostExecute(String s[]){
            if(s.length == 0){
                Toast.makeText(context, "There was an error refreshing", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(false);
            }else{
                Log.e("s=", Arrays.toString(s));
                String temp = s[5];
                String temp2 = s[7];
                s[5] = s[6];
                s[7] = temp;
                s[6] = temp2;
                Log.e("s after swap=", Arrays.toString(s));
                data.clear();
                cap.clear();
//                adapter.notifyDataSetChanged();
                Log.e("s.length=",s.length+"");
                for(int i = 0; i<s.length-1; i++){
                    if(s[i].length() > 2){

                        Log.e("s[i]=","'"+s[i]+"'s.length="+s[i].length());
                        String[] parts = s[i].split(" : ");

                        Log.e("parts.length", parts.length+"");
                        parts[0] = parts[0].replaceAll("[^a-zA-Z0-9]","");
                        parts[0] = parts[0].substring(0,4)+ " " +parts[0].substring(4, parts[0].length());
                        parts[1] = parts[1].replaceAll("[^0-9]","");
                        if(parts[0].equals("LWSN B131")){
                            parts[1] = parts[1]+"/"+"24? Computers";
                        }else if(parts[0].equals("LWSN B146")){
                            parts[1] = parts[1]+"/"+"24 Computers";
                        }else if(parts[0].equals("LWSN B148")){
                            parts[1] = parts[1]+"/"+"25 Computers";
                        }else if(parts[0].equals("LWSN B158")){
                            parts[1] = parts[1]+"/"+"24 Computers";
                        }else if(parts[0].equals("HAAS G56")){
                            parts[1] = parts[1]+"/"+"24 Computers";
                        }else if(parts[0].equals("LWSN B160")){
                            parts[1] = parts[1]+"/"+"25 Computers";
                        }else if(parts[0].equals("HAAS G40")){
                            parts[1] = parts[1]+"/"+"24 Computers";
                        }else if(parts[0].equals("HAAS 257")){
                            parts[1] = parts[1]+"/"+"21 Computers";
                        }
                        data.add(parts[0]);
                        cap.add(parts[1]);
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(false);
                adapter.notifyDataSetChanged();
            }

        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public void addClassData(){
//        Toast.makeText(context, email, Toast.LENGTH_SHORT).show();
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
    @Override
        public void onResume(){
        super.onResume();
        FloatingActionButton fabstatus = (FloatingActionButton) getView().findViewById(R.id.set_status);
        if(preferences.getBoolean("pref_broadcast",false) == false){
            fabstatus.setVisibility(View.GONE);
        }else{
            fabstatus.setVisibility(View.VISIBLE);
        }
//        System.out.println("FINAL STIRNG FOUND: "+found);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

        /*
        KEYS
        classes_help      what classes you need help with
        pref_status       what your willing to help on
        pw                password
        email             email/username
        pref_languages    languages you know
        curr_classes      current classes
        pref_classes      older classes
        TA                classes TA for
        */
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_refresh){
//            server_request test = new server_request();
//            test.LabData(found_array1);
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setRefreshing(true);
            new RefreshRoomData().execute();

        }else if(id == R.id.action_logout){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();
//            Intent i = new Intent(getActivity(), MainActivity.class);
//            startActivity(i);
//            getActivity().finish();
            getActivity().recreate();
        }else if(id == R.id.action_user_preferences){
//            Toast.makeText(MainActivity.this,  "Todo profile settings page", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getActivity(), UserPreferences.class);
            startActivity(i);

        }

        return true;
    }
}
