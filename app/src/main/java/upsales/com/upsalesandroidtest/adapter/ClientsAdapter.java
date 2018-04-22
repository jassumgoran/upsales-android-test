package upsales.com.upsalesandroidtest.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import upsales.com.upsalesandroidtest.ui.listeners.CustomItemClickListener;
import upsales.com.upsalesandroidtest.R;
import upsales.com.upsalesandroidtest.model.Account;

/**
 * Created by Goran on 20.4.2018.
 */

public class ClientsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected boolean showLoader;
    private static final int VIEWTYPE_ITEM = 1;
    private static final int VIEWTYPE_LOADER = 2;

    CustomItemClickListener listener;
    List<Account> clients;

    public ClientsAdapter(List<Account> clients, CustomItemClickListener listener) {
        this.clients = clients;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.company_row, parent, false);

        RecyclerView.ViewHolder holder = null;

        if (viewType == VIEWTYPE_LOADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner, parent, false);
            holder = new LoaderViewHolder(view);
        }
        else if(viewType == VIEWTYPE_ITEM){
            holder =  new ClientViewHolder(itemView);
            RecyclerView.ViewHolder finalHolder = holder;
            itemView.setOnClickListener(view -> listener.onItemClick(view, finalHolder.getAdapterPosition()));
        }

        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ClientViewHolder) {
            Account client = clients.get(position);
            ClientViewHolder clientHolder = (ClientViewHolder)holder;
            clientHolder.name.setText(client.getName());
            clientHolder.accountManager.setText(client.getAccountManager());
        }else{
            LoaderViewHolder loaderViewHolder = (LoaderViewHolder)holder;
            if (showLoader) {
                loaderViewHolder.mProgressBar.setVisibility(View.VISIBLE);
            } else {
                loaderViewHolder.mProgressBar.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {

        // If no items are present, there's no need for loader
        if (clients == null || clients.size() == 0) {
            return 0;
        }

        // +1 for loader
        return clients.size() + 1;
    }

    @Override
    public long getItemId(int position) {

        // loader can't be at position 0
        // loader can only be at the last position
        if (position != 0 && position == getItemCount() - 1) {

            // id of loader is considered as -1 here
            return -1;
        }
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        // loader can't be at position 0
        // loader can only be at the last position
        if (position != 0 && position == getItemCount() - 1) {
            return VIEWTYPE_LOADER;
        }

        return VIEWTYPE_ITEM;
    }

    public void showLoading(boolean status) {
        showLoader = status;
    }

    public void setItems(List<Account> items) {
        clients = items;
    }


    class ClientViewHolder extends RecyclerView.ViewHolder{

        public TextView name, accountManager;

        public ClientViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtName);
            accountManager = itemView.findViewById(R.id.txtAccountManager);
        }
    }

    static class LoaderViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar mProgressBar;
        public LoaderViewHolder(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
