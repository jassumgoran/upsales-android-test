package upsales.com.upsalesandroidtest.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import upsales.com.upsalesandroidtest.R;

/**
 * Created by Goran on 22.4.2018.
 */

public class PagerIndicatorView extends LinearLayout{

    private int pages = 1;
    private int selectedIndex = 0;

    public PagerIndicatorView(@NonNull Context context) {
        super(context);
        init();
    }

    public PagerIndicatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        init(pages,selectedIndex);
    }

    private void init() {
        setOrientation(HORIZONTAL);
    }

    public void init(int pages, int selectedIndex) {
        this.pages = pages;
        this.selectedIndex = selectedIndex;

        LayoutInflater inflater = LayoutInflater.from(getContext());

        this.removeAllViews();

        for (int i = 0; i < pages; i++) {

            boolean isSel = (i == selectedIndex);

           View indicatorView = inflater.inflate(R.layout.pager_indicator, this, false);
            if(isSel){
                indicatorView.setBackgroundResource(R.drawable.pager_indicator_selected);
            }
            else{
                indicatorView.setBackgroundResource(R.drawable.pager_indicator_normal);
            }

            this.addView(indicatorView);

        }
    }
}
