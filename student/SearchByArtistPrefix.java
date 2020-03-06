package student;
import java.io.*;
import java.util.*;

/*
 * SearchByArtistPrefix.java
 * starting code
 * Boothe 2016
 */
public class SearchByArtistPrefix {

	private Song[] songs;  // The constructor fetches and saves a reference to the song array here

	public SearchByArtistPrefix(SongCollection sc) {
		songs = sc.getAllSongs();
	}

	/**
	 * find all songs matching artist prefix
	 * uses binary search
	 * should operate in time log n + k (# matches)
	 */
	public Song[] search(String artistPrefix) {
		//field declaration
		Song.CmpArtist cmpArtist = new Song.CmpArtist();
		int lower, upper;
		
		//searching songs
		lower = Math.abs(Arrays.binarySearch(songs, new Song(artistPrefix, "", ""), cmpArtist));
		//System.out.println("comparison count for binary search: " + ((CmpCnt)cmpArtist).getCmpCnt());
		cmpArtist.resetCmpCnt();
		upper = lower;
		//while for lower bounds
		while(lower >= 0 && 
			songs[lower].getArtist().toLowerCase().startsWith(artistPrefix.toLowerCase()) == true) {
			cmpArtist.cmpCnt++;
			lower --;
		}
		lower++;
		//while for upper bounds
		while(upper < songs.length && 
			songs[upper].getArtist().toLowerCase().startsWith(artistPrefix.toLowerCase()) == true) {
			cmpArtist.cmpCnt++;
			upper++;
		}

		System.out.println("comparison count for LB and UB search: " + cmpArtist.getCmpCnt());
		//return found indices in new simple array
		if(upper - lower < 0) {
			return new Song[0]; 
		}else {
			return Arrays.copyOfRange(songs, lower, upper);
		}
	}



	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("usage: prog songfile [search string]");
			return;
		}

		SongCollection sc = new SongCollection(args[0]);
		SearchByArtistPrefix sbap = new SearchByArtistPrefix(sc);

		if (args.length > 1){
			System.out.println("searching for: "+args[1]);
			Song[] byArtistResult = sbap.search(args[1]);

			// to do: show first 10 matches
			SongCollection.firstTen(byArtistResult);
		}
	}
}