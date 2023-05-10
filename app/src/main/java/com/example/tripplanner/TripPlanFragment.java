package com.example.tripplanner;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tripplanner.models.TripPlan;
import com.example.tripplanner.utils.DateFormat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TripPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripPlanFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "TripPlanFragment";

    private TripPlanActivityWindow tripPlanActivity;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText planName;
    private EditText startDate;
    private EditText endDate;
    private EditText description;
    private TextView tripDuration;
    private TripPlan tripPlan;
    private ItineraryAdapter itineraryAdapter;
    private RecyclerView recyclerView;
    private DatabaseReference planRef;

    public TripPlanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TripPlanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TripPlanFragment newInstance(String param1, String param2) {
        TripPlanFragment fragment = new TripPlanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        tripPlanActivity = (TripPlanActivityWindow) context;
        planRef = tripPlanActivity.getPlanRef();

        planRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripPlan = snapshot.getValue(TripPlan.class);
                if (tripPlan != null)
                {
                    String durationText = "Trip duration ≈ ";
                    planName.setText(tripPlan.getTitle(), TextView.BufferType.EDITABLE);
                    startDate.setText(tripPlan.getStartOnlyDate(), TextView.BufferType.EDITABLE);
                    endDate.setText(tripPlan.getEndOnlyDate(), TextView.BufferType.EDITABLE);
                    description.setText(tripPlan.getDescription(), TextView.BufferType.EDITABLE);
                    tripDuration.setText(durationText + tripPlan.getTripDuration());
                    itineraryAdapter = new ItineraryAdapter(getContext(), tripPlan.getItinerary());
                    recyclerView.setAdapter(itineraryAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: Error getting data: " + error.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip_plan, container, false);

        planName = rootView.findViewById(R.id.edittext_plan_name);
        startDate = rootView.findViewById(R.id.edittext_start_date);
        endDate = rootView.findViewById(R.id.edittext_end_date);
        description = rootView.findViewById(R.id.edittext_description);
        tripDuration = rootView.findViewById(R.id.approx_duration);
        recyclerView = rootView.findViewById(R.id.itinerariesView);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        planName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                {
                    planRef.child("title").setValue(planName.getText().toString());
                }
            }
        });

        startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                {
                    openStartDatePicker();
                }
            }
        });

        endDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                {
                    openEndDatePicker();
                }
            }
        });

        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                {
                    planRef.child("description").setValue(description.getText().toString());
                }
            }
        });

        // Return the root view
        return rootView;
    }

    private void openStartDatePicker()
    {
        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1++;
                startDate.setText(i + "-" + i1 + "-" + i2);
                try {
                    planRef.child("startDate").setValue(new DateFormat().getDate(startDate.getText().toString()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 2023, 0, 1);

        dialog.show();
    }

    private void openEndDatePicker()
    {
        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1++;
                endDate.setText(i + "-" + i1 + "-" + i2);
                try {
                    planRef.child("endDate").setValue(new DateFormat().getDate(endDate.getText().toString()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 2023, 0, 1);

        dialog.show();
    }

    public void saveTime(int value, int position)
    {
        Map<String, Object> update = new HashMap<String, Object>();
        update.put("extraDurationValue", value);
        planRef.child("itinerary").child(String.valueOf(position)).updateChildren(update);
    }
}