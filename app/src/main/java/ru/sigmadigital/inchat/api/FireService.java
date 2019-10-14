package ru.sigmadigital.inchat.api;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ru.sigmadigital.inchat.App;
import ru.sigmadigital.inchat.R;

public class FireService extends FirebaseMessagingService {

    private static MutableLiveData<String> liveData = new MutableLiveData<>();



    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            Log.e("Notification Msg", remoteMessage.getNotification().getBody());
        } else {
            Log.e("Notification Msg", "null");
        }
        Log.e("Notification From", remoteMessage.getFrom());


        String channelId = getString(R.string.app_name);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getAppContext(), channelId);
        //builder.setColor(getResources().getColor(R.color.colorPrimary));
        //builder.setSmallIcon(R.drawable.ic_orange_juice);


        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setContentTitle(remoteMessage.getData().get("title"));
        builder.setContentText(remoteMessage.getData().get("text"));

        Log.e("title Msg", remoteMessage.getData().get("title"));
        Log.e("text Msg", remoteMessage.getData().get("text"));

        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setVibrate(new long[]{300, 300, 300, 300, 300, 300});

        //builder.setChannelId("67");


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setVibrationPattern(new long[]{300, 100 ,40, 400, 10, 10 ,10});
            manager.createNotificationChannel(channel);
        }
        try {
            manager.notify("test",14, builder.build());
        } catch (Exception e) {
            Log.e("Notification Msg", "Not create native notification");
        }

        //put to live data
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                liveData.setValue(remoteMessage.getData().get("text"));
            }
        });





    }

    @Override
    public void onNewToken(String s) {
        Log.e("onNewToken", s);
    }



    public static MutableLiveData<String> getLiveData() {
        return liveData;
    }
}

