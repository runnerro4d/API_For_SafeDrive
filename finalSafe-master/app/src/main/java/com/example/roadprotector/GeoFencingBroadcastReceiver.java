package com.example.roadprotector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeoFencingBroadcastReceiver extends BroadcastReceiver {


    private static final String TAG = "GeofenceBroadcastReceiv";

    @Override
    public void onReceive(Context context, Intent intent) {

        // an Intent broadcast.
//        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

      //  NotificationHelper notificationHelper = new NotificationHelper(context);

        MediaPlayer mediaPlayer;

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());

        }
//        Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();


        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                mediaPlayer = MediaPlayer.create(context, R.raw.entering);
                mediaPlayer.start();
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
              //  notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MapsActivity.class);
                break;
//            case Geofence.GEOFENCE_TRANSITION_DWELL:
//                mediaPlayer = MediaPlayer.create(context, R.raw.inside_zone);
//                mediaPlayer.start();
//                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
//               // notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", MapsActivity.class);
//                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                mediaPlayer = MediaPlayer.create(context, R.raw.exiting_zone);
                mediaPlayer.start();
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
               // notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MapsActivity.class);
                break;
        }

    }
}
