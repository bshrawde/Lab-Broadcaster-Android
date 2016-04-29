package cs490.labbroadcaster;

import android.content.res.AssetManager;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

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
     AlertDialog dialog =null;

    String found = "";
    String[] found_array= new String[15];
    private boolean populate = false;
    protected boolean loginvalid =false;
    protected boolean registervalid = false;
    protected boolean pullingprefs = false;
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
                    //todo get broadcasters here?
                    //new GetBroadcasters().execute();
                    final ArrayList<String> username = new ArrayList<>();
                    final ArrayList<String> status = new ArrayList<>();
                    if(cap.get(position).toString().charAt(0) != '?'){
                        if(cap.get(position).toString().charAt(1) != '/'){ //double digit number
                            t = cap.get(position).toString().substring(0,2);
                        }else{
                            t = cap.get(position).toString().substring(0,1);

                        }
                        int currentcap = Integer.parseInt(t);

                        for(int i = 0; i<currentcap; i++){
                            username.add("nmoorthy");
                            status.add("I need help with eating cookies");
                        }
                    }


                    rv.setVisibility(View.VISIBLE);

                    final LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
                    rv.setLayoutManager(layoutManager1);
                    ViewLabsRecyclerAdapter adapter2 = new ViewLabsRecyclerAdapter(getActivity(), username, status, new CustomItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
//                            Toast.makeText(getActivity(), "View Profile TODO", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ViewUserProfile.class);
                            intent.putExtra("user", username.get(position));
                            intent.putExtra("status", status.get(position));
                            startActivity(intent);


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
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                builder.setView(status);
                builder.setView(status, 25, 0, 25, 0);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("pref_status", status.getText().toString());
                        editor.commit();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(status.getWindowToken(), 0);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
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
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            builder.setCancelable(false);
            builder.setNeutralButton("Create Account",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    //create_account_area
                }

            });
            builder.setPositiveButton("Login",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    //login_area
                }
            });
            dialog = builder.create();
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
                        Toast.makeText(getActivity(), email+" is not a vaild purdue.edu address", Toast.LENGTH_SHORT).show();
                    }else if(password.equals("")){
                        Toast.makeText(getActivity(), "Password cannot be blank", Toast.LENGTH_SHORT).show();
                    }else{
                        SharedPreferences.Editor editor = sharedPref.edit();

                        int f = email.indexOf('@');
                        String u_name = email.substring(0,f);
                        editor.putString("email",u_name);
                        editor.putString("pw",password);
                        editor.commit();
                        new Register().execute();   //register a new user
                        registervalid = true;
                        if(registervalid==false) {
                            Toast.makeText(getActivity(),"Error creating user",Toast.LENGTH_SHORT).show();
                        }else{
                            //dialog.dismiss();
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(input2.getWindowToken(), 0);
//                            addClassData();
                            mSwipeRefreshLayout.setEnabled(true);
                            mSwipeRefreshLayout.setRefreshing(true);
                            //new RefreshRoomData().execute();
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

                    if(debug == 0){
                        if((!email.contains("@purdue.edu"))){
                            Toast.makeText(getActivity(), email+" is not a vaild purdue.edu address", Toast.LENGTH_SHORT).show();
                        }else if(password.equals("")){
                            Toast.makeText(getActivity(), "Invalid password", Toast.LENGTH_SHORT).show();
                        } else{

                            SharedPreferences.Editor editor = sharedPref.edit();
                            int f = email.indexOf('@');
                            String u_name = email.substring(0,f);
                            editor.putString("email",u_name);
                            editor.putString("pw",password);
                            editor.commit();
                            new LoginAuth().execute();
                            loginvalid = true;
                            if(loginvalid==false){
                                Toast.makeText(getActivity(),"Error username or password incorrect",Toast.LENGTH_SHORT).show();

                            }else {
                                //dialog.dismiss();
                                //new GetUserPrefs().execute();
                        /*TODO: Keyboard not hiding automatically for some reason WTF*/
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(input2.getWindowToken(), 0);
//                            addClassData();
                                mSwipeRefreshLayout.setEnabled(true);
                                mSwipeRefreshLayout.setRefreshing(true);

                            }
                        }
                    }else{
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("email", email);
                        editor.putString("pw", password);
                        editor.commit();
                        dialog.dismiss();
                        /*TODO: Keyboard not hiding automatically for some reason WTF*/
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(input2.getWindowToken(), 0);
//                        addClassData();
                        mSwipeRefreshLayout.setEnabled(true);
                        mSwipeRefreshLayout.setRefreshing(true);
                        new RefreshRoomData().execute();

                    }
                }
            });
            //endregion
        }else{
            System.out.println("Refreshing...");
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            });
//            mSwipeRefreshLayout.setRefreshing(true);
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
            Log.e("AsyncTask running","");
            InputStream in;
            HttpURLConnection con;
            found = "";
            String[] found_array1= new String[10];
            InputStream caInput = null;
            InputStream is = null;

            Certificate ca = null;
            AssetManager assManager = getActivity().getAssets();
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
                URL url = new URL("https://mc15.cs.purdue.edu:5001/status");
                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
