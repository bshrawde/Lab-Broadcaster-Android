package cs490.labbroadcaster;

        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
        import android.os.Message;
        import android.preference.PreferenceManager;
        import android.util.Log;
        import android.widget.EditText;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.io.BufferedInputStream;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.Reader;
        import java.io.UnsupportedEncodingException;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.HashMap;

        import cs490.labbroadcaster.JsonParser;
        import cs490.labbroadcaster.MainActivity;

/**
 * Created by Brian on 2/26/2016.
 */


class server_request{
    int counter = 0;
    String found = "";
    String[] found_array = new String[10];
    public SharedPreferences sharedPref;
    public void LabData(final String fa[]) {
        //final String url = "http://www.google.com";
        final String url = "http://mc15.cs.purdue.edu:5000";

        new Thread() {
            public void run() {
                Log.e("Thread running","");
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
                    //System.out.println("avaliable: "+t);
                    char d = (char)in.read();
                    char c='a';
                    found+=d;
                    //System.out.println("FIRST CHAR: "+d);
                    while(in.available()>0){
                        c = (char)in.read();
                        found+=c;
                        //System.out.println("CHARS FROM REDER: "+c);

                    }
                    if(c =='}'){
                        int counter = 0;
                        String temp = "";
                        in.close();
                        con.disconnect();
                        for(int i=0;i<found.length();i++){
                            char b = found.charAt(i);
                            temp +=b;
                            if(b=='\n'){
                                found_array[counter] = temp;
                                fa[counter] = temp;
                                Log.e("FOUND ARRAY AT: ",counter+": "+found_array[counter]);
                                temp ="";
                                counter++;
                            }
                        }
                        counter = 0;
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

            }

        }.start();

    }

}