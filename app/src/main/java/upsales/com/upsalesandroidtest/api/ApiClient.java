package upsales.com.upsalesandroidtest.api;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import upsales.com.upsalesandroidtest.model.User;
import upsales.com.upsalesandroidtest.model.response.UsersResponse;

/**
 * Created by Goran on 20.4.2018.
 */

public class ApiClient {

    private static final String KEY_TOKEN = "token";
    private static final String TOKEN = "fab2dd8eb69dcdc3d108c7e7bfa6c4932fe69b06ba4cfe4c8cebe45d08a5b0a2";

    private static UpsalesAPI upsalesApi;

    public static UpsalesAPI getClient(){
        if(upsalesApi == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(UpsalesAPI.BASE_URL)
                    .client(getHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            upsalesApi = retrofit.create(UpsalesAPI.class);
        }
        return upsalesApi;
    }

    private static OkHttpClient getHttpClient(){
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter(KEY_TOKEN, TOKEN)
                        .build();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        return httpClient.build();
    }



}
