package com.example.tripplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripplanner.models.TripPlan;

import java.util.ArrayList;

public class GroupTripAdapter extends RecyclerView.Adapter<GroupTripAdapter.ViewHolder> {


    private Context mContext;
    private TripPlan tripPlan;
    private ArrayList<Integer> dummyData;

    public GroupTripAdapter(Context mContext, TripPlan tripPlan) {
        this.mContext = mContext;
        this.tripPlan = tripPlan;

        dummyData = new ArrayList<Integer>(){
            {
                add(100);
                add(250);
                add(300);
                add(50);
            }
        };
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_view_layout_budget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.friendEmail.setText(tripPlan.getTripFriends().get(holder.getAdapterPosition()));
        holder.budgetInt.setText(dummyData.get(holder.getAdapterPosition()).toString());
    }

    @Override
    public int getItemCount() {
        return tripPlan.getTripFriends().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView friendEmail;
        TextView budgetInt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friendEmail = itemView.findViewById(R.id.group_friend);
            budgetInt = itemView.findViewById(R.id.budget_int);
        }
    }
}
