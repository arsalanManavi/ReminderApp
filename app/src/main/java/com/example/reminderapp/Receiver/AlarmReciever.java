package com.example.reminderapp.Receiver;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;

import android.media.MediaPlayer;


import android.os.Build;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;


import com.example.reminderapp.AlarmActivity;
import com.example.reminderapp.MainActivity;
import com.example.reminderapp.R;
import com.example.reminderapp.model.Note;
import com.example.reminderapp.viewmodel.MainViewModel;


import java.util.Random;


public class AlarmReciever extends BroadcastReceiver {


    public static String KEY_TITLE = "T";
    public static String KEY_DESC = "d";
    public static String KEY_DATE = "D";
    public static String KEY_TIME = "t";
    public static final String channelId = "reminder";
    String title, desc, date, time;



    @Override
    public void onReceive(Context context, Intent intent) {

        title = intent.getStringExtra(MainActivity.KEY_TITLE);
        desc = intent.getStringExtra(MainActivity.KEY_DESC);
        date = intent.getStringExtra(MainActivity.KEY_DATE);
        time = intent.getStringExtra(MainActivity.KEY_TIME);

        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra(KEY_TITLE, title);
        alarmIntent.putExtra(KEY_DESC, desc);
        alarmIntent.putExtra(KEY_DATE, date);
        alarmIntent.putExtra(KEY_TIME, time);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationCompat = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("alarm")
                .setContentText("click here for more")
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +context.getPackageName()+"/"+R.raw.alarm))
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);


//        AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "alarmchannelName";
            String description = "channel for this application";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, name, importance);
            notificationChannel.setDescription(description);
            //  notificationChannel.setSound(alarmsound,audioAttributes);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationCompat.setChannelId(channelId);
        }
        int id = 0;

        notificationManagerCompat.notify(id++, notificationCompat.build());



    }


}
