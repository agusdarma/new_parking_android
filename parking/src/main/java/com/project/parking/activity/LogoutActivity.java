package com.project.parking.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.project.parking.MainFragment;
import com.project.parking.R;
import com.project.parking.data.LoginData;
import com.project.parking.data.MessageVO;
import com.project.parking.service.ApiInterface;
import com.project.parking.util.ApiClient;
import com.project.parking.util.CipherUtil;
import com.project.parking.util.HttpClientUtil;
import com.project.parking.util.SharedPreferencesUtils;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutActivity extends AppCompatActivity {
    private static final String TAG = "LogoutActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_FORGOT_PASSWORD = 0;

    private Context ctx;
//    private ReqLogoutTask reqLogoutTask = null;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Bind(R.id.btn_logout) Button _logoutButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        ctx = LogoutActivity.this;
        ButterKnife.bind(this);

        _logoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logout();
            }
        });


    }

    public void logout() {
        Log.d(TAG, "Logout");

//        if (!validate()) {
//            onLoginFailed();
//            return;
//        }

        _logoutButton.setEnabled(false);

//        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLogoutSuccess();
                        // onLoginFailed();
//                        progressDialog.dismiss();
                    }
                }, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainFragment
        moveTaskToBack(true);
    }

    public void onLogoutSuccess() {
        _logoutButton.setEnabled(true);
//        Toast.makeText(getBaseContext(), "Login Success", Toast.LENGTH_LONG).show();

        // doLogout
        doLogout();
//        reqLogoutTask = new ReqLogoutTask();
//        reqLogoutTask.execute("");

    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _logoutButton.setEnabled(true);
    }

    private void doLogout() {
        LoginData loginData = SharedPreferencesUtils.getLoginData(ctx);
        String s = null;
        try {
            s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(loginData);
        } catch (IOException e) {
            Log.e(TAG, "IOException : "+e);
            e.printStackTrace();
        }
        s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
        Log.d(TAG,"Request: " + s);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<String> call = apiService.doLogout(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String>call, Response<String> response) {
//				List<Movie> movies = response.body().getResults();
//				Log.d(TAG, "Number of movies received: " + movies.size());
                Log.d(TAG, "Response: " + response.body().toString());
//				Toast.makeText(getActivity(), "Berhasil Masukk...", Toast.LENGTH_LONG).show();
                if(!response.body().toString().isEmpty()){
                    try {
                        String respons = CipherUtil.decryptTripleDES(response.body().toString(), CipherUtil.PASSWORD);
                        MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);
                        if(messageVO.getRc()==0){
                            goToLoginActivity(ctx);
                        }
                        else{
                            Toast.makeText(ctx, messageVO.getMessageRc(), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(ctx, LogoutActivity.this.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ctx, LogoutActivity.this.getResources().getString(R.string.message_unexpected_error_server), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                Toast.makeText(ctx, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

//    public boolean validate() {
//        boolean valid = true;
//
//        String email = _emailText.getText().toString();
//        String password = _passwordText.getText().toString();
//
//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            _emailText.setError("enter a valid email address");
//            valid = false;
//        } else {
//            _emailText.setError(null);
//        }
//
//        if (password.isEmpty() || password.length() < 3 ) { //|| password.length() > 10
//            _passwordText.setError("min 6 alphanumeric characters");
//            valid = false;
//        } else {
//            _passwordText.setError(null);
//        }
//
//        return valid;
//    }

//    public class ReqLogoutTask  extends AsyncTask<String, Void, Boolean> {
//        private ProgressDialog progressDialog = null;
//        private final HttpClient client = HttpClientUtil.getNewHttpClient();
//        String respString = null;
//
//        protected void onPreExecute() {
//            progressDialog = new ProgressDialog(LogoutActivity.this,
//                    R.style.AppTheme_Dark_Dialog);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setMessage(ctx.getResources().getString(R.string.progress_dialog));
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
//        @Override
//        protected Boolean doInBackground(String... arg0) {
//            boolean result = false;
//            try {
//                LoginData loginData = SharedPreferencesUtils.getLoginData(ctx);
//                String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(loginData);
//                s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
//                Log.d(TAG,"Request: " + s);
//                StringEntity entity = new StringEntity(s);
//                HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_LOGOUT);
//                post.setHeader(HttpClientUtil.CONTENT_TYPE, HttpClientUtil.JSON);
//                post.setEntity(entity);
//                // Execute HTTP request
//                Log.d(TAG,"Executing request: " + post.getURI());
//                HttpResponse response = client.execute(post);
//                HttpEntity respEntity = response.getEntity();
//                respString = EntityUtils.toString(respEntity);
//                result = true;
//            } catch (ClientProtocolException e) {
//                Log.e(TAG, "ClientProtocolException : "+e);
//                respString = ctx.getResources().getString(R.string.message_unexpected_error_message_server);
//                cancel(true);
//            } catch (IOException e) {
//                Log.e(TAG, "IOException : "+e);
//                respString = ctx.getResources().getString(R.string.message_no_internet_connection);
//                cancel(true);
//            } catch (Exception e) {
//                Log.e(TAG, "Exception : "+e);
//                respString = ctx.getResources().getString(R.string.message_unexpected_error_message_server);
//                cancel(true);
//            }
//            return result;
//        }
//
//        @Override
//        protected void onCancelled() {
//            if(progressDialog.isShowing()){
//                progressDialog.dismiss();
//            }
//            Toast.makeText(getBaseContext(), respString, Toast.LENGTH_LONG).show();
////            MessageUtils messageUtils = new MessageUtils(ctx);
////            messageUtils.snackBarMessage(LoginActivity.this,respString);
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            reqLogoutTask = null;
//            if (success) {
//                if(!respString.isEmpty()){
//                    try {
//                        String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
//                        final MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);
//                        if(messageVO.getRc()==0){
////                            MessageUtils messageUtils = new MessageUtils(ctx);
////                            messageUtils.snackBarMessage(getActivity(),messageVO.getOtherMessage());
//                            goToLoginActivity(ctx);
//                        }else{
////                            MessageUtils messageUtils = new MessageUtils(ctx);
////                            messageUtils.snackBarMessage(getActivity(),messageVO.getMessageRc());
//                            new Timer().schedule(new TimerTask() {
//                                @Override
//                                public void run() {
//                                    if(messageVO.getRc()== Constants.SESSION_EXPIRED||messageVO.getRc()==Constants.SESSION_DIFFERENT||messageVO.getRc()==Constants.USER_NOT_LOGIN){
//                                        RedirectUtils redirectUtils = new RedirectUtils(ctx, LogoutActivity.this);
//                                        redirectUtils.redirectToLogin();
//                                    }
//                                }
//                            }, Constants.REDIRECT_DELAY_LOGIN);
//                        }
//                    } catch (Exception e) {
//                        Toast.makeText(getBaseContext(), LogoutActivity.this.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
////                        MessageUtils messageUtils = new MessageUtils(ctx);
////                        messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_message_server));
//                    }
//                }else{
//                    Toast.makeText(getBaseContext(), LogoutActivity.this.getResources().getString(R.string.message_unexpected_error_server), Toast.LENGTH_LONG).show();
////                    MessageUtils messageUtils = new MessageUtils(ctx);
////                    messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server));
//                }
//            }else{
//                Toast.makeText(getBaseContext(), LogoutActivity.this.getResources().getString(R.string.message_unexpected_error_server), Toast.LENGTH_LONG).show();
////                MessageUtils messageUtils = new MessageUtils(ctx);
////                messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server));
//            }
//            if(progressDialog.isShowing()){
//                progressDialog.dismiss();
//            }
//        }
//    }

    private void goToLoginActivity(Context ctx)
    {
        LogoutActivity.this.finish();
        Intent intent = new Intent(ctx, MainFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
