package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripplanner.models.TripPlan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripPlanActivityWindow extends AppCompatActivity {
    private static final String TAG = "TripPlanActivity";
    private TripPlan tripPlan;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private DatabaseReference planRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_plan_window);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        BottomNavigationView bottomNav = findViewById(R.id.navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = FirebaseAuth.getInstance().getCurrentUser();

        String planNo = getIntent().getExtras().getString("planNo");

        if (planNo.equals("-1") && tripPlan == null)
        {
            String id = mDatabase.child("users").child(user.getUid()).child("plans").push().getKey();
            String email = user.getEmail().replace('.', '_');
            tripPlan = new TripPlan(id, "My trip plan", email);
            mDatabase.child("users").child(user.getUid()).child("plans").child(tripPlan.getId()).setValue(tripPlan).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    listenToDataChange();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TripPlanFragment()).commit();
                }
            });
        }
        else
        {
            listenToDataChange();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TripPlanFragment()).commit();
        }
    }

    private void listenToDataChange()
    {
        String planNo = getIntent().getExtras().getString("planNo");

        if (planNo.equals("-1"))
        {
            planNo = tripPlan.getId();
        }

        planRef = mDatabase.child("users").child(user.getUid()).child("plans").child(planNo);

        planRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripPlan = snapshot.getValue(TripPlan.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: Error getting data: " + error.getMessage());
            }
        });
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.goto_maps_btn) {
            Intent intent = new Intent(TripPlanActivityWindow.this, MapsActivity.class);
            intent.putExtra("planNo", tripPlan.getId());
            startActivity(intent);
        } else if (itemId == R.id.menu_plan) {
            selectedFragment = new TripPlanFragment();
        } else if (itemId == R.id.group_trip) {
            selectedFragment = new GroupTripFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
        return true;
    };

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
                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            // Delete the item for each user
                            userSnapshot.child("plans").child(tripPlan.getId()).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(TripPlanActivityWindow.this, "Trip plan removed successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(TripPlanActivityWindow.this, MainActivity.class));
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveTime(int value, int position)
    {
        Map<String, Object> update = new HashMap<String, Object>();
        update.put("extraDurationValue", value);
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> emails = new ArrayList<>(tripPlan.getTravelers().keySet());
                emails.replaceAll(s -> s.replace('_', '.'));

                for (DataSnapshot userSnapshot : snapshot.getChildren())
                {
                    String email = userSnapshot.child("email").getValue().toString();
                    for (int i = 0; i < emails.size(); i++)
                    {
                        if (email.equals(emails.get(i)))
                        {
                            userSnapshot.child("plans").child(tripPlan.getId()).child("itinerary").child(String.valueOf(position)).getRef().updateChildren(update);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //planRef.child("itinerary").child(String.valueOf(position)).updateChildren(update);
    }

    public DatabaseReference getPlanRef() {
        return planRef;
    }

    public TripPlan getTripPlan() {
        return tripPlan;
    }
}