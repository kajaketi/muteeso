package com.nisaidie.nisaidie1.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nisaidie.nisaidie1.R;
import com.nisaidie.nisaidie1.helper.EmContactsDetails;

import java.util.List;


public class EmContactViewAdapter extends RecyclerView.Adapter<EmContactViewAdapter.ViewHolder> {
    Context context;
    List<EmContactsDetails> EmContactList;

    public EmContactViewAdapter(Context context, List<EmContactsDetails> TempList) {

        this.EmContactList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emrecyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        EmContactsDetails emContactsDetails = EmContactList.get(position);
        holder.EmFirstNameTextView.setText(emContactsDetails.getfName());
        holder.EmLastNameTextView.setText(emContactsDetails.getlName());
        holder.EmPrimContTextView.setText(emContactsDetails.getPrimaryPhone());
        holder.EmOtherContTextView.setText(emContactsDetails.getOtherPhone());
        holder.EmAddressTextView.setText(emContactsDetails.getAddress());
        holder.EmRelationshipTextView.setText(emContactsDetails.getRship());
    }

    @Override
    public int getItemCount() {
        return EmContactList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView EmFirstNameTextView, EmLastNameTextView, EmPrimContTextView, EmOtherContTextView, EmAddressTextView, EmRelationshipTextView;
       // public TextView StudentNumberTextView;

        public ViewHolder(View itemView) {

            super(itemView);

            EmFirstNameTextView = (TextView) itemView.findViewById(R.id.ShowFirstNameTextView);
            EmLastNameTextView = (TextView) itemView.findViewById(R.id.ShowLastNameTextView);
            EmPrimContTextView = (TextView) itemView.findViewById(R.id.ShowPrimContactTextView);
            EmOtherContTextView = (TextView) itemView.findViewById(R.id.ShowOtherContactTextView);
            EmAddressTextView = (TextView) itemView.findViewById(R.id.ShowEmAddressTextView);
            EmRelationshipTextView = (TextView) itemView.findViewById(R.id.ShowEmRelationshipTextView);

        }
    }
}
