package com.example.pathways;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TripUnitTest {
    @Test
    public void testAddStartLocation() {
        Trip trip = new Trip();
        Location loc = new Location();

        trip.addStartLocation(loc);

        assertTrue("Location added", trip.getStartLocation().equals(loc));
    }

    @Test
    public void testAddEndLocation() {
        Trip trip = new Trip();
        Location loc = new Location();

        trip.addEndLocation(loc);

        assertTrue("Location added", trip.getEndLocation().equals(loc));
    }
}
