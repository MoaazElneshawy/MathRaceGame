package com.beansproduction.mathrace.Utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.beansproduction.mathrace.R;
import com.beansproduction.mathrace.UI.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingClass extends FirebaseMessagingService {
    String foregroundMessage, foregroundText;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        foregroundMessage = remoteMessage.getData().get("message");
        foregroundText = remoteMessage.getData().get("type");

        // Log.e("FirebaseMessagingClass", " text > " + foregroundText + " - message > " + foregroundMessage + " --- " + remoteMessage.getData().toString());
//        if (remoteMessage.getMessageType().contains("PlayStore")) {
//            final String appPackageName = getApplicationContext().getPackageName(); // getPackageName() from Context or Activity object
//            try {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//            } catch (android.content.ActivityNotFoundException anfe) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//            }
//            return;
//        }
        if (foregroundMessage != null) {
            if (foregroundText.equalsIgnoreCase("PlayStore") || foregroundMessage.equalsIgnoreCase("PlayStore")
                    ) {
                final String appPackageName = getApplicationContext().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            } else {
                Intent toSplash = new Intent(this, SplashActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, toSplash,
                        PendingIntent.FLAG_ONE_SHOT);
                mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setSmallIcon(R.drawable.logo);
                mBuilder.setContentTitle(foregroundText)
                        .setContentText(foregroundMessage)
                        .setAutoCancel(true)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(pendingIntent);


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel(getString(R.string.default_notification_channel_id),
                            "NOTIFICATION_CHANNEL_NAME", importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.RED);
                    notificationChannel.enableVibration(true);

                    notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    assert mNotificationManager != null;
                    mBuilder.setChannelId(getString(R.string.default_notification_channel_id));
                    mNotificationManager.createNotificationChannel(notificationChannel);
                }

                mNotificationManager.notify(0, mBuilder.build());
            }
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        // Log.e("TOOOKEN", "token > " + s);
    }
}
