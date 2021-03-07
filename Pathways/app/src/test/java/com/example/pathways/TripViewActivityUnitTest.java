package com.example.pathways;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TripViewActivityUnitTest {
    TripViewActivity activity;
    TripEntity tripEntity;

    @Mock
    TripDao mockTripDao;

    @Mock
    UserDao mockUserDao;

    @Before
    public void initAll () {
        activity = new TripViewActivity();
        activity._tripDao = mockTripDao;
        activity._userDao = mockUserDao;
        tripEntity = new TripEntity();
        activity._tripEntity = tripEntity;
    }

    @Test
    public void testDeleteTrip () throws InterruptedException {
        UserEntity user = new UserEntity();
        user.email = "email";
        user.tripIds = new ArrayList<>();
        user.tripIds.add(0l);
        tripEntity.tripid = 0l;
        activity._userEmail = user.email;
        when(mockUserDao.findByEmail(user.email)).thenReturn(user);

        activity.deleteTrip();
        Thread.sleep(2000);

        assertTrue("Trip ID removed from user entity", !user.tripIds.contains(0l));
    }

    @Test
    public void testGenerateTripFromTripEntity() {
        activity._tripEntity = new TripEntity();
        activity._tripEntity.tripName = "testName";

        activity.generateTripFromTripEntity();

        assertEquals("Trip updated with trip entity name", activity._trip._name, activity._tripEntity.tripName);
    }


}
