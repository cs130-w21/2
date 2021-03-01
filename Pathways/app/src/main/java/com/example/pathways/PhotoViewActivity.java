package com.example.pathways;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.FileNotFoundException;

public class PhotoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        Uri targetUri = Uri.parse(getIntent().getStringExtra("URI"));
        Bitmap bitmap;

        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);

        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
            photoView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}