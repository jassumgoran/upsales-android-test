package upsales.com.upsalesandroidtest.ui.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import upsales.com.upsalesandroidtest.R;
import upsales.com.upsalesandroidtest.adapter.AdressCardAdapter;
import upsales.com.upsalesandroidtest.model.Account;
import upsales.com.upsalesandroidtest.model.Address;
import upsales.com.upsalesandroidtest.tasks.GeocodeAddressTask;

/**
 * Created by Goran on 20.4.2018.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GeocodeAddressTask.GeocodeAddressCallback {

    private GoogleMap mMap;
    private boolean pendingGeocodingRequest = false;
    private Address pendingGeocodingAddress = null;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.btnClose)
     View btnClose;

    Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(getIntent() != null && getIntent().getExtras() != null){
            extractBundle(getIntent().getExtras());
        }

        setUiProperties();
        handleEvents();

    }

    private void extractBundle(Bundle extras){
        if(extras.containsKey(Account.TAG)){
            Account account = extras.getParcelable(Account.TAG);
            if(account != null){

                onAccount(account);
            }
        }
    }

    private void setUiProperties(){
        int paddingPx = getResources().getDimensionPixelSize(R.dimen.adress_card_pager_padding);
        int marginPx = getResources().getDimensionPixelSize(R.dimen.adress_card_page_margin);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(paddingPx, 0, paddingPx, 0);
        viewPager.setPageMargin(marginPx);
    }

    private void handleEvents(){

        btnClose.setOnClickListener(view -> finish());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onFocusedAddress(mAccount.getAddresses().get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

   private void onFocusedAddress(Address address){
    if(address.hasLatLng()){
        LatLng latLng = address.getLatLng();
        onLatLng(latLng);
    }
    else{
        new GeocodeAddressTask(MapActivity.this, address, this).execute();
    }
   }

    private void onAccount(Account account){
        mAccount = account;
        if(mMap != null)
            new GeocodeAddressTask(MapActivity.this, account.getAddresses().get(0), this).execute();
        else{
            pendingGeocodingRequest = true;
            pendingGeocodingAddress = account.getAddresses().get(0);
        }

        if(account.getAddresses() != null)
        viewPager.setAdapter(new AdressCardAdapter(getSupportFragmentManager(), account.getAddresses()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(pendingGeocodingRequest && pendingGeocodingAddress != null){
            new GeocodeAddressTask(MapActivity.this, pendingGeocodingAddress, this).execute();
        }
    }

    private void onLatLng(LatLng latLng){

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude,latLng.longitude),16));
        mMap.clear();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mMap.addMarker(markerOptions);

    }

    @Override
    public void onGeocodeLatLng(Address address, LatLng latLng) {
        onLatLng(latLng);
    }

    @Override
    public void onGeocodeException(Address address, Exception e) {
        Toast.makeText(this, getString(R.string.coordinates_error) + address.toString(), Toast.LENGTH_SHORT).show();
    }

}