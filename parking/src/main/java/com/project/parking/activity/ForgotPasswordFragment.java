package com.project.parking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.parking.MainFragment;
import com.project.parking.R;
import com.project.parking.adapter.CustomToast;
import com.project.parking.data.InqLoginRequest;
import com.project.parking.data.MessageVO;
import com.project.parking.service.ApiInterface;
import com.project.parking.util.ApiClient;
import com.project.parking.util.CipherUtil;
import com.project.parking.util.HttpClientUtil;
import com.project.parking.util.Utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordFragment extends Fragment implements
		OnClickListener {
	private static final String TAG = "ForgotPasswordFragment";
	private static View view;

	private static EditText emailId;
	private static TextView submit, back;

	public ForgotPasswordFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frame_forgot_password_layout, container,
				false);
		initViews();
		setListeners();
		return view;
	}

	// Initialize the views
	private void initViews() {
		emailId = (EditText) view.findViewById(R.id.registered_emailid);
		submit = (TextView) view.findViewById(R.id.forgot_button);
		back = (TextView) view.findViewById(R.id.backToLoginBtn);

		// Setting text selector over textviews
//		XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
//		try {
//			ColorStateList csl = ColorStateList.createFromXml(getResources(),
//					xrp);
//
//			back.setTextColor(csl);
//			submit.setTextColor(csl);
//
//		} catch (Exception e) {
//		}

	}

	// Set Listeners over buttons
	private void setListeners() {
		back.setOnClickListener(this);
		submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backToLoginBtn:

			// Replace Login Fragment on Back Presses
			new MainFragment().replaceLoginFragment();
			break;

		case R.id.forgot_button:
			// validate email
			validateEmailId();

			// Call Submit button task
			submitButtonTask();
			break;

		}

	}

	private boolean validateEmailId() {
		boolean valid  = true;

		String getEmailId = emailId.getText().toString();

		// Pattern for email id validation
		Pattern p = Pattern.compile(Utils.regEx);

		// Match the pattern
		Matcher m = p.matcher(getEmailId);

		// First check if email id is not null else show error toast
		if (getEmailId.equals("") || getEmailId.length() == 0){
			valid = false;
			new CustomToast().Show_Toast(getActivity(), view,
					"Please enter your Email Id.");
		}
		// Check if email id is valid or not
		else if (!m.find()){
			valid = false;
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");
		}
		// Else submit email id and fetch passwod or do your stuff
		else
			Toast.makeText(getActivity(), "Get Forgot Password.",
					Toast.LENGTH_SHORT).show();

		return valid;
	}

	private void submitButtonTask() {
		String getEmailId = emailId.getText().toString();

		InqLoginRequest inqLoginRequest = new InqLoginRequest();
		String email = getEmailId;
		inqLoginRequest.setEmail(email);
		String s = null;
		try {
			s = HttpClientUtil.getObjectMapper(getActivity()).writeValueAsString(inqLoginRequest);
		} catch (IOException e) {
			Log.e(TAG, "IOException : "+e);
			e.printStackTrace();
		}
		s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
		Log.d(TAG,"Request: " + s);

		ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
		Call<String> call = apiService.doForgetPassword(s);
		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String>call, Response<String> response) {
				Log.d(TAG, "Response: " + response.body().toString());
//				Toast.makeText(getActivity(), "Berhasil Masukk...", Toast.LENGTH_LONG).show();
				if(!response.body().toString().isEmpty()){
					try {
						String respons = CipherUtil.decryptTripleDES(response.body().toString(), CipherUtil.PASSWORD);
						MessageVO messageVO = HttpClientUtil.getObjectMapper(getActivity()).readValue(respons, MessageVO.class);
						if(messageVO.getRc()==0){
							Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.message_register_success), Toast.LENGTH_LONG).show();
							Intent i = new Intent(getActivity(), LoginFragment.class);
							startActivity(i);
							getActivity().finish();
						}
						else{
							Toast.makeText(getActivity(), messageVO.getMessageRc(), Toast.LENGTH_LONG).show();
						}

					} catch (Exception e) {
						Toast.makeText(getActivity(), ForgotPasswordFragment.this.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
//                    Toast.makeText(getActivity(), SignUpFragment.this.getResources().getString(R.string.message_unexpected_error_server), Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(Call<String>call, Throwable t) {
				// Log error here since request failed
				Log.e(TAG, t.toString());
				Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
			}
		});
	}
}