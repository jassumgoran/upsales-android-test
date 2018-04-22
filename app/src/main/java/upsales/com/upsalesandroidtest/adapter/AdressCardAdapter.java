package upsales.com.upsalesandroidtest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import upsales.com.upsalesandroidtest.ui.fragments.CardFragment;
import upsales.com.upsalesandroidtest.model.Address;

/**
 * Created by Goran on 22.4.2018.
 */

public class AdressCardAdapter extends FragmentStatePagerAdapter {

    List<Address> addresses;

    public AdressCardAdapter(FragmentManager fm, List<Address> addresses) {
        super(fm);
        this.addresses = addresses;
    }

    @Override
    public Fragment getItem(final int position) {
        return CardFragment.newInstance(addresses.get(position), addresses.size(), position);
    }

    @Override
    public int getCount() {
        return addresses.size();
    }

}