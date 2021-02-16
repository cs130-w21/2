package com.example.pathways;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private ArrayList<SpotifyActivity.SongInfo> _songInfos;
    private LayoutInflater _layoutInlater;
    private AdapterCallbacks _callbacks;

    public interface AdapterCallbacks {
        void onSongDeleted(int index);

        void onItemClicked(int index);
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

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        SpotifyActivity.SongInfo songInfo = _songInfos.get(position);
        holder._textViewTrackName.setText(songInfo.trackName);
        holder._textViewAlbumName.setText(songInfo.albumName);
        holder._textViewArtistName.setText(songInfo.artist);
        Picasso.get().load(songInfo.imageUrl).into(holder._artwork);

    }

    @Override
    public int getItemCount() {
        return _songInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView _textViewTrackName;
        TextView _textViewArtistName;
        TextView _textViewAlbumName;
        ImageButton _imageButton;
        RelativeLayout _listItem;
        ImageView _artwork;
        AdapterCallbacks _adapterCallbacks;


        public ViewHolder(@NonNull View itemView, AdapterCallbacks adapterCallbacks) {
            super(itemView);
            _textViewTrackName = itemView.findViewById(R.id.text_view_tack_name);
            _textViewArtistName = itemView.findViewById(R.id.text_view_artist_name);
            _textViewAlbumName = itemView.findViewById(R.id.text_view_album_name);
            _imageButton = itemView.findViewById(R.id.delete_song_button);
            _imageButton.setOnClickListener(this);
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
