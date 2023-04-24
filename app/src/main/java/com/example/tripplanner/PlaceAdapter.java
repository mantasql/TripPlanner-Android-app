package com.example.tripplanner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripplanner.models.PlaceInfo;
import com.example.tripplanner.models.TripPlan;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<PlaceInfo> placeInfos = new ArrayList<>();
    private Context mContext;

    public PlaceAdapter(Context mContext, ArrayList<PlaceInfo> placeInfos) {
        this.placeInfos = placeInfos;
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

        holder.destinationName.setText(placeInfos.get(holder.getAdapterPosition()).getName());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked on item");
                Toast.makeText(mContext, placeInfos.get(holder.getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView destinationName;
        RelativeLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            destinationName = itemView.findViewById(R.id.destination_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
