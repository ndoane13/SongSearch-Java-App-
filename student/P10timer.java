package student;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

// timer for Prog10 searching and ranking of lyrics phrase
// read the allSongs.txt
// building the map of words to songs
// time doing 20 searches on famous song quotes
public class P10timer {

   static String[] quotes = {
      "we don't need no education",
      "I get by with a little help from my friends",
      "Let it be",
      "Excuse me while I kiss the sky",
      "I'd rather laugh with the sinners than cry with the saints",
      "Wish You Were Here",
      "you don't know what you've got till it's gone",
      "You can checkout any time you like, But you can never leave",
      "There must be some kind of way out of here, said the joker to the thief",
      "the answer is blowin' in the wind",
      "The sun will shine in my back door one day",
      "It's better to burn out, than to fade away",
      "All you need is love",
      "Before you accuse me, take a look at yourself",
      "Don't ask me what I think of you",
      "a man hears what he wants to hear and disregards the rest",
      "This land was made for you and me",
      "And in the end, the love you take is equal to the love you make",
      "I ain't no fortunate one",
      "Freedom's just another word for nothin' left to lose"
   };

   static int correctNumMatches[] = {1, 1, 598, 1, 1, 18, 1, 3, 1, 1, 0, 0, 81, 1, 3, 1, 0, 1, 1, 3};

   public static void main(String[] args) {
      if (args.length == 0) {
         System.err.println("uses arguments: songfile [-verbose]");
         System.err.println("\nTiming results are written to the file: timeinfofile");
         return;
      }

      // command line argument to enable printing results
      boolean verbose = args.length == 2 && args[1].equals("-verbose");

      // read the song collection and build the search class' data structure
      SongCollection sc = new SongCollection(args[0]);
      SearchByLyricsPhrase searchByLP = new SearchByLyricsPhrase(sc);

      // it is too hard to check rankings because they could vary a bit
      // based on treatment of endofline characters and possible future
      // liberalization of ranking rules
      // just check number of matches
      // start timing 
      long startTime = System.nanoTime();

      int tooManyCnt = 0, tooFewCnt=0;
      for (int i = 0; i < quotes.length; i++) { // for every test phrase
         String phrase = quotes[i];      
         if (verbose) System.out.println("\nSearch: "+phrase);
         Song[] songList = searchByLP.search(phrase);
         int matches = 0;
         if (songList != null) {
        	 matches = songList.length;
        	 if (verbose) { // list found songs for verification
        		 for (Song song : songList) { // for every song
        			 String lyrics = song.getLyrics();
        			 int rank = PhraseRanking.rankPhrase(lyrics, phrase);
        			 System.out.printf("%d %s \"%s\"\n", rank, song.getArtist(), song.getTitle());
        		 }
        	 }
         }
         // System.out.print(matches + ", ");  // for generating correct counts
         if (matches != correctNumMatches[i]) {
        	System.out.flush();
            System.out.printf("ERROR: search \"%s\"\nexpected %d matches but found %d\n",
                  phrase, correctNumMatches[i], matches);
            if (matches < correctNumMatches[i])
               tooFewCnt++;
            else
               tooManyCnt++; 
            System.out.flush();
         }
      }

      // stop timing
      long finishTime = System.nanoTime();

      // Calculate the elapsed time:
      long elapsedTime = finishTime - startTime;
      File f = new File("timeinfofile");
      PrintStream outputFile = null;
      try {
         outputFile = new PrintStream(f);
      } catch (FileNotFoundException e) {
         System.err.println("Could not create timeinfofile");
         System.exit(1);
      }
      outputFile.printf(" %.2fms ",(elapsedTime / 1000000.0) );
      System.out.printf(" %.2fms ",(elapsedTime / 1000000.0) );
      if (tooFewCnt+tooManyCnt > 0) {
         outputFile.print("  errors ");
         if (tooFewCnt > 0)
            outputFile.printf("(%d too few) ", tooFewCnt);
         if (tooManyCnt > 0)
            outputFile.printf("(%d too many) ", tooManyCnt);
      }
      outputFile.println();
      outputFile.close();
   }  

   /**
    * helper method to print out the first N from an array of songs
    * copied from my SongCollection
    * @param array of songs
    */
   public static void showFirstNSongs(Song[] songList, int n) {
      if (songList == null || songList.length == 0) {
         System.out.println("No results returned.");
         return;
      }
      System.out.print("Total songs = " + songList.length);
      if (n < songList.length) {
         System.out.println(", first " + n + " matches:");
      } else {
         System.out.println(" :");
         n = songList.length;
      }

      for (int i = 0; i < n; i++)
         System.out.println(songList[i]);
   }

}
