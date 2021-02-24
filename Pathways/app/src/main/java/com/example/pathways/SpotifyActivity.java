package com.example.pathways;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    private final Gson _gson = new Gson();
    private List<SongInfo> _songInfos = new ArrayList<>();
    private PlaylistAdapter _playlistAdapter;
    private ItemTouchHelper _touchHelper;
    private int _currentSongIndex = -1;
    private ImageButton _playPauseButton;
    private ImageButton _previousButton;
    private ImageButton _nextButton;
    private TextView _artistTextView;
    private TextView _albumTextView;
    private TextView _songTextView;
    private ImageView _albumArt;
    private ConstraintLayout _playerElements;
    private AppDatabase _db;
    private TripDao _tripDao;
    private Executor _executor = Executors.newSingleThreadExecutor();
    private TripEntity _tripEntity;
    private TextView _emptyTextView;
    RecyclerView _playlistRecyclerView;
    private View _playerSpacer;

    private long searchQueryTimeStamp;
    final Timer queryTimer =  new Timer();
    final static int SEARCH_DEBOUNCE_TIME = 200;

    private boolean paused = true;

    static class SongInfo {
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

        _playerElements = findViewById(R.id.player_elements);
        _playerElements.setVisibility(View.GONE);

        _playerSpacer = findViewById(R.id.spacer);
        _playerSpacer.setVisibility(View.GONE);

        _playPauseButton = findViewById(R.id.play_button);
        _playPauseButton.setOnClickListener((View view) -> {
            if (paused) {
                play(true);
            } else {
                pause();
            }
        });

        _previousButton = findViewById(R.id.previous_button);
        _previousButton.setOnClickListener((View view) -> {
            if (_currentSongIndex != 0) {
                _currentSongIndex -= 1;
            }

            play(false);

        });

        _nextButton = findViewById(R.id.next_button);
        _nextButton.setOnClickListener((View view) -> {
            if (_currentSongIndex == _songInfos.size() - 1) {
                _currentSongIndex = 0;
            } else {
                _currentSongIndex += 1;
            }

            play(false);
        });

        _artistTextView = findViewById(R.id.player_artist_name);
        _albumTextView = findViewById(R.id.player_album_name);
        _songTextView = findViewById(R.id.player_tack_name);
        _albumArt = findViewById(R.id.player_album_art);

        _emptyTextView = findViewById(R.id.empty_playlist_text);

        _playlistRecyclerView = findViewById(R.id.playlist_recycler_view);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        _playlistRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(_playlistRecyclerView.getContext(),
                layoutManager.getOrientation());
        _playlistRecyclerView.addItemDecoration(dividerItemDecoration);

        _playlistAdapter = new PlaylistAdapter(this, _songInfos, this);
        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(_playlistAdapter);
        _touchHelper = new ItemTouchHelper(callback);
        _touchHelper.attachToRecyclerView(_playlistRecyclerView);

        _playlistRecyclerView.setAdapter(_playlistAdapter);

        _db = DatabaseSingleton.getInstance(this);
        _tripDao = _db.tripDao();

        Long tripId = (Long) getIntent().getLongExtra("TRIP ID", 0);
        _executor.execute(() -> {
            _tripEntity = _tripDao.findByID(tripId);
            tripDependentInit();
        });
    }

    private void tripDependentInit () {
        getSupportActionBar().setTitle(_tripEntity.tripName + " Playlist");
        if (_tripEntity.songInfos != null) {
            if (_tripEntity.songInfos.size() > 0) {
                _emptyTextView.setVisibility(View.GONE);
            }

            for (SongInfo info : _tripEntity.songInfos) {
                _songInfos.add(info);
                runOnUiThread(() -> _playlistAdapter.notifyDataSetChanged());
            }
        }
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
                if (_songInfos.size() == 0) {
                    _emptyTextView.setVisibility(View.GONE);
                }

                CursorAdapter c = searchView.getSuggestionsAdapter();
                Cursor cur = c.getCursor();

                while (!(cur.getString(
                        cur.getColumnIndex(BaseColumns._ID))).equals(i + "")) {
                    cur.move(1);
                }

                Log.v("CURR", i + " " + cur.getString(
                        cur.getColumnIndex(BaseColumns._ID)));

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
                Log.v("WTF", "Song added");

                _playlistAdapter.notifyDataSetChanged();

                if (_tripEntity.songInfos == null) {
                    _tripEntity.songInfos = new ArrayList<>();
                }

                _tripEntity.songInfos.add(songInfo);
                _executor.execute(() -> _tripDao.updateTrips(_tripEntity));

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
                boolean res =  attemptSearch(newText, suggestionAdapter);

                // If user no longer changes query text, still need to perform original query.
                queryTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.v("RUN", newText);
                        attemptSearch(newText, suggestionAdapter);
                    }
                }, SEARCH_DEBOUNCE_TIME + 1);

                return res;
            }
        });

        return true;
    }

    private boolean attemptSearch(String newText, CursorAdapter suggestionAdapter) {
        // Debounce query
        if (System.currentTimeMillis() - searchQueryTimeStamp < SEARCH_DEBOUNCE_TIME) {
            searchQueryTimeStamp = System.currentTimeMillis();
            return false;
        }

        searchQueryTimeStamp = System.currentTimeMillis();


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


                    String[] tmp = {i + "", tracksPager.tracks.items.get(i).name + " - " +
                            tracksPager.tracks.items.get(i).artists.get(0).name, trackJson};
                    Log.v("MC", i + " " + tmp[1]);
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

    private void play(boolean resume) {
        if (_songInfos.size() == 0) {
            return;
        }

        if (_currentSongIndex == -1) {
            _currentSongIndex = 0;
        }

        _playerElements.setVisibility(View.VISIBLE);
        _playerSpacer.setVisibility(View.VISIBLE);



        if (resume) {
            _spotifyAppRemote.getPlayerApi().resume();
        } else {
            _spotifyAppRemote.getPlayerApi().play(_songInfos.get(_currentSongIndex).spotifyUri);
        }

        SongInfo songInfo = _songInfos.get(_currentSongIndex);
        _artistTextView.setText(songInfo.artist);
        _albumTextView.setText(songInfo.albumName);
        _songTextView.setText(songInfo.trackName);
        Picasso.get().load(songInfo.imageUrl).into(_albumArt);

    }

    private void pause() {
        _spotifyAppRemote.getPlayerApi().pause();
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

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(_spotifyAppRemote);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            Log.d("token", response.getAccessToken());

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    String accessToken = response.getAccessToken();
                    _webApi.setAccessToken(accessToken);
                    _webSpotify = _webApi.getService();


                    // Set the connection parameters
                    ConnectionParams connectionParams =
                            new ConnectionParams.Builder(CLIENT_ID)
                                    .setRedirectUri(REDIRECT_URI)
                                    .showAuthView(true)
                                    .build();

                    SpotifyAppRemote.connect(this, connectionParams,
                            new Connector.ConnectionListener() {

                                @Override
                                public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                                    _spotifyAppRemote = spotifyAppRemote;
                                    _spotifyAppRemote.getPlayerApi()
                                            .subscribeToPlayerState()
                                            .setEventCallback(playerState -> {

                                                if (playerState.isPaused) {
                                                    _playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_64);
                                                    paused = true;
                                                } else {
                                                    _playPauseButton.setImageResource(R.drawable.ic_baseline_pause_64);
                                                    paused = false;
                                                }

                                            });
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    Log.e("SpotifyActivity", throwable.getMessage(), throwable);

                                    // Something went wrong when attempting to connect! Handle errors here
                                }
                            });
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.e("AUTHENTICATION FAILED", "YOU ARE A FAILURE");
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
        Log.v("WTF", index + "");
        if (index <= _currentSongIndex) {
            _currentSongIndex -= 1;
            Log.v("DELETE", "i: " + index + ", cSI: " + _currentSongIndex);
            if (index - 1 == _currentSongIndex) {
                if (index == 0) {
                    pause();
                    _playerElements.setVisibility(View.GONE);
                    _playerSpacer.setVisibility(View.GONE);
                } else {
                    play(false);
                    queryTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            pause();
                        }
                    }, 10);
                }
            }
        }

        _songInfos.remove(index);
        _playlistAdapter.notifyDataSetChanged();

        _tripEntity.songInfos = _songInfos;
        _executor.execute(() -> _tripDao.updateTrips(_tripEntity));
    }

    @Override
    public void onItemClicked(int index) {
        _currentSongIndex = index;
        play(false);
    }

    @Override
    public void onItemMoved(int oldIndex, int newIndex) {
        if (_currentSongIndex == oldIndex) {
            _currentSongIndex = newIndex;
        }

        _tripEntity.songInfos = _songInfos;
        _executor.execute(() -> _tripDao.updateTrips(_tripEntity));
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        _touchHelper.startDrag(viewHolder);
    }
}