package com.project.parking.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.project.parking.data.InqSignupRequest;
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

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_mobile) EditText _mobileText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;

    private Context ctx;
    private ReqRegistrationTask reqRegistrationTask = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        ctx = SignupActivity.this;

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

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

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

//        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Creating Account...");
//        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
//                        progressDialog.dismiss();
                    }
                }, 0);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

//        Toast.makeText(getBaseContext(), "Processing Signup...", Toast.LENGTH_LONG).show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        // register user
        reqRegistrationTask = new ReqRegistrationTask();
        reqRegistrationTask.execute(name, email, mobile, password, reEnterPassword);

    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length()<10) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }

    public class ReqRegistrationTask  extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progressDialog = null;
        private final HttpClient client = HttpClientUtil.getNewHttpClient();
        String respString = null;
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SignupActivity.this,
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
                InqSignupRequest inqRegRequest = new InqSignupRequest();
                inqRegRequest.setName(arg0[0]);
                inqRegRequest.setEmail(arg0[1]);
                inqRegRequest.setPhoneNo(arg0[2]);
                inqRegRequest.setPassword(arg0[3]);
                inqRegRequest.setConfirmPassword(arg0[4]);
                String s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(inqRegRequest);
                s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
                Log.d(TAG,"Request: " + s);
                StringEntity entity = new StringEntity(s);
                HttpPost post = new HttpPost(HttpClientUtil.URL_BASE+HttpClientUtil.URL_USER_REG);
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
//            messageUtils.snackBarMessage(RegisterActivity.this,respString);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            reqRegistrationTask = null;
            if (success) {
                if(!respString.isEmpty()){
                    try {
                        String respons = CipherUtil.decryptTripleDES(respString, CipherUtil.PASSWORD);
                        MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);
                        if(messageVO.getRc()==0){
                            Toast.makeText(getBaseContext(), ctx.getResources().getString(R.string.message_register_success), Toast.LENGTH_LONG).show();
                            Intent i = new Intent(ctx, LoginActivity.class);
                            startActivity(i);
                            finish();
//                            MessageUtils messageUtils = new MessageUtils(ctx);
//                            messageUtils.showDialogInfoCallback(ctx.getResources().getString(R.string.message_register_title), ctx.getResources().getString(R.string.message_register_success), buttonCallback);
                        }
                        else{
                            Toast.makeText(getBaseContext(), messageVO.getMessageRc(), Toast.LENGTH_LONG).show();
//                            MessageUtils messageUtils = new MessageUtils(ctx);
//                            messageUtils.snackBarMessage(RegisterActivity.this,messageVO.getMessageRc());
                        }

                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), ctx.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
//                        MessageUtils messageUtils = new MessageUtils(ctx);
//                        messageUtils.snackBarMessage(RegisterActivity.this,RegisterActivity.this.getResources().getString(R.string.message_unexpected_error_message_server));
                    }
                }else{
                    Toast.makeText(getBaseContext(), ctx.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
//                    MessageUtils messageUtils = new MessageUtils(ctx);
//                    messageUtils.snackBarMessage(RegisterActivity.this,RegisterActivity.this.getResources().getString(R.string.message_unexpected_error_server));
                }
            }else{
                Toast.makeText(getBaseContext(), ctx.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
//                MessageUtils messageUtils = new MessageUtils(ctx);
//                messageUtils.snackBarMessage(RegisterActivity.this,RegisterActivity.this.getResources().getString(R.string.message_unexpected_error_server));
            }
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
    }

}