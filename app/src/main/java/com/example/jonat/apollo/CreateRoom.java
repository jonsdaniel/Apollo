package com.example.jonat.apollo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class CreateRoom extends AppCompatActivity {

    //FirebaseApp.initializeApp(this);
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Message");

    public DatabaseReference getMyRef() {
        myRef.setValue("Hello, World!");
        return myRef;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        myRef = FirebaseDatabase.getInstance().getReference();

        EditText roomNameInput = (EditText) findViewById(R.id.roomTextInput);
        final EditText roomCodeInput = (EditText) findViewById(R.id.roomCodeInput);
        EditText roomAliasInput = (EditText) findViewById(R.id.roomAliasInput);

        Button joinButton = (Button) findViewById(R.id.nextButton);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String enteredCode = roomCodeInput.getText().toString();

                        Map<String, Object> collectRoomCode = (Map<String, Object>) dataSnapshot.getValue();
                        String strCodes = "";

                        dataSnapshot.getChildren();

                        System.out.println(dataSnapshot.child("rooms").getChildrenCount());

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot postSnapShot2 : postSnapshot.getChildren()) {
                                //System.out.println(postSnapShot2.child("room_code").getValue());
                                strCodes += " " + postSnapShot2.child("room_code").getValue();
                            }
                        }

                        System.out.println(strCodes);

                        String[] codes = strCodes.split("\\s+");
                        for (int i = 0; i < codes.length; i++) {
                            codes[i] = codes[i].replaceAll("[^\\w]", "");
                            System.out.println(codes[i]);
                        }

                        System.out.println(enteredCode);
                        if (ArrayUtils.contains(codes, enteredCode)) {
                            roomCodeInput.setText("");
                            Toast.makeText(CreateRoom.this, "Room code already exists, please try again.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent correctCode = new Intent(CreateRoom.this, HostPlaylist.class);
                            startActivity(correctCode);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Failed to read value
                        Log.e("DBVAL", "Failed to read value.", databaseError.toException());
                    }

                });
            }
        });
    }
}