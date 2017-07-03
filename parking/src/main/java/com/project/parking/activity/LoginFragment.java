package com.project.parking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.parking.R;
import com.project.parking.adapter.CustomToast;
import com.project.parking.data.Constants;
import com.project.parking.data.InqLoginRequest;
import com.project.parking.data.LoginData;
import com.project.parking.data.MessageVO;
import com.project.parking.menu.MenuActivity;
import com.project.parking.service.ApiInterface;
import com.project.parking.util.ApiClient;
import com.project.parking.util.CipherUtil;
import com.project.parking.util.HttpClientUtil;
import com.project.parking.util.SharedPreferencesUtils;
import com.project.parking.util.Utils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements OnClickListener {
	private static final String TAG = "LoginFragment";
	private static View view;

//	private static EditText emailid, password;
	private static Button loginButton;
	private static TextView forgotPassword, signUp;
	private static CheckBox show_hide_password;
	private static LinearLayout loginLayout;
	private static Animation shakeAnimation;
	private static FragmentManager fragmentManager;

	@Bind(R.id.login_emailid) EditText emailText;
	@Bind(R.id.login_password) EditText password;

	public LoginFragment() {

	}

//	@Override
//	public void onBackPressed() {
//		// Disable going back to the MainFragment
//		moveTaskToBack(true);
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frame_login_layout, container, false);
		ButterKnife.bind(this, view);
		initViews();
		setListeners();
		return view;
	}

	// Initiate Views
	private void initViews() {
		fragmentManager = getActivity().getSupportFragmentManager();

//		emailid = (EditText) view.findViewById(R.id.login_emailid);
//		password = (EditText) view.findViewById(R.id.login_password);
		loginButton = (Button) view.findViewById(R.id.loginBtn);
		forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
		signUp = (TextView) view.findViewById(R.id.createAccount);
		show_hide_password = (CheckBox) view
				.findViewById(R.id.show_hide_password);
		loginLayout = (LinearLayout) view.findViewById(R.id.login_layout);

		// Load ShakeAnimation
		shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shake);

//		// Setting text selector over textviews
//		XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
//		try {
//			ColorStateList csl = ColorStateList.createFromXml(getResources(),
//					xrp);
//
//			forgotPassword.setTextColor(csl);
//			show_hide_password.setTextColor(csl);
//			signUp.setTextColor(csl);
//		} catch (Exception e) {
//		}
	}

	// Set Listeners
	private void setListeners() {
		loginButton.setOnClickListener(this);
		forgotPassword.setOnClickListener(this);
		signUp.setOnClickListener(this);

		// Set check listener over checkbox for showing and hiding password
		show_hide_password
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean isChecked) {
					// If it is checkec then show password else hide
					// password
					if (isChecked) {
						show_hide_password.setText(R.string.hide_pwd);// change checkbox text
						password.setInputType(InputType.TYPE_CLASS_TEXT);
						password.setTransformationMethod(HideReturnsTransformationMethod
								.getInstance());// show password
					} else {
						show_hide_password.setText(R.string.show_pwd);// change checkbox text
						password.setInputType(InputType.TYPE_CLASS_TEXT
								| InputType.TYPE_TEXT_VARIATION_PASSWORD);
						password.setTransformationMethod(PasswordTransformationMethod
								.getInstance());// hide password
					}
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginBtn:
			if(checkValidation())
				doLogin();
			break;

		case R.id.forgot_password:

			// Replace forgot password fragment with animation
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
					.replace(R.id.frameContainer,
							new ForgotPasswordFragment(),
							Constants.ForgotPassword_Fragment).commit();
			break;
		case R.id.createAccount:

			// Replace signup frgament with animation
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(R.anim.right_enter, R.anim.left_out)
					.replace(R.id.frameContainer, new SignUpFragment(),
							Constants.SignUp_Fragment).commit();
			break;
		}

	}

	// Check Validation before login
	private boolean checkValidation() {
		boolean valid = true;
		// Get email id and password
		String getEmailId = emailText.getText().toString();//emailid.getText().toString();
		String getPassword = password.getText().toString();

		// Check patter for email id
		Pattern p = Pattern.compile(Utils.regEx);

		Matcher m = p.matcher(getEmailId);

		// Check for both field is empty or not
		if (getEmailId.equals("") || getEmailId.length() == 0
				|| getPassword.equals("") || getPassword.length() == 0) {
			emailText.setError("Please enter your mail address..");
			password.setError("Please enter your password..");
			loginLayout.startAnimation(shakeAnimation);
			valid = false;
			new CustomToast().Show_Toast(getActivity(), view,
					"Enter both credentials.");

		}
		// Check if email id is valid or not
		else if (!m.find()){
			emailText.setError("enter a valid email address");
			valid = false;
			new CustomToast().Show_Toast(getActivity(), view,
					"Your Email Id is Invalid.");
		}
		// Else do login and do your stuff
		else{
			emailText.setError(null);
			password.setError(null);
			valid = true;
			Toast.makeText(getActivity(), "Do Login.", Toast.LENGTH_SHORT)
					.show();
		}
		return valid;
	}

	private void doLogin() {
		String getEmailId = emailText.getText().toString();//emailid.getText().toString();
		String getPassword = password.getText().toString();

		InqLoginRequest inqLoginRequest = new InqLoginRequest();
		inqLoginRequest.setEmail(getEmailId);
		inqLoginRequest.setPassword(getPassword);
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
		Call<String> call = apiService.doLogin(s);
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
						MessageVO messageVO = HttpClientUtil.getObjectMapper(getActivity()).readValue(respons, MessageVO.class);
						if(messageVO.getRc()==0){
							SharedPreferencesUtils.saveLoginData(messageVO.getOtherMessage(), getActivity());
							LoginData loginData = SharedPreferencesUtils.getLoginData(getActivity());
							Intent i = new Intent(getActivity(), MenuActivity.class);
							startActivity(i);
							getActivity().finish();
						}
						else{
							Toast.makeText(getActivity(), messageVO.getMessageRc(), Toast.LENGTH_LONG).show();
						}

					} catch (Exception e) {
						Toast.makeText(getActivity(), LoginFragment.this.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(getActivity(), LoginFragment.this.getResources().getString(R.string.message_unexpected_error_server), Toast.LENGTH_LONG).show();
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
