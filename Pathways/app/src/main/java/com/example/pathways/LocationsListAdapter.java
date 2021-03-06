package com.example.pathways;

import android.content.Context;
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
    private AdapterCallbacks _callbacks;

    public interface AdapterCallbacks {
        void onStopDeleted(String placeId);

        void onItemClicked(String placeId);

        void addNoteForLocation(String placeId, String locationName);

        void addImageForLocation(String placeId, String locationName);
    }

    public LocationsListAdapter(Context context,
                                ArrayList<Location> locations, AdapterCallbacks adapterCallbacks){
        _layoutInlater = LayoutInflater.from(context);
        _locations = locations;
        _callbacks = adapterCallbacks;
    }

    @NonNull
    @Override
    public LocationsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = _layoutInlater.inflate(R.layout.locations_list_item, parent, false);
        return new LocationsListAdapter.ViewHolder(view, _callbacks);
    }

    /**
     * Populates list of locations on trip page
     *
     * @param holder - Container for locations
     * @param position - Position of location in location list
     */
    @Override
    public void onBindViewHolder(@NonNull LocationsListAdapter.ViewHolder holder, int position) {
        Location location = _locations.get(position);
        holder.setPlaceId(location.getPlaceId());

        if (position == 0){
            holder._textViewStopType.setText("Start Location");
            holder._textViewStopType.setVisibility(View.VISIBLE);
            holder._textViewDuration.setVisibility(View.GONE);
            holder._removeStopButton.setVisibility(View.GONE);
        } else if (position == _locations.size() - 1) {
            holder._textViewStopType.setText("End Location");
            holder._textViewStopType.setVisibility(View.VISIBLE);
            holder._removeStopButton.setVisibility(View.GONE);
        } else {
            holder._textViewStopType.setVisibility(View.GONE);
            holder._removeStopButton.setVisibility(View.VISIBLE);
        }

        holder._textViewStopAddress.setVisibility(View.GONE);
        if(location.getAddress() != null && !location.getAddress().equals("")) {
            holder._textViewStopAddress.setVisibility(View.VISIBLE);
            holder._textViewStopAddress.setText(location.getAddress());
        }

        holder._textViewStopName.setText(location.getName());
        holder._textViewDuration.setText(location.getDuration());

        if (location.getRating().equals("" + -1d) || location.getRating().equals("")) {
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
        ImageButton _removeStopButton;
        ImageButton _addNoteButton;
        ImageButton _addImageButton;
        RelativeLayout _listItem;
        String _placeId;
        AdapterCallbacks _adapterCallbacks;

        public void setPlaceId(String placeId){
            _placeId = placeId;
        }

        public ViewHolder(@NonNull View itemView, AdapterCallbacks adapterCallbacks) {
            super(itemView);
            _textViewStopName = itemView.findViewById(R.id.text_view_stop_name);
            _textViewStopAddress = itemView.findViewById(R.id.text_view_stop_address);
            _textViewStopType = itemView.findViewById(R.id.text_view_stop_type);
            _textViewRating = itemView.findViewById(R.id.text_view_rating);
            _textViewDuration = itemView.findViewById(R.id.text_view_duration);

            _removeStopButton = itemView.findViewById(R.id.delete_stop_button);
            _removeStopButton.setOnClickListener(this);
            _addImageButton = itemView.findViewById(R.id.add_image_button);
            _addImageButton.setOnClickListener(this);
            _addNoteButton = itemView.findViewById(R.id.add_note_button);
            _addNoteButton.setOnClickListener(this);

            _listItem = itemView.findViewById(R.id.layout_locations_list_item);
            _listItem.setOnClickListener(this);
            _adapterCallbacks = adapterCallbacks;
        }

        /**
         * Handles clicking of location
         *
         * @param view - View that was clicked on
         */
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.delete_stop_button) {
                _adapterCallbacks.onStopDeleted(_placeId);
            } else if (view.getId() == R.id.add_image_button) {
                _adapterCallbacks.addImageForLocation(_placeId, _textViewStopName.getText().toString());
            } else if (view.getId() == R.id.add_note_button) {
                _adapterCallbacks.addNoteForLocation(_placeId, _textViewStopName.getText().toString());
            } else {
                _adapterCallbacks.onItemClicked(_placeId);
            }

        }
    }
}
