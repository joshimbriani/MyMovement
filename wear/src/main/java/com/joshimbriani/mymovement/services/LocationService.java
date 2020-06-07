package com.joshimbriani.mymovement.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.joshimbriani.mymovement.MainActivity;
import com.joshimbriani.mymovement.R;
import com.joshimbriani.mymovement.data.GsonWithZonedDateTime;
import com.joshimbriani.mymovement.data.MovementPoint;
import com.joshimbriani.mymovement.data.MovementRepository;

public class LocationService extends Service {
    private static final String TAG = "com.joshimbriani.mymove";
    public static long serviceId = -1;
    public static int refreshInterval;
    public static boolean serviceRunning = false;

    public static final String CHANNEL_ID = "LocationServiceChannel";
    private HandlerThread handlerThread = new HandlerThread("LocationGetter");
    private Handler handler;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private DataClient dataClient;
    private Gson gson;

    private MovementRepository movementRepository;

    @Override
    public void onCreate() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        movementRepository = new MovementRepository(getApplication());
        dataClient = Wearable.getDataClient(getApplicationContext());
        gson = GsonWithZonedDateTime.getGson();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        registerReceiver(stopServiceReceiver, new IntentFilter("StopService"));

        serviceId = intent.getLongExtra("movementId", 1);
        refreshInterval = intent.getIntExtra("refreshInterval", 60);
        serviceRunning = true;
        locationCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                MovementPoint movementPoint = new MovementPoint(serviceId, locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude(), "wear");
                movementRepository.insert(movementPoint);

                syncMovementPoint(movementPoint);
            }
        };

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        PendingIntent cancelIntent = PendingIntent.getBroadcast(this, 0, new Intent("StopService"), PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MyMovement Location Tracker")
                .setSmallIcon(R.drawable.ic_location_notification)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_location_notification, "Stop Movement", cancelIntent)
                .build();
        startForeground(1, notification);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.post(locationGetter);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        serviceRunning = false;
        serviceId = -1;
        unregisterReceiver(stopServiceReceiver);
    }

    protected BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            handler.removeCallbacks(locationGetter);
            handlerThread.quit();
            stopForeground(true);
            stopSelf();
        }
    };

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "MyMovement Location Tracker", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Runnable locationGetter = () -> getAndSaveLocationToMovement();

    private void getAndSaveLocationToMovement() {
        locationRequest = createLocationRequest();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.e("TEST", "Location " + task.getResult());
                        }
                    }
                });

        try {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e("TEST", "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(refreshInterval * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    private void syncMovementPoint(MovementPoint movementPoint) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/movement/" + serviceId + "/" + movementPoint.getId());
        putDataMapRequest.getDataMap().putString("com.joshimbriani.mymovement.movement", gson.toJson(movementPoint));
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Task<DataItem> putDataTask = dataClient.putDataItem(putDataRequest);

        putDataTask.addOnCompleteListener(task -> Log.e(TAG, "Synced data point"));
    }
}
