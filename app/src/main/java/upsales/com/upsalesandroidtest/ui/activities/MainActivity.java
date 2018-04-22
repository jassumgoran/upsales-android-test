package upsales.com.upsalesandroidtest.ui.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import upsales.com.upsalesandroidtest.App;
import upsales.com.upsalesandroidtest.CustomItemClickListener;
import upsales.com.upsalesandroidtest.EndlessRecyclerViewScrollListener;
import upsales.com.upsalesandroidtest.R;
import upsales.com.upsalesandroidtest.adapter.ClientsAdapter;
import upsales.com.upsalesandroidtest.model.Account;
import upsales.com.upsalesandroidtest.model.Filter;
import upsales.com.upsalesandroidtest.model.events.AccountsErrorResponse;
import upsales.com.upsalesandroidtest.model.response.AccountsResponse;

public class MainActivity extends FloatingToolbarActivity implements  Callback<AccountsResponse>, CustomItemClickListener {

    public static final int PAGE_SIZE = 10;
    int mOffset = 0;

    List<Account> clients;

    private EndlessRecyclerViewScrollListener scrollListener;
    ClientsAdapter adapter;

    View filterIconView;

    private static final int REQUEST_CODE_FILTERS = 1001;

    Filter mFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSubtitle("");

        if(getIntent() != null && getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            if(extras.containsKey(Filter.TAG)){
                mFilter = extras.getParcelable(Filter.TAG);
            }
        }

        clients = new ArrayList<>();



        if(mFilter != null){
//            List<Account> cachedFilteredAccounts = ((App)getApplication()).getCachedFilteredAccounts();
//            if(cachedFilteredAccounts != null && cachedFilteredAccounts.size() > 0){
//                clients = cachedFilteredAccounts;
//                mOffset = 1;
//            }

            ((App)getApplication()).fetchClientsByAccountManager(mOffset, PAGE_SIZE, mFilter.getUserIds());
        }
        else{
            ((App)getApplication()).fetchClients(mOffset, PAGE_SIZE);
        }



        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.company_rows_vertical_spacing)));
        adapter = new ClientsAdapter(clients, this);
        recyclerView.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.v("LOAD_MORE", String.valueOf(mOffset));
                showSpinner();
                if (mFilter != null) {
                    ((App) getApplication()).fetchClientsByAccountManager(mOffset, PAGE_SIZE, mFilter.getUserIds());
                } else {
                    ((App) getApplication()).fetchClients(mOffset, PAGE_SIZE);
                }
            }

        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);
        showSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clients, menu);
        filterIconView = menu.findItem(R.id.menu_item_filter).getActionView();
        filterIconView.setSelected(mFilter != null);
        filterIconView.setOnClickListener(view -> openFilters());
        //filterIconView.setSelected(true);
        return true;
    }

    private void openFilters(){
        Intent intent = new Intent(this, FiltersActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FILTERS);
        overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }

    /*
    private void fetchClients(int offset, int limit){
        ApiClient.getClient().getAccounts(offset * limit, limit, "name").enqueue(this);
    }*/

    @Subscribe
    public void onAccountsResponse(AccountsResponse accountsResponse){
        hideSpinner();

        Log.v("OFFSET", String.valueOf(mOffset));
        if(accountsResponse.getData() != null && !accountsResponse.getData().isEmpty()) {
            onNewClients(accountsResponse.getData());
        }

        if(accountsResponse.getMetadata().getTotal() <= clients.size()){
            recyclerView.removeOnScrollListener(scrollListener);
        }
        else {
            mOffset++;
        }


    }

    @Subscribe
    public void onAccountsErrorResponse(AccountsErrorResponse errorResponse){
        hideSpinner();
    }

    @Override
    public void onResponse(Call<AccountsResponse> call, Response<AccountsResponse> response) {
        if(response.isSuccessful()){
            AccountsResponse accountsResponse = response.body();
            mOffset++;
            Log.v("OFFSET", String.valueOf(mOffset));
            if(accountsResponse.getData() != null && !accountsResponse.getData().isEmpty()) {
                onNewClients(accountsResponse.getData());
            }
        }
    }

    @Override
    public void onFailure(Call<AccountsResponse> call, Throwable t) {
        Log.v("FAILURE", "FAILURE");
    }

    private void onNewClients(List<Account>  newClients){
        this.clients.addAll(newClients);
        adapter.notifyDataSetChanged();
        setSubtitle(String.valueOf(this.clients.size()) + " " + getString(R.string.companies));
    }


    @Override
    public void onItemClick(View v, int position) {
        Account account = clients.get(position);
        Intent intent = new Intent(this, CompanyActivity.class);
        intent.putExtra(Account.TAG, account);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_FILTERS){
            if(resultCode == RESULT_OK){
                Filter filter = data.getParcelableExtra(Filter.TAG);
                if(filter != null){
                    onFilterResult(filter);
                }
            }
            else if(resultCode == RESULT_CANCELED){
                if(mFilter != null || ((App)getApplication()).getCachedFilter() != null){
                    clearFilter();
                    switchNonFilterMode();
                }
            }

        }

    }

    private void clearFilter(){
        mFilter = null;
        ((App)getApplication()).clearFilter();
        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(Filter.TAG)){
            getIntent().getExtras().remove(Filter.TAG);
        }
    }

    private void switchNonFilterMode(){
         mOffset = 0;
        ((App)getApplication()).fetchClients(mOffset, PAGE_SIZE);
        if(filterIconView != null){
            filterIconView.setSelected(false);
        }
        if(scrollListener != null){
            recyclerView.addOnScrollListener(scrollListener);
        }
    }


    private void onFilterResult(Filter filter){
        ((App)getApplication()).setCachedFilter(filter);
        List<Account> filteredAccounts = ((App)getApplication()).getCachedFilteredAccounts();
        if(filteredAccounts != null){
            this.clients.clear();
            this.clients.addAll(filteredAccounts);
            this.adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void showSpinner(){
        adapter.showLoading(true);
        adapter.notifyDataSetChanged();
    }

    private void hideSpinner(){
        adapter.showLoading(false);
        adapter.notifyDataSetChanged();
    }


}
