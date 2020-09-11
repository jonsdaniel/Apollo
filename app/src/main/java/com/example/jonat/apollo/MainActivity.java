package com.example.jonat.apollo;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "9ec85e438bcd42039606c4639bac9900";
    private static final String REDIRECT_URI = "com.example.jonat.apollo://callback";

    // request code to verify if result comes from login activity, can be any integer
    private static final int REQUEST_CODE = 1738;
    private SpotifyAppRemote mSpotifyAppRemote;

   // AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID), AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

    //AuthenticationClient

    String chosenIntent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] data = new String[]{"Create Room", "Join Room"};
        final NumberPicker picker = (NumberPicker) findViewById(R.id.number_picker);
        picker.setMinValue(0);
        picker.setMaxValue(1);
        picker.setDisplayedValues(data);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        Button selectButton = (Button) findViewById(R.id.selectButton);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picker.getValue() == 1) {
                    Intent joinIntent = new Intent(MainActivity.this, JoinRoom.class);
                    startActivity(joinIntent);
                }

                if (picker.getValue() == 0) {
                    Intent createIntent = new Intent(MainActivity.this, CreateRoom.class);
                    startActivity(createIntent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                        mSpotifyAppRemote = spotifyAppRemote;
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
    }


    private void connected() {
        // GuestPlaylist a playlist
        //mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;
                        if (track != null) {
                            System.out.println("MainActivity " + track.name + " by " + track.artist.name);
                            //Toast.makeText(MainActivity.this, "Now playing: " + track.name + " by " + track.artist.name, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}
