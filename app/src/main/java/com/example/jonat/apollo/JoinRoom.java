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

/*
 * enter room code here
 */
public class JoinRoom extends AppCompatActivity {

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference myRef = database.getReference("Message");

    public DatabaseReference getMyRef() {
        myRef.setValue("Hello, World!");
        return myRef;
    }

    static String enteredCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        myRef = FirebaseDatabase.getInstance().getReference();

        final EditText roomText = (EditText) findViewById(R.id.textInput);

        Button joinButton = (Button) findViewById(R.id.nextButton);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        enteredCode = roomText.getText().toString();
                        String strCodes = "";

                        dataSnapshot.getChildren();

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot postSnapShot2 : postSnapshot.getChildren()) {
                                strCodes += " " + postSnapShot2.child("room_code").getValue();
                            }
                        }

                        String[] codes = strCodes.split("\\s+");
                        for (int i = 0; i < codes.length; i++) {
                            codes[i] = codes[i].replaceAll("[^\\w]", "");
                        }

                        System.out.println(enteredCode);
                        if (ArrayUtils.contains(codes, enteredCode)) {
                            Intent correctCode = new Intent(JoinRoom.this, GuestPlaylist.class);
                            startActivity(correctCode);
                        } else {
                            roomText.setText("");
                            Toast.makeText(JoinRoom.this, "Invalid room code, please try again.", Toast.LENGTH_SHORT).show();
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



//                        for (int i = 0; i < dataSnapshot.child("rooms").getChildrenCount(); i++) {
////                           // THERE WE GO
//                            System.out.println(dataSnapshot.child("rooms").getChildren());
//
//                        }

//                        //iterate through each room, ignoring room ID
//                        for (Map.Entry<String, Object> entry : collectRoomCode.entrySet()) {
//
//                            //Get room map
//                            Map singleRoom = (Map)entry.getValue();
//                            System.out.println(singleRoom);
//                            System.out.println();
//
//                            //Get phone field and append to list
//                            codes.add((Long) singleRoom.get("room_code"));
//                        }
//
//                        System.out.println(codes.toString());


//                        //3 differents ways to iterate over the map
//                        for (String key : collectRoomCode.keySet()){
//                            //iterate over keys
//                            System.out.println(key+" "+collectRoomCode.get(key));
//                        }
//
//                        for (Object value : collectRoomCode.values()){
//                            //iterate over values
//                            System.out.println(value.toString());
//                        }
//
//                        for (Map.Entry<String,Object> pair : collectRoomCode.entrySet()){
//                            //iterate over the pairs
//                            System.out.println(pair.getKey()+" "+pair.getValue());
//                        }


//                        Log.d("DBVAL", "Value is: " + collectRoomCode.toString());
//
//                        Set< Map.Entry< String,Object> > st = map.entrySet();
//
//                        for (Map.Entry< String,Object> me:st) {
//                            System.out.print(me.getKey()+":");
//                            System.out.println(me.getValue());
//                        }

//                        System.out.println(map.get("rooms"));

//                        try {
//                            System.out.println(new JSONObject(map).toString(2));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }

//                        Set keys = map.keySet();
//                        for(Object key: keys) {
//                            System.out.println(key + ": " + map.get(key));
//                        }

//                        JSONObject obj = new JSONObject(collectRoomCode);
//
//                        List<String> rooms = new ArrayList<String>();
//                        JSONArray jsonArray;
//
//                        try {
//                            jsonArray = obj.getJSONArray("rooms");
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                rooms.add(jsonArray.getJSONObject(i).getString("rooms"));
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//                        for (int i = 0; i < rooms.size(); i++) {
//                            System.out.println(rooms.get(i));
//                            Log.e("ROOM", "Room: " + rooms.get(i));
//                        }
