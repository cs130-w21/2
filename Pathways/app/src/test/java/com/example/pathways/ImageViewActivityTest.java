package com.example.pathways;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;

@RunWith(MockitoJUnitRunner.class)
public class ImageViewActivityTest extends TestCase {
    private ImageViewActivity activity;

    @Mock
    ImageListAdapter mockAdapter;
    @Mock
    ImageDao mockImageDao;
    @Mock
    TripDao mockTripDao;

    @Before
    public void initAll() {
        activity = new ImageViewActivity();
        activity._imagesListAdapter = mockAdapter;
        activity._imageDao =  mockImageDao;
        activity._tripDao = mockTripDao;
    }


    @Test
    public void testAddImagetoDB() throws InterruptedException {
        ImageEntity testImage = new ImageEntity();
        ArrayList<ImageEntity> addedImages = new ArrayList<ImageEntity>();
        TripEntity tripEntity = new TripEntity();
        activity._tripEntity = tripEntity;
        when(mockImageDao.insertImage(any(ImageEntity.class))).thenReturn(0l);
        doNothing().when(mockTripDao).updateTrips(tripEntity);
        activity.TestingOnAct(testImage);
        Thread.sleep(2000);
        assertTrue("Image ID added to trip entity", tripEntity.imageIds.contains(0l));
        Assert.assertNotEquals("Image ID is not the wrong one",
                java.util.Optional.ofNullable(testImage.imageId),
                2l);
    }
}