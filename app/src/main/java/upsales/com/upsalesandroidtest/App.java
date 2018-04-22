package upsales.com.upsalesandroidtest;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import upsales.com.upsalesandroidtest.api.ApiClient;
import upsales.com.upsalesandroidtest.api.UpsalesAPI;
import upsales.com.upsalesandroidtest.model.Account;
import upsales.com.upsalesandroidtest.model.Filter;
import upsales.com.upsalesandroidtest.model.events.AccountsErrorResponse;
import upsales.com.upsalesandroidtest.model.response.AccountsResponse;

/**
 * Created by Goran on 20.4.2018.
 */

public class App extends Application{

    Gson gson;
    List<Account> cachedAccounts;
    List<Account> cachedFilteredAccounts;
    Filter cachedFilter;

    @Override
    public void onCreate() {
        super.onCreate();

        gson = new Gson();
    }

    public Gson getGson() {
        return gson;
    }

    public List<Account> getCachedAccounts() {
        return cachedAccounts;
    }

    public void setCachedAccounts(List<Account> cachedAccounts) {
        this.cachedAccounts = cachedAccounts;
    }

    public List<Account> getCachedFilteredAccounts() {
        return cachedFilteredAccounts;
    }

    public void setCachedFilteredAccounts(List<Account> cachedFilteredAccounts) {
        this.cachedFilteredAccounts = cachedFilteredAccounts;
    }

    public Filter getCachedFilter() {
        return cachedFilter;
    }

    public void setCachedFilter(Filter cachedFilter) {
        this.cachedFilter = cachedFilter;
    }

    public boolean hasFilter(){
        if(cachedFilter == null){
            return false;
        }
        return true;
    }

    public void clearFilter(){
        this.cachedFilter = null;
    }

    public void fetchClients(int offset, int limit){
        ApiClient.getClient().getAccounts(offset * limit, limit, "name").enqueue(new Callback<AccountsResponse>() {

            @Override
            public void onResponse(Call<AccountsResponse> call, Response<AccountsResponse> response) {
                if(response.isSuccessful()) {
                    AccountsResponse accountsResponse = response.body();
                    EventBus.getDefault().post(accountsResponse);
                }

            }

            @Override
            public void onFailure(Call<AccountsResponse> call, Throwable t) {

            }



        });
    }

    public void fetchClientsByAccountManager(int offset, int limit, List<Integer> userIds){
        ApiClient.getClient().getClientsByAccountManager(offset * limit, limit, "name", userIds).enqueue(new Callback<AccountsResponse>() {

            @Override
            public void onResponse(Call<AccountsResponse> call, Response<AccountsResponse> response) {
                if(response.isSuccessful()) {

                    AccountsResponse accountsResponse = response.body();
                    EventBus.getDefault().post(accountsResponse);

                    setCachedFilteredAccounts(accountsResponse.getData());
                }
                else{
                    EventBus.getDefault().post(new AccountsErrorResponse());
                }
            }

            @Override
            public void onFailure(Call<AccountsResponse> call, Throwable t) {
                EventBus.getDefault().post(new AccountsErrorResponse(t));

            }



        });
    }
}
