package com.kickstarter.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.lzyzsd.randomcolor.RandomColor;
import com.kickstarter.R;
import com.kickstarter.beans.JSONBeans;
import com.kickstarter.helper.TimeHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class JSONAdapter extends ArrayAdapter{
    ArrayList<JSONBeans> arrayList;
    LayoutInflater vi;
    int Resource;
    private Context context;

    public JSONAdapter(Context context, int resource, ArrayList<JSONBeans> objects) {
        super(context, resource, objects);

        this.context = context;

        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        arrayList = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = vi.inflate(Resource, null);

        TextView tv1 = (TextView) v.findViewById(R.id.tv1);
        TextView tv2 = (TextView) v.findViewById(R.id.tv2);
        TextView tv3 = (TextView) v.findViewById(R.id.tv3);
        TextView tv4 = (TextView) v.findViewById(R.id.tv4);
        View view = v.findViewById(R.id.view);

        RandomColor randomColor = new RandomColor();
        randomColor.randomColor();
        view.setBackgroundColor(Color.rgb(randomColor.randomColor(),randomColor.randomColor(),randomColor.randomColor()));

        tv1.setText(arrayList.get(position).getTitle()+"\n");
        tv2.setText("By: "+ arrayList.get(position).getPublisher());
        tv3.setText(TimeHelper.convertTime(arrayList.get(position).getTimestamp()));
        tv4.setText("Category: "+arrayList.get(position).getCategory());

        return v;
    }
}
