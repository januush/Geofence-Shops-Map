package com.example.shopslistwithgeo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import java.util.ArrayList;
import java.util.List;



public class GeofenceBroadcastReceiver extends BroadcastReceiver {

private final static String TAG = "GeofenceTransition";

    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {

            return;
        }
        Log.d(TAG, "onReceive broadcast");

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition== Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            //Get the transition details as a String.
            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences

            );

            for (int i = 0; i < triggeringGeofences.size(); i++) {
                String msg = null;
                if (geofenceTransition == 1) {
                    msg = " ENTER ";
                } else if (geofenceTransition == 2) {
                    msg = " EXIT ";
                }
                Log.d(TAG, "Triggered: " + msg + triggeringGeofences.get(i).getRequestId());
                Toast.makeText(context, msg + triggeringGeofences.get(i).getRequestId(), Toast.LENGTH_LONG).show();

            }

            // Send notification and log the transition details.
            showNotification(context, intent, triggeringGeofences);

        }



    }



    private void showNotification(Context context, Intent intent, List<Geofence> trigeringGeo) {
        String title = intent.getStringExtra("title");
        String msg = intent.getStringExtra("msg");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(context, notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        Intent intentResult = new Intent();
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context,0,intentResult,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        builder = builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(trigeringGeo.get(0).getRequestId())
                .setContentText(trigeringGeo.get(0).getRequestId())

                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());

    }

    private String getGeofenceTransitionDetails(
            GeofenceBroadcastReceiver geofenceBroadcastReceiver,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "EXIT";
            default:
                return "Unknown";
        }
    }
}