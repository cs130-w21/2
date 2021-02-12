package com.example.pathways;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LocationsListAdapter extends RecyclerView.Adapter<LocationsListAdapter.ViewHolder> {
    private ArrayList<Location> _locations;
    private Location _startLocation;
    private Location _endLocation;
    private LayoutInflater _layoutInlater;

    public LocationsListAdapter(Context context,
                                ArrayList<Location> locations){
        _layoutInlater = LayoutInflater.from(context);
        _locations = locations;
    }

    @NonNull
    @Override
    public LocationsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = _layoutInlater.inflate(R.layout.locations_list_item, parent, false);
        return new LocationsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationsListAdapter.ViewHolder holder, int position) {
        Location location = _locations.get(position);

        if (position == 0){
            holder._textViewStopType.setText("Start Location");
            holder._textViewStopType.setVisibility(View.VISIBLE);
            holder._textViewDuration.setVisibility(View.GONE);
            holder._imageButton.setVisibility(View.GONE);
        } else if (position == _locations.size() - 1) {
            holder._textViewStopType.setText("End Location");
            holder._textViewStopType.setVisibility(View.VISIBLE);
            holder._imageButton.setVisibility(View.GONE);
        } else {
            holder._textViewStopType.setVisibility(View.GONE);
            holder._imageButton.setVisibility(View.VISIBLE);
        }

        holder._textViewStopAddress.setVisibility(View.GONE);
        if(location.getAddress() != null) {
            holder._textViewStopAddress.setVisibility(View.VISIBLE);
            holder._textViewStopAddress.setText(location.getAddress());
        }

        holder._textViewStopName.setText(location.getName());
        holder._textViewDuration.setText(location.getDuration());

        if (location.getRating().equals("" + -1d)) {
            holder._textViewRating.setVisibility(View.GONE);
        } else {
            holder._textViewRating.setText(location.getRating());
            holder._textViewRating.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return _locations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView _textViewStopName;
        TextView _textViewStopAddress;
        TextView _textViewStopType;
        TextView _textViewRating;
        TextView _textViewDuration;
        ImageButton _imageButton;
        RelativeLayout _listItem;
        String _placeId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _textViewStopName = itemView.findViewById(R.id.text_view_stop_name);
            _textViewStopAddress = itemView.findViewById(R.id.text_view_stop_address);
            _textViewStopType = itemView.findViewById(R.id.text_view_stop_type);
            _textViewRating = itemView.findViewById(R.id.text_view_rating);
            _textViewDuration = itemView.findViewById(R.id.text_view_duration);
            _imageButton = itemView.findViewById(R.id.delete_stop_button);
            _imageButton.setOnClickListener(this);
            _listItem = itemView.findViewById(R.id.layout_locations_list_item);
            _listItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Handle deletion here.
        }
    }
}
