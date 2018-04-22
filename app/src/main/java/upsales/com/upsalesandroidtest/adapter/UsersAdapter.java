package upsales.com.upsalesandroidtest.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import upsales.com.upsalesandroidtest.R;
import upsales.com.upsalesandroidtest.model.User;

/**
 * Created by Goran on 21.4.2018.
 */

public class UsersAdapter extends BaseAdapter {

    List<User> users;
    Set<Integer> selectedIds;
    boolean selectAll;


    public UsersAdapter(List<User> users) {
        this.users = users;
        this.selectedIds = new HashSet<>();
    }

    public UsersAdapter(List<User> users, Set<Integer> selectedIds) {
        this.users = users;
        this.selectedIds = selectedIds;
    }

    public UsersAdapter(List<User> users, boolean selectAll) {
        this.users = users;
        this.selectedIds = new HashSet<>();
        this.selectAll = selectAll;
    }

    public void setSelectedIds(Set<Integer> selectedIds) {
        this.selectedIds = selectedIds;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserViewHolder viewHolder;
        Context context = parent.getContext();

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.user_row, null);
            viewHolder = new UserViewHolder();
            viewHolder.name = convertView.findViewById(R.id.txtName);
            viewHolder.button = convertView.findViewById(R.id.imgCheck);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (UserViewHolder) convertView.getTag();
        }

        User user = users.get(position);

        viewHolder.name.setText(user.getName());
        //viewHolder.button.setSelected(selectAll);

        Drawable drawable = viewHolder.button.getDrawable();
        if(selectAll){
            drawable.clearColorFilter();
            viewHolder.button.setSelected(true);
        }
        else{
            //to maintain the dynamic size from the icon asset
            if(selectedIds.contains(new Integer(String.valueOf(user.getId())))){
                drawable.clearColorFilter();
                viewHolder.button.setSelected(true);
            }
            else{
                drawable.setColorFilter(context.getResources().getColor(R.color.colorUpsalesTableBorders), PorterDuff.Mode.SRC_ATOP);
                viewHolder.button.setSelected(false);
            }

        }

        return convertView;
    }

    private static class UserViewHolder {
        public TextView name;
        public ImageButton button;
    }
}
