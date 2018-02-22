package com.nisaidie.nisaidie1.database;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.databinding.AccountSummaryBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by obaro on 26/09/2016.
 */

public class RecyclerViewCursorAdapter extends RecyclerView.Adapter<RecyclerViewCursorAdapter.ViewHolder> {

    Context mContext;
    Cursor mCursor;

    public RecyclerViewCursorAdapter(Context context, Cursor cursor) {

        mContext = context;
        mCursor = cursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AccountSummaryBinding itemBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }

        public void bindCursor(Cursor cursor) {
            itemBinding.nameLabel.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(DBContract.Nisaidie_user.COLUMN_NAME)
            ));
            itemBinding.descLabel.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(DBContract.Nisaidie_user.COLUMN_DESCRIPTION)
            ));
            itemBinding.emailLabel.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(DBContract.Nisaidie_user.COLUMN_EMAIL)
            ));
            itemBinding.uphoneLabel.setText(cursor.getString(
                    cursor.getColumnIndexOrThrow(DBContract.Nisaidie_user.COLUMN_PHONE)
            ));

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(
                    cursor.getColumnIndexOrThrow(DBContract.Nisaidie_user.COLUMN_JOINED_DATE)));
            itemBinding.joinedLabel.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
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
                R.layout.account_summary, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
}