package cs490.labbroadcaster;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONObject;

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


class server_request extends AsyncTask<String, String, JSONObject> {

    JsonParser jsonParser = new JsonParser();

    private ProgressDialog pDialog;

    private static final String LOGIN_URL = "http://www.example.com/testGet.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onPreExecute() {
        //pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Attempting login...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... args) {

        try {

            HashMap<String, String> params = new HashMap<>();
            params.put("name", args[0]);
            params.put("password", args[1]);

            Log.d("request", "starting");

            JSONObject json = jsonParser.makeHttpRequest(
                    LOGIN_URL, "GET", params);

            if (json != null) {
                Log.d("JSON result", json.toString());

                return json;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void onPostExecute(JSONObject json) {

        int success = 0;
        String message = "";

        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }

        if (json != null) {
            String res = json.toString();
            System.out.println("REUSLT: "+res);
            try {
                success = json.getInt(TAG_SUCCESS);
                message = json.getString(TAG_MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (success == 1) {
            Log.d("Success!", message);
        }else{
            Log.d("Failure", message);
        }
    }

}