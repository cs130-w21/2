package com.example.pathways;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Context;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageViewActivity extends AppCompatActivity {
    List<ImageView> targetImages = new ArrayList<>();
    List<String> ImageTexts = new ArrayList<>();
    ImageView i0;
    String ImageText;
    int numImages = 0;
    //int maxImages = 6;
    private AppDatabase _db;
    private TripDao _tripDao;
    private Executor _executor = Executors.newSingleThreadExecutor();
    private ImageListAdapter _imagesListAdapter;
    private Trip _trip;
    // Database entity
    private TripEntity _tripEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Button buttonLoadImage = (Button)findViewById(R.id.loadimage);
        //i0 = (ImageView) findViewById(R.id.targetimage0);

        _db = DatabaseSingleton.getInstance(this);
        _tripDao = _db.tripDao();

        RecyclerView imagesList = findViewById(R.id.images_list_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        imagesList.setLayoutManager(layoutManager);

        Long tripId = (Long) getIntent().getLongExtra("TRIP ID", 0);
        Log.v("ID", tripId + "");
        _executor.execute(() -> {
            TripEntity tripEntity = _tripDao.findByID(tripId);
            _tripEntity = tripEntity;
            Log.v("NAME", tripEntity.tripName);
            _trip = new Trip(_tripEntity.tripName);
            List<Bitmap> savedImages = _tripEntity.imageBitmaps;
            List<String> ImageTexts = _tripEntity.imageText;

            if (savedImages == null)
            {
                _imagesListAdapter = new ImageListAdapter(this, _trip.getImages(), _trip.getImageNames());
            }
            else {
                _trip.setImages(savedImages, ImageTexts);
                int numSavedImages = _trip.getNumImages();
                Log.v("NUM_SAVED_IMAGES", numSavedImages + "");

                _imagesListAdapter = new ImageListAdapter(this, savedImages, ImageTexts);
            }
            imagesList.setAdapter(_imagesListAdapter);
        });


        buttonLoadImage.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Context c = ImageViewActivity.this;
                EditText taskEditText = new EditText(c);
                AlertDialog dialog = new AlertDialog.Builder(c)
                        .setTitle("Location")
                        .setMessage(" ")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ImageText = String.valueOf(taskEditText.getText());
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, 0);

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
            }});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            try{
                BitmapFactory.Options dbo = new BitmapFactory.Options();
                dbo.inSampleSize = 6;

                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri), null, dbo);
                _trip.addImage(bitmap, ImageText);
                _tripEntity.imageBitmaps = _trip.getImages();
                _tripEntity.imageText = _trip.getImageNames();

                _executor.execute(() -> _tripDao.updateTrips(_tripEntity));

                _imagesListAdapter.notifyDataSetChanged();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
    }
}
