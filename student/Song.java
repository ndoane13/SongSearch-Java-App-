package student;

import java.util.*;

/*
 * Song objects contain Strings for a song's artist, title, and lyrics
 * 
 * Starting code by Prof. Boothe 2019
 * 
 * COS285
 * Completed: by Nicholas Doane and Luke Jillings
 * Date: 9/10/2019
 */
public class Song implements Comparable<Song> {
	// fields
	String artist, lyrics, title;
	//added
	int rank;

	// constructor
	public Song(String artist, String title, String lyrics) {
		this.artist = artist;
		this.lyrics = lyrics;
		this.title = title;
	}

	public String getArtist() {
		return this.artist;
	}

	public String getLyrics() {
		return this.lyrics;
	}

	public String getTitle() {
		return this.title;
	}
	//added
	public int getRank() {
		return this.rank;
	}
	//added
	public void setRank(int rank) {
		this.rank = rank;
	}

	public String toString() {
		return this.getArtist() + " \"" + this.getTitle() + "\"";
	}

	/* 
	 * the default comparison of songs
	 * primary key: artist, secondary key: title
	 * used for sorting and searching the song array
	 * if two songs have the same artist and title they are considered the same
	 */
	public int compareTo(Song song2) {
		int compareArtist = this.getArtist().compareToIgnoreCase(song2.getArtist());
		int compareTitle = this.getTitle().compareToIgnoreCase(song2.getTitle());
		return compareArtist == 0? compareTitle:compareArtist;
	}
	
	
	public static class CmpArtist extends CmpCnt implements Comparator<Song> {
		public int compare(Song s1, Song s2) {
			cmpCnt++;
			return s1.getArtist().compareToIgnoreCase(s2.getArtist());
		}
	}
	
	public static class CmpTitle extends CmpCnt implements Comparator<Song> {
		public int compare(Song s1, Song s2) {
			cmpCnt++;
			return s1.getTitle().compareToIgnoreCase(s2.getTitle());
		}
	}
	//added
	public static class CmpRank extends CmpCnt implements Comparator<Song> {
		public int compare(Song s1, Song s2) {
			cmpCnt++;
			int rank = s1.getRank() - s2.getRank();
			if(rank > 0) {
				return 1;
			}else if (rank < 0) {
				return -1;
			}else {
				return 0;
			}
		}
	}
	


	// testing method to test this class
	public static void main(String[] args) {
		Song s1 = new Song("Professor B",
				"Small Steps",
				"Write your programs in small steps\n"+
						"small steps, small steps\n"+
						"Write your programs in small steps\n"+
				"Test and debug every step of the way.\n");

		Song s2 = new Song("Brian Dill",
				"Ode to Bobby B",
				"Professor Bobby B., can't you see,\n"+
						"sometimes your data structures mystify me,\n"+
						"the biggest algorithm pro since Donald Knuth,\n"+
				"here he is, he's Robert Boothe!\n");

		Song s3 = new Song("Professor B",
				"Debugger Love",
				"I didn't used to like her\n"+
						"I stuck with what I knew\n"+
						"She was waiting there to help me,\n"+
						"but I always thought print would do\n\n"+
						"Debugger love .........\n"+
				"Now I'm so in love with you\n");

		System.out.println("Testing getArtist: " + s1.getArtist());
		System.out.println("Testing getTitle: " + s1.getTitle());
		System.out.println("Testing getLyrics:\n" + s1.getLyrics());

		System.out.println();
		System.out.println("Testing toString:");
		System.out.println("Song 1: " + s1);
		System.out.println("Song 2: " + s2);
		System.out.println("Song 3: " + s3);

		System.out.println();
		System.out.println("Testing compareTo:");
		System.out.println("Song1 vs Song2 = " + s1.compareTo(s2));
		System.out.println("Song2 vs Song1 = " + s2.compareTo(s1));
		System.out.println("Song1 vs Song3 = " + s1.compareTo(s3));
		System.out.println("Song3 vs Song1 = " + s3.compareTo(s1));
		System.out.println("Song1 vs Song1 = " + s1.compareTo(s1));
		
		//testing compare artist/comparator class
		CmpArtist cmpArtist = new CmpArtist();
		System.out.println();
		System.out.println("Testing CmpArtist:");
		System.out.println("Song1 vs Song2 = " + cmpArtist.compare(s1, s2));
		System.out.println("Song2 vs Song1 = " + cmpArtist.compare(s2, s1));
		System.out.println("Song1 vs Song3 = " + cmpArtist.compare(s1, s3));
		System.out.println("Song3 vs Song1 = " + cmpArtist.compare(s3, s1));
		System.out.println("Song1 vs Song1 = " + cmpArtist.compare(s1, s1));
		
		//testing compare title
		CmpTitle cmpTitle = new CmpTitle();
		System.out.println();
		System.out.println("Testing CmpTitle:");
		System.out.println("Song1 vs Song2 = " + cmpTitle.compare(s1, s2));
		System.out.println("Song2 vs Song1 = " + cmpTitle.compare(s2, s1));
		System.out.println("Song1 vs Song3 = " + cmpTitle.compare(s1, s3));
		System.out.println("Song3 vs Song1 = " + cmpTitle.compare(s3, s1));
		System.out.println("Song1 vs Song1 = " + cmpTitle.compare(s1, s1));
		

	}
}
