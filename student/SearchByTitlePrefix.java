package student;

import student.Song.CmpTitle;

public class SearchByTitlePrefix{
	
	//class fields
	private AddendumList<Song> songAL;
	public CmpTitle cmpTitle;
	
	public SearchByTitlePrefix(SongCollection sc) {
		//constructor fields
		cmpTitle = new CmpTitle();
		songAL = new AddendumList<Song>(cmpTitle);
		
		//add the songs to the ADDL
		for(int index = 0; index < sc.songs.length; index++) {
			songAL.add(sc.songs[index]);
		}
		System.out.println("Total comparisons to build ADDL: " + cmpTitle.getCmpCnt());
	}
	
	public Song[] search(String titlePrefix) {
		//fields
		AddendumList<Song> tempList;
		Song fromIndex = new Song("generic", titlePrefix, "generic"), toIndex;
		cmpTitle.cmpCnt = 0;
		
		//assigning toIndex
		String cutPrefix, newLetter;
		cutPrefix = titlePrefix.substring(0, titlePrefix.length()-1);
		newLetter = Character.toString(titlePrefix.charAt(titlePrefix.length()-1)+1); 
		toIndex = new Song("generic", cutPrefix.concat(newLetter), "generic");
		
		//searching addl
		tempList = songAL.subList(fromIndex, toIndex);
		Song[] titleList = new Song[tempList.size()];
		
		//return...
		System.out.println("Total comparison count: " + cmpTitle.getCmpCnt());
		return tempList.toArray(titleList); 
	}

	//just for testing
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("usage: prog songfile [search string]");
			return;
		}

		SongCollection sc = new SongCollection(args[0]);
		SearchByTitlePrefix sbtp = new SearchByTitlePrefix(sc);

		if (args.length >= 1){
			System.out.println("\nsearching for: "+args[1]);
			Song[] byTitleResult1 = sbtp.search(args[1]);

			// to do: show first 10 matches
			SongCollection.firstTen(byTitleResult1);
			
			System.out.println("\nsearching for: "+args[2]);
			Song[] byTitleResult2 = sbtp.search(args[2]);

			// to do: show first 10 matches
			SongCollection.firstTen(byTitleResult2);
			
			System.out.println("\nsearching for: "+args[3]);
			Song[] byTitleResult3 = sbtp.search(args[3]);

			// to do: show first 10 matches
			SongCollection.firstTen(byTitleResult3);
		}

	}

}
