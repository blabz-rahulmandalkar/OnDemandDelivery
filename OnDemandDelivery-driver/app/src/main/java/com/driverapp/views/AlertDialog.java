package com.driverapp.views;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.driverapp.R;


/**
 * Created by bridgelabz on 03/07/18.
 */

public class AlertDialog extends Dialog implements View.OnClickListener{
    private Activity activity;
    private TextView textView;
    private Button acceptBtn,rejectBtn;
    private String message;
    private ClickListener clickListener;

    public AlertDialog(@NonNull Activity activity, String message, ClickListener clickListener) {
        super(activity);
        this.activity = activity;
        this.message = message;
        this.clickListener = clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);
        acceptBtn =findViewById(R.id.dialog_layout_btn_accept);
        rejectBtn =findViewById(R.id.dialog_layout_btn_reject);

        textView =findViewById(R.id.dialog_layout_title);
        textView.setText(message);
        acceptBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_layout_btn_accept:
                dismiss();
                clickListener.onAccepted(v);
                break;
            case R.id.dialog_layout_btn_reject:
                dismiss();
                clickListener.onRejected(v);
                break;
        }
    }
    public interface ClickListener{
        public void onAccepted(View view);
        public void onRejected(View view);

    }
}
