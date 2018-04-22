package upsales.com.upsalesandroidtest.tasks;

import android.content.Context;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import upsales.com.upsalesandroidtest.model.Address;

/**
 * Created by Goran on 22.4.2018.
 */

public class GeocodeAddressTask extends AsyncTask<String, Void, LatLng> {

    public interface GeocodeAddressCallback{
        public void onGeocodeLatLng(Address address, LatLng latLng);
        public void onGeocodeException(Address address, Exception e);
    }

    Context context;
    Address address;
    GeocodeAddressCallback callback;

    Exception e;

    public GeocodeAddressTask(Context context, Address address, GeocodeAddressCallback callback) {
        this.context = context;
        this.address = address;
        this.callback = callback;
    }

    @Override
    protected LatLng doInBackground(String... strings) {


        Geocoder gc = new Geocoder(context);
        if(gc.isPresent()){
            List<android.location.Address> list = null;
            try {
                list = gc.getFromLocationName(address.toString(), 1);
                if(list == null || list.isEmpty()){
                    return null;
                }
                android.location.Address address = list.get(0);
                double lat = address.getLatitude();
                double lng = address.getLongitude();
                return new LatLng(lat, lng);
            } catch (IOException e) {
                e.printStackTrace();
                this.e = e;
            }

        }

        return null;
    }

    @Override
    protected void onPostExecute(LatLng latLng) {
        super.onPostExecute(latLng);
        if(latLng != null){
            cacheLatLng(address, latLng);
            callback.onGeocodeLatLng(address, latLng);
        }
        else if(this.e != null){
            callback.onGeocodeException(address, e);
        }
    }

    private void cacheLatLng(Address address, LatLng latLng){
        address.setLatitude(latLng.latitude);
        address.setLongitude(latLng.longitude);
    }
}