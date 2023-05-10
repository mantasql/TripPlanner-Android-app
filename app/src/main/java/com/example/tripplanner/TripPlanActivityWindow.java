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
            tripPlan = new TripPlan(id, "My trip plan");
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
        } else if (itemId == R.id.group_trip) {
            selectedFragment = new TripPlanFragment();
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
                mDatabase.child("users").child(user.getUid()).child("plans").child(tripPlan.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(TripPlanActivityWindow.this, "Trip plan removed successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TripPlanActivityWindow.this, MainActivity.class));
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public DatabaseReference getPlanRef() {
        return planRef;
    }

    public TripPlan getTripPlan() {
        return tripPlan;
    }
}