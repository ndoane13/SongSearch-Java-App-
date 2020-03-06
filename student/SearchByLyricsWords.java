package student;

import java.text.DecimalFormat;
import java.util.*;

public class SearchByLyricsWords {

	//class fields
	public String[] comWords = {"the", "of", "and", "a", "to", "in", "is", "you", "that", "it", "he", "for", "was", "on", 
			"are", "as", "with", "his", "they", "at", "be", "this", "from", "I", "have", "or", 
			"by", "one", "had", "not", "but", "what", "all", "were", "when", "we", "there",
			"can", "an", "your", "which", "their", "if", "do", "will", "each", "how", "them",
			"then", "she", "many", "some", "so", "these", "would", "into", "has", "more",
			"her", "two", "him", "see", "could", "no", "make", "than", "been", "its", "now",
			"my", "made", "did", "get", "our", "me", "too"};//76 words
	private TreeMap<String, TreeSet<Song>> allSongs;
	private TreeSet<String> commonWords;

	//method for original top10words()
	private TreeMap<Integer, TreeSet<String>> top10 = new TreeMap<Integer, TreeSet<String>>();

	public SearchByLyricsWords(SongCollection sc) {
		//constructor fields
		allSongs = new TreeMap<String, TreeSet<Song>>();
		commonWords = new TreeSet<String>();
		Song[] songs = sc.getAllSongs();
		String[] lyrics;
		String currentWord;

		//creating commonWords set
		for(int i = 0; i < comWords.length; i++) {
			commonWords.add(comWords[i]);
		}

		//inserting all lyrics and songs to map
		for(int songIndex = 0; songIndex < songs.length; songIndex++) {
			lyrics = songs[songIndex].getLyrics().split("[^a-zA-Z]+");
			for(int wordIndex = 0; wordIndex < lyrics.length; wordIndex++) {
				currentWord = lyrics[wordIndex].toLowerCase();
				//check that the current word isn't a single char or empty string
				if(currentWord.length() <= 1 || commonWords.contains(currentWord)) {continue;}
				//adding song to set
				TreeSet<Song> set = allSongs.get(currentWord);
				if(set == null) {
					set = new TreeSet<Song>();
					allSongs.put(currentWord, set);
				}
				set.add(songs[songIndex]);
			}
		}
	}

	//TODO complete search method
	//Search method for use with the SongSearchGUI
	public Song[] search(String lyricsWords) {
		//fields
		String[] splitLyrics = lyricsWords.toLowerCase().split("[^a-zA-Z]+");
		TreeSet<String> setLyrics = new TreeSet<String>();
		TreeSet<Song> tempSet = new TreeSet<Song>();

		//creating the set of lyrics
		for(int index = 0; index < splitLyrics.length; index++) {
			if(splitLyrics[index].length() > 1) {
				setLyrics.add(splitLyrics[index]);
			}
		}    	
		setLyrics.removeAll(commonWords);

		//adding the sets
		for(String word: setLyrics) {
			if(allSongs.containsKey(word)) {tempSet.addAll(allSongs.get(word));}
		}
		//intersecting the sets
		for(String word: setLyrics) {
			if(allSongs.containsKey(word)) {tempSet.retainAll(allSongs.get(word));}
		}

		return tempSet.toArray(new Song[tempSet.size()]);
	}

	public void statistics() {
		//class fields
		Iterator<TreeSet<Song>> itr = allSongs.values().iterator();
		int s = 0;

		//Print out the count of all keys
		System.out.println("Total Number of Keys: " + allSongs.size());
		//create the iterator for the total size of the DS
		while(itr.hasNext()) {
			TreeSet<Song> set = itr.next();
			s += set.size();
		}
		int totalSpaceMap = allSongs.size()*6, totalSpaceSets = s*6;
		double coefficient = (double)(totalSpaceMap+totalSpaceSets)/s;
		DecimalFormat df = new DecimalFormat("#.######");
		System.out.println("Total size of structure: " + s);
		System.out.println("Total space used just by the TreeMap: " + totalSpaceMap + " (based on 6N)");
		System.out.println("Total space used just by the TreeSet: " + totalSpaceSets + " (based on 6N)");
		System.out.println("Total space used by CDS: " + (totalSpaceMap + totalSpaceSets));
		System.out.println("Space usage factor used by CDS: " + df.format(coefficient));
	}

	//extra credit method
	public void top10words() {
		for(String keyWord: allSongs.keySet()) {
			TreeSet<String> set = top10.get(allSongs.get(keyWord).size());
			if(set == null) {
				set = new TreeSet<String>();
				top10.put((Integer)allSongs.get(keyWord).size(), set);
			}
			set.add(keyWord);
		}
		for(int i = 1; i < 11; i++) {
			System.out.println(top10.pollLastEntry());
		}
	}

	//main method for testing proper creation
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		//testing creation of sbl
		SongCollection sc = new SongCollection(args[0]);
		SearchByLyricsWords sblw = new SearchByLyricsWords(sc);
		//printing statistics
		sblw.statistics();

		//testing search()
		if (args.length >= 1){
			System.out.println("\nsearching for: "+args[1]);
			Song[] byTitleResult1 = sblw.search(args[1]);
			SongCollection.firstTen(byTitleResult1);
		}

		//checking for top10 call
		for(String top10: args) {
			if(top10.compareTo("-top10words") == 0)
				sblw.top10words();
		}

		long endTime = System.nanoTime();
		double duration = ((endTime - startTime)/1000000);
		System.out.println("\nTime taken for main method to work = " + duration + " milliseconds");
	}
}