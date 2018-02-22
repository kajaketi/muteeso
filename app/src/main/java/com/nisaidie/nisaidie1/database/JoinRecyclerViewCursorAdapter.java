package com.nisaidie.nisaidie1.database;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.databinding.EmergencyListBinding;


import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by obaro on 26/09/2016.
 */

public class JoinRecyclerViewCursorAdapter extends RecyclerView.Adapter<JoinRecyclerViewCursorAdapter.ViewHolder> {

    Context mContext;
    Cursor mCursor;

    public JoinRecyclerViewCursorAdapter(Context context, Cursor cursor) {

        mContext = context;
        mCursor = cursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EmergencyListBinding itemBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }

        public void bindCursor(Cursor cursor) {
            itemBinding.firstnameLabel.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(DBContract.Emergency_contacts.COLUMN_FIRSTNAME)
            ));
            itemBinding.lastnameLabel.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(DBContract.Emergency_contacts.COLUMN_LASTNAME)
            ));
            itemBinding.phoneLabel.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(DBContract.Emergency_contacts.COLUMN_PHONE)
            ));
            itemBinding.otherLabel.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(DBContract.Emergency_contacts.COLUMN_OTHER)
            ));

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(
                    cursor.getColumnIndexOrThrow(DBContract.Emergency_contacts.COLUMN_ADDED_DATE)));
            itemBinding.addedLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));

            itemBinding.nameLabel.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(DBContract.Nisaidie_user.COLUMN_NAME)
            ));
            itemBinding.descLabel.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(DBContract.Nisaidie_user.COLUMN_DESCRIPTION)
            ));

        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.bindCursor(mCursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.emergency_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
}