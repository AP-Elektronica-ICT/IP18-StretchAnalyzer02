package be.eaict.stretchalyzer2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Cédric on 3/22/2018.
 */

public class Notification_reciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, HomeActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
        notification.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        notification.setContentIntent(pendingIntent);
        notification.setSmallIcon(R.drawable.stretch);
        notification.setAutoCancel(true);
        notification.setContentTitle("Time to stretch !");
        notification.setContentText("come on");

        assert notificationManager != null;
        notificationManager.notify(100,notification.build());

    }}

