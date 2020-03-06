package student;
import java.util.*;
public class PhraseRanking {

	//Class that takes in a phrase to search for, and returns a ranking of song lyrics
	//ranking is based on length of the phrase, where exact match of length is the best ranking
	//multiple songs can be ranked equally

	static int rankPhrase(String lyrics, String lyricsPhrase) {
		//fields
		String word;
		String[] phrase;
		int count = 1, firstWordIndex = 0,lastWordIndex = 0, firstWordLength, lastWordLength;
		PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>();

		//split lyricsPhrase into its words by white space.
		phrase = lyricsPhrase.toLowerCase().split("\\s");
		lyrics = lyrics.toLowerCase();
		firstWordLength = phrase[0].length();
		//TODO: first while-loop searches lyrics for instances of the first word
		while (lyrics.indexOf(phrase[0], firstWordIndex) != -1) {
			firstWordIndex = lyrics.indexOf(phrase[0], firstWordIndex);
			if(firstWordIndex != 0 && (Character.isLetter(lyrics.charAt(firstWordIndex-1)) ||
					Character.isLetter(lyrics.charAt(firstWordIndex + firstWordLength)))) {
				firstWordIndex += firstWordLength;
				continue;
			}
			firstWordIndex += firstWordLength;
			lastWordIndex = firstWordIndex;

			//TODO: second while-loop searches for every instance of next words in lyricsPhrase
			while(lyrics.indexOf(phrase[count], lastWordIndex) != -1) {
				lastWordIndex = lyrics.indexOf(phrase[count], lastWordIndex);
				lastWordLength = phrase[count].length();
				if(!Character.isLetter(lyrics.charAt(lastWordIndex - 1))
						&& !Character.isLetter(lyrics.charAt(lastWordIndex + lastWordLength))) {
					lastWordIndex += phrase[count].length();
					count++;
					if(count == phrase.length) {
						break;
					}
					continue;
				}else {
					lastWordIndex += phrase[count].length();
					continue;
				}
			}
			if(count == phrase.length) {
				minHeap.add(lastWordIndex - (firstWordIndex - firstWordLength));
			}
			count = 1;
		}

		//TODO:if indexOf() first word returns -1, the loop is complete

		//TODO: return best ranking
		if(minHeap.isEmpty()) {
			return -1;
		}else {
			return minHeap.remove();
		}
	}


	public static void main(String[] args) {
		SongCollection sc = new SongCollection(args[0]);
		String lyricsPhrase = args[1];
		Song[] songs = sc.getAllSongs();
		String lyrics;
		int count = 0;
		System.out.println("Searching for " + args[1]);
		for(int songIndex = 0; songIndex < songs.length; songIndex++) {
			lyrics = songs[songIndex].getLyrics();
			int rank = rankPhrase(lyrics,lyricsPhrase);
			if (rank != -1) {
				count++;
				System.out.println(rank + " " + songs[songIndex].artist + " \"" + songs[songIndex].title + "\"");
			}


		}
		System.out.println("Total songs found: " + count);

	}
}
