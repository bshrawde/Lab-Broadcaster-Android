package cs490.labbroadcaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

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
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import cs490.labbroadcaster.adapters.MainRecyclerAdapter;
import cs490.labbroadcaster.adapters.ViewLabsRecyclerAdapter;

public class ViewLabFragment extends Fragment {

    Context context = getActivity();
    private String room;
    TextView mTitle;
    TextView mCapacity;
    ImageView groupimage;
    private String capacity;
    RecyclerView recyclerView;
    ArrayList<String> bun = new ArrayList<>();
    ArrayList<String> bstatus = new ArrayList<>();
    private ViewLabsRecyclerAdapter adapter;

    private static final String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_view_lab_fragment, container, false);
        Toolbar toolbar =  (Toolbar) view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);


        Bundle args = getArguments();
        if(args != null){
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        room = (args != null) ? args.getString("labRoom") : "";
        capacity = (args != null) ? args.getString("capacity") : "";

//        if(getActivity().getIntent().hasExtra("labRoom")){
//            room = getActivity().getIntent().getStringExtra("labRoom");
//        }
//        if(getActivity().getIntent().hasExtra("capacity")){
//            capacity = getActivity().getIntent().getStringExtra("capacity");
//        }

//        Log.e("Room",room);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(room);
        Typeface robotoMono = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto-mono-regular.ttf");
        mTitle.setTypeface(robotoMono);
        mCapacity = (TextView) view.findViewById(R.id.currentusers);
        groupimage = (ImageView) view.findViewById(R.id.groupimage);
        String t = "";
        if(capacity!= ""){
            if(capacity.charAt(0) == '0'){
                groupimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_people_outline_white_24dp));
                mCapacity.setText(capacity.substring(0,capacity.indexOf('C'))+ "Current Users");
            }else if(capacity.charAt(0) == '1' && capacity.charAt(1) == '/'){
                groupimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_person_white_24dp));
                mCapacity.setText(capacity.substring(0,capacity.indexOf('C'))+ "Current User");
            }else{
                groupimage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_group_white_24dp));
                mCapacity.setText(capacity.substring(0,capacity.indexOf('C'))+ "Current Users");
            }
            if(capacity.charAt(0)!='?'){
                if(capacity.charAt(1) != '/'){ //double digit number
                    t = capacity.substring(0,2);
                }else{
                    t = capacity.substring(0,1);
                }
                int currentcap = Integer.parseInt(t);
            }
        }




        mCapacity.setTypeface(robotoMono);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ViewLabsRecyclerAdapter(getActivity(), bun,bstatus, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                //Toast.makeText(getActivity(), "View Profile TODO", Toast.LENGTH_SHORT).show();
                View labView = getActivity().findViewById(R.id.viewlab);
                /*Intent intent = new Intent(getActivity(), ViewUserProfile.class);
                intent.putExtra("user", username.get(position));
                intent.putExtra("status", status.get(position));
                startActivity(intent);*/
                boolean isDual = labView != null && labView.getVisibility() == View.VISIBLE;
                if(isDual){

                }else{

                }
            }
        });
        recyclerView.setAdapter(adapter);
        new GetBroadcasters().execute();

        FloatingActionButton fabcalendar = (FloatingActionButton) view.findViewById(R.id.fab_calendar);
        FloatingActionButton fabwebcam = (FloatingActionButton) view.findViewById(R.id.fab_webcam);
        FloatingActionButton fabusage = (FloatingActionButton) view.findViewById(R.id.fab_usage);


        FloatingActionsMenu fab = (FloatingActionsMenu) view.findViewById(R.id.fab);

        if(mTitle.getText().equals("")){
            fab.setVisibility(View.GONE);

        }else{
            fab.setVisibility(View.VISIBLE);
        }
        fabwebcam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkText = "";
                /*if("LWSN B146".equals(mTitle.getText())){
                    linkText = "http://lwsnb146-cam.cs.purdue.edu/view/index.shtml";
                }else if("LWSN B148".equals(mTitle.getText())){
                    linkText = "http://lwsnb148-cam.cs.purdue.edu/view/index.shtml";
                }else if("LWSN B158".equals(mTitle.getText())){
                    linkText = "http://lwsnb158-cam.cs.purdue.edu/view/index.shtml";
                }else if("HAAS 257".equals(mTitle.getText())){
                    linkText = "http://haas257-cam.cs.purdue.edu/view/index.shtml";
                }else if("LWSN B160".equals((mTitle.getText()))){
                    linkText = "http://lwsnb160-cam.cs.purdue.edu/view/index.shtml";
                }else if("LWSN B131".equals((mTitle.getText()))){
                    linkText = "http://lwsnb131-cam.cs.purdue.edu/view/index.shtml";
                }else if("HAAS G40".equals((mTitle.getText()))){
                    linkText = "http://haasg040-cam.cs.purdue.edu/view/index.shtml";
                } else if ("HAAS G56".equals((mTitle.getText()))) {

                    linkText = "http://haasg056-cam.cs.purdue.edu/view/index.shtml";
                }*/
                if("LWSN B146".equals(mTitle.getText())){
                    linkText = "http://lwsnb146-cam.cs.purdue.edu/mjpg/video.mjpg";
                }else if("LWSN B148".equals(mTitle.getText())){
                    linkText = "http://lwsnb148-cam.cs.purdue.edu/mjpg/video.mjpg";
                }else if("LWSN B158".equals(mTitle.getText())){
                    linkText = "http://lwsnb158-cam.cs.purdue.edu/mjpg/video.mjpg";
                }else if("HAAS 257".equals(mTitle.getText())){
                    linkText = "http://haas257-cam.cs.purdue.edu/mjpg/video.mjpg";
                }else if("LWSN B160".equals((mTitle.getText()))){
                    linkText = "http://lwsnb160-cam.cs.purdue.edu/mjpg/video.mjpg";
                }else if("LWSN B131".equals((mTitle.getText()))){
                    linkText = "http://lwsnb131-cam.cs.purdue.edu/mjpg/video.mjpg";
                }else if("HAAS G40".equals((mTitle.getText()))){
                    linkText = "http://haasg040-cam.cs.purdue.edu/mjpg/video.mjpg";
                } else if ("HAAS G56".equals((mTitle.getText()))) {

                    linkText = "http://haasg056-cam.cs.purdue.edu/mjpg/video.mjpg";
                }
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(getActivity(), Uri.parse(linkText));
            }
        });

        fabcalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkText = "";
                if("LWSN B146".equals(mTitle.getText())){
                    linkText = "https://calendar.google.com/calendar/embed?showTitle=0&showCalendars=0&mode=WEEK&width=600&height=600&wkst=1&bgcolor=%23FFFFFF&src=1r5tgrb2sln4oe4h4f4gfeece8@group.calendar.google.com&color=%23528800&ctz=America/New_York";
                }else if("LWSN B148".equals(mTitle.getText())){
                    linkText = "https://calendar.google.com/calendar/embed?showTitle=0&showCalendars=0&mode=WEEK&width=600&height=600&wkst=1&bgcolor=%23FFFFFF&src=hd9ldosmo9upm7u5scvq8uumig@group.calendar.google.com&color=%23528800&ctz=America/New_York";
                }else if("LWSN B158".equals(mTitle.getText())){
                    linkText = "https://calendar.google.com/calendar/embed?showTitle=0&showCalendars=0&mode=WEEK&width=600&height=600&wkst=1&bgcolor=%23FFFFFF&src=25m91hh3dpdlcguv5h30i749pg@group.calendar.google.com&color=%23528800&ctz=America/New_York";
                }else if("HAAS 257".equals(mTitle.getText())){
                    linkText = "https://calendar.google.com/calendar/embed?showTitle=0&showCalendars=0&mode=WEEK&width=600&height=600&wkst=1&bgcolor=%23FFFFFF&src=tg56f4t31msvg4o56iuf37luqk@group.calendar.google.com&color=%23528800&ctz=America/New_York";
                }else if("LWSN B160".equals((mTitle.getText()))){
                    linkText = "https://calendar.google.com/calendar/embed?showTitle=0&showCalendars=0&mode=WEEK&width=600&height=600&wkst=1&bgcolor=%23FFFFFF&src=bvh903t70gtpvgtl6m8vhl2e1s@group.calendar.google.com&color=%23528800&ctz=America/New_York";
                }else if("LWSN B131".equals((mTitle.getText()))){
                    linkText = "https://calendar.google.com/calendar/embed?showTitle=0&showCalendars=0&mode=WEEK&width=600&height=600&wkst=1&bgcolor=%23FFFFFF&src=isp5jp397bj8et0i5r8g6kd3uo@group.calendar.google.com&color=%23528800&ctz=America/New_York";
                }else if("HAAS G40".equals((mTitle.getText()))){
                    linkText = "https://calendar.google.com/calendar/embed?showTitle=0&showCalendars=0&mode=WEEK&width=600&height=600&wkst=1&bgcolor=%23FFFFFF&src=jv11mjte5oupheck2kmv36mn2o@group.calendar.google.com&color=%23528800&ctz=America/New_York";
                } else if ("HAAS G56".equals((mTitle.getText()))) {
                    linkText = "https://calendar.google.com/calendar/embed?showTitle=0&showCalendars=0&mode=WEEK&width=600&height=600&wkst=1&bgcolor=%23FFFFFF&src=2c8utbns46atet3h1stfpaud88@group.calendar.google.com&color=%23528800&ctz=America/New_York";
                }
