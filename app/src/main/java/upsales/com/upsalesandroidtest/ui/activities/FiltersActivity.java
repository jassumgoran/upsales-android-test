package upsales.com.upsalesandroidtest.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import upsales.com.upsalesandroidtest.App;
import upsales.com.upsalesandroidtest.R;
import upsales.com.upsalesandroidtest.model.Filter;

/**
 * Created by Goran on 20.4.2018.
 */

public class FiltersActivity extends AppCompatActivity {

    @BindView(R.id.txtLabel)
    TextView txtAccountLabel;

    @BindView(R.id.txtNumber)
    TextView txtAccountNumber;

    @BindView(R.id.rowAccountManagers)
    ViewGroup rowAccountManagers;

    @BindView(R.id.btnCancel)
    View btnCancel;

    private static final int REQUEST_CODE_USERS = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        ButterKnife.bind(this);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        rowAccountManagers.setOnClickListener( view ->  onAccountManagersClick());
        Filter currentFilter = ((App)getApplication()).getCachedFilter();

        //only one type at this time
        if(currentFilter != null && currentFilter.getType() == Filter.TYPE_ACCOUNT_MANAGERS){
            setFilterData(rowAccountManagers, currentFilter);
        }

        btnCancel.setOnClickListener(view -> onCancel());
    }

    private void setFilterData(ViewGroup rowAccountManagers, Filter filter){
        ((TextView)rowAccountManagers.findViewById(R.id.txtNumber)).setText(filter.getLabel());
    }

    private void onAccountManagersClick(){
        Intent intent = new Intent(this, UsersActivity.class);
        startActivityForResult(intent, REQUEST_CODE_USERS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_USERS){
            if(resultCode == RESULT_OK){
                setResult(RESULT_OK, data);
            }
            else if(resultCode == RESULT_CANCELED){
                setResult(RESULT_CANCELED);
                finish();
            }

        }
    }

    private void onCancel(){
        finish();
    }
}
