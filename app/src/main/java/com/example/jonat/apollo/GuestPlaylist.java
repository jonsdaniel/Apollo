package com.example.jonat.apollo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GuestPlaylist extends AppCompatActivity {

    private static final String CLIENT_ID = "9ec85e438bcd42039606c4639bac9900";
    private static final String REDIRECT_URI = "com.example.jonat.apollo://callback";

    // request code to verify if result comes from login activity, can be any integer
    private static final int REQUEST_CODE = 1738;
    private SpotifyAppRemote mSpotifyAppRemote;

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference myRef = database.getReference("Message");

    public DatabaseReference getMyRef() {
        myRef.setValue("Hello, World!");
        return myRef;
    }

    public List<String> trackList = new ArrayList<>();
    public List<String> songIDList = new ArrayList<>();
    public List<String> parentSongIDList = new ArrayList<>();
    public List<Integer> voteList = new ArrayList<>();
    public static String playlistID = "";
    public String parentRoomID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_playlist);

        Button refreshButton = (Button) findViewById(R.id.refreshText); // original add asset button
        refreshButton.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent lstn_intent1 = new Intent(getApplicationContext(), GuestPlaylist.class);
                startActivity(lstn_intent1);
            }
        });

        myRef = FirebaseDatabase.getInstance().getReference();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String enteredCode = JoinRoom.enteredCode;
                Toast.makeText(GuestPlaylist.this, "The room code that was entered is " + enteredCode, Toast.LENGTH_SHORT).show();

                String strCodes = "";

                String rooms = "";

                dataSnapshot.getChildren();

                // get parent ID (roomID)
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapShot2 : postSnapshot.getChildren()) {
                        strCodes += " " + postSnapShot2.child("room_code").getValue();
                        if (strCodes.substring(strCodes.lastIndexOf(" ") + 1).trim().equals(enteredCode)) {
                            parentRoomID = postSnapShot2.getKey();
                            playlistID = postSnapShot2.child("spotify_playlist_id").getValue().toString();
                        }
                    }
                }

                // get artists and songs for the room
                ArrayList<String> songArrayList = new ArrayList<>();
                ArrayList<String> artistArrayList = new ArrayList<>();
                ArrayList<MyTrack> vTrackList = new ArrayList<MyTrack>();

                String tempSong = "";
                String tempArtist = "";
                int tempVote = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot2 : postSnapshot.getChildren()) {
                        rooms += " " + postSnapshot2.getKey();
                        if (rooms.substring(rooms.lastIndexOf(" ") + 1).trim().equals(parentRoomID)) {
                            for (DataSnapshot postSnapshot3 : postSnapshot2.getChildren()) {
                                for (DataSnapshot postSnapshot4 : postSnapshot3.getChildren()) {
                                    if (postSnapshot4.child("name").getValue() != null) {
                                        songArrayList.add(postSnapshot4.child("name").getValue().toString());
                                        voteList.add(Integer.parseInt(postSnapshot4.child("votes").getValue().toString()));
                                        songIDList.add(postSnapshot4.child("id").getValue().toString());
                                        tempSong = postSnapshot4.child("name").getValue().toString();
                                        tempVote = Integer.parseInt(postSnapshot4.child("votes").getValue().toString());
                                        parentSongIDList.add(postSnapshot4.getKey());
                                    }
                                    for (DataSnapshot postSnapshot5 : postSnapshot4.getChildren()) {
                                        if (postSnapshot5.getKey().equals("artist")) {
                                            artistArrayList.add(postSnapshot5.child("name").getValue().toString());
                                            tempArtist = postSnapshot5.child("name").getValue().toString();
                                            trackList.add(tempSong + " - " + tempArtist);
                                            vTrackList.add(new MyTrack(tempSong, tempArtist, tempVote));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

//                for (int i = 0; i < artistArrayList.size(); i++) {
//                    System.out.println("Artist " + i + ": " + artistArrayList.get(i));
//                }
//
//                for (int i = 0; i < songArrayList.size(); i++) {
//                    System.out.println("Song " + i + ": " + songArrayList.get(i));
//                }

                for (int i = 0; i < trackList.size(); i++) {
                    System.out.println(trackList.get(i));
                }

                for (int i = 0; i < songIDList.size(); i++) {
                    System.out.println(songIDList.get(i));
                }

                Collections.sort(vTrackList);

                trackList.clear();
                for (int i = 0; i < vTrackList.size(); i++) {
                    trackList.add(vTrackList.get(i).toString());
                }

                System.out.println("AFTER SORTING: \n");
                for (int i = 0; i < trackList.size(); i++) {
                    System.out.println(trackList.get(i));
                }

                for (int i = 0; i < vTrackList.size(); i++) {
                    System.out.println(vTrackList.get(i).toString());
                }

                bindGridView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.e("DBVAL", "Failed to read value.", databaseError.toException());
            }
        });


        for (int i = 0; i < trackList.size(); i++) {
            System.out.println(trackList.get(i));
        }

        System.out.println("Track list size: " + trackList.size());
    }

    /**
     * loads the gridview with the trackList data
     */
    public void bindGridView() {
        final GridView gv = (GridView) findViewById(R.id.gv);

        final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, trackList);

        gv.setAdapter(gridViewArrayAdapter);
        gridViewArrayAdapter.notifyDataSetChanged();

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * @param parent
             * @param view
             * @param position
             * @param id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                try {
                    new AlertDialog.Builder(GuestPlaylist.this)
                            .setTitle(trackList.get(position))
                            .setMessage("")

                            .setPositiveButton("UNLIKE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference myRef = database.getReference().child("rooms").child(parentRoomID).child("songs").child(parentSongIDList.get(position)).child("votes");
                                    System.out.println(myRef.toString());

                                    // push vote to firebase
                                    myRef.setValue(voteList.get(position)-1);
                                }
                            })
                            .setNegativeButton("LIKE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseReference myRef = database.getReference().child("rooms").child(parentRoomID).child("songs").child(parentSongIDList.get(position)).child("votes");
                                    System.out.println(myRef.toString());

                                    // push vote to firebase
                                    myRef.setValue(voteList.get(position)+1);
                                }
                            })
                            .show();
                } catch (Exception e) {
                    Log.e("GV ERR", e.toString());
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();
        bindGridView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindGridView();

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
        // play the playlist of room
        //mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:" + playlistID);

        //play the first track
        mSpotifyAppRemote.getPlayerApi().play("spotify:track:" + songIDList.get(0));

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;

                        if (track != null) {
                            //System.out.println("MainActivity " + track.name + " by " + track.artist.name);
                            Toast.makeText(GuestPlaylist.this, "Now playing: " + track.name + " by " + track.artist.name, Toast.LENGTH_LONG).show();
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