package com.example.pathways;

import android.content.Context;
import android.graphics.BitmapFactory;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    private List<Bitmap> _images = new ArrayList<>();
    private List<String> _image_locations = new ArrayList<>();
    private LayoutInflater _layoutInflater;



    public ImageListAdapter(Context context,
                                List<Bitmap> images, List<String> image_locations){
        _layoutInflater = LayoutInflater.from(context);
        _images = images;
        _image_locations = image_locations;
    }

    @NonNull
    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = _layoutInflater.inflate(R.layout.images_list_item, parent, false);
        return new ImageListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListAdapter.ViewHolder holder, int position) {
        Bitmap bitmap = _images.get(position);
        String text = _image_locations.get(position);
        holder._textView.setText(text);
        if (bitmap != null) {
            holder._imageView.setImageBitmap(bitmap);
        } else {
            holder._imageView.setImageBitmap(null);
        }



    }

    @Override
    public int getItemCount() {
        return _images.size();
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