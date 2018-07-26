package com.driverapp.services;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;
import java.util.Locale;

public class AddressFinder {
    private LatLng locationLatLong;
    private Listener listener;
    private Activity activity;
    public AddressFinder(Activity activity, LatLng locationLatLong, Listener listener){
        this.activity = activity;
        this.locationLatLong = locationLatLong;
        this.listener = listener;
    }

    //TODO:Find Address from Location
    public class AddressManager extends AsyncTask<LatLng,String,String> {

        @Override
        protected String doInBackground(LatLng... latLngs) {
            String strAdd = "";
            Geocoder geocoder = new Geocoder(activity,Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latLngs[0].latitude, latLngs[0].longitude, 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    strAdd = strReturnedAddress.toString();
                    Log.w("Current loction address", strReturnedAddress.toString());
                } else {
                    Log.w("Current loction address", "No Address returned!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.w("Current loction address", "Canont get Address!");
            }
            return strAdd;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listener.onAddressReceived(s);
        }
    }

    public void startAddressFinding(){
        AddressManager addressManager = new AddressManager();
        addressManager.execute(locationLatLong);
    }

    public interface Listener{
        public void onAddressReceived(String address);
    }


}
