package com.example.tripplanner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripplanner.models.TripPlan;
import com.example.tripplanner.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class GroupTripAdapter extends RecyclerView.Adapter<GroupTripAdapter.ViewHolder> {

    private static final String TAG = "GroupTripAdapter";
    private Context mContext;
    private TripPlan tripPlan;
    private ArrayList<String> users;
    private ArrayList<Integer> budget;

    private FirebaseUser user;

    public GroupTripAdapter(Context mContext, TripPlan tripPlan) {
        this.mContext = mContext;
        this.tripPlan = tripPlan;
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (tripPlan == null)
        {
            return;
        }

        refreshArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_view_layout_budget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (tripPlan.getTravelers().size() == 0)
        {
            return;
        }

        Log.d(TAG, "onBindViewHolder: users size: " + users.size());
        Log.d(TAG, "onBindViewHolder: Adapter pos: " + holder.getAdapterPosition());

        if (holder.getAdapterPosition() >= users.size())
        {
            refreshArray();
        }

        if (users.size() != 0 || budget.size() != 0)
        {
            holder.friendEmail.setText(users.get(holder.getAdapterPosition()));
            holder.budgetInt.setText(budget.get(holder.getAdapterPosition()).toString() + " €");
        }
    }

    @Override
    public int getItemCount() {
        if (tripPlan == null)
        {
            return 0;
        }

        if (tripPlan.getTravelers().size() == 0)
        {
            return 0;
        }

        return tripPlan.getTravelers().size() - 1;
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

    private void refreshArray()
    {
        users = new ArrayList<>(tripPlan.getTravelers().keySet());
        budget = new ArrayList<>(tripPlan.getTravelers().values());

        users.replaceAll(s -> s.replace('_', '.'));

        for (int i = 0; i < users.size(); i++)
        {
            if (users.get(i).equals(user.getEmail()))
            {
                users.remove(i);
                budget.remove(i);
                break;
            }
        }
    }
}
