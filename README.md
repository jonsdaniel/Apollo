# Apollo
This application is designed to allow a gathering of people (in an event/party/etc.) to be able to log into a room and request/vote for songs to be played at the event. 
This fixes the issue of each individual at the event having to connect to the host’s WiFi, skip through songs unintentionally, and gives power to the users to have a voice in deciding what music the group will listen to.

Apollo was created in 3 different platforms, a React based web application, an iOS application, and an Android application.
This repository contains the source code for the Android application version of Apollo.

Software Requirements Specification Document: https://docs.google.com/document/d/1bDcC3uYnCwGPnkBc8Pi3BhpDWu7hOrjcJi2Eq6X75hY/edit?usp=sharing

The Android platform of Apollo currently operates in two different flows, as per the requirements. 
There is a host side for creating a room and a client side for joining a room. The application is fully functioning with our Firebase database and the Android Spotify SDK. 
The application now allows for a host to create a room, that is then replicated on Firebase. At this point any user can join the room and add songs to the room playlist, as well as vote (satisfying these requirements). A user may also join an existing room, this works by reading from Firebase and gathering the required data to display room information. The only feature not currently operational on the Android platform is the user adding songs via a search query of the Spotify library. The Android Spotify SDK is still in its beta version (at the time of development), thus preventing this use case from being satisfied. Users may however vote on existing songs in the playlist from the Android side.

Submitted in partial fulfillment of the requirements of IT 326 - Principles of Software Engineering at Illinois State University.
