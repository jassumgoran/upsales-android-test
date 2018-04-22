package upsales.com.upsalesandroidtest.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import upsales.com.upsalesandroidtest.App;
import upsales.com.upsalesandroidtest.Constants;
import upsales.com.upsalesandroidtest.R;
import upsales.com.upsalesandroidtest.adapter.ClientsAdapter;
import upsales.com.upsalesandroidtest.model.Account;
import upsales.com.upsalesandroidtest.model.Filter;
import upsales.com.upsalesandroidtest.model.events.AccountsErrorResponse;
import upsales.com.upsalesandroidtest.model.response.AccountsResponse;
import upsales.com.upsalesandroidtest.ui.VerticalSpaceItemDecoration;
import upsales.com.upsalesandroidtest.ui.listeners.CustomItemClickListener;
import upsales.com.upsalesandroidtest.ui.listeners.EndlessRecyclerViewScrollListener;

public class MainActivity extends FloatingToolbarActivity implements  CustomItemClickListener {

    private static final int REQUEST_CODE_FILTERS = 1001;

    int mOffset = 0;

    List<Account> clients;
    ClientsAdapter adapter;

    Filter mFilter;
    View filterIconView;

    EndlessRecyclerViewScrollListener scrollListener;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        setupRecyclerView();
        showSpinner();

        if(mFilter != null){
            //TODO A redundant network request can be avoided with this control logic
            //first 10 accounts are previously fetched from Filter / Users screen
//            List<Account> cachedFilteredAccounts = ((App)getApplication()).getCachedFilteredAccounts();
//            if(cachedFilteredAccounts != null && cachedFilteredAccounts.size() > 0){
//                clients = cachedFilteredAccounts;
//                mOffset = 1;
//            }
            ((App)getApplication()).fetchClientsByAccountManager(mOffset, Constants.PAGE_SIZE, mFilter.getUserIds());
        }
        else{
            ((App)getApplication()).fetchClients(mOffset, Constants.PAGE_SIZE);
        }
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

    private void init(){
        setSubtitle("");
        clients = new ArrayList<>();
        extractBundle();
    }

    private void extractBundle(){
        if(getIntent() != null && getIntent().getExtras() != null){
            Bundle extras = getIntent().getExtras();
            if(extras.containsKey(Filter.TAG)){
                mFilter = extras.getParcelable(Filter.TAG);
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    //  USER INTERFACE METHODS
    //----------------------------------------------------------------------------------------------

    private void setupRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        int itemSpacingPx = getResources().getDimensionPixelSize(R.dimen.company_rows_vertical_spacing);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(itemSpacingPx));
        adapter = new ClientsAdapter(clients, this);
        recyclerView.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewListener(layoutManager);
        recyclerView.addOnScrollListener(scrollListener);
    }

    private void showSpinner(){
        adapter.showLoading(true);
        adapter.notifyDataSetChanged();
    }

    private void hideSpinner(){
        adapter.showLoading(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clients, menu);
        filterIconView = menu.findItem(R.id.menu_item_filter).getActionView();
        filterIconView.setSelected(mFilter != null);
        filterIconView.setOnClickListener(view -> openFilters());
        return true;
    }

    //----------------------------------------------------------------------------------------------
    //  LISTENERS
    //----------------------------------------------------------------------------------------------

    @Override
    public void onItemClick(View v, int position) {
        Account account = clients.get(position);
        openCompany(account);
    }

    private class EndlessRecyclerViewListener extends EndlessRecyclerViewScrollListener {

        public EndlessRecyclerViewListener(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        public void onLoadMore(int page, int totalItemsCount) {
            showSpinner();
            if (mFilter != null) {
                ((App) getApplication()).fetchClientsByAccountManager(mOffset, Constants.PAGE_SIZE, mFilter.getUserIds());
            } else {
                ((App) getApplication()).fetchClients(mOffset, Constants.PAGE_SIZE);
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    //  API RESPONSE
    //----------------------------------------------------------------------------------------------

    @Subscribe
    public void onAccountsResponse(AccountsResponse accountsResponse){
        hideSpinner();

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

    private void onNewClients(List<Account>  newClients){
        this.clients.addAll(newClients);
        adapter.notifyDataSetChanged();
        setSubtitle(String.valueOf(this.clients.size()) + " " + getString(R.string.companies));
    }

    //----------------------------------------------------------------------------------------------
    //  FILTERING
    //----------------------------------------------------------------------------------------------
    //TODO unused method, can be removed or used in future onActivityResult (alternative) handling
    private void onFilterResult(Filter filter){
        ((App)getApplication()).setCachedFilter(filter);
        List<Account> filteredAccounts = ((App)getApplication()).getCachedFilteredAccounts();
        if(filteredAccounts != null){
            this.clients.clear();
            this.clients.addAll(filteredAccounts);
            this.adapter.notifyDataSetChanged();
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
        ((App)getApplication()).fetchClients(mOffset, Constants.PAGE_SIZE);
        if(filterIconView != null){
            filterIconView.setSelected(false);
        }
        if(scrollListener != null){
            recyclerView.addOnScrollListener(scrollListener);
        }
    }

    //----------------------------------------------------------------------------------------------
    //  NAVIGATION
    //----------------------------------------------------------------------------------------------

    private void openFilters(){
        Intent intent = new Intent(this, FiltersActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FILTERS);
        overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }

    private void openCompany(Account account){
        Intent intent = new Intent(this, CompanyActivity.class);
        intent.putExtra(Account.TAG, account);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_FILTERS){
            //"Show Results" from Users / Filter screen restarts the activity with new Intent
            /*if(resultCode == RESULT_OK){
                Filter filter = data.getParcelableExtra(Filter.TAG);
                if(filter != null){
                    onFilterResult(filter);
                }
                return;
            }*/

             if(resultCode == RESULT_CANCELED){
                if(mFilter != null || ((App)getApplication()).getCachedFilter() != null){
                    clearFilter();
                    switchNonFilterMode();
                }
            }

        }
    }

}
