package com.driverapp.interfaces;

/**
 * Created by bridgelabz on 25/07/18.
 */

public interface BaseContract {

    public interface IBaseView {
        public void showDialog(String message);
        public void hideDialog(String message);
        public void showMessage(String message);
    }

    public interface IBasePresenter {
        public void attachView(IBaseView baseView);
        public void dettachView();
    }

}
