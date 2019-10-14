package ru.sigmadigital.inchat;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.sigmadigital.inchat.activity.MainActivity;
import ru.sigmadigital.inchat.api.MyRetrofit;


public class App extends Application {

    private static App context;
    private Realm realm;
    private MyRetrofit.ServiceManager service;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Realm.init(this);

        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(context);
    }

    public static Context getAppContext() {
        return context;
    }


    private static Realm createRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("db.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        return Realm.getInstance(config);
    }

    public synchronized static Realm getRealm() {
        if (context == null) {
            return null;
        }
        if (context.realm != null) {
            return context.realm;
        } else {
            context.realm = createRealm();
            return context.realm;
        }
    }


    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Intent intent = new Intent(App.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                // VKAccessToken is invalid
            }
        }
    };

    public static MyRetrofit.ServiceManager getRetrofitService(){
        if(context == null){
            return null;
        }
        if(context.service != null){
            return context.service;
        } else {
            context.service = MyRetrofit.getInstance();
            return context.service;
        }
    }









}

