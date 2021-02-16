package com.example.pathways;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.gson.Gson;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SpotifyActivity extends AppCompatActivity implements PlaylistAdapter.AdapterCallbacks {
    private static final String CLIENT_ID = "6e6189c30dfb45c2b04f74db3c832444";
    private static final String REDIRECT_URI = "https://www.google.com";
    private SpotifyAppRemote _spotifyAppRemote;

    private static final int REQUEST_CODE = 1337;
    private SpotifyApi _webApi = new SpotifyApi();
    private SpotifyService _webSpotify;
    private String _playListId = "";
    private String _spotifyId = "";
    private Gson _gson = new Gson();
    private ArrayList<SongInfo> _songInfos = new ArrayList<>();
    private PlaylistAdapter _playlistAdapter;

    class SongInfo {
        public String imageUrl;
        public String artist;
        public String albumName;
        public String trackName;
        public String spotifyUri;

        public SongInfo(String imageUrl, String artist, String albumName, String trackName, String spotifyUri) {
            this.imageUrl = imageUrl;
            this.artist = artist;
            this.albumName = albumName;
            this.trackName = trackName;
            this.spotifyUri = spotifyUri;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);
        handleIntent(getIntent());

        RecyclerView playlist = findViewById(R.id.playlist_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        playlist.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(playlist.getContext(),
                layoutManager.getOrientation());
        playlist.addItemDecoration(dividerItemDecoration);

        _playlistAdapter = new PlaylistAdapter(this, _songInfos, this);
        playlist.setAdapter(_playlistAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.spotify_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_spotify).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        final CursorAdapter suggestionAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                CursorAdapter c = searchView.getSuggestionsAdapter();
                Cursor cur = c.getCursor();
                cur.move(i);

                Track track = _gson.fromJson(cur.getString(
                        cur.getColumnIndex(SearchManager.SUGGEST_COLUMN_INTENT_DATA)), Track.class);

                String imageUrl = "";
                for (Image image : track.album.images) {
                    if (image.width == 300) {
                        imageUrl = image.url;
                    }
                }

                String artist = track.artists.get(0).name;
                String albumName = track.album.name;
                String trackName = track.name;
                String spotifyUri = track.uri;

                SongInfo songInfo = new SongInfo(imageUrl, artist, albumName, trackName, spotifyUri);
                _songInfos.add(songInfo);
                _playlistAdapter.notifyDataSetChanged();


//                _spotifyAppRemote.getPlayerApi().play(uri);
//                // Subscribe to PlayerState
//                _spotifyAppRemote.getPlayerApi()
//                        .subscribeToPlayerState()
//                        .setEventCallback(playerState -> {
//                            final Track track = playerState.track;
//                            if (track != null) {
//                                Log.d("MainActivity", track.name + " by " + track.artist.name);
//                            }
//                        });

                return false;
            }
        });

        searchView.setSuggestionsAdapter(suggestionAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Put your magic here
                _webSpotify.searchTracks(newText, new Callback<TracksPager>() {
                    @Override
                    public void success(TracksPager tracksPager, Response response) {
                        String[] columns = {
                                BaseColumns._ID,
                                SearchManager.SUGGEST_COLUMN_TEXT_1,
                                SearchManager.SUGGEST_COLUMN_INTENT_DATA
                        };

                        MatrixCursor cursor = new MatrixCursor(columns);

                        for (int i = 0; i < tracksPager.tracks.items.size(); i++) {
                            Track track = tracksPager.tracks.items.get(i);
                            String trackJson = _gson.toJson(track);


                            String[] tmp = {i + "", tracksPager.tracks.items.get(i).name, trackJson};
                            cursor.addRow(tmp);
                        }

                        suggestionAdapter.changeCursor(cursor);

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

                return false;
            }
        });

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.v("Query", "wut");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // Do work using string
            Log.v("Query", query);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming", "playlist-modify-private"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    private void connected() {
        // Then we will write some more code here.
//        _spotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
//        // Subscribe to PlayerState
//        _spotifyAppRemote.getPlayerApi()
//                .subscribeToPlayerState()
//                .setEventCallback(playerState -> {
//                    final Track track = playerState.track;
//                    if (track != null) {
//                        Log.d("MainActivity", track.name + " by " + track.artist.name);
//                    }
//                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(_spotifyAppRemote);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    String accessToken = response.getAccessToken();
                    _webApi.setAccessToken(accessToken);
                    _webSpotify = _webApi.getService();

                    _webSpotify.getMe(new Callback<UserPrivate>() {
                        @Override
                        public void success(UserPrivate userPrivate, Response response) {
                            _spotifyId = userPrivate.id;
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });

                    // Set the connection parameters
                    ConnectionParams connectionParams =
                            new ConnectionParams.Builder(CLIENT_ID)
                                    .setRedirectUri(REDIRECT_URI)
                                    .showAuthView(false)
                                    .build();

                    SpotifyAppRemote.connect(this, connectionParams,
                            new Connector.ConnectionListener() {

                                @Override
                                public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                                    _spotifyAppRemote = spotifyAppRemote;
                                    Log.d("MainActivity", "Connected! Yay!");

                                    // Now you can start interacting with App Remote
                                    connected();
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    Log.e("MainActivity", throwable.getMessage(), throwable);

                                    // Something went wrong when attempting to connect! Handle errors here
                                }
                            });
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default: 
                    // Handle other cases
            }
        }
    }

    @Override
    public void onSongDeleted(int index) {

    }

    @Override
    public void onItemClicked(int index) {
        _spotifyAppRemote.getPlayerApi().play(_songInfos.get(index).spotifyUri);
                // Subscribe to PlayerState
                _spotifyAppRemote.getPlayerApi()
                        .subscribeToPlayerState()
                        .setEventCallback(playerState -> {
                            final com.spotify.protocol.types.Track track = playerState.track;
                            if (track != null) {
                                Log.d("SpotifyActivity", track.name + " by " + track.artist.name);
                            }
                        });
    }
}