package com.example.tripplanner;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.tripplanner.models.Itinerary;
import com.example.tripplanner.models.PlaceInfo;
import com.example.tripplanner.models.TripPlan;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TripPlanUnitTests {
    ArrayList<PlaceInfo> placeInfos;

    @Before
    public void setUp(){
        placeInfos = new ArrayList<PlaceInfo>()
        {
            {
                add(new PlaceInfo("0", "name1", "address1", "phone1", null, null, null, null));
                add(new PlaceInfo("1", "name2", "address2", "phone2", null, null, null, null));
                add(new PlaceInfo("2", "name3", "address3", "phone3", null, null, null, null));
                add(new PlaceInfo("3", "name4", "address4", "phone4", null, null, null, null));
                add(new PlaceInfo("4", "name5", "address5", "phone5", null, null, null, null));
            }
        };
    }

    @Test
    public void getSortedItineraries_isSuccessful() {
        ArrayList<Itinerary> actual = new ArrayList<Itinerary>()
        {
            {
                add(new Itinerary(0, placeInfos.get(0)));
                add(new Itinerary(1, placeInfos.get(1)));
                add(new Itinerary(2, placeInfos.get(2)));
                add(new Itinerary(3, placeInfos.get(3)));
                add(new Itinerary(4, placeInfos.get(4)));
            }
        };

        TripPlan tripPlanActual = new TripPlan();
        HashMap<String, Itinerary> actualIt = new HashMap<>();
        actualIt.put("1", new Itinerary(2, placeInfos.get(0)));
        actualIt.put("2", new Itinerary(3, placeInfos.get(1)));
        actualIt.put("3", new Itinerary(1, placeInfos.get(2)));
        actualIt.put("4", new Itinerary(4, placeInfos.get(3)));
        actualIt.put("5", new Itinerary(0, placeInfos.get(4)));
        tripPlanActual.setItineraryHash(actualIt);

        //tripPlanActual.getItinerariesFromHash();

        //assertArrayEquals(tripPlanActual.getItinerariesFromHash().toArray(), actual.toArray());
    }

    @Test
    public void removeItinerary_isSuccessful() {
        TripPlan tripPlanActual = new TripPlan();
        HashMap<String, Itinerary> actualIt = new HashMap<>();
        actualIt.put("1", new Itinerary(0, placeInfos.get(0)));
        actualIt.put("2", new Itinerary(1, placeInfos.get(1)));
        actualIt.put("3", new Itinerary(2, placeInfos.get(2)));
        actualIt.put("4", new Itinerary(3, placeInfos.get(3)));
        actualIt.put("5", new Itinerary(4, placeInfos.get(4)));
        tripPlanActual.setItineraryHash(actualIt);

        ArrayList<Itinerary> actual1 = new ArrayList<Itinerary>()
        {
            {
                add(new Itinerary(0, placeInfos.get(0)));
                add(new Itinerary(1, placeInfos.get(2)));
                add(new Itinerary(2, placeInfos.get(3)));
                add(new Itinerary(3, placeInfos.get(4)));
            }
        };

        ArrayList<Itinerary> actual2 = new ArrayList<Itinerary>()
        {
            {
                add(new Itinerary(0, placeInfos.get(0)));
                add(new Itinerary(1, placeInfos.get(2)));
                add(new Itinerary(2, placeInfos.get(4)));
            }
        };

        ArrayList<Itinerary> actual3 = new ArrayList<Itinerary>()
        {
            {
                add(new Itinerary(0, placeInfos.get(2)));
                add(new Itinerary(1, placeInfos.get(4)));
            }
        };

/*        tripPlanActual.removeItinerary(1);
        assertArrayEquals(tripPlanActual.getItineraries().toArray(), actual.toArray());
        actual.remove(2);
        tripPlanActual.removeItinerary(3);
        assertArrayEquals(tripPlanActual.getItineraries().toArray(), actual.toArray());
        actual.remove(2);
        tripPlanActual.removeItinerary(4);
        assertArrayEquals(tripPlanActual.getItineraries().toArray(), actual.toArray());*/

        tripPlanActual.removeItinerary(1);
        //assertArrayEquals(tripPlanActual.getItinerariesFromHash().toArray(), actual1.toArray());
        tripPlanActual.removeItinerary(2);
        //assertArrayEquals(tripPlanActual.getItinerariesFromHash().toArray(), actual2.toArray());
        tripPlanActual.removeItinerary(0);
        //assertArrayEquals(tripPlanActual.getItinerariesFromHash().toArray(), actual3.toArray());
    }
}