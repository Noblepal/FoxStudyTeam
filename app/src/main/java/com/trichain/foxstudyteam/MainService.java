package com.trichain.foxstudyteam;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.ads.AdRequest;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainService extends Service {
    private final static int FOREGROUND_ID = 999;
    private static final String TAG = "MainService";
    private Uri audioFileUri = null;
    private String audioFileUrl = "";
    Intent broadCastIntent = null;
    private boolean streamAudio = false;
    private MediaPlayer audioPlayer = null;
    private boolean isAudioPlaying = false;
    private Context context = null;
    NotificationCompat.Builder builder;
    NotificationManager manager;
    private Handler audioProgressUpdateHandler;
    private Handler secondaryAudioProgressUpdateHandler;

    PendingIntent nextPendingIntent, playPausePendingIntent;

    public static final int notify = 1000;  //interval between two services(Here Service run every 5 seconds)
    int count = 0;  //number of times service is display
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling

    public final int UPDATE_AUDIO_PROGRESS_BAR = 1;
    public int bufferProgressPercent = 0;
    private int length = 0;

    //For notifications
    private static final String CHANNEL_ID = "channel_id01";
    public static final int NOTIFICATION_ID = 1;

    private ScheduledExecutorService scheduler, scheduler2;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = MainService.this;
        showNotification();

        Log.e(TAG, "onStartCommand: " );
        return START_STICKY;
    }

    public void showNotification() {
        createNotificationChannel();

        /*//start PlayerActivity on by Tapping notification
        Intent playPauseIntent = new Intent(context, MainService.class);
        playPauseIntent.putExtra("play_pause", "play_pause");
        playPausePendingIntent = PendingIntent.getService(context, 0, playPauseIntent, PendingIntent.FLAG_ONE_SHOT);
*/
        //Click Play/Pause button to start pause/play audio

        Intent nextIntent = new Intent(context, ItemListActivity.class);
        nextIntent.putExtra("category","trending");
        nextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        nextPendingIntent = PendingIntent.getService(context, 0, nextIntent, PendingIntent.FLAG_ONE_SHOT);

        //creating notification
        builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        //icon
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        //title
        builder.setContentTitle("News feed.io");
        //description
        builder.setContentText("Breaking news for You");
        //set priority
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //dismiss on tap
        builder.setAutoCancel(false);
        builder.setOngoing(false);
        //start intent on notification tap (MainActivity)
        builder.setContentIntent(nextPendingIntent);
        builder.setFullScreenIntent(nextPendingIntent,true);

        //notification manager
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
        Log.e(TAG, "showNotification: " );
        startNotification();

        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    startNotification();
                    Log.e(TAG, "run: showing" );
                }
            }, 360, 360, TimeUnit.SECONDS);

        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "News feed.io";
            String description = "Breaking news for You";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }
    }
    private void startNotification() {
        Log.i("NextActivity", "startNotification");

        // Sets an ID for the notification
        int mNotificationId = 001;

        // Build Notification , setOngoing keeps the notification always in status bar
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Chosen For you")
                        .setContentText("Check this breaking news")
                        .setChannelId(CHANNEL_ID)
                        .setOngoing(false);

        // Create pending intent, mention the Activity which needs to be
        //triggered when user clicks on notification(StopScript.class in this case)

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ItemListActivity.class).putExtra("category","trending"), PendingIntent.FLAG_UPDATE_CURRENT);


        mBuilder.setContentIntent(contentIntent);


        // Gets an instance of the NotificationManager service
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        // Builds the notification and issues it.
        mNotificationManager.notify(mNotificationId, mBuilder.build());


    }



    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: " );
        stopForeground(true);
    }
}