//                URLConnection urlConnection = url.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                in = urlConnection.getInputStream();

                int t = in.available();
                Log.e("avaliable: ",t+"");
                char d = (char)in.read();
                char c='a';
                found+=d;
//                System.out.println("FIRST CHAR: "+d);
                while(in.available()>0){
                    c = (char)in.read();
                    found+=c;
//                    System.out.println("CHARS FROM READER: "+c);

                }
//                Log.e("found=",found);
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
                System.out.println("\n\nTHERE WAS AN ERROR8");
                e.printStackTrace();
            } catch (KeyManagementException e) {
                System.out.println("\n\nTHERE WAS AN ERROR8");
                e.printStackTrace();
            }
            counter = 0;
            Log.e("found length=",found.length()+"");
            return found_array1;
        }

        @Override
        protected void onPostExecute(String s[]){
            if(s.length == 0){
                Toast.makeText(getActivity(), "There was an error refreshing", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(false);
            }else{
//                Log.e("s=", Arrays.toString(s));
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
                    if(s[i] != null){
                        if(s[i].length() > 2){

//                            Log.e("s[i]=","'"+s[i]+"'s.length="+s[i].length());
                            String[] parts = s[i].split(" : ");

//                            Log.e("parts.length", parts.length+"");
                            parts[0] = parts[0].replaceAll("[^a-zA-Z0-9]","");
                            parts[0] = parts[0].substring(0,4)+ " " +parts[0].substring(4, parts[0].length());
                            parts[1] = parts[1].replaceAll("[^0-9]","");
                            if(parts[0].equals("LWSN B131")){
                                parts[1] = parts[1]+"/"+"24 Computers";
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
                    }else{
                        if(i+1==s.length-1 && data.size() == 0){
                            data.add("LWSN B131");
                            cap.add("?/"+"24 Computers");
                            data.add("LWSN B146");
                            cap.add("?/"+"24 Computers");
                            data.add("LWSN B148");
                            cap.add("?/"+"25 Computers");
                            data.add("LWSN B158");
                            cap.add("?/"+"24 Computers");
                            data.add("LWSN B160");
                            cap.add("?/"+"25 Computers");
                            data.add("HAAS G40");
                            cap.add("?/"+"24 Computers");
                            data.add("HAAS G56");
                            cap.add("?/"+"24 Computers");
                            data.add("HAAS 257");
                            cap.add("?/"+"21 Computers");



                            Toast.makeText(getActivity(), "Error retrieving room capacities", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(false);
                adapter.notifyDataSetChanged();
                new GetUserPrefs().execute();
            }

        }
    }

    public class Register extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... params) {
            SharedPreferences logger = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String uname = logger.getString("email","");
            String pass = logger.getString("pw","");
            Log.e("AsyncTask running","WTF");
            InputStream in;
            HttpURLConnection con;
            found = "";
            String[] found_array1= new String[10];
            InputStream caInput = null;
            InputStream is = null;

            Certificate ca = null;
            AssetManager assManager = getActivity().getAssets();
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
                URL url = new URL("https://mc15.cs.purdue.edu:5001/user/registration");

                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
                urlConnection.setRequestProperty("Accept","​/​");
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());

                out.write("{\"username\" : "+"\""+uname+"\",  "+"\"password\" : "+"\""+pass+"\"}");
                out.close();
                Log.e("before input stream:","BLAH");
                int status = urlConnection.getResponseCode();
                Log.e("response code",status+"");
                if(status>300){
                    return "";
                }
                    in = urlConnection.getInputStream();

                    Log.e("After input stream:","OREOS");

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
            if(s.length() == 0 || s.equals("")){
                Toast.makeText(getActivity(), "There was an error creating account", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(false);
                registervalid = false;
            }else{
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                registervalid = true;
                }
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(false);
                adapter.notifyDataSetChanged();
            }
    }
    public class LoginAuth extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... params) {
            SharedPreferences logger = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Log.e("AsyncTask running","WTF");
            InputStream in;
            HttpURLConnection con;
            found = "";
            String[] found_array1= new String[10];
            InputStream caInput = null;
            InputStream is = null;

            Certificate ca = null;
            AssetManager assManager = getActivity().getAssets();
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
                URL url = new URL("https://mc15.cs.purdue.edu:5001/user/login");

                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                String uname = logger.getString("email","");
                String pass = logger.getString("pw","");

                out.write("{\"username\" : "+"\""+uname+"\",  "+"\"password\" : "+"\""+pass+"\"}");
                out.close();
                int status = urlConnection.getResponseCode();
                Log.e("response code",status+"");
                if(status>300){
                    return "";
                }
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
            if(s.length() == 0||s.equals("")){
                Toast.makeText(getActivity(), "Invalid Login", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(false);
                loginvalid = false;
            }else{
                Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                loginvalid = true;
                SharedPreferences.Editor editor = sharedPref.edit();
                s=s.substring(13,s.length()-2);
                Log.e("session",s+"");
                editor.putString("sessionID", s);
                editor.commit();
                dialog.dismiss();

                new RefreshRoomData().execute();
            }
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(false);


        }
    }
    public class GetUserPrefs extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... params) {
//            pullingprefs = true;
            SharedPreferences logger = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Log.e("AsyncTask running","WTF");
            InputStream in;
            HttpURLConnection con;
            String found = "";
            String[] found_array1= new String[10];
            InputStream caInput = null;
            InputStream is = null;

            Certificate ca = null;
            AssetManager assManager = getActivity().getAssets();
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
                urlConnection.setRequestMethod("POST");

                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                String uname = logger.getString("email","");
                String pass = logger.getString("pw","");
                String session = logger.getString("sessionID","");
                Log.e("Sessino id:",session+"");


                //TODO: CHANGE OUT.WRITE
                out.write("{\"username\" : "+"\""+uname+"\",  "+"\"session\" : "+"\""+session+"\"}");
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
                Log.e("GET found=",found);
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
                Log.e("why AM I 0","hihj");
            }else{
                int c = 0;
                String[] dat = s.split("\n");
                Log.e("dat size", dat.length+"");
                //skip 0,1,5
                Log.e("dat[2]","'"+dat[2]+"'");
                Log.e("dat[3]","'"+dat[3]+"'");
                Log.e("dat[4]","'"+dat[4]+"'");
                dat[2] = dat[2].substring(15, dat[2].length()-2);

                dat[3] = dat[3].substring(15, dat[3].length()-2);

                dat[4] = dat[4].substring(17, dat[4].length()-1);
                Log.e("dat[2]:after","'"+dat[2]+"'");
                Log.e("dat[3]:after","'"+dat[3]+"'");
                Log.e("dat[4]:after","'"+dat[4]+"'");

                String[] courses = dat[2].split(",");
                for(int i = 0; i< courses.length; i++){
                    courses[i] = courses[i].trim();
                    //courses[i] = courses[i].replace(",", "");
                    Log.e(i+".", "'"+courses[i]+"'");
                }


                String[] coursestaken = dat[3].split(",");
                String[] languages = dat[4].split(",");

                for(int i = 0; i< coursestaken.length; i++){
                    coursestaken[i] = coursestaken[i].trim();
                    //coursestaken[i] = coursestaken[i].replace(",", "");
                    Log.e(i+".", "'"+coursestaken[i]+"'");
                }

                for(int i = 0; i< languages.length; i++){
                    languages[i] = languages[i].trim();
                    //languages[i] = languages[i].replace(",", "");
                    Log.e(i+".", "'"+languages[i]+"'");
                }



                //courses[0] = courses[0].substring(1,courses[0].length());
                //coursestaken[0] = coursestaken[0].substring(1,coursestaken[0].length());
                //languages[0] = languages[0].substring(1,languages[0].length());

                //SharedPreferences sharedPrefs = getSharedPreferences("pref_classes", 0);
//                SharedPreferences sharedPrefs1 = getSharedPreferences("curr_classes", 0);
//                SharedPreferences sharedPrefs2 = getSharedPreferences("pref_languages", 0);
                Set<String> classset = new HashSet<>(Arrays.asList(courses));
                Set<String> classset1 = new HashSet<>(Arrays.asList(coursestaken));
                Set<String> langs = new HashSet<>(Arrays.asList(languages));

           /*     for(int i=0;i<classset.size();i++){
                    Log.e("Data: ",classset.toString());
                }*/


                //SharedPreferences.Editor editor = sharedPrefs.edit();
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPrefs.edit();

                editor.putStringSet("pref_classes",classset);
                editor.putStringSet("curr_classes", classset1);
                editor.putStringSet("pref_languages", langs);
                //editor.putString("pref_classes","CS 307: Software Engineering-checked");
                editor.commit();


//                Log.e("dats", Arrays.toString(courses)+"\n"+Arrays.toString(coursestaken)+"\n"+Arrays.toString(languages));
                //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
//                pullingprefs = false;
            }
        }
    }
    public class GetBroadcasters extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... params) {
            SharedPreferences logger = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
                urlConnection.setRequestMethod("POST");
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                String uname = logger.getString("email","");
                String pass = logger.getString("pw","");


                //TODO: CHANGE OUT.WRITE
                out.write("{\"username\" : "+"\""+uname+"\",  "+"\"password\" : "+"\""+pass+"\"}");
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

                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
            /*if(pullingprefs == true){
                Toast.makeText(getActivity(), "Still refreshing data, try again in a moment", Toast.LENGTH_SHORT).show();
            }else{

            }*/
            Intent i = new Intent(getActivity(), UserPreferences.class);
            startActivity(i);

        }

        return true;
    }
}
