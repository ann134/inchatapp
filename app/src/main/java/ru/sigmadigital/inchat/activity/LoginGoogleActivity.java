package ru.sigmadigital.inchat.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.common.AccountPicker;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.sigmadigital.inchat.R;
import ru.sigmadigital.inchat.models.AuthGoogle;
import ru.sigmadigital.inchat.models.Error;
import ru.sigmadigital.inchat.models.JsonParser;
import ru.sigmadigital.inchat.models.Registration;
import ru.sigmadigital.inchat.models.Token;
import ru.sigmadigital.inchat.utils.ErrorHandler;
import ru.sigmadigital.inchat.utils.GoogleAuthHelper;
import ru.sigmadigital.inchat.api.MyRetrofit;
import ru.sigmadigital.inchat.utils.SettingsHelper;

public class LoginGoogleActivity extends BaseActivity{

    MyRetrofit.ServiceManager service;

    GoogleAuthHelper gAuthHelper;

    private final static String G_PLUS_SCOPE = "oauth2:https://www.googleapis.com/auth/plus.me";
    private final static String USERINFO_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private final static String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    private final static String SCOPES = G_PLUS_SCOPE + " " + USERINFO_SCOPE + " " + EMAIL_SCOPE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginvk);

        service = MyRetrofit.getInstance();

        gAuthHelper = new GoogleAuthHelper(this);

        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                false, null, null, null, null);
        startActivityForResult(intent, 123);






       /* GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth"))
                .build();



        GoogleApiClient mApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mApiClient);
        startActivityForResult(signInIntent, 123);*/


        /*Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.e("OnError", "OnError");
                return false;
            }
        });


        AccountManager am = AccountManager.get(this);
        Bundle options = new Bundle();

        am.getAuthToken(am.getAccountsByType("google")[0], "Test", true, new OnTokenAcquired(), handler);*/




//        auth();



    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == 123 && resultCode == RESULT_OK) {
            final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

            for (Account acc : gAuthHelper.getAccounts()) {
                if (acc.name.equals(accountName)){
                    Log.e(acc.name, accountName);
                    auth(acc);
                }

            }
        }
    }


    private void auth(Account acc){
        gAuthHelper.getAuthToken(acc, new GoogleAuthHelper.OAuthCallbackListener() {
            @Override
            public void callback(String authToken) {
                Log.e("token", authToken);
                //Log.e("token", gAuthHelper.getAccounts()[0].name + " " + gAuthHelper.getAccounts()[0].type + " " + gAuthHelper.getAccounts()[0].toString());

                login(authToken);

            }
        });
    }

    private void login(String token){
       final Registration registration =  new Registration("", new AuthGoogle(token));
       Log.e("send", registration.toJson());

        service.registration(registration).enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.e("onResponseCode", response.code() + " ");


                if (response.body() != null ) {
                    Log.e("onResponse", " " + response.body().toJson());
                    SettingsHelper.setToken(response.body());
                    SettingsHelper.setRegistration(registration);
                    startMainActivity();
                } else if (response.errorBody() != null) {
                    try {
                        String responseError = response.errorBody().string();
                        Log.e("onResponseError",  " " + responseError);
                        Error error  = JsonParser.fromJson(responseError, Error.class);
                        if (error != null){
                            Log.e("Error",  " " + error.getDescription() + " " + error.getCode());
                            ErrorHandler.catchError(error, LoginGoogleActivity.this);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.e("onFailure", t+"");
                Toast.makeText(getBaseContext(), "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
           finish();
    }




















   /* private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            // Get the result of the operation from the AccountManagerFuture.
            Bundle bundle = null;
            try {
                bundle = result.getResult();
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            }

            // The token is a named value in the bundle. The name of the value
            // is stored in the constant AccountManager.KEY_AUTHTOKEN.
            String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            Log.e("token", token);

        }
    }*/


    /*private class OnError implements  AccountManagerCallback<Bundle> {

        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            Log.e("OnError", "OnError");
        }
    }*/



    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult", "onActivityResult");
        if (requestCode == 123) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.e("isSuccess", result.isSuccess()+"");
            Log.e("isSuccess", result.getSignInAccount()+"");
            //Log.e("isSuccess", result.getSignInAccount().getServerAuthCode()+"");
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String authCode = acct.getServerAuthCode();
                getAccessToken(authCode);
                Log.e("name", acct.getDisplayName());

            }
        }
    }*/


   /* @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("onConnectionFailed", "onConnectionFailed");
    }

   */


  /*  private void getAccessToken(String aauthCode) {
        Log.e("aauthCode", aauthCode);
    }
*/


    /*protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        if (requestCode == 123 && resultCode == RESULT_OK) {
            final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            AsyncTask<Void, Void, String> getToken = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    try {
                        String token = GoogleAuthUtil.getToken(LoginGoogleActivity.this, accountName,
                                SCOPES);
                        return token;

                    } catch (UserRecoverableAuthException userAuthEx) {
                        startActivityForResult(userAuthEx.getIntent(), 123);
                    }  catch (IOException ioEx) {
                        Log.d(TAG, "IOException");
                    }  catch (GoogleAuthException fatalAuthEx)  {
                        Log.d(TAG, "Fatal Authorization Exception" + fatalAuthEx.getLocalizedMessage());
                    }
                    return token;
                }

                @Override
                protected void onPostExecute(String token) {
                    reg(token);
                }

            };
            getToken.execute(null, null, null);
        }
    }

    private void reg(String token){

    }*/


}
