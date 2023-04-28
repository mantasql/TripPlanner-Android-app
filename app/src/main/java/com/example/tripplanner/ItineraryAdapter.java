package com.example.tripplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripplanner.models.Itinerary;
import com.example.tripplanner.models.PlaceInfo;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder>{

    private static final String TAG = "ItineraryAdapter";
    private ArrayList<Itinerary> itineraries;
    private Context mContext;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    public ItineraryAdapter(Context mContext, ArrayList<Itinerary> itineraries) {
        this.itineraries = itineraries;
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

        holder.destinationName.setText(itineraries.get(holder.getAdapterPosition()).getPlace().getName());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked on item");
                Toast.makeText(mContext, itineraries.get(holder.getAdapterPosition()).getPlace().getName(), Toast.LENGTH_SHORT).show();
                openLocationInfo(view, holder.getAdapterPosition());
                if (mContext instanceof MapsActivity)
                {
                    ((MapsActivity)mContext).geoLocate(new LatLng(itineraries.get(holder.getAdapterPosition()).getPlace().getLatLng().getLatitude(),
                                    itineraries.get(holder.getAdapterPosition()).getPlace().getLatLng().getLongitude()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itineraries.size();
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

    private void openLocationInfo(View view, int position)
    {
        dialogBuilder = new AlertDialog.Builder(view.getContext());
        LayoutInflater li = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View locationPopupView = li.inflate(R.layout.fragment_location_info, null);

        TextView locationName = locationPopupView.findViewById(R.id.location_info_text);
        TextView addressText = locationPopupView.findViewById(R.id.address_info_text);
        TextView phoneText = locationPopupView.findViewById(R.id.phone_info_text);
        TextView ratingText = locationPopupView.findViewById(R.id.rating_info_text);
        TextView priceLevelText = locationPopupView.findViewById(R.id.price_level_info_text);
        TextView websiteText = locationPopupView.findViewById(R.id.website_info_text);
        Button closeBtn = locationPopupView.findViewById(R.id.close_info_btn);

        locationName.setText(itineraries.get(position).getPlace().getName());
        addressText.setText(itineraries.get(position).getPlace().getAddress());
        phoneText.setText(phoneText.getText() + itineraries.get(position).getPlace().getPhoneInfo());
        ratingText.setText(ratingText.getText() + itineraries.get(position).getPlace().getRatingInfo());
        priceLevelText.setText(priceLevelText.getText() + itineraries.get(position).getPlace().getPriceLevelInfo());
        websiteText.setText(websiteText.getText() + itineraries.get(position).getPlace().getWebsiteInfo());

        dialogBuilder.setView(locationPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
