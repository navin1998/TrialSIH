package com.navin.trialsih.doctorActivities.bottomNavigation;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.navin.trialsih.R;

import java.util.ArrayList;

public class PrescriptionAdapter extends BaseAdapter {

    private Context context;
    public ArrayList list;

    LayoutInflater mInflater;
    public PrescriptionAdapter(Context context, ArrayList list){
        this.context = context;
        this.list  =list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        final ViewHolder1 holder;
        convertView=null;
        holder = new ViewHolder1();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.activity_listview_adapter, null);
        /*holder.date = convertView
                .findViewById(R.id.date);
        holder.date.setText(list.get(position).toString());
        holder.urlPres=convertView.findViewById(R.id.urlPres);*/
        return convertView;
    }

}

class ViewHolder1 {
    TextView date,urlPres;
}

