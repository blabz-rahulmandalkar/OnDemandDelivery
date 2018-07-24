package com.driverapp.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.driverapp.Constants;
import com.driverapp.R;
import com.driverapp.Utils;

public class LoginActivity extends BaseActivity {

    TextView tvIconTitle;
    EditText etPhoneNumber,etOTP;
    Button btnLogin,btnResendOTP;
    ProgressDialog progressDialog;

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
        tvIconTitle = findViewById(R.id.login_logo_view_tv_app_name);
        tvIconTitle.setText(Html.fromHtml(Constants.APP_NAME_ATTRIBUTED));
        etPhoneNumber = findViewById(R.id.login_et_phonenumber);
        etOTP = findViewById(R.id.login_et_otp);
        btnLogin = findViewById(R.id.login_btn_login);
        btnResendOTP = findViewById(R.id.login_btn_resend_otp);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
    }

    @Override
    public void setListeners() {
        super.setListeners();
        btnLogin.setOnClickListener(this);
        btnResendOTP.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.login_btn_login:
                Utils.showToast(this,"Pressed login");
                break;
            case R.id.login_btn_resend_otp:
                Utils.showToast(this,"Pressed resend login");
                break;
        }
    }
}
