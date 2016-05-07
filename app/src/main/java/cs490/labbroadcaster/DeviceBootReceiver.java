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

/**
 * Created by Nishant on 5/7/2016.
 */
public class DeviceBootReceiver extends BroadcastReceiver {
    PendingIntent refreshCapacities;
    Intent intent1;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            intent1  = new Intent(context, UpdateCapacitiesService.class);
            refreshCapacities = PendingIntent.getService(context,0,intent1, 0);


            String refreshRate = sharedPreferences.getString("refresh_rate", "1");

            if(refreshRate.equals("manual-checked")){ /* manual */
                manual(context);
            }else{
                hourly(context);
            }
        }
    }

    public void manual(Context context){
        Log.e("MANUAL REFRESH", "BRUH!");
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(refreshCapacities);
    }

    public void hourly(Context context){
        Log.e("HOURLY REFRESH", "BRUH!");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1); /*refresh in 1 hour*/
        calendar.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE));
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HOUR, refreshCapacities);


    }
}
