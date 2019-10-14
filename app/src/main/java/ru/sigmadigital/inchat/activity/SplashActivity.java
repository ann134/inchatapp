package ru.sigmadigital.inchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessaging;

import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.utils.ValidateToken;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseMessaging.getInstance().subscribeToTopic("test");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               checkToken();

                //stG();


            }
        }, 1000);

    }

    private void stG(){
        startActivity(new Intent(this, LoginActivity.class));
        this.finish();
    }

    private void checkToken() {
        ValidateToken.validate(this, new ValidateToken.IValid() {
            @Override
            public void OnValid() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
    }

}
