package com.project.parking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.parking.MainFragment;
import com.project.parking.R;
import com.project.parking.adapter.CustomToast;
import com.project.parking.data.Constants;
import com.project.parking.data.InqSignupRequest;
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

public class SignUpFragment extends Fragment implements OnClickListener {
    private static final String TAG = "SignUpFragment";
	private static View view;
	private static EditText fullName, emailId, mobileNumber, location,
			password, confirmPassword; //notUsed.
	private static TextView login;
	private static Button signUpButton;
	private static CheckBox terms_conditions;

//	@Bind(R.id.fullName) EditText fullName;
//	@Bind(R.id.userEmailId) EditText emailId;
//	@Bind(R.id.mobileNumber) EditText mobileNumber;
//	@Bind(R.id.password) EditText password;
//	@Bind(R.id.confirmPassword) EditText confirmPassword;
//	@Bind(R.id.btn_signup) Button signupButton;
//	@Bind(R.id.link_login) TextView loginLink;

	public SignUpFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frame_signup_layout, container, false);
		initViews();
		setListeners();
		return view;
	}

	// Initialize all views
	private void initViews() {
		fullName = (EditText) view.findViewById(R.id.fullName); //notUsed.
        emailId = (EditText) view.findViewById(R.id.userEmailId); //notUsed.
        mobileNumber = (EditText) view.findViewById(R.id.mobileNumber); //notUsed.
//        location = (EditText) view.findViewById(R.id.location); //notUsed.
        password = (EditText) view.findViewById(R.id.password); //notUsed.
        confirmPassword = (EditText) view.findViewById(R.id.confirmPassword); //notUsed.
		signUpButton = (Button) view.findViewById(R.id.signUpBtn);
		login = (TextView) view.findViewById(R.id.already_user);
		terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);

		// Setting text selector over textviews
//		XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
//		try {
//			ColorStateList csl = ColorStateList.createFromXml(getResources(),
//					xrp);

//			login.setTextColor(csl);
//			terms_conditions.setTextColor(csl);
//		} catch (Exception e) {
//		}
	}

	// Set Listeners
	private void setListeners() {
		signUpButton.setOnClickListener(this);
		login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.signUpBtn:

			// Call checkValidation method
			checkValidation();

            // call process signup
            signupProcess();

			break;

		case R.id.already_user:

			// Replace login fragment
			new MainFragment().replaceLoginFragment();
			break;
		}

	}

	// Check Validation Method
	private boolean checkValidation() {
        boolean valid = true;
		// Get all edittext texts
		String getFullName = fullName.getText().toString();
		String getEmailId = emailId.getText().toString();
		String getMobileNumber = mobileNumber.getText().toString();
//		String getLocation = location.getText().toString(); //notUsed.
		String getPassword = password.getText().toString();
		String getConfirmPassword = confirmPassword.getText().toString();

		// Pattern match for email id
		Pattern p = Pattern.compile(Utils.regEx);
		Matcher m = p.matcher(getEmailId);

		// Check if all strings are null or not
		if (getFullName.equals("") || getFullName.length() == 0
				|| getEmailId.equals("") || getEmailId.length() == 0
				|| getMobileNumber.equals("") || getMobileNumber.length() == 0
//				|| getLocation.equals("") || getLocation.length() == 0 //notUsed.
				|| getPassword.equals("") || getPassword.length() == 0
				|| getConfirmPassword.equals("")
				|| getConfirmPassword.length() == 0){
            valid = false;
            new CustomToast().Show_Toast(getActivity(), view,
                    "All fields are required.");
        }

		// Check if email id valid or not
		else if (!m.find()){
            valid = false;
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Email Id is Invalid.");
        }

        // Check length of the password
        else if ( getPassword.length() < Constants.LENGTH_OF_THE_PASSWORD){
            valid = false;
            new CustomToast().Show_Toast(getActivity(), view,
                    "min 6 alphanumeric characters");
        }

        // Check length of the new password
        else if ( getPassword.length() < Constants.LENGTH_OF_THE_PASSWORD){
            valid = false;
            new CustomToast().Show_Toast(getActivity(), view,
                    "min 6 alphanumeric characters");
        }

		// Check if both password should be equal
		else if (!getConfirmPassword.equals(getPassword)){
            valid = false;
            new CustomToast().Show_Toast(getActivity(), view,
                    "Both password doesn't match.");
        }

		// Make sure user should check Terms and Conditions checkbox
		else if (!terms_conditions.isChecked()){
            valid = false;
            new CustomToast().Show_Toast(getActivity(), view,
                    "Please select Terms and Conditions.");
        }

		// Else do signup or do your stuff
		else
			Toast.makeText(getActivity(), "Do SignUp.", Toast.LENGTH_SHORT)
					.show();

        return valid;

	}

    private void signupProcess() {
        String getFullName = fullName.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();

        InqSignupRequest inqRegRequest = new InqSignupRequest();
        inqRegRequest.setName(getFullName);
        inqRegRequest.setEmail(getEmailId);
        inqRegRequest.setPhoneNo(getMobileNumber);
        inqRegRequest.setPassword(getPassword);
        inqRegRequest.setConfirmPassword(getConfirmPassword);
        String s = null;
        try {
            s = HttpClientUtil.getObjectMapper(getActivity()).writeValueAsString(inqRegRequest);
        } catch (IOException e) {
            Log.e(TAG, "IOException : "+e);
            e.printStackTrace();
        }
        s = CipherUtil.encryptTripleDES(s, CipherUtil.PASSWORD);
        Log.d(TAG,"Request: " + s);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<String> call = apiService.doSignUp(s);
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
                        Toast.makeText(getActivity(), SignUpFragment.this.getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.message_unexpected_error_message_server), Toast.LENGTH_LONG).show();
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
