package ru.sigmadigital.inchat.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.annotations.EverythingIsNonNull;
import ru.sigmadigital.inchat.models.Token;

@GlideModule
public class MyGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        OkHttpClient client;
        String token = "";
        Token tr = SettingsHelper.getToken();
        if (tr != null) {
            token = tr.getAccess();
        }
        final String stk = token;
        client = new OkHttpClient.Builder()

                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(@EverythingIsNonNull Chain chain) throws IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("token", stk)
                                .build();
                        return chain.proceed(newRequest);
                    }


                })
                .build();

        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(client);
        glide.getRegistry().replace(GlideUrl.class, InputStream.class, factory);
    }
}
