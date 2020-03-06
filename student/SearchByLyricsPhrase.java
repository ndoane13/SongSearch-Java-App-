package student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import student.Song.CmpRank;

public class SearchByLyricsPhrase {

	//class fields
	private Song[] songs;

	public SearchByLyricsPhrase(SongCollection sc) {
		//constructor fields
		songs = sc.getAllSongs();
	}

	//Search method for use with the SongSearchGUI
	public Song[] search(String lyricsPhrase) {
		//fields
		String[] splitLyrics = lyricsPhrase.toLowerCase().split("[^a-zA-Z]+");
		String lyrics;
		int count = 1, firstWordIndex = 0,lastWordIndex = 0, firstWordLength, lastWordLength;
		ArrayList<Song> returnSongs = new ArrayList<Song>();
		TreeMap<Integer, Song> min = new TreeMap<Integer, Song>();

		firstWordLength = splitLyrics[0].length();
		//for loop goes through the song collection that is passed
		for(int index = 0; index < songs.length; index++) {
			lyrics = songs[index].getLyrics().toLowerCase();
			firstWordIndex = 0;
			//while loop for finding the indices of first song
			while (lyrics.indexOf(splitLyrics[0], firstWordIndex) != -1) {
				firstWordIndex = lyrics.indexOf(splitLyrics[0], firstWordIndex);
				//general case that works for everything else
				if(firstWordIndex != 0 && (Character.isLetter(lyrics.charAt(firstWordIndex-1)) ||
						Character.isLetter(lyrics.charAt(firstWordIndex + firstWordLength)))) {
					firstWordIndex += firstWordLength;
					continue;
				}
				//case for it being the first word in lyrics
				else if(firstWordIndex == 0 && 
				        Character.isLetter(lyrics.charAt(firstWordIndex + firstWordLength))) {
					firstWordIndex += firstWordLength;
					continue;
				}
				firstWordIndex += firstWordLength;
				lastWordIndex = firstWordIndex;

				//second while-loop searches for every instance of next words in lyricsPhrase
				while(lyrics.indexOf(splitLyrics[count], lastWordIndex) != -1) {
					lastWordIndex = lyrics.indexOf(splitLyrics[count], lastWordIndex);
					lastWordLength = splitLyrics[count].length();
					//if the word matches we increase count and continue looking for next word
					if(!Character.isLetter(lyrics.charAt(lastWordIndex - 1))
							&& !Character.isLetter(lyrics.charAt(lastWordIndex + lastWordLength))) {
						lastWordIndex += splitLyrics[count].length();
						count++;
						//if count == search phrase, we break out of this loop
						if(count == splitLyrics.length) {
							break;
						}
						//otherwise continue on to the next word
						continue;
					//if word doesnt match we continue searching the lyrics	
					}else {
						lastWordIndex += splitLyrics[count].length();
						continue;
					}
				}
				//if the count found everything, assign the rank to the song and add it
				if(count == splitLyrics.length) {
					Integer rank = lastWordIndex - (firstWordIndex - firstWordLength);
					if(!min.containsKey(rank))
						min.put(rank, songs[index]);
				}
				count = 1;
			}
			if(!min.isEmpty()) {
				min.get(min.firstKey()).setRank(min.firstKey());
				returnSongs.add(min.get(min.firstKey()));
				min.clear();
			}
		}
		
		//sorting the songs and returning them
		CmpRank cmpRank = new CmpRank();
		Song[] sorted = returnSongs.toArray(new Song[returnSongs.size()]);
		Arrays.parallelSort(sorted, cmpRank);
		return sorted;
	}

	//main method for testing proper creation
	public static void main(String[] args) {
		//testing creation of sblp
		SongCollection sc = new SongCollection(args[0]);
		SearchByLyricsPhrase sblp = new SearchByLyricsPhrase(sc);

		//testing search()
		if (args.length >= 1){
			System.out.println("\nsearching for: "+args[1]);
			Song[] byTitleResult1 = sblp.search(args[1]);
			SongCollection.firstTenRanked(byTitleResult1);
		}
	}
}
