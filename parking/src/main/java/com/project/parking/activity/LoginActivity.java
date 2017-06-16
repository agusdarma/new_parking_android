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
import com.project.parking.menu.MenuActivity;
import com.project.parking.data.InqLoginRequest;
import com.project.parking.data.LoginData;
import com.project.parking.data.MessageVO;
import com.project.parking.util.CipherUtil;
import com.project.parking.util.HttpClientUtil;
import com.project.parking.util.SharedPreferencesUtils;

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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_FORGOT_PASSWORD = 0;

    private Context ctx;
    private ReqLoginTask reqLoginTask = null;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    @Bind(R.id.link_forgot_password) TextView _forgotPasswordLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx = LoginActivity.this;
        ButterKnife.bind(this);
        
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _forgotPasswordLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                startActivityForResult(intent, REQUEST_FORGOT_PASSWORD);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

//        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
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
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
//        Toast.makeText(getBaseContext(), "Login Success", Toast.LENGTH_LONG).show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // login user
        reqLoginTask = new ReqLoginTask();
        reqLoginTask.execute(email, password);

    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 3 ) { //|| password.length() > 10
            _passwordText.setError("min 6 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public class ReqLoginTask  extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progressDialog = null;
        private final HttpClient client = HttpClientUtil.getNewHttpClient();
        String respString = null;

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(ctx.getResources().getString(R.string.progress_dialog));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(String... arg0) {
            boolean result = false;
            try {
                InqLoginRequest inqLoginRequest = new InqLoginRequest();
                String email = arg0[0];
                String password = arg0[1];
                inqLoginRequest.setEmail(email);
                inqLoginRequest.setPassword(password);
                String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(inqLoginRequest);
                s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
                Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);
                HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_LOGIN);
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
            reqLoginTask = null;
            if (success) {
                if(!respString.isEmpty()){
                    try {
                        String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
                        MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);
                        if(messageVO.getRc()==0){
                            SharedPreferencesUtils.saveLoginData(messageVO.getOtherMessage(), ctx);
                            LoginData loginData = SharedPreferencesUtils.getLoginData(ctx);
                            Intent i = new Intent(ctx, MenuActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast.makeText(getBaseContext(), messageVO.getMessageRc(), Toast.LENGTH_LONG).show();
//                            MessageUtils messageUtils = new MessageUtils(ctx);
//                            messageUtils.snackBarMessage(LoginActivity.this,messageVO.getMessageRc());
                        }

                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), LoginActivity.this.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
//                        MessageUtils messageUtils = new MessageUtils(ctx);
//                        messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_message_server));
                    }
                }else{
                    Toast.makeText(getBaseContext(), LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server), Toast.LENGTH_LONG).show();
//                    MessageUtils messageUtils = new MessageUtils(ctx);
//                    messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server));
                }
            }else{
                Toast.makeText(getBaseContext(), LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server), Toast.LENGTH_LONG).show();
//                MessageUtils messageUtils = new MessageUtils(ctx);
//                messageUtils.snackBarMessage(LoginActivity.this,LoginActivity.this.getResources().getString(R.string.message_unexpected_error_server));
            }
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }
}
