package com.example.pathways;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.FileNotFoundException;

public class PhotoViewActivity extends AppCompatActivity{

    /**
     * Initializes UI components to view photos
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        getSupportActionBar().hide();

        Uri targetUri = Uri.parse(getIntent().getStringExtra("URI"));
        Bitmap bitmap;

        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        photoView.setOnOutsidePhotoTapListener(imageView -> PhotoViewActivity.this.finish());

        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
            photoView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}