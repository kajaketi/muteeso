package com.nisaidie.nisaidie1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nisaidie.nisaidie1.R;


/**
 * Created by USER on 1/4/2016.
 */
public class Adapter extends ArrayAdapter<String>{

    Context context;
    String[] categories;
    int[] images;
    LayoutInflater inflater;
    public Adapter(Context context, String[] categories, int[] images) {
        super(context, R.layout.category_row,categories);

        this.context = context;
        this.categories =categories;
        this.images=images;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView =inflater.inflate(R.layout.category_row,null);
        }

        TextView categoryName= (TextView) convertView.findViewById(R.id.category_name);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.category_image);

        categoryName.setText(categories[position]);
        //imageView.setImageResource(images[position]);

        return convertView;

    }
}
