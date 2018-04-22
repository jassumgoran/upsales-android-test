package upsales.com.upsalesandroidtest.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

import upsales.com.upsalesandroidtest.CustomItemClickListener;
import upsales.com.upsalesandroidtest.R;
import upsales.com.upsalesandroidtest.model.Account;
import upsales.com.upsalesandroidtest.model.Address;
import upsales.com.upsalesandroidtest.model.User;
import upsales.com.upsalesandroidtest.tasks.GeocodeAddressTask;

/**
 * Created by Goran on 21.4.2018.
 */

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.AddressViewHolder> {

    Context context;
    List<Address> addresses;
    CustomItemClickListener listener;

    HashMap<String, String> cachedMapUrls;

    public AddressesAdapter(Context context, List<Address> addresses, CustomItemClickListener listener) {
        this.context = context;
        this.addresses = addresses;
        this.listener = listener;

        this.cachedMapUrls = new HashMap<>();
    }

    @NonNull
    @Override
    public AddressesAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_row, parent, false);

        AddressViewHolder holder =  new AddressViewHolder(itemView);
        itemView.setOnClickListener(v -> listener.onItemClick(v, holder.getAdapterPosition()));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddressesAdapter.AddressViewHolder holder, int position) {
        Address address = addresses.get(position);

        holder.street.setText(address.getAddress());
        holder.city.setText(address.getZipcode() + " " + address.getCity());
        holder.map.setImageResource(android.R.color.transparent);

        String mapUrl = context.getString(R.string.google_static_maps_base_url).replace(":address",
                address.toString()).replace(":key", context.getString(R.string.google_maps_key));

        Glide.with(context).load(mapUrl).into(holder.map);
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder{

        public TextView street, city;
        public ImageView map;

        public AddressViewHolder(View itemView) {
            super(itemView);
            street = itemView.findViewById(R.id.txtStreet);
            city = itemView.findViewById(R.id.txtCity);
            map = itemView.findViewById(R.id.imgMap);
        }
    }
}
