package upsales.com.upsalesandroidtest.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import upsales.com.upsalesandroidtest.App;
import upsales.com.upsalesandroidtest.R;
import upsales.com.upsalesandroidtest.adapter.UsersAdapter;
import upsales.com.upsalesandroidtest.api.ApiClient;
import upsales.com.upsalesandroidtest.model.Filter;
import upsales.com.upsalesandroidtest.model.User;
import upsales.com.upsalesandroidtest.model.response.AccountsResponse;
import upsales.com.upsalesandroidtest.model.response.UsersResponse;
import upsales.com.upsalesandroidtest.ui.views.ExpandableHeightListView;

/**
 * Created by Goran on 20.4.2018.
 */

public class UsersActivity extends AppCompatActivity implements TextWatcher {

    @BindView(R.id.listViewChecked)
    ExpandableHeightListView listViewChecked;

    @BindView(R.id.listViewAll)
    ExpandableHeightListView listViewAll;

    @BindView(R.id.txtHeaderSelected)
    TextView txtHeaderSelected;

    @BindView(R.id.txtHeaderUsers)
    TextView txtHeaderUsers;

    @BindView(R.id.backView)
    View backView;

    @BindView(R.id.txtFooterCancel)
    View txtFooterCancel;

    @BindView(R.id.txtFooterResults)
    TextView txtFooterResults;

    @BindView(R.id.footerShowResults)
            View footerShowResults;

    @BindView(R.id.inputSearch)
    EditText inputSearch;

    List<User> nonSelectedUsers;
    List<User> checkedUsers;
    List<User> allUsers;
    List<User> filteredUsers;

    UsersAdapter adapterAll, adapterSelected;

    boolean hasBeenChecked;
    boolean mSearchMode;

    Set<Integer> mSelectedIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.bind(this);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        mSelectedIds = new HashSet<>();
        allUsers = new ArrayList<>();
        nonSelectedUsers = new ArrayList<>();
        checkedUsers = new ArrayList<>();
        filteredUsers = new ArrayList<>();

        adapterAll = new UsersAdapter(nonSelectedUsers, false);
        adapterSelected = new UsersAdapter(checkedUsers, true);

        listViewAll.setAdapter(adapterAll);
        listViewChecked.setAdapter(adapterSelected);

        listViewAll.setExpanded(true);
        listViewChecked.setExpanded(true);