/*                if("LWSN B146".equals(mTitle.getText())){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb146.html";
                }else if("LWSN B148".equals(mTitle.getText())){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb148.html";
                }else if("LWSN B158".equals(mTitle.getText())){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb158.html";
                }else if("HAAS 257".equals(mTitle.getText())){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/haas257.html";
                }else if("LWSN B160".equals((mTitle.getText()))){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb160.html";
                }else if("LWSN B131".equals((mTitle.getText()))){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/lwsnb131.html";
                }else if("HAAS G40".equals((mTitle.getText()))){
                    linkText = "https://www.cs.purdue.edu/resources/facilities/haasg40.html";
                } else if ("HAAS G56".equals((mTitle.getText()))) {
                    linkText = "https://www.cs.purdue.edu/resources/facilities/haasg56.html";
                }*/
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(getActivity(), Uri.parse(linkText));
            }
        });

        fabusage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkText = "";
                if("LWSN B146".equals(mTitle.getText())){
                    linkText = "";
                }else if("LWSN B148".equals(mTitle.getText())){
                    linkText = "";
                }else if("LWSN B158".equals(mTitle.getText())){
                    linkText = "";
                }else if("HAAS 257".equals(mTitle.getText())){
                    linkText = "";
                }else if("LWSN B160".equals((mTitle.getText()))){
                    linkText = "";
                }else if("LWSN B131".equals((mTitle.getText()))){
                    linkText = "";
                }else if("HAAS G40".equals((mTitle.getText()))){
                    linkText = "";
                } else if ("HAAS G56".equals((mTitle.getText()))) {
                    linkText = "";
                }

               /* CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(getActivity(), Uri.parse(linkText));*/
            }
        });

        return view;
    }


    public class GetBroadcasters extends AsyncTask<String[], Void, String> {
        @Override
        protected String doInBackground(String[]... params) {
//            SharedPreferences logger = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
                URL url = new URL("https://mc15.cs.purdue.edu:5001/broadcasters");

                HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
                urlConnection.setSSLSocketFactory(context.getSocketFactory());
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                /*String uname = logger.getString("email","");
                String pass = logger.getString("pw","");*/

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                String r = sharedPrefs.getString("selectedRoom", "LWSN B148");
//                Log.e("R",r);
                String s = sharedPrefs.getString("pref_status", "I need help on CS 240");
//                r = r.substring(0, r.length()-8);

                //TODO: CHANGE OUT.WRITE
                out.write("{\"room\" : "+"\""+room+"\"}");
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
                Log.e("GetBroadcaster ",found);
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

                s = s.substring(1, s.length()-2);
                int c = 0;
                String[] dat = s.split("[}]");
                Log.e("dat size", dat.length+"");
                bstatus.clear();
                bun.clear();
                for(int i  = 0; i< dat.length; i++){
                    dat[i] = dat[i].replace("{", "");
                    dat[i] = dat[i].replace("}", "");
//                    Log.e("dat ", "'"+dat[i]+"'");
                    String [] temp = dat[i].split("\n");

                    Log.e("temp", temp.length+"\ntemp[0]"+temp[0]);
                    if(temp.length < 4){
                        continue;
                    }
                    String unam = temp[1].substring(16, temp[1].length()-2);
                    Log.e("uname", unam);
                    bun.add(unam);
                    String stat = temp[3].substring(15, temp[3].length()-2);
                    Log.e("status", stat);
                    bstatus.add(stat);
                }
                //skip 0,1,5
                adapter.notifyDataSetChanged();
                //Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
