package com.project.parking.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.parking.R;
import com.project.parking.data.Constants;
import com.project.parking.data.InqChangePasswordRequest;
import com.project.parking.data.LoginData;
import com.project.parking.data.MessageVO;
import com.project.parking.util.CipherUtil;
import com.project.parking.util.HttpClientUtil;
import com.project.parking.util.RedirectUtils;
import com.project.parking.util.SharedPreferencesUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yohanes on 14/06/2017.
 */

public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = "ChangePasswordActivity";
    private static final int REQUEST_SIGNUP = 0;
    private String email;
    private String sessionkey;

    private Context ctx;
    private ReqChangePasswordTask reqChangePasswordTask = null;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Bind(R.id.input_old_password) EditText _oldPassword;
    @Bind(R.id.input_new_password) EditText _newPassword;
    @Bind(R.id.btn_save) Button _saveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ctx = ChangePasswordActivity.this;
        ButterKnife.bind(this);

        _saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                forgetPassword();
            }
        });

    }

    public void forgetPassword() {
        Log.d(TAG, "Change Password");

        if (!validate()) {
            onFailed();
            return;
        }

        _saveButton.setEnabled(false);

//        final ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this,
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Processing...");
//        progressDialog.show();

//        String oldPassword = _oldPassword.getText().toString();
//        String newPassword = _newPassword.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onProcessingSuccess();
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
        // Disable going back to the MainActivity
//        moveTaskToBack(true);
        RedirectUtils redirectUtils = new RedirectUtils(ctx, ChangePasswordActivity.this);
        redirectUtils.redirectToMainMenu();
    }

    public void onProcessingSuccess() {
        _saveButton.setEnabled(true);

        Toast.makeText(getBaseContext(), "Processing..", Toast.LENGTH_LONG).show();

        String oldPassword = _oldPassword.getText().toString();
        String newPassword = _newPassword.getText().toString();

        LoginData loginData = SharedPreferencesUtils.getLoginData(ctx);
        // ambil dari session untuk email, session key
        email = loginData.getEmail();
        sessionkey = loginData.getSessionKey();
        if (!oldPassword.isEmpty() && !newPassword.isEmpty()&& !email.isEmpty()&& !sessionkey.isEmpty()) {
            reqChangePasswordTask = new ReqChangePasswordTask();
            reqChangePasswordTask.execute(oldPassword, newPassword);
        }

    }

    public void onFailed() {
        Toast.makeText(getBaseContext(), ctx.getResources().getString(R.string.message_password_required), Toast.LENGTH_LONG).show();

        _saveButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String oldPassword = _oldPassword.getText().toString();
        String newPassword = _newPassword.getText().toString();

//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            _emailText.setError("enter a valid email address");
//            valid = false;
//        } else {
//            _emailText.setError(null);
//        }

        if (oldPassword.isEmpty() || oldPassword.length() < 3 ) { //|| password.length() > 10
            _oldPassword.setError("min 6 alphanumeric characters");
            valid = false;
        } else {
            _oldPassword.setError(null);
        }

        if (newPassword.isEmpty() || newPassword.length() < 3 ) { //|| password.length() > 10
            _newPassword.setError("min 6 alphanumeric characters");
            valid = false;
        } else {
            _newPassword.setError(null);
        }

        return valid;
    }

    public class ReqChangePasswordTask  extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progressDialog = null;
        private final HttpClient client = HttpClientUtil.getNewHttpClient();
        String respString = null;

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ChangePasswordActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(ctx.getResources().getString(R.string.progress_dialog));
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(String... arg0) {
            boolean result = false;
            try {
                InqChangePasswordRequest inqChangePasswordRequest = new InqChangePasswordRequest();
                inqChangePasswordRequest.setEmail(email);
                inqChangePasswordRequest.setPassword(arg0[0]);
                inqChangePasswordRequest.setNewPassword(arg0[1]);
                inqChangePasswordRequest.setSessionKey(sessionkey);
                String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(inqChangePasswordRequest);
                s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
                Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);
                HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_CHANGE_PASSWORD);
                post.setHeader(HttpClientUtil.CONTENT_TYPE, HttpClientUtil.JSON);
                post.setEntity(entity);
                // Execute HTTP request
                Log.d(TAG,"Executing request: " + post.getURI());
                HttpResponse response = client.execute(post);
                HttpEntity respEntity = response.getEntity();
                respString = EntityUtils.toString(respEntity);
                result = true;
            } catch (ClientProtocolException e) {
                Log.e(TAG, "ClientProtocolException : "+e);
                respString = ctx.getResources().getString(R.string.message_unexpected_error_message_server);
                cancel(true);
            } catch (IOException e) {
                Log.e(TAG, "IOException : "+e);
                respString = ctx.getResources().getString(R.string.message_no_internet_connection);
                cancel(true);
            } catch (Exception e) {
                Log.e(TAG, "Exception : "+e);
                respString = ctx.getResources().getString(R.string.message_unexpected_error_message_server);
                cancel(true);
            }
            return result;
        }

        @Override
        protected void onCancelled() {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            Toast.makeText(getBaseContext(), respString, Toast.LENGTH_LONG).show();
//            MessageUtils messageUtils = new MessageUtils(ctx);
//            messageUtils.snackBarMessage(LoginActivity.this,respString);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            reqChangePasswordTask = null;
            if (success) {
                if(!respString.isEmpty()){
                    try {
                        String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
                        final MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);
                        if(messageVO.getRc()==0){
                            Toast.makeText(getBaseContext(), messageVO.getOtherMessage(), Toast.LENGTH_LONG).show();
                            clearInput();
                        }
                        else{
                            Toast.makeText(getBaseContext(), messageVO.getMessageRc(), Toast.LENGTH_LONG).show();
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if(messageVO.getRc()==Constants.SESSION_EXPIRED||messageVO.getRc()==Constants.SESSION_DIFFERENT||messageVO.getRc()==Constants.USER_NOT_LOGIN){
                                        RedirectUtils redirectUtils = new RedirectUtils(ctx, ChangePasswordActivity.this);
                                        redirectUtils.redirectToLogin();
                                    }
                                }
                            }, Constants.REDIRECT_DELAY_LOGIN);
//                            MessageUtils messageUtils = new MessageUtils(ctx);
//                            messageUtils.snackBarMessage(LoginActivity.this,messageVO.getMessageRc());
                        }

                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), ChangePasswordActivity.this.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
//                        MessageUtils messageUtils = new MessageUtils(ctx);
//                        messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_message_server));
                    }
                }else{
                    Toast.makeText(getBaseContext(), ChangePasswordActivity.this.getResources().getString(R.string.message_unexpected_error_server), Toast.LENGTH_LONG).show();
//                    MessageUtils messageUtils = new MessageUtils(ctx);
//                    messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server));
                }
            }else{
                Toast.makeText(getBaseContext(), ChangePasswordActivity.this.getResources().getString(R.string.message_unexpected_error_server), Toast.LENGTH_LONG).show();
//                MessageUtils messageUtils = new MessageUtils(ctx);
//                messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server));
            }
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }

    private void clearInput(){
        _oldPassword.setText("");
        _newPassword.setText("");
    }
}
