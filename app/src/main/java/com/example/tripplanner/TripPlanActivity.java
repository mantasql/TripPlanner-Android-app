package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripplanner.models.TripPlan;
import com.example.tripplanner.utils.DateFormat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

public class TripPlanActivity extends AppCompatActivity {

    private static final String TAG = "TripPlanActivity";
    private EditText planName;
    private EditText startDate;
    private EditText endDate;
    private EditText description;
    private ImageView mapsBtn;
    private TripPlan tripPlan;
    private FirebaseUser user;
    private ItineraryAdapter itineraryAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference planRef;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_plan);

        this.planName = findViewById(R.id.edittext_plan_name);
        this.startDate = findViewById(R.id.edittext_start_date);
        this.endDate = findViewById(R.id.edittext_end_date);
        this.description = findViewById(R.id.edittext_description);
        this.mapsBtn = findViewById(R.id.goto_maps_btn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = FirebaseAuth.getInstance().getCurrentUser();

        String planNo = getIntent().getExtras().getString("planNo");

        if (planNo.equals("-1") && tripPlan == null)
        {
            String id = mDatabase.child("users").child(user.getUid()).child("plans").push().getKey();
            tripPlan = new TripPlan(id, "My trip plan");
            mDatabase.child("users").child(user.getUid()).child("plans").child(tripPlan.getId()).setValue(tripPlan).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    listenToDataChange();
                }
            });
        }
        else
        {
            listenToDataChange();
        }
    }

    private void init()
    {
        recyclerView = findViewById(R.id.itinerariesView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        planName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(TAG, "onFocusChange: focus changed plan name!");
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

        mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripPlanActivity.this, MapsActivity.class);
                intent.putExtra("planNo", tripPlan.getId());
                startActivity(intent);
            }
        });
    }

    private void listenToDataChange()
    {
        String planNo = getIntent().getExtras().getString("planNo");

        if (planNo.equals("-1"))
        {
            planNo = tripPlan.getId();
        }

        planRef = mDatabase.child("users").child(user.getUid()).child("plans").child(planNo);
        init();

        planRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripPlan = snapshot.getValue(TripPlan.class);
                if (tripPlan != null)
                {
                    planName.setText(tripPlan.getTitle(), TextView.BufferType.EDITABLE);
                    startDate.setText(tripPlan.getStartOnlyDate(), TextView.BufferType.EDITABLE);
                    endDate.setText(tripPlan.getEndOnlyDate(), TextView.BufferType.EDITABLE);
                    description.setText(tripPlan.getDescription(), TextView.BufferType.EDITABLE);
                    Log.d(TAG, "onDataChange: tripPlan: " + tripPlan);
                    itineraryAdapter = new ItineraryAdapter(TripPlanActivity.this, tripPlan.getItinerary());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trip_plan, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete_trip_plan:
                mDatabase.child("users").child(user.getUid()).child("plans").child(tripPlan.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(TripPlanActivity.this, "Trip plan removed successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TripPlanActivity.this, MainActivity.class));
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openStartDatePicker()
    {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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
}