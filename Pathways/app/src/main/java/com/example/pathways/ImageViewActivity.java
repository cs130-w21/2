package com.example.pathways;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Context;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageViewActivity extends AppCompatActivity {
    private AppDatabase _db;
    private TripDao _tripDao;
    private ImageDao _imageDao;
    private Executor _executor = Executors.newSingleThreadExecutor();
    private ImageListAdapter _imagesListAdapter;
    // Database entity
    private TripEntity _tripEntity;
    private String _placeId = "";
    private String _locationName = "";
    private ArrayList<ImageEntity> _imageEntities = new ArrayList<>();
    private TextView _emptyNotesTextView;
    private TextView _locationTextView;

    /**
     * Initializes necessary components of Image page UI
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        FloatingActionButton buttonLoadImage = (FloatingActionButton) findViewById(R.id.loadimage);
        //i0 = (ImageView) findViewById(R.id.targetimage0);

        _db = DatabaseSingleton.getInstance(this);
        _tripDao = _db.tripDao();
        _imageDao = _db.imageDao();

        _emptyNotesTextView = findViewById(R.id.empty_images_text);


        RecyclerView imagesList = findViewById(R.id.images_list_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        imagesList.setLayoutManager(layoutManager);

        _imagesListAdapter = new ImageListAdapter(this, _imageEntities);
        imagesList.setAdapter(_imagesListAdapter);

        Long tripId = (Long) getIntent().getLongExtra("TRIP ID", 0);
        Log.v("ID", tripId + "");
        _executor.execute(() -> {
            TripEntity tripEntity = _tripDao.findByID(tripId);
            _tripEntity = tripEntity;

            addImagesFromImageIds();
        });

        _locationTextView = findViewById(R.id.location_textview);
        String[] idAndName = getIntent().getStringArrayExtra("PLACE ID AND NAME");
        if (idAndName != null) {
            _placeId = idAndName[0];
            _locationName = idAndName[1];

            String locationText = "Location: " + _locationName;
            _locationTextView.setText(locationText);
            _locationTextView.setVisibility(View.VISIBLE);

            String emptyNotesText = "Add images for the location: " + idAndName[1];
            _emptyNotesTextView.setText(emptyNotesText);
        }


        buttonLoadImage.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
    }

    /**
     * fetches image ids from TripEntity fetched from database and then gets the corresponding images
     */
    private void addImagesFromImageIds() {
        if (_tripEntity.imageIds == null) {
            _tripEntity.imageIds = new ArrayList<>();
        }

        ImageEntity imageEntity;
        for (Long imageId : _tripEntity.imageIds) {
            imageEntity = _imageDao.findById(imageId);
            // If only looking at one location, filter by placeId.
            if (_placeId.equals("") || _placeId.equals(imageEntity.placeId)) {
                _imageEntities.add(imageEntity);
                _emptyNotesTextView.setVisibility(View.GONE);
            }
        }
        _imagesListAdapter.notifyDataSetChanged();
    }

    /**
     * When selected image is done being fetched, ass image to database
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            try {
                getContentResolver().takePersistableUriPermission(targetUri, takeFlags);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            _emptyNotesTextView.setVisibility(View.GONE);

            ImageEntity imageEntity = new ImageEntity();
            imageEntity.locationName = _locationName;
            imageEntity.placeId = _placeId;
            imageEntity.imageUri = targetUri.toString();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            imageEntity.date = df.format(new Date());

            _executor.execute(() -> {
                Long imageId = _imageDao.insertImage(imageEntity);
                imageEntity.imageId = imageId;
                _imageEntities.add(imageEntity);
                runOnUiThread(() -> _imagesListAdapter.notifyDataSetChanged());

                if (_tripEntity.imageIds == null) {
                    _tripEntity.imageIds = new ArrayList<>();
                }

                _tripEntity.imageIds.add(imageId);
                _tripDao.updateTrips(_tripEntity);
            });


        }
    }
}
