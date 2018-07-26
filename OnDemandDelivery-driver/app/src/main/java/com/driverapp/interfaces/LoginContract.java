package com.driverapp.interfaces;

/**
 * Created by bridgelabz on 25/07/18.
 */

public interface LoginContract extends BaseContract {

    public interface ILoginView extends IBaseView {
        public void showOTPField();
        public void showMobileField();
        public void openDashboard();
    }

    public interface ILoginPresenter extends IBasePresenter{
        public void getOTP(String mobileNumber);
        public void verifyOTP(String otp);
        public void resendOTP(String mobileNumber);
    }

}
