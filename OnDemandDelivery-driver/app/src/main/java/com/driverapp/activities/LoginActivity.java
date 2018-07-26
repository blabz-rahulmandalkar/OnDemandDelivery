package com.driverapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.driverapp.Constants;
import com.driverapp.R;
import com.driverapp.Utils;
import com.driverapp.interfaces.LoginContract;
import com.driverapp.presenters.LoginPresenter;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

public class LoginActivity extends BaseActivity implements LoginContract.ILoginView {

    TextView tvIconTitle;
    EditText etPhoneNumber,etOTP;
    Button btnLogin,btnResendOTP;
    RelativeLayout rlOTP,rlMobile;
    ProgressDialog progressDialog;
    SmsVerifyCatcher smsVerifyCatcher;
    LoginContract.ILoginPresenter loginPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialiseViews();
        setListeners();
    }

    @Override
    public void initialiseViews() {
        super.initialiseViews();
        loginPresenter = new LoginPresenter();
        loginPresenter.attachView(this);
        tvIconTitle = findViewById(R.id.login_logo_view_tv_app_name);
        tvIconTitle.setText(Html.fromHtml(Constants.APP_NAME_ATTRIBUTED));
        etPhoneNumber = findViewById(R.id.login_et_phonenumber);
        etOTP = findViewById(R.id.login_et_otp);
        btnLogin = findViewById(R.id.login_btn_login);
        btnLogin.setText(Constants.TXT_GET_OTP);
        btnResendOTP = findViewById(R.id.login_btn_resend_otp);
        btnResendOTP.setText(Constants.TXT_RESEND_OTP);
        rlOTP = findViewById(R.id.login_otp_view_layout);
        rlMobile = findViewById(R.id.login_mobile_view_layout);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
    }

    @Override
    public void setListeners() {
        super.setListeners();
        btnLogin.setOnClickListener(this);
        btnResendOTP.setOnClickListener(this);
        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                //TODO: Handle Incoming SMS
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.login_btn_login:
                    switch (btnLogin.getText().toString()){
                        case Constants.TXT_GET_OTP:
                            loginPresenter.getOTP(etPhoneNumber.getText().toString());
                            break;
                        case Constants.TXT_LOGIN:
                            loginPresenter.verifyOTP(etOTP.getText().toString());
                            break;
                    }
                    break;
            case R.id.login_btn_resend_otp:
                    loginPresenter.resendOTP(etPhoneNumber.getText().toString());
                    break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void showDialog(String message) {

    }

    @Override
    public void hideDialog(String message) {

    }

    @Override
    public void showMessage(String message) {
        Utils.showToast(this,message);
    }

    @Override
    public void showOTPField() {
        rlOTP.setVisibility(View.VISIBLE);
        btnResendOTP.setVisibility(View.VISIBLE);
        btnLogin.setText(Constants.TXT_LOGIN);
        etOTP.requestFocus();
    }

    @Override
    public void showMobileField() {
        rlOTP.setVisibility(View.GONE);
        btnResendOTP.setVisibility(View.GONE);
        btnLogin.setText(Constants.TXT_GET_OTP);
        etPhoneNumber.requestFocus();
    }

    @Override
    public void openDashboard() {
        Intent intent = new Intent(LoginActivity.this,HomeActivity2.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenter.dettachView();
    }
}
