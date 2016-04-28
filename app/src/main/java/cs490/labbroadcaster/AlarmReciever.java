package cs490.labbroadcaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;
/**
 * Created by Nishant on 4/28/2016.
 */

public class AlarmReciever extends BroadcastReceiver{
    public Context mcontext;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mcontext = context;
        Log.d("BROADCAST RECIEVER", "Recurring alarm, requesting updated lab capacities");
        SharedPreferences sharedPref  = PreferenceManager.getDefaultSharedPreferences(mcontext);
        String refreshSetting = sharedPref.getString("refresh_rate", "manual-checked");

        String r = intent.getStringExtra("refresh");

        Log.e("refresh", r);
        if(r.equals("hourly-checked") || refreshSetting.equals("hourly-checked")){
            Log.e("Starting alarm", "swegweg");
            setAlarm();

        }else{
            Log.e("Disabling alarm", "ewgf");
            disableAlarm();
        }

    }
    private void setAlarm(){
        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.SECOND,30);
//        calendar.set(Calendar.HOUR_OF_DAY,17);
//        calendar.set(Calendar.MINUTE,43);

//        Every  hour refresh all the capacities

        //Create
        Intent intent = new Intent(mcontext, UpdateCapacitiesService.class);

//        recurringDownload = PendingIntent.getService(context,0,intent, 0);

        PendingIntent recurringRefresh = PendingIntent.getBroadcast(mcontext,0,intent, 0);
        AlarmManager alarmManager = (AlarmManager) mcontext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),1000*30,recurringRefresh);
//        alarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HOUR,recurringRefresh);
//        Intent i = new Intent(context, UpdateScoresService.class);
//        context.startService(i);
    }

    private void disableAlarm(){
//        Intent intent = new Intent(context, AlarmReciever.class);
        Intent intent = new Intent(mcontext, UpdateCapacitiesService.class);

        PendingIntent recurringDownload =PendingIntent.getBroadcast(mcontext,0,intent, 0);

        AlarmManager alarmManager = (AlarmManager) mcontext.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(recurringDownload);
    }
}
