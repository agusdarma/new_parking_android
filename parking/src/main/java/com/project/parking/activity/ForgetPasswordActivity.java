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
import android.widget.TextView;
import android.widget.Toast;

import com.project.parking.R;
import com.project.parking.data.InqLoginRequest;
import com.project.parking.data.MessageVO;
import com.project.parking.util.CipherUtil;
import com.project.parking.util.HttpClientUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Yohanes on 14/06/2017.
 */

public class ForgetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private Context ctx;
    private ReqForgotPasswordTask reqForgotPasswordTask = null;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.btn_forget_password) Button _forgetPasswordButton;
    @Bind(R.id.link_login) TextView _loginLink;
//    @Bind(R.id.link_forgot_password) TextView _forgotPasswordLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ctx = ForgetPasswordActivity.this;
        ButterKnife.bind(this);

        _forgetPasswordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                forgetPassword();
            }
        });

//        _forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // Start the Signup activity
//                Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
//                startActivityForResult(intent, REQUEST_SIGNUP);
//                finish();
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//            }
//        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void forgetPassword() {
        Log.d(TAG, "Forget Password");

        if (!validate()) {
//            onLoginFailed();
            return;
        }

        _forgetPasswordButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ForgetPasswordActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        String email = _emailText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onProcessingSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
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
        moveTaskToBack(true);
    }

    public void onProcessingSuccess() {
        _forgetPasswordButton.setEnabled(true);

        Toast.makeText(getBaseContext(), "Submitted..", Toast.LENGTH_LONG).show();

        String email = _emailText.getText().toString();

        // login user
        reqForgotPasswordTask = new ReqForgotPasswordTask();
        reqForgotPasswordTask.execute(email);
    }

//    public void onLoginFailed() {
//        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
//
//        _forgetPasswordButton.setEnabled(true);
//    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
//        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

//        if (password.isEmpty() || password.length() < 3 ) { //|| password.length() > 10
//            _passwordText.setError("min 6 alphanumeric characters");
//            valid = false;
//        } else {
//            _passwordText.setError(null);
//        }

        return valid;
    }

    public class ReqForgotPasswordTask  extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progressDialog = null;
        private final HttpClient client = HttpClientUtil.getNewHttpClient();
        String respString = null;

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ForgetPasswordActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(ctx.getResources().getString(R.string.progress_dialog));
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(String... arg0) {
            boolean result = false;
            try {
                InqLoginRequest inqLoginRequest = new InqLoginRequest();
                String email = arg0[0];
                inqLoginRequest.setEmail(email);
                String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(inqLoginRequest);
                s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
                Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);
                HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_FORGOT_PASSWORD);
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
            reqForgotPasswordTask = null;
            if (success) {
                if(!respString.isEmpty()){
                    try {
                        String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
                        MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);
                        if(messageVO.getRc()==0){
                            Intent i = new Intent(ctx, LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast.makeText(getBaseContext(), messageVO.getMessageRc(), Toast.LENGTH_LONG).show();
//                            MessageUtils messageUtils = new MessageUtils(ctx);
//                            messageUtils.snackBarMessage(LoginActivity.this,messageVO.getMessageRc());
                        }

                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), ForgetPasswordActivity.this.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
//                        MessageUtils messageUtils = new MessageUtils(ctx);
//                        messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_message_server));
                    }
                }else{
                    Toast.makeText(getBaseContext(), ForgetPasswordActivity.this.getResources().getString(R.string.message_unexpected_error_server), Toast.LENGTH_LONG).show();
//                    MessageUtils messageUtils = new MessageUtils(ctx);
//                    messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server));
                }
            }else{
                Toast.makeText(getBaseContext(), ForgetPasswordActivity.this.getResources().getString(R.string.message_unexpected_error_server), Toast.LENGTH_LONG).show();
//                MessageUtils messageUtils = new MessageUtils(ctx);
//                messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server));
            }
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
}
