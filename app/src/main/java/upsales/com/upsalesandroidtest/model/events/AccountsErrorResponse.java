package upsales.com.upsalesandroidtest.model.events;

/**
 * Created by Goran on 22.4.2018.
 */

public class AccountsErrorResponse {

    Throwable t;

    public AccountsErrorResponse() {
    }

    public AccountsErrorResponse(Throwable t) {
        this.t = t;
    }
}
