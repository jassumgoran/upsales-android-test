package upsales.com.upsalesandroidtest.api;

/**
 * Created by Goran on 20.4.2018.
 */

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import upsales.com.upsalesandroidtest.model.response.AccountsResponse;
import upsales.com.upsalesandroidtest.model.response.UsersResponse;

public interface UpsalesAPI {

    public static final String BASE_URL = "https://integration.upsales.com/";

    public static final String API_ACCOUNTS = "/api/v2/accounts";
    public static final String API_USERS = "/api/v2/users";
    public static final String API_ACTIVE_USERS = API_USERS + "?active=1";

    @GET(API_ACCOUNTS)
    Call<AccountsResponse> getAccounts(@Query("offset") int offset, @Query("limit") int limit, @Query("sort") String sortBy);

    @GET(API_ACCOUNTS)
    Call<AccountsResponse> getClientsByAccountManager(@Query("offset") int offset, @Query("limit") int limit, @Query("sort") String sortBy,@Query("user.id") List<Integer> userIds);

    @GET(API_ACTIVE_USERS)
    Call<UsersResponse> getActiveUsers();


}