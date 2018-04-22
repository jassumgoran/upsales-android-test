package upsales.com.upsalesandroidtest.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import upsales.com.upsalesandroidtest.CustomItemClickListener;
import upsales.com.upsalesandroidtest.R;
import upsales.com.upsalesandroidtest.adapter.AddressesAdapter;
import upsales.com.upsalesandroidtest.model.Account;

/**
 * Created by Goran on 20.4.2018.
 */

public class CompanyActivity extends FloatingToolbarActivity implements CustomItemClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_company);
        //ButterKnife.bind(this);

        setTitle("");
        setSubtitle("");
        setBackIcon();

        if(getIntent() != null && getIntent().getExtras() != null){
            extractBundle(getIntent().getExtras());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void extractBundle(Bundle extras){
        if(extras.containsKey(Account.TAG)){
            Account account = extras.getParcelable(Account.TAG);
            if(account != null){
                onAccount(account);
            }
        }
    }

    private void onAccount(Account account){
        setTitle(account.getName());

        if(account.getAddresses() != null && !account.getAddresses().isEmpty()) {
            setSubtitle(account.getAddresses().get(0).getAddress());
            AddressesAdapter adapter = new AddressesAdapter(this, account.getAddresses(), this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        Intent intent = new Intent(this, MapActivity.class);
        Account account = getIntent().getExtras().getParcelable(Account.TAG);
        intent.putExtra(Account.TAG, account);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
