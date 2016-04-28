package cs490.labbroadcaster;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Nishant on 4/28/2016.
 */
public class UpdateCapacitiesService extends Service {
    Context context = this;
    DataWrap db;

    ArrayList<String> data = new ArrayList<>();

    ArrayList<String> cap = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.e("STARTING SERVICE", "GOGOGO");
        new RefreshCapacities().execute();
        return Service.START_FLAG_REDELIVERY;

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private class RefreshCapacities extends AsyncTask<String[], Void, String[]> {

        @Override
        protected String[] doInBackground(String[]... params) {
            Log.e("AsyncTask running","");
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
        protected void onPostExecute(String s[]) {
            super.onPostExecute(s);
            Toast.makeText(context, "BRUH", Toast.LENGTH_SHORT).show();
            if(s.length == 0){

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
                float percentage;
                String under50 = "";
                int count = 0;
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
                                percentage = Float.parseFloat(parts[1])/24;
                                if(percentage<.5) {
                                    count++;
                                    if(under50.equals("")){
                                        under50  = "LWSN B131";
                                    }else{
                                        under50+=", LWSN B131";
                                    }
                                }
                            }else if(parts[0].equals("LWSN B146")){
                                parts[1] = parts[1]+"/"+"24 Computers";
                                percentage = Float.parseFloat(parts[1])/24;
                                if(percentage<.5) {
                                    count++;
                                    if(under50.equals("")){
                                        under50  = "LWSN B146";
                                    }else{
                                        under50+=", LWSN B146";
                                    }
                                }
                            }else if(parts[0].equals("LWSN B148")){
                                parts[1] = parts[1]+"/"+"25 Computers";
                                percentage = Float.parseFloat(parts[1])/25;
                                if(percentage<.5) {
                                    count++;
                                    if(under50.equals("")){
                                        under50  = "LWSN B148";
                                    }else{
                                        under50+=", LWSN B148";
                                    }
                                }
                            }else if(parts[0].equals("LWSN B158")){
                                parts[1] = parts[1]+"/"+"24 Computers";
                                percentage = Float.parseFloat(parts[1])/24;
                                if(percentage<.5) {
                                    count++;
                                    if(under50.equals("")){
                                        under50  = "LWSN B158";
                                    }else{
                                        under50+=", LWSN B158";
                                    }
                                }
                            }else if(parts[0].equals("HAAS G56")){
                                parts[1] = parts[1]+"/"+"24 Computers";
                                percentage = Float.parseFloat(parts[1])/24;
                                if(percentage<.5) {
                                    count++;
                                    if(under50.equals("")){
                                        under50  = "HAAS G56";
                                    }else{
                                        under50+=", HAAS G56";
                                    }
                                }
                            }else if(parts[0].equals("LWSN B160")){
                                parts[1] = parts[1]+"/"+"25 Computers";
                                percentage = Float.parseFloat(parts[1])/25;
                                if(percentage<.5) {
                                    count++;
                                    if(under50.equals("")){
                                        under50  = "LWSN B160";
                                    }else{
                                        under50+=", LWSN B160";
                                    }
                                }
                            }else if(parts[0].equals("HAAS G40")){
                                parts[1] = parts[1]+"/"+"24 Computers";
                                percentage = Float.parseFloat(parts[1])/24;
                                if(percentage<.5) {
                                    count++;
                                    if(under50.equals("")){
                                        under50  = "HAAS G40";
                                    }else{
                                        under50+=", HAAS G40";
                                    }
                                }
                            }else if(parts[0].equals("HAAS 257")){
                                parts[1] = parts[1]+"/"+"21 Computers";
                                percentage = Float.parseFloat(parts[1])/21;
                                if(percentage<.5) {
                                    count++;
                                    if(under50.equals("")){
                                        under50  = "HAAS 257";
                                    }else{
                                        under50+=", and HAAS 257";
                                    }
                                }
                            }
                            data.add(parts[0]);
                            cap.add(parts[1]);
                        }
                    }else{
                        if(i+1==s.length-1 && data.size() == 0){
//                            Toast.makeText(context, "Error retrieving room capacities", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if(count >= 0){
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        builder.setSmallIcon(R.drawable.ic_domain_white_24dp);
                        builder.setContentTitle("Lab Broadcaster");
                        builder.setAutoCancel(true);

                        if(count==1){
                            builder.setContentText(under50+" has under 50% usage!");
                        }else{
                            builder.setContentText(count+" labs including "+ under50+" have under 50% usage!");
                        }

                        Intent intent = new Intent(context, MainActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(MainActivity.class);
                        stackBuilder.addNextIntent(intent);
                        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(pendingIntent);

                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(0,builder.build());
                }


            }
            /*TODO: maybe add to database probably not though*/

            stopSelf();
        }
    }
}
