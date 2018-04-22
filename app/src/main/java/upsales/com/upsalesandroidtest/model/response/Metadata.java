package upsales.com.upsalesandroidtest.model.response;

/**
 * Created by Goran on 20.4.2018.
 */

public class Metadata {

    int total;
    int limit;
    int offset;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
