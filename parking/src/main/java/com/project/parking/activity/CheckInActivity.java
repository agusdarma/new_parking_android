package com.project.parking.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.parking.R;
import com.project.parking.data.BookingVO;
import com.project.parking.data.Constants;
import com.project.parking.data.LoginData;
import com.project.parking.data.MessageVO;
import com.project.parking.service.ApiInterface;
import com.project.parking.util.ApiClient;
import com.project.parking.util.CipherUtil;
import com.project.parking.util.HttpClientUtil;
import com.project.parking.util.RedirectUtils;
import com.project.parking.util.SharedPreferencesUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yohanes on 04/07/2017.
 */

public class CheckInActivity extends AppCompatActivity {
    private static final String TAG = CheckInActivity.class.getSimpleName();
    private Context ctx;

//    private EditText bookingCode;
//    private Button btnCheckIn;
//    private Button btnCheckInOk;
    private ScrollView resultScrollView;
    private TextView bookingName;
    private TextView bookingPhone;
    private TextView bookingEmail;
    private TextView bookingMall;
    private TextView bookingId;
    private TextView bookingDate;
    private TextView bookingStatus;
    private String email;
    private String sessionkey;
    private String bookingCodeInput;

    @Bind(R.id.bookingCode) EditText bookingCode;
    @Bind(R.id.btnCheckIn) Button btnCheckIn;
    @Bind(R.id.btnCheckInOk) Button btnCheckInOk;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        ctx = CheckInActivity.this;
        ButterKnife.bind(this);

        btnCheckIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!validateBookingCode()) {
                    onFailed();
                    return;
                }
                doCheckIn();
            }
        });

        btnCheckInOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!validateBookingCode()) {
                    onFailed();
                    return;
                }
                doCheckInOk();
            }
        });

    }

    public void onFailed() {
        Toast.makeText(getBaseContext(), ctx.getResources().getString(R.string.message_booking_code_required), Toast.LENGTH_LONG).show();

        btnCheckIn.setEnabled(true);
    }

    public boolean validateBookingCode(){
        boolean valid = true;

        String bookingCodes = bookingCode.getText().toString();
        if (bookingCodes.isEmpty() || bookingCodes.length() < 3 ) {
            bookingCode.setError("min 6 alphanumeric characters");
            valid = false;
        } else {
            bookingCode.setError(null);
        }
        return valid;
    }

    public void doCheckIn() {
        Toast.makeText(getBaseContext(), ctx.getResources().getString(R.string.message_confirm_check_kode_booking), Toast.LENGTH_LONG).show();
        Log.d(TAG, "Check In..");

        bookingCodeInput = bookingCode.getText().toString();
        LoginData loginData = SharedPreferencesUtils.getLoginData(ctx);
        // ambil dari session untuk email, session key
        email = loginData.getEmail();
        sessionkey = loginData.getSessionKey();

        BookingVO bookingVO = new BookingVO();
        bookingVO.setEmail(email);
        bookingVO.setSessionKey(sessionkey);
        bookingVO.setBookingCode(bookingCodeInput);
        String s = null;
        try {
            s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(bookingVO);
        } catch (IOException e) {
            Log.e(TAG, "IOException : "+e);
            e.printStackTrace();
        }
        s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
        Log.d(TAG,"Request: " + s);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<String> call = apiService.checkBookingCode(s);
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
                            BookingVO bookingVO = HttpClientUtil.getObjectMapper(ctx).readValue(messageVO.getOtherMessage(), BookingVO.class);
                            bookingName.setText(bookingVO.getName());
                            bookingPhone.setText(bookingVO.getPhoneNo());
                            bookingEmail.setText(bookingVO.getEmail());
                            bookingMall.setText(bookingVO.getMallName());
                            bookingId.setText(bookingVO.getBookingId());
                            bookingDate.setText(bookingVO.getBookingDateValue());
                            bookingStatus.setText(bookingVO.getBookingStatusValue());
                            resultScrollView.setVisibility(View.VISIBLE);
                        }
                        else{
                            Toast.makeText(getBaseContext(), messageVO.getMessageRc(), Toast.LENGTH_LONG).show();
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if(messageVO.getRc()==Constants.SESSION_EXPIRED||messageVO.getRc()==Constants.SESSION_DIFFERENT||messageVO.getRc()==Constants.USER_NOT_LOGIN){
                                        RedirectUtils redirectUtils = new RedirectUtils(ctx, CheckInActivity.this);
                                        redirectUtils.redirectToLogin();
                                    }
                                }
                            }, Constants.REDIRECT_DELAY_LOGIN);
                        }

                    } catch (Exception e) {
                        Toast.makeText(ctx, CheckInActivity.this.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
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



    public void doCheckInOk() {
        Toast.makeText(getBaseContext(), ctx.getResources().getString(R.string.message_confirm_check_in), Toast.LENGTH_LONG).show();
        Log.d(TAG, "Check In Ok..");

        bookingCodeInput = bookingCode.getText().toString();
        LoginData loginData = SharedPreferencesUtils.getLoginData(ctx);
        // ambil dari session untuk email, session key
        email = loginData.getEmail();
        sessionkey = loginData.getSessionKey();

        BookingVO bookingVO = new BookingVO();
        bookingVO.setEmail(email);
        bookingVO.setSessionKey(sessionkey);
        bookingVO.setBookingCode(bookingCodeInput);
        String s = null;
        try {
            s = HttpClientUtil.getObjectMapper(ctx).writeValueAsString(bookingVO);
        } catch (IOException e) {
            Log.e(TAG, "IOException : "+e);
            e.printStackTrace();
        }
        s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
        Log.d(TAG,"Request: " + s);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<String> call = apiService.confirmCodeBooking(s);
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
//                            MessageUtils messageUtils = new MessageUtils(ctx);
//                            messageUtils.snackBarMessage(getActivity(),messageVO.getOtherMessage());
                            Toast.makeText(getBaseContext(), messageVO.getOtherMessage(), Toast.LENGTH_LONG).show();
                            resultScrollView.setVisibility(View.GONE);
                        }
                        else{
                            Toast.makeText(getBaseContext(), messageVO.getMessageRc(), Toast.LENGTH_LONG).show();
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    if(messageVO.getRc()==Constants.SESSION_EXPIRED||messageVO.getRc()==Constants.SESSION_DIFFERENT||messageVO.getRc()==Constants.USER_NOT_LOGIN){
                                        RedirectUtils redirectUtils = new RedirectUtils(ctx, CheckInActivity.this);
                                        redirectUtils.redirectToLogin();
                                    }
                                }
                            }, Constants.REDIRECT_DELAY_LOGIN);
                        }

                    } catch (Exception e) {
                        Toast.makeText(ctx, CheckInActivity.this.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
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
