package com.example.pathways;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainActivityUnitTest {
    @Mock TripDao mockTripDao;
    @Mock UserDao mockUserDao;


    MainActivity activity;
    UserEntity user = new UserEntity();

    private void sleep(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void initAll() {
        activity = new MainActivity();
        activity._tripDao = mockTripDao;
        activity._userDao = mockUserDao;
        activity._user = user;
        activity._tripNameList = new ArrayList<>();
        activity._tripList = new ArrayList<>();
    }

    @Test
    public void testCreateTrip() {
        TripEntity testTrip = new TripEntity();
        testTrip.tripName = "test trip";

        when(mockTripDao.insert(testTrip)).thenReturn(1l);
        doNothing().when(mockUserDao).updateUser(user);

        // The call to createTrip
        activity.createTrip(testTrip);

        assertTrue("Trip added to _tripList", activity._tripList.contains(testTrip));
        assertTrue("Trip name added to _tripNameList", activity._tripNameList.contains(testTrip.tripName));
        assertTrue("noteDao insert is called and TripEntity receives ID", testTrip.tripid == 1l);
        assertTrue("tripId added to user", user.tripIds.contains(1l));
    }

    @Test
    public void testPopulateTrips() {
        UserEntity testUser = new UserEntity();
        testUser.tripIds = new ArrayList<>();

        testUser.tripIds.add(1l);
        TripEntity tripOne = new TripEntity();
        tripOne.tripid = 1l;
        tripOne.tripName = "first";
        when(mockTripDao.findByID(1l)).thenReturn(tripOne);

        testUser.tripIds.add(2l);
        TripEntity tripTwo = new TripEntity();
        tripTwo.tripid = 2l;
        tripTwo.tripName = "second";
        when(mockTripDao.findByID(2l)).thenReturn(tripTwo);

        testUser.tripIds.add(3l);
        TripEntity tripThree = new TripEntity();
        tripThree.tripid = 3l;
        tripThree.tripName = "third";
        when(mockTripDao.findByID(3l)).thenReturn(tripThree);

        activity.populateTrips(testUser);
        sleep(2000);

        assertTrue("Trip One added to _tripList", activity._tripList.contains(tripOne));
        assertTrue("Trip Two added to _tripList", activity._tripList.contains(tripTwo));
        assertTrue("Trip Three added to _tripList", activity._tripList.contains(tripThree));

        assertTrue("Trip One added to _tripNameList", activity._tripNameList.contains(tripOne.tripName));
        assertTrue("Trip Two added to _tripNameList", activity._tripNameList.contains(tripTwo.tripName));
        assertTrue("Trip Three added to _tripNameList", activity._tripNameList.contains(tripThree.tripName));
    }

}
