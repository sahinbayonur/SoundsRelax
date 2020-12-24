package com.sahinbay.voice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.ToggleButton;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.sahinbay.voice.App.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {

    public SeekBar whiteVolumeSeekBar, pinkVolumeSeekBar, brownVolumeSeekBar;
    public MediaPlayer whiteNoiseMP, brownNoiseMP, pinkNoiseMP;
    public ToggleButton whiteNoiseToggle, brownNoiseToggle, pinkNoiseToggle;

    int soundNumber = 0;

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notificationManager = NotificationManagerCompat.from(this);

        //Sets up white, brown, and pink noise players. Uses toggle buttons to turn sound on
        //and off.
        whiteNoiseMP = MediaPlayer.create(MainActivity.this, R.raw.beach);
        brownNoiseMP = MediaPlayer.create(MainActivity.this, R.raw.birds);
        pinkNoiseMP = MediaPlayer.create(MainActivity.this, R.raw.sheep);

        whiteNoiseToggle = (ToggleButton) findViewById(R.id.whitenoisebutton);
        whiteNoiseToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toogleBtn(isChecked,whiteNoiseMP);
            }
        });

        brownNoiseToggle = (ToggleButton) findViewById(R.id.brownnoisebutton);
        brownNoiseToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toogleBtn(isChecked,brownNoiseMP);
            }
        });

        pinkNoiseToggle = (ToggleButton) findViewById(R.id.pinknoisebutton);
        pinkNoiseToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toogleBtn(isChecked,pinkNoiseMP);
            }
        });
        //End of sound controller initializations

        //Start of control volume with sliders//
        try {
            seekBarChange(whiteVolumeSeekBar, whiteNoiseMP, R.id.whiteVolumeSeekBar);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }

        try {
            seekBarChange(brownVolumeSeekBar, brownNoiseMP, R.id.brownVolumeSeekBar);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }

        try {
            seekBarChange(pinkVolumeSeekBar, pinkNoiseMP, R.id.pinkVolumeSeekBar);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        //end of volume controller
    }

    public void seekBarChange(SeekBar sss, final MediaPlayer mmm, int id) {
        sss = (SeekBar) findViewById(id);
        sss.setMax(100);
        sss.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                float log1 = (float) (Math.log(100 - progress) / Math.log(100));
                mmm.setVolume(1 - log1, 1 - log1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sss.setProgress(50);
    }

    public void toogleBtn(boolean isChecked, MediaPlayer mp){
        if (isChecked) {
            // The toggle is enabled
            mp.start();
            mp.setLooping(true);
            showNotification();
        } else {
            // The toggle is disabled
            mp.pause();
            soundNumber--;
            if (soundNumber == 0) {
                stopNotification();
            }
        }
    }

    public void showNotification() {

        int notificationId = 5;

        soundNumber++;

        //the actions in a notification are handeled through intents
        RemoteViews collapsedView = new RemoteViews(getPackageName(),
                R.layout.notification_collapsed);
        RemoteViews expandedView = new RemoteViews(getPackageName(),
                R.layout.notification_expanded);
        Intent clickIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this,
                0, clickIntent, 0);
        collapsedView.setTextViewText(R.id.text_view_collapsed_1, "Hello World!");
        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);

        //init notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.bird)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.fb))
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(collapsedView)
                .setContentTitle("Natural Sounds")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Click view to visit Google."))
                .setAutoCancel(true)
                .setOngoing(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .addAction(android.R.drawable.ic_menu_view, "VIEW", clickPendingIntent);

        /* Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(path); */

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        notificationManager.notify(notificationId, builder.build());
    }

    public void stopNotification() {

        try {
            // we don't need to be foreground anymore
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // notificationManager.cancelAll();
            notificationManager.cancelAll();

        } catch (Exception e) {
            //
        }
    }
}