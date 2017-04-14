package com.example.jokklan.finalproject;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.intentfilter.androidpermissions.PermissionManager;

import static java.util.Collections.singleton;

/**
 * JobService to be scheduled by the JobScheduler.
 * Requests scheduled with the JobScheduler call the "onStartJob" method
 */
public class LocationIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 0;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "LocationJobService";
    private static int notificationId = 1;

    public LocationIntentService() {
        super("LocationIntentService");
    }

    // This method is called when the service instance
    // is created
    @Override
    public void onCreate() {
        Log.i(TAG, "myService created");
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if( mGoogleApiClient != null ) {
            mGoogleApiClient.connect();
        }
    }

    // This method is called when the service instance
    // is destroyed
    @Override
    public void onDestroy() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }

        Log.i(TAG, "myService destroyed");

        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");


        askForPermission();

        String placesMessage = guessCurrentPlace();
        sendNotification(placesMessage);

        //WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected String guessCurrentPlace() {
        Log.d(TAG, "Starting guessCurrentPlace");
        PendingResult<PlaceLikelihoodBuffer> query = Places.PlaceDetectionApi.getCurrentPlace( mGoogleApiClient, null );

        Log.d(TAG, "Current places are running");

        PlaceLikelihoodBuffer likelyPlaces = query.await();

        Log.d(TAG, "Running location callback");

        int count = likelyPlaces.getCount();
        String status = "";

        Log.d(TAG, "Likely places status: " + likelyPlaces.getStatus());
        Log.d(TAG, "Likely places count: " + count);

        if(count > 0) {
            PlaceLikelihood placeLikelihood = likelyPlaces.get(0);
            String content = "";
            if (placeLikelihood != null && placeLikelihood.getPlace() != null && !TextUtils.isEmpty(placeLikelihood.getPlace().getName()))
                content = "Most likely place: " + placeLikelihood.getPlace().getName() + "\n";
            if (placeLikelihood != null)
                content += "Percent change of being there: " + (int) (placeLikelihood.getLikelihood() * 100) + "%";

            status = content;
        } else {
            status = "Could not find your current location";
        }

        likelyPlaces.release();
        return status;
    }

    private void sendNotification(String message) {
        Log.d(TAG, "sendNotification: " + message);

        Intent resultIntent = new Intent(this, LocationResultActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(LocationResultActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(this)
                .setContentTitle("Found a location near you: " + message)
                .setSmallIcon(R.drawable.notification_icon);

        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, builder.build());

        this.notificationId += 1;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    public void askForPermission() {
        Log.d(TAG, "askForPermission");
        PermissionManager permissionManager = PermissionManager.getInstance(getApplicationContext());
        permissionManager.checkPermissions(singleton(Manifest.permission.ACCESS_FINE_LOCATION), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "Permissions Granted");
                Toast.makeText(getApplicationContext(), "Permissions Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied() {
                Log.d(TAG, "Permissions Denied");
                Toast.makeText(getApplicationContext(), "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

}