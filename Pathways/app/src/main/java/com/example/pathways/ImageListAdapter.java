package com.example.pathways;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;
import android.provider.MediaStore;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private List<ImageEntity> _imageEntities = new ArrayList<>();
    private LayoutInflater _layoutInflater;
    private Context _context;



    public ImageListAdapter(Context context,
                            List<ImageEntity> images){
        _layoutInflater = LayoutInflater.from(context);
        _imageEntities = images;
        _context = context;
    }

    @NonNull
    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = _layoutInflater.inflate(R.layout.images_list_item, parent, false);
        return new ImageListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListAdapter.ViewHolder holder, int position) {
        ImageEntity imageEntity = _imageEntities.get(position);

        Log.v("URI", imageEntity.imageUri);

        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inSampleSize = 6;

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(_context.getContentResolver().openInputStream(Uri.parse(imageEntity.imageUri)), null, dbo);
            holder._imageView.setImageBitmap(bitmap);

            String text = _imageEntities.get(position).locationName;
            holder._textView.setText(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }




    }

    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = _context.getContentResolver().query(contentUri, proj, null, null,
                    null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public int getItemCount() {
        return _imageEntities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView _imageView;
        TextView _textView;
        RelativeLayout _listItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _imageView = itemView.findViewById(R.id.imageView);
            _textView = itemView.findViewById(R.id.textView);

        }


    }
}