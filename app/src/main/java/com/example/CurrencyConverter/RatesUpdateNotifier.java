package com.example.CurrencyConverter;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

/**
 * Notifier class notifies the user after rates have been updated
 */
public class RatesUpdateNotifier {

    private static final int NOTIFICATION_ID = 1;

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    public RatesUpdateNotifier(Context context) {
        notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.rates_updated)
                .setContentTitle("Updated Currencies!")
                .setContentText("Everything fresh...")
                .setAutoCancel(true);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0, resultIntent, 0);
        notificationBuilder.setContentIntent(resultPendingIntent);

        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showOrUpdateNotification() {
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}


