package upsales.com.upsalesandroidtest.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import upsales.com.upsalesandroidtest.R;
import upsales.com.upsalesandroidtest.model.Address;
import upsales.com.upsalesandroidtest.ui.views.PagerIndicatorView;

/**
 * Created by Goran on 22.4.2018.
 */

public class CardFragment extends Fragment {

    private String type;
    private String address;
    private String city;
    private String country;

    private int pages;
    private int selectedIndex;

    // newInstance constructor for creating fragment with arguments
    public static CardFragment newInstance(Address address, int pages, int selectedIndex) {
        CardFragment fragmentFirst = new CardFragment();
        Bundle args = new Bundle();
        args.putString("type", address.getType());
        args.putString("address", address.getAddress());
        args.putString("city", address.getZipcode() + " " + address.getCity());
        args.putString("country", address.getCountry());
        args.putInt("pages", pages);
        args.putInt("selectedIndex", selectedIndex);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString("type", "");
        address = getArguments().getString("address", "");
        city = getArguments().getString("city", "");
        country = getArguments().getString("country", "");
        pages = getArguments().getInt("pages", 1);
        selectedIndex = getArguments().getInt("selectedIndex", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_card, container, false);
        ((TextView)view.findViewById(R.id.txtAddressType)).setText(type);
        ((TextView)view.findViewById(R.id.txtStreet)).setText(address);
        ((TextView)view.findViewById(R.id.txtCity)).setText(city);
        ((TextView)view.findViewById(R.id.txtCountry)).setText(country);

        ((PagerIndicatorView)view.findViewById(R.id.indicator)).init(pages, selectedIndex);
        return view;
    }
}
