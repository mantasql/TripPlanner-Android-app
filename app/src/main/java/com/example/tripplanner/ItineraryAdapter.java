package com.example.tripplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripplanner.models.Itinerary;
import com.example.tripplanner.models.PlaceInfo;
import com.example.tripplanner.utils.DateFormat;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.util.ArrayList;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder>{

    private static final String TAG = "ItineraryAdapter";
    private ArrayList<Itinerary> itineraries;
    private Context mContext;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Integer extraTimeValue = 0;

    public ItineraryAdapter(Context mContext, ArrayList<Itinerary> itineraries) {
        this.itineraries = itineraries;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext instanceof TripPlanActivity)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tripplan_listitem, parent, false);
            return new ViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.destinationName.setText(itineraries.get(holder.getAdapterPosition()).getPlace().getName());

        if (itineraries.get(holder.getAdapterPosition()).getDuration() == null)
        {
            holder.duration.setText("");
        }
        else if (mContext instanceof MapsActivity)
        {
            holder.duration.setText(itineraries.get(holder.getAdapterPosition()).getDuration());
        }
        else
        {
            holder.duration.setText(itineraries.get(holder.getAdapterPosition()).getOverallDuration());
        }

        if (mContext instanceof TripPlanActivity)
        {
            holder.addTimeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAddTimeWindow(view, holder.getAdapterPosition());
                }
            });
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked on item");
                Toast.makeText(mContext, itineraries.get(holder.getAdapterPosition()).getPlace().getName(), Toast.LENGTH_SHORT).show();
                if (mContext instanceof MapsActivity)
                {
                    ((MapsActivity)mContext).geoLocate(new LatLng(itineraries.get(holder.getAdapterPosition()).getPlace().getLatLng().getLatitude(),
                                    itineraries.get(holder.getAdapterPosition()).getPlace().getLatLng().getLongitude()));
                }

                openLocationInfo(view, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itineraries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView destinationName;
        TextView duration;
        RelativeLayout parentLayout;
        ImageView addTimeBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            destinationName = itemView.findViewById(R.id.destination_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            duration = itemView.findViewById(R.id.duration_text);
            addTimeBtn = itemView.findViewById(R.id.add_time);
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

    private void openAddTimeWindow(View view, int position)
    {
        dialogBuilder = new AlertDialog.Builder(view.getContext());
        LayoutInflater li = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View timePopupView = li.inflate(R.layout.fragment_add_time, null);

        EditText timeText = timePopupView.findViewById(R.id.add_time_edit_text);
        Button saveTimeBtn = timePopupView.findViewById(R.id.save_time_btn);
        Button cancelTimeBtn = timePopupView.findViewById(R.id.add_time_cancel);
        ImageView timeClearBtn = timePopupView.findViewById(R.id.time_clear_btn);

        timeText.setText(String.valueOf(itineraries.get(position).getExtraDuration()), TextView.BufferType.EDITABLE);

        extraTimeValue = 0;

        dialogBuilder.setView(timePopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        timeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                {
                    openTimePicker(timeText);
                }
            }
        });

        timeClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeText.setText("0");
                if (mContext instanceof TripPlanActivity)
                {
                    ((TripPlanActivity)mContext).saveTime(0,position);
                }
            }
        });

        saveTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mContext instanceof TripPlanActivity)
                {
                    ((TripPlanActivity)mContext).saveTime(extraTimeValue,position);
                }

                dialog.dismiss();
            }
        });

        cancelTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void openTimePicker(EditText timeText)
    {
        TimePickerDialog dialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                extraTimeValue = (hours * 3600) + minutes * 60;
                timeText.setText(String.format("%02d:%02d", hours, minutes));
            }
        }, 0, 00, true);

        dialog.show();
    }
}