        handleEvents();
        toggleShowResults();
        refreshHeaders();
        fetchUsers();

    }

    private void toggleVisibility(List list, View view){
        if(list.isEmpty())
            view.setVisibility(View.GONE);
        else
            view.setVisibility(View.VISIBLE);

    }

    private void setSelectedListVisibility(boolean visible){

        if(visible){
            txtHeaderSelected.setVisibility(View.VISIBLE);
            listViewChecked.setVisibility(View.VISIBLE);
        }
        else{
            txtHeaderSelected.setVisibility(View.GONE);
            listViewChecked.setVisibility(View.GONE);
        }


    }

    private void refreshHeaders(){
        txtHeaderSelected.setText(String.valueOf(checkedUsers.size()) + " " + getString(R.string.selected));
        txtHeaderUsers.setText(String.valueOf(nonSelectedUsers.size()) + " " + getString(R.string.users));

        toggleVisibility(nonSelectedUsers, txtHeaderUsers);
        toggleVisibility(checkedUsers, txtHeaderSelected);
    }

    private void handleEvents(){
        listViewAll.setOnItemClickListener(new UserItemClickListener(nonSelectedUsers));
        listViewChecked.setOnItemClickListener(new UserItemClickListener(checkedUsers));

        backView.setOnClickListener(view -> finish());
        txtFooterCancel.setOnClickListener(view -> onCancel());
        footerShowResults.setOnClickListener(view -> showResults());

        inputSearch.addTextChangedListener(this);

    }

    private void toggleShowResults(){
        if(this.checkedUsers != null && this.checkedUsers.size() > 0){
            footerShowResults.setVisibility(View.VISIBLE);
        }
        else{
            footerShowResults.setVisibility(View.GONE);
        }
    }

    private void onCancel(){
        setResult(RESULT_CANCELED);
        finish();
    }

    private void showResults(){



        Intent intent = new Intent(this, MainActivity.class);
        //Intent intent = new Intent();
        Filter filter = new Filter(Filter.TYPE_ACCOUNT_MANAGERS);
        filter.setLabel(getFilterLabel());
        filter.setUserIds(getSelectedUserIds());
        intent.putExtra(Filter.TAG, filter);
        //setResult(RESULT_OK, intent);

        ((App)getApplication()).setCachedFilter(filter);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private String getFilterLabel(){
        StringBuilder builder = new StringBuilder();
        if(checkedUsers.isEmpty())
            return "";

        User firstUser = checkedUsers.get(0);
        return checkedUsers.size() > 1 ? firstUser.getName() + "+" +  String.valueOf(checkedUsers.size()-1) : firstUser.getName();
    }



    private class AllItemCheckedListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hasBeenChecked = true;
            User removeUser = nonSelectedUsers.remove(position);
            mSelectedIds.remove(new Integer(String.valueOf(removeUser.getId())));
            onSelectedUsersChanged();
            refreshHeaders();
            refreshLists();
            toggleShowResults();

        }
    }

    private class SelectedItemCheckedListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            User addUser = checkedUsers.remove(position);
            mSelectedIds.add((int) addUser.getId());
            nonSelectedUsers.add(addUser);
            onSelectedUsersChanged();
            sortList(nonSelectedUsers);
            refreshHeaders();
            refreshLists();
            toggleShowResults();
        }
    }

    private List<Integer> getSelectedUserIds(){
        return new ArrayList<>(mSelectedIds);
    }

    private void onSelectedUsersChanged(){
            ((App) getApplication()).fetchClientsByAccountManager(0, 10, getSelectedUserIds());
    }

    private void refreshLists(){
        adapterSelected.notifyDataSetChanged();
        adapterAll.notifyDataSetChanged();
    }

    private void sortList(List<User> users){
        Collections.sort(users, (obj1, obj2) -> obj1.getName().compareToIgnoreCase(obj2.getName()));
    }

    private void fetchUsers(){
        ApiClient.getClient().getActiveUsers().enqueue(new Callback<UsersResponse>() {
            @Override
            public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
                if(response.isSuccessful()){
                    List<User> newUsers = response.body().getData();
                    if(newUsers != null)
                    onUsersResponse(newUsers);
                }
            }

            @Override
            public void onFailure(Call<UsersResponse> call, Throwable t) {

            }
        });
    }

    private void onUsersResponse(List<User> newUsers){
        if(((App)getApplication()).getCachedFilter() != null){
            Set<Integer> filterUserIds = new HashSet<>();
            filterUserIds.addAll(((App)getApplication()).getCachedFilter().getUserIds());
            mSelectedIds = filterUserIds;

            for(User user : newUsers){
                if(filterUserIds.contains(new Integer(String.valueOf(user.getId())))){
                    this.checkedUsers.add(user);
                }
                else{
                    this.nonSelectedUsers.add(user);
                }
            }
        }
        else {
            this.allUsers.addAll(newUsers);
            this.nonSelectedUsers.addAll(newUsers);
        }
        refreshLists();
        refreshHeaders();
        toggleShowResults();
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

    @Subscribe
    public void onAccountsResponse(AccountsResponse accountsResponse){

        if(accountsResponse.getData() != null) {
            txtFooterResults.setText(String.valueOf(accountsResponse.getMetadata().getTotal()));
        }
    }

    private void setSearchMode(){
        mSearchMode = true;
        setSelectedListVisibility(false);
        listViewAll.setOnItemClickListener(new SearchUserItemClickListener());
        adapterAll = new UsersAdapter(filteredUsers, mSelectedIds);
        listViewAll.setAdapter(adapterAll);


    }

    private void clearSearchMode(){
        mSearchMode = false;
        setSelectedListVisibility(true);
        toggleVisibility(checkedUsers, listViewChecked);
        adapterAll = new UsersAdapter(nonSelectedUsers);
        listViewAll.setAdapter(adapterAll);
        listViewAll.setOnItemClickListener(new UserItemClickListener(nonSelectedUsers));
        loadSplitListData();

    }

    private void setUsersNumber(int size){
        txtHeaderUsers.setText(String.valueOf(size) + " " + getString(R.string.users));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(count > 0){
            if(!mSearchMode) {
                setSearchMode();
            }
            filterBy(s.toString().toLowerCase());
        }
        else{
            clearSearchMode();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void filterBy(String text){
        List<User> filteredUsers = getFilteredUsers(text);
        setUsersNumber(filteredUsers.size());
        this.filteredUsers.clear();
        this.filteredUsers.addAll(filteredUsers);
        this.adapterAll.notifyDataSetChanged();

    }

    private List<User> getFilteredUsers(String text){
        List<User> filteredUsers = new ArrayList<>();
        for(User user : allUsers){
            if(user.getName().toLowerCase().contains(text))
                filteredUsers.add(user);
        }
        return filteredUsers;
    }

    private class UserItemClickListener implements AdapterView.OnItemClickListener{

        List<User> list;

        public UserItemClickListener(List<User> list) {
            this.list = list;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            User user = list.get(position);
            toggleUserSelection(user);
            onSelectedUsersChanged();
            loadSplitListData();
        }
    }

    private class SearchUserItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            User user = filteredUsers.get(position);
            toggleUserSelection(user);
            onSelectedUsersChanged();
            adapterAll.setSelectedIds(mSelectedIds);
            adapterAll.notifyDataSetChanged();
        }
    }

    private void toggleUserSelection(User user){
        Integer key = new Integer(String.valueOf(user.getId()));
        if(mSelectedIds.contains(key)){
            mSelectedIds.remove(key);
        }else{
            mSelectedIds.add(key);
        }

    }

    private void loadSplitListData(){
        List<User> checkedUsers = new ArrayList<>();
        List<User> nonCheckedUsers = new ArrayList<>();
        for(User user : allUsers){
            Integer key = new Integer(String.valueOf(user.getId()));
            if(mSelectedIds.contains(key)){
                checkedUsers.add(user);
            }
            else{
                nonCheckedUsers.add(user);
            }
        }

        this.checkedUsers.clear();
        this.checkedUsers.addAll(checkedUsers);
        this.adapterSelected.notifyDataSetChanged();

        sortList(nonCheckedUsers);
        this.nonSelectedUsers.clear();
        this.nonSelectedUsers.addAll(nonCheckedUsers);
        this.adapterAll.notifyDataSetChanged();

        refreshHeaders();
        toggleShowResults();
    }
}
