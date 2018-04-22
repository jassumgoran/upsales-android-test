package upsales.com.upsalesandroidtest.model.response;

import java.util.List;

import upsales.com.upsalesandroidtest.model.Account;
import upsales.com.upsalesandroidtest.model.User;

/**
 * Created by Goran on 20.4.2018.
 */

public class UsersResponse {

    Metadata metadata;
    List<User> data;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<User> getData() {
        return data;
    }

    public void setData(List<User> data) {
        this.data = data;
    }
}
