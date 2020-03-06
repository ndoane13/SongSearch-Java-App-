package student;

import java.util.*;

import java.io.*;

/*
 * SongCollection.java
 * Read the specified data file and build an array of songs.
 * 
 * Starting code by Prof. Boothe 2016
 * 
 * COS285
 * Completed: by Nicholas Doane and Luke Jillings
 * Date: 9/10/2019
 */
public class SongCollection {

	public Song[] songs;

	public SongCollection(String filename){

		//Fields
		String[] songInfo = new String[2];
		ArrayList <Song> songList = new ArrayList<>();
		StringBuilder lyrics = new StringBuilder();
		Scanner songScan = null;

		//in-method handling of FNFE
		try {
			songScan = new Scanner(new File(filename));
		}catch(FileNotFoundException err){
			System.out.println("File not Found.");
			err.printStackTrace();
		}

		// read in the song file and build the songs array
		while(songScan.hasNext()) {
			String tempLine = songScan.nextLine();
			if(tempLine.substring(0, 6).compareTo("ARTIST") == 0) {
				songInfo[0] = tempLine.substring(8, tempLine.length()-1);
			}else if (tempLine.substring(0, 5).compareTo("TITLE") == 0) {
				songInfo[1] = tempLine.substring(7, tempLine.length()-1);
			}else {
				if(tempLine.substring(0,6).compareTo("LYRICS") == 0) {
					tempLine = tempLine.substring(8, tempLine.length());
					lyrics.append(tempLine + "\n");

				}
				while(tempLine.indexOf('"') == -1) {
					tempLine = songScan.nextLine();
	                lyrics.append(tempLine + "\n");
				}
				//Lyrics are finished, song is created and
				//added to ArrayList
				if(tempLine.compareTo("\"") == 0) {
					songList.add(new Song(songInfo[0], songInfo[1], 
							lyrics.toString()));
					//reset SB, otherwise memory heap overflow
					lyrics.setLength(0);
				}
			}
		}

		//close scanner and change ArrayList to plain Array
		songScan.close();
		songs = songList.toArray(new Song[songList.size()]);

		// sort the songs array
		Arrays.sort(songs);
	}


	// returns the array of all Songs
	// this is used as the data source for building other data structures
	public Song[] getAllSongs() {
		return songs;
	}

	//method for printing the first 10 songs in collection
	//name and title 1 per line + song count
	//also accounts for SongCollections less than 10
	public static void firstTen(Song[] songs) {
		int index = 0;
		System.out.println("Total songs = " + songs.length
				+ ", first songs:");
		while(index < songs.length && index < 10) {
			System.out.println(songs[index]);
			index++;
		}
	}
	
	   public static void firstTenRanked(Song[] songs) {
	        int index = 0;
	        System.out.println("Total songs = " + songs.length
	                + ", first songs:");
	        while(index < songs.length && index < 10) {
	            System.out.println(songs[index].getRank() + " " + songs[index]);
	            index++;
	        }
	    }

	// testing method
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("usage: prog songfile");
			return;
		}

		SongCollection sc = new SongCollection(args[0]);

		// show song count and first 10 songs (name & title only, 1 per line)
		//firstTen(sc);
	}
}