package com.example.tripplanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;

import com.example.tripplanner.models.TripPlan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupTripFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private DatabaseReference planRef;
    private TripPlanActivityWindow tripPlanActivity;
    private GroupTripAdapter groupAdapter;
    private TripPlan tripPlan;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupTripFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupTripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupTripFragment newInstance(String param1, String param2) {
        GroupTripFragment fragment = new GroupTripFragment();
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
                groupAdapter = new GroupTripAdapter(getContext(), tripPlan);
                recyclerView.setAdapter(groupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_trip, container, false);

        recyclerView = rootView.findViewById(R.id.budget_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        BottomNavigationView bottomNav = rootView.findViewById(R.id.group_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        return rootView;
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.add_friend_item) {
            item.getActionView();
            openAddFriendWindow(getView());
        } else if (itemId == R.id.add_budget_item) {
            openAddBudgetWindow(getView());
        } else if (itemId == R.id.split_budget_item) {
            splitBudget(getView());
        }

        return true;
    };

    private void splitBudget(View view) {
    }

    private void openAddBudgetWindow(View view) {
    }

    private void openAddFriendWindow(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());
        LayoutInflater li = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View addFriendPopupView = li.inflate(R.layout.fragment_add_friend, null);

        EditText addFriendEditText = addFriendPopupView.findViewById(R.id.add_friend_edit_text);
        Button addBtn = addFriendPopupView.findViewById(R.id.add_friend_btn);
        Button closeBtn = addFriendPopupView.findViewById(R.id.add_friend_cancel);

        dialogBuilder.setView(addFriendPopupView);
        Dialog dialog = dialogBuilder.create();
        dialog.show();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save to db
                if(addFriendEditText.getText() != null)
                {
                    tripPlan.getTripFriends().add(addFriendEditText.getText().toString());
                    planRef.child("tripFriends").setValue(tripPlan.getTripFriends()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}