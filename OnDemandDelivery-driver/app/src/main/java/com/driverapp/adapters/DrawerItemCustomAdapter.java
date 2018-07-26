package com.driverapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.driverapp.R;
import com.driverapp.models.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class DrawerItemCustomAdapter extends ArrayAdapter<MenuItem> {

    Context mContext;
    int layoutResourceId;
    List<MenuItem> data;

     class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId) {

        super(mContext, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = new ArrayList<>();
        this.data.add(new MenuItem(R.drawable.ic_history_black_24dp,"History"));
        this.data.add(new MenuItem(R.drawable.ic_chat_black_24dp,"Call Support"));
        this.data.add(new MenuItem(R.drawable.ic_phone_black_24dp,"Call Responder"));
        this.data.add(new MenuItem(R.drawable.ic_people_black_24dp,"Terms and Conditions"));
        this.data.add(new MenuItem(R.drawable.ic_exit_to_app_black_24dp,"Logout"));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            view = inflater.inflate(layoutResourceId, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.item_nav_menu_imv);
            viewHolder.textView = (TextView) view.findViewById(R.id.item_nav_menu_tv);
            view.setTag(viewHolder);
        }
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            MenuItem menuItem = data.get(position);
            viewHolder.textView.setText(menuItem.getTitle());
            viewHolder.imageView.setImageResource(menuItem.getResourceId());

        return view;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}