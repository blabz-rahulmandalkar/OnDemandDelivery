package com.driverapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Helper {

    public static boolean isMobileNUmber(String mobileNumber){
        String regex = "\\d{10}";
        return Pattern.matches(regex,mobileNumber);
    }

    public static boolean isOTP(String otp){
        String regex = "\\d{4}";
        return Pattern.matches(regex,otp);
    }


}
