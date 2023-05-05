package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tripplanner.models.Itinerary;
import com.example.tripplanner.models.PlaceInfo;
import com.example.tripplanner.models.TripPlan;
import com.example.tripplanner.models.User;
import com.example.tripplanner.utils.ItineraryComparator;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.tripplanner.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 20f;
    private PlacesClient placesClient;
    private ImageView mGps;
    private ImageView mDriving;
    private ImageView mWalking;
    private ImageView mBicycling;
    private TravelMode travelMode = TravelMode.DRIVING;
    private AutocompleteSupportFragment autocompleteFragment;
    private RecyclerView tripsList;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private Location currentLocation;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    //private ItineraryAdapter adapter;
    private List<Place.Field> placeFields;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private TripPlan tripPlan;

    private DatabaseReference mDatabase;
    private DatabaseReference planRef;
    private ValueEventListener tripPlanDataListener;
    private User loggedInUser;
    private RecyclerView recyclerView;
    private boolean init = true;

    private ArrayList<String> durations = new ArrayList<>();

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
            | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT) {

        boolean isDragging = false;
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            if (isDragging)
            {
                tripPlan.getItinerary().get(fromPosition).setPosition(toPosition);
                tripPlan.getItinerary().get(toPosition).setPosition(fromPosition);
                Collections.swap(tripPlan.getItinerary(), fromPosition, toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            }
            return false;
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);

            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && isDragging)
            {
                planRef.child("itinerary").setValue(tripPlan.getItinerary());
                isDragging = false;
            }
            else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG)
            {
                isDragging = true;
            }
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction)
            {
                case ItemTouchHelper.LEFT:
                    tripPlan.removeAndRearrange(position);
                    planRef.child("itinerary").setValue(tripPlan.getItinerary());
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MapsActivity.this, com.google.android.libraries.places.R.color.quantum_googred400))
                    .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.ICON_URL,
                Place.Field.WEBSITE_URI, Place.Field.RATING, Place.Field.PHONE_NUMBER, Place.Field.PRICE_LEVEL);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mDriving = (ImageView) findViewById(R.id.ic_driving);
        mWalking = (ImageView) findViewById(R.id.ic_walking);

        tripsList = findViewById(R.id.TripsList);

        mDatabase = FirebaseDatabase.getInstance("https://trip-planner-21c97-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        if (!Places.isInitialized())
        {
            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        }
        placesClient = Places.createClient(this);

        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(placeFields);

        getLocationPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String planNo = getIntent().getExtras().getString("planNo");
        planRef = mDatabase.child("users").child(user.getUid()).child("plans").child(planNo);
        initPlaces();

        tripPlanDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripPlan = snapshot.getValue(TripPlan.class);
                tripPlan.getItinerary().sort(new ItineraryComparator());

                direction(travelMode);
                updateRecyclerView();
                init = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onComplete: Error getting data: " + error.getMessage());
            }
        };
    }

    private void initPlaces()
    {
        recyclerView = findViewById(R.id.TripsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void init()
    {
        Log.d(TAG, "init: initializing");
        mDriving.setBackgroundColor(getResources().getColor(com.google.android.libraries.places.R.color.quantum_googblue300));

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        mDriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDriving.setBackgroundColor(getResources().getColor(com.google.android.libraries.places.R.color.quantum_googblue300));
                mWalking.setBackgroundColor(Color.TRANSPARENT);
                travelMode = TravelMode.DRIVING;
                direction(travelMode);
            }
        });

        mWalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWalking.setBackgroundColor(getResources().getColor(com.google.android.libraries.places.R.color.quantum_googblue300));
                mDriving.setBackgroundColor(Color.TRANSPARENT);
                travelMode = TravelMode.WALKING;
                direction(travelMode);
            }
        });

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Log.d(TAG, "An error occurred: " + status);
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                geoLocate(place.getLatLng());
                openLocationInfo(new PlaceInfo(place));
            }
        });

        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(@NonNull PointOfInterest pointOfInterest) {
                FetchPlaceRequest request = FetchPlaceRequest.newInstance(pointOfInterest.placeId, placeFields);

                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                   Place place = response.getPlace();

                   geoLocate(place.getLatLng());
                   openLocationInfo(new PlaceInfo(place));

                }).addOnFailureListener((exception) ->{
                    if (exception instanceof ApiException)
                    {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();

                        //handle error

                        Log.e(TAG, "onPoiClick: Place not found: " + exception.getMessage());
                    }
                });
            }
        });

        hideSoftKeyboard();
    }

    public void geoLocate(LatLng latLng) {

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e)
        {
            Log.d(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0)
        {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();

        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
    }

    private void getDeviceLocation()
    {
        Log.d(TAG, "getDeviceLocation: getting current device location");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted)
            {
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: found location");
                            currentLocation = (Location) task.getResult();

                            if (init)
                            {
                                planRef.addValueEventListener(tripPlanDataListener);
                            }

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM, "My location");
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: location not found");
                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        catch (SecurityException e)
        {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void getLocationPermission()
    {
        String[] permissions =
                {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                };

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionsGranted = true;
                initMap();
            }
        }

        if (!mLocationPermissionsGranted)
        {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;

        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE:
            {
                if (grantResults.length > 0)
                {
                    for (int i = 0; i < grantResults.length; i++)
                    {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title)
    {
        Log.d(TAG, "moveCamera: moving camera to lat: " + latLng.latitude + " lng: " + latLng.longitude);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        hideSoftKeyboard();
    }

    private void hideSoftKeyboard()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void openLocationInfo(PlaceInfo place)
    {
        dialogBuilder = new AlertDialog.Builder(this);
        final View locationPopupView = getLayoutInflater().inflate(R.layout.fragment_add_location, null);

        TextView locationName = locationPopupView.findViewById(R.id.location_text);
        TextView addressText = locationPopupView.findViewById(R.id.address_text);
        Button addLocationBtn = locationPopupView.findViewById(R.id.add_location);
        Button addLocationCancelBtn = locationPopupView.findViewById(R.id.add_location_cancel);

        locationName.setText(place.getName());
        addressText.setText(place.getAddress());

        dialogBuilder.setView(locationPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        addLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geoLocate(new LatLng(place.getLatLng().getLatitude(), place.getLatLng().getLongitude()));
                Itinerary it = new Itinerary(tripPlan.getItinerary().size(), place);
                tripPlan.getItinerary().add(it);
                planRef.child("itinerary").setValue(tripPlan.getItinerary()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                    }
                });

            }
        });

        addLocationCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void direction(TravelMode travelMode)
    {
        mMap.clear();

        if (tripPlan.getItinerary().size() == 0)
        {
            return;
        }

        com.example.tripplanner.models.LatLng destinationLatLng = tripPlan.getItinerary().get(tripPlan.getItinerary().size() - 1).getPlace().getLatLng();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Uri.Builder urlBuilder = Uri.parse("https://maps.googleapis.com/maps/api/directions/json")
                .buildUpon()
                .appendQueryParameter("destination", destinationLatLng.getLatitude() + ", " + destinationLatLng.getLongitude())
                .appendQueryParameter("origin", currentLocation.getLatitude() + ", " + currentLocation.getLongitude())
                .appendQueryParameter("key", BuildConfig.MAPS_API_KEY)
                .appendQueryParameter("mode", travelMode.mode)
                .appendQueryParameter("departure_time", "now");

        if (tripPlan.getItinerary().size() > 1)
        {
            StringBuilder stopOversSB = new StringBuilder();
            for (int i = 0 ; i < tripPlan.getItinerary().size() - 1; i++)
            {
                com.example.tripplanner.models.LatLng dest =  tripPlan.getItinerary().get(i).getPlace().getLatLng();
                stopOversSB.append(dest.getLatitude() + "," + dest.getLongitude() + "|");
            }
            stopOversSB.deleteCharAt(stopOversSB.length() - 1);
            urlBuilder.appendQueryParameter("waypoints", stopOversSB.toString());
        }

        String url = urlBuilder.toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    Log.d(TAG, "onResponse: STATUS CODE: " + status);
                    if (status.equals("OK"))
                    {
                        JSONArray routes = response.getJSONArray("routes");

                        ArrayList<LatLng> points;
                        PolylineOptions polylineOptions = null;

                        for (int i = 0; i < routes.length(); i++)
                        {
                            points = new ArrayList<>();
                            polylineOptions = new PolylineOptions();
                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");

                            for (int j = 0; j < legs.length(); j++)
                            {
                                JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");

                                String duration = legs.getJSONObject(j).optJSONObject("duration").getString("text");
                                String durationValue = legs.getJSONObject(j).optJSONObject("duration").getString("value");
                                tripPlan.getItinerary().get(j).setDuration(duration);
                                tripPlan.getItinerary().get(j).setDurationValue(Integer.valueOf(durationValue));
                                //durations.add(duration);
                                Log.d(TAG, "onResponse: duration: " + duration);

                                for (int k = 0; k < steps.length(); k++)
                                {
                                    String polyline = steps.getJSONObject(k).getJSONObject("polyline").getString("points");
                                    List<LatLng> list = decodePoly(polyline);

                                    for (int l = 0; l < list.size(); l++)
                                    {
                                        LatLng position = new LatLng((list.get(l)).latitude, (list.get(l)).longitude);
                                        points.add(position);
                                    }
                                }
                            }
                            polylineOptions.addAll(points);
                            polylineOptions.width(10);
                            polylineOptions.color(ContextCompat.getColor(MapsActivity.this, R.color.purple_500));
                            polylineOptions.geodesic(true);
                        }
                        mMap.addPolyline(polylineOptions);

                        for (int i = 0; i < tripPlan.getItinerary().size(); i++)
                        {
                            mMap.addMarker(new MarkerOptions().position(
                                    new LatLng(tripPlan.getItinerary().get(i).getPlace().getLatLng().getLatitude(), tripPlan.getItinerary().get(i).getPlace().getLatLng().getLongitude()))
                                    .title(tripPlan.getItinerary().get(i).getPlace().getName()));
                        }

                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(new LatLng(destinationLatLng.getLatitude(), destinationLatLng.getLongitude()))
                                .include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                                .build();

                        Point point = new Point();
                        getWindowManager().getDefaultDisplay().getSize(point);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, point.x, 150, 30));

                        Map<String, Object> update = new HashMap<String, Object>();
                        update.put("itinerary", tripPlan.getItinerary());
                        planRef.updateChildren(update);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                
            }
        });
        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        requestQueue.add(jsonObjectRequest);
    }

    private ArrayList<LatLng> decodePoly(String encoded) {

        Log.i("Location", "String received: "+encoded);
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(((double) lat / 1E5), ((double) lng / 1E5));
            poly.add(p);
        }

        for(int i=0;i<poly.size();i++){
            Log.i("Location", "Point sent: Latitude: "+poly.get(i).latitude+" Longitude: "+poly.get(i).longitude);
        }
        return poly;
    }

    private void updateRecyclerView()
    {
        ItineraryAdapter adapter = new ItineraryAdapter(MapsActivity.this, tripPlan.getItinerary());
        recyclerView.setAdapter(adapter);
    }
}