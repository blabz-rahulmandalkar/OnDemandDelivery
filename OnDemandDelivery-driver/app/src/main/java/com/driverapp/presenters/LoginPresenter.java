package com.driverapp.presenters;

import com.driverapp.Constants;
import com.driverapp.Helper;
import com.driverapp.Utils;
import com.driverapp.interfaces.BaseContract;
import com.driverapp.interfaces.LoginContract;

/**
 * Created by bridgelabz on 25/07/18.
 */

public class LoginPresenter extends BasePresenter implements LoginContract.ILoginPresenter {

    LoginContract.ILoginView loginView;

    @Override
    public void attachView(BaseContract.IBaseView baseView){
        try {
            this.loginView = (LoginContract.ILoginView) baseView;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void dettachView(){
        this.loginView = null;
    }

    @Override
    public void getOTP(String mobileNumber) {
        if (Helper.isMobileNUmber(mobileNumber)){
            loginView.showOTPField();
        }else {
            loginView.showMessage(Constants.MSG_INVALID_MOBILE);
        }
    }

    @Override
    public void verifyOTP(String otp) {
        if (Helper.isOTP(otp)){
            //TODO: Open Dashboard
            loginView.openDashboard();
        }else {
            loginView.showMessage(Constants.MSG_INVALID_OTP);
        }
    }

    @Override
    public void resendOTP(String mobileNumber) {
        loginView.showMessage(Constants.MSG_RESENDING_OTP);
    }
}
