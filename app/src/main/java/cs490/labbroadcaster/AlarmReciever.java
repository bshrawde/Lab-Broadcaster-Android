package cs490.labbroadcaster;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;
/**
 * Created by Nishant on 4/28/2016.
 */

public class AlarmReciever extends BroadcastReceiver{
    public Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d("BROADCAST RECIEVER", "Recurring alarm, requesting updated lab capacities");
//        Intent i = new Intent(context, UpdateScoresService.class);
//        context.startService(i);
        setAlarm();

    }

    private void setAlarm(){
        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeZone(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,10);
        calendar.set(Calendar.MINUTE,00);

//        Every  hour refresh all the capacities

        //Create
        Intent intent = new Intent(context, UpdateCapacitiesService.class);

        PendingIntent recurringDownload = PendingIntent.getService(context,0,intent, 0);

//        PendingIntent recurringDownload = PendingIntent.getBroadcast(context,0,downloader, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HOUR,recurringDownload);

    }
}
