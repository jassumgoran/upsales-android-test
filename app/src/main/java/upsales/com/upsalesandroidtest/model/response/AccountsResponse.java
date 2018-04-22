package upsales.com.upsalesandroidtest.model.response;

import java.util.List;

import upsales.com.upsalesandroidtest.model.Account;

/**
 * Created by Goran on 20.4.2018.
 */

public class AccountsResponse {

    Metadata metadata;
    List<Account> data;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<Account> getData() {
        return data;
    }

    public void setData(List<Account> data) {
        this.data = data;
    }
}
