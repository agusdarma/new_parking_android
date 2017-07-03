package com.project.parking.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.project.parking.R;
import com.project.parking.data.Constants;
import com.project.parking.data.InqChangePasswordRequest;
import com.project.parking.data.MessageVO;
import com.project.parking.service.ApiInterface;
import com.project.parking.util.ApiClient;
import com.project.parking.util.CipherUtil;
import com.project.parking.util.HttpClientUtil;
import com.project.parking.util.RedirectUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yohanes on 30/06/2017.
 */

public class RefreshingMallActivity extends AppCompatActivity {
    private static final String TAG = "RefreshingMallActivity";
    private Context ctx;

    @Bind(R.id.changePasswordBtn) Button _refreshButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ctx = RefreshingMallActivity.this;
        ButterKnife.bind(this);

        _refreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                refreshingMall();
            }
        });
    }

    public void refreshingMall() {
        Log.d(TAG, "Refreshing Mall");

        _refreshButton.setEnabled(false);

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
    public void onBackPressed() {
        // Disable going back to the MainFragment
//        moveTaskToBack(true);
        RedirectUtils redirectUtils = new RedirectUtils(ctx, RefreshingMallActivity.this);
        redirectUtils.redirectToMainMenu();
    }

    public void onProcessingSuccess() {
        _refreshButton.setEnabled(true);

        Toast.makeText(getBaseContext(), "Processing..", Toast.LENGTH_LONG).show();

        // do refreshing mall
        doRefreshingMall();
    }

    public void onFailed() {
        Toast.makeText(getBaseContext(), ctx.getResources().getString(R.string.message_password_required), Toast.LENGTH_LONG).show();

        _refreshButton.setEnabled(true);
    }

    public void doRefreshingMall(){
        InqChangePasswordRequest inqChangePasswordRequest = new InqChangePasswordRequest();
        String s = null;
        try {
            s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(inqChangePasswordRequest);
        } catch (IOException e) {
            Log.e(TAG, "IOException : "+e);
            e.printStackTrace();
        }
        s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
        Log.d(TAG,"Request: " + s);


        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<String> call = apiService.doRefreshingMall(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String>call, Response<String> response) {
                Log.d(TAG, "Response: " + response.body().toString());
//				Toast.makeText(getActivity(), "Berhasil Masukk...", Toast.LENGTH_LONG).show();
                if(!response.body().toString().isEmpty()){
                    try {
                        String respons = CipherUtil.decryptTripleDES(response.body().toString(), CipherUtil.PASSWORD);
                        final MessageVO messageVO = HttpClientUtil.getObjectMapper(ctx).readValue(respons, MessageVO.class);
                        if(messageVO.getRc()==0){
                            Toast.makeText(getBaseContext(), messageVO.getOtherMessage(), Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getBaseContext(), messageVO.getMessageRc(), Toast.LENGTH_LONG).show();
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if(messageVO.getRc()==Constants.SESSION_EXPIRED||messageVO.getRc()==Constants.SESSION_DIFFERENT||messageVO.getRc()==Constants.USER_NOT_LOGIN){
                                        RedirectUtils redirectUtils = new RedirectUtils(ctx, RefreshingMallActivity.this);
                                        redirectUtils.redirectToLogin();
                                    }
                                }
                            }, Constants.REDIRECT_DELAY_LOGIN);
//                            MessageUtils messageUtils = new MessageUtils(ctx);
//                            messageUtils.snackBarMessage(LoginActivity.this,messageVO.getMessageRc());
                        }

                    } catch (Exception e) {
                        Toast.makeText(ctx, RefreshingMallActivity.this.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ctx, ctx.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
//                    Toast.makeText(getActivity(), SignUpFragment.this.getResources().getString(R.string.message_unexpected_error_server), Toast.LENGTH_LONG).show();
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

}
