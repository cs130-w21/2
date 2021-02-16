package com.example.pathways;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import static java.util.Collections.*;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
    private ArrayList<SpotifyActivity.SongInfo> _songInfos;
    private LayoutInflater _layoutInlater;
    private AdapterCallbacks _callbacks;

    public interface AdapterCallbacks {
        void onSongDeleted(int index);

        void onItemClicked(int index);

        void requestDrag(RecyclerView.ViewHolder viewHolder);
    }

    public PlaylistAdapter(Context context,
                                ArrayList<SpotifyActivity.SongInfo> songInfos, AdapterCallbacks adapterCallbacks){
        _layoutInlater = LayoutInflater.from(context);
        _songInfos = songInfos;
        _callbacks = adapterCallbacks;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = _layoutInlater.inflate(R.layout.playlist_item, parent, false);
        return new PlaylistAdapter.ViewHolder(view, _callbacks);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        SpotifyActivity.SongInfo songInfo = _songInfos.get(position);
        holder._textViewTrackName.setText(songInfo.trackName);
        holder._textViewAlbumName.setText(songInfo.albumName);
        holder._textViewArtistName.setText(songInfo.artist);
        Picasso.get().load(songInfo.imageUrl).into(holder._artwork);

        holder._reorderButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() ==
                        MotionEvent.ACTION_DOWN) {
                    _callbacks.requestDrag(holder);
                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return _songInfos.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                swap(_songInfos, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                swap(_songInfos, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(ViewHolder viewHolder) {
    }

    @Override
    public void onRowClear(ViewHolder viewHolder) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView _textViewTrackName;
        TextView _textViewArtistName;
        TextView _textViewAlbumName;
        ImageButton _deleteSongButton;
        ImageButton _reorderButton;

        RelativeLayout _listItem;
        ImageView _artwork;
        AdapterCallbacks _adapterCallbacks;


        public ViewHolder(@NonNull View itemView, AdapterCallbacks adapterCallbacks) {
            super(itemView);
            _textViewTrackName = itemView.findViewById(R.id.text_view_tack_name);
            _textViewArtistName = itemView.findViewById(R.id.text_view_artist_name);
            _textViewAlbumName = itemView.findViewById(R.id.text_view_album_name);
            _deleteSongButton = itemView.findViewById(R.id.delete_song_button);
            _deleteSongButton.setOnClickListener(this);
            _reorderButton = itemView.findViewById(R.id.reorder_song_button);
            _listItem = itemView.findViewById(R.id.layout_playlist_item);
            _artwork = itemView.findViewById(R.id.album_art);
            _listItem.setOnClickListener(this);
            _adapterCallbacks = adapterCallbacks;
        }

        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            if (view.getId() == R.id.delete_song_button) {
                _adapterCallbacks.onSongDeleted(index);
            } else {
                _adapterCallbacks.onItemClicked(index);
            }

        }
    }

}


