package com.example.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripplanner.models.TripPlan;

import java.util.ArrayList;

public class TripPlansAdapter extends RecyclerView.Adapter<TripPlansAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<TripPlan> tripPlans = new ArrayList<>();
    private Context mContext;

    public TripPlansAdapter(Context mContext, ArrayList<TripPlan> tripPlans) {
        this.tripPlans = tripPlans;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.destinationName.setText(tripPlans.get(holder.getAdapterPosition()).getTitle());

        holder.duration.setText("");
        
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked on item");
                Toast.makeText(mContext, tripPlans.get(holder.getAdapterPosition()).getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), TripPlanActivityWindow.class);
                Log.d(TAG, "onClick: my id: " + tripPlans.get(holder.getAdapterPosition()).getId());
                intent.putExtra("planNo", tripPlans.get(holder.getAdapterPosition()).getId());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripPlans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView duration;
        TextView destinationName;
        RelativeLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            destinationName = itemView.findViewById(R.id.destination_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            duration = itemView.findViewById(R.id.duration_text);
        }
    }
}
