package com.example.jonat.apollo;

public class MyTrack implements Comparable<MyTrack> {

    private String songName;
    private String artistName;
    private int votes;

    public MyTrack(String songName, String artistName, int votes) {
        this.songName = songName;
        this.artistName = artistName;
        this.votes = votes;
    }

    public MyTrack() {
        // default constructor
    }

    public String toString() {
        return songName + " by " + artistName + "\nVotes: " + votes;
    }

    public int getVotes() {
        return votes;
    }


    public int compareTo(MyTrack compareV) {
        int compareVotes=((MyTrack)compareV).getVotes();
        /* For Ascending order*/
        //return this.votes-compareVotes;

        /* For Descending order do like this */
        return compareVotes-this.votes;
}

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
