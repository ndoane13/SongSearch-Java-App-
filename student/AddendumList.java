package student;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import student.AddendumList.L2Array;

/*
 * AddendumList.java
 * Luke Jillings and Nicholas Doane
 * 
 * Initial starting code by Prof. Boothe Sep. 2019
 *
 * To an external user and AddendumList appears as a single sorted list ordered
 * by the comparator. Duplicates are allowed, and new items with duplicate keys
 * are added after any matching items.
 * 
 * Internally, at its simplest, an AddendumList is one big sorted array, but
 * additions are added to a small secondary (addendum) array. Searching first
 * checks the big array, and if a match is not found it then checks the
 * addendum array. Searching is fast because it can use binary search, and
 * adding is fast because adds are added into the small addendum array.
 * 
 * In fact there can be multiple levels of addendum arrays of exponentially
 * decreasing sizes. Searching works it's way through all of them. 
 * 
 * All additions are to an array of minimum size. When the minimum sized array
 * becomes full, it is possibly merged with the preceding array. Merging occurs
 * when the preceding array is of equal or greater size. This merging might
 * then continue up the chain to the top.
 * 
 * After a merger there will be no minimum sized array. A new one is created
 * upon the next addition.
 *  
 * The implementation the AddendumList is stored internally as an array of
 * arrays.
 * 
 * The top level array (called level 1) contains references to the 2nd level
 * arrays.
 * 
 * NOTE: normally fields, internal nested classes and non API methods should
 *       all be private, however they have been made public so that the tester
 *       can access them.
 */
@SuppressWarnings("unchecked")  // added to suppress warnings about all the
// type casting of Object arrays
public class AddendumList<E> implements Iterable<E> {
	private static final int L1_STARTING_SIZE = 4;
	private static final int L2_MINIMUM_SIZE = 4;   
	public int size;             // total number of elements stored
	public Object[] l1array;     // really is an array of L2Array, but the 
	// compiler won't let me cast to that
	public int l1numUsed;
	private Comparator<E> comp;

	// create an empty list
	// Always has at least 1 second level array even if empty. It makes the
	// code easier. 
	// (DONE)
	public AddendumList(Comparator<E> c){
		size = 0;
		// the l1array is really an array of L2Array's, but for backwards
		// compatibility reasons Java does not allow array declarations
		// involving generic types. Instead we must declare it as just an array
		// of Objects.
		l1array = new Object[L1_STARTING_SIZE]; // because no generic arrays
		l1array[0] = new L2Array(L2_MINIMUM_SIZE);    // first 2nd level array
		l1numUsed = 1;
		comp = c;
	}

	// nested class for 2nd level arrays
	// (DONE)
	public class L2Array {
		public E[] items;  
		public int numUsed;

		public L2Array(int capacity) {
			// an L2Array is really an array of the generic type E elements,
			// but you can't create an array of a generic type, so it is created
			// as an array of Objects and then cast to and array of E
			items = (E[])new Object[capacity];
			numUsed = 0;
		}
	}

	//total size (number of entries) in the entire data structure
	// (DONE)
	public int size(){
		return size;
	}

	// null out all references so that the garbage collector can recycle them
	// but keep the (now empty) l1array 
	// (DONE)
	public void clear(){
		size = 0;
		Arrays.fill(l1array, null);  // clear l1 array
		l1array[0] = new L2Array(L2_MINIMUM_SIZE);    // first 2nd level array
		l1numUsed = 1;
	}


	// Find the index of the 1st matching entry in the specified array.
	// If the item is not found, it returns a negative value
	// (-insertion point -1). 
	// The insertion point is the point at which the key would be inserted into
	// the array, which may be an unused slot at the end of the array
	public int findFirstInArray(E item, L2Array a){	
		int low = 0, mid, hold = -1, high = a.numUsed - 1, compared;
		//actual search
		while(low <= high) {
			mid = (low + high) / 2;
			compared = comp.compare(a.items[mid], item);
			if (compared == 0) {
				//hold mid while it continues searching left
				hold = mid;
				high = mid - 1;
			}else if(compared < 0) {
				low = mid + 1;
			}else {
				high = mid - 1;
			}
		}
		//if it never finds anything return where it should be
		if(hold == -1) {
			return -(low);
			//if it does find something, return the last number held.
		}else {
			return hold;
		}
	}



	/**
	 * check if list contains a match
	 * searches all levels, largest levels first
	 */
	public boolean contains(E item){
		for(int x = 0; l1array[x] != null; x++) {
			L2Array l2array = (L2Array)l1array[x];
			if(Arrays.binarySearch(l2array.items, 0, l2array.numUsed, item, comp) >= 0){       	
				return true;
			}		
		}
		return false;  // never found a match
	}

	// find the index of the insertion point of this item, which is
	// the index after any smaller or matching entries
	// this might be an unused slot at the end of the array
	// note: this will only be used on the last (very small) addendum array
	//       before this is called, the caller should make sure that there is
	//       space to add an item

	public int findIndexAfter(E item, L2Array a){
		int low = 0, mid, hold = -1, high = a.numUsed - 1, compared;
		while(low <= high) {
			mid = (low + high) / 2;
			compared = comp.compare(a.items[mid], item);
			if (compared == 0) {
				hold = mid;
				low = mid + 1;
			}else if(compared < 0) {
				low = mid + 1;
			}else {
				high = mid - 1;
			}
		}
		if(hold == -1) {
			return low;
		}else {
			return hold + 1;
		}
	}


	/** 
	 * Add this new item after any other matching items.
	 * 
	 * Always adds to the last (and minimum sized) addendum array. 
	 * 
	 * If the last array is full or not minimum sized, it first performs
	 * any required merging and then makes a new minimum size array
	 * for adding into.
	 * 
	 * Use findIndexAfter() to find the insertion position.
	 * Use merge1Level() for merging.
	 * 
	 * Remember to increment numUsed for the L2Array inserted into, and
	 * increment size for the whole data structure.
	 */
	public boolean add(E item){
		//this sets the target array that were adding to
		//and declares another array to check if merging is need later
		L2Array targetArr = (L2Array)l1array[l1numUsed-1], checkArr;
		//if there is more than 1 l2array we can create the check array
		//if not, no check array, otherwise index oob.
		if(l1numUsed > 1) {
			checkArr = (L2Array)l1array[l1numUsed - 2];
			//if target array is full or, because of merging,
			//becomes greater than its predecessor, continue merging.
			while(targetArr.numUsed >= checkArr.numUsed) {
				merge1Level();
				//from here we reset the target array to the newly merged array
				targetArr = (L2Array)l1array[l1numUsed - 1];
				//like earlier we make sure that the check array
				//wont cause an index oob before setting
				//if it merged all the way to the top we break out
				if(l1numUsed > 1) {
					checkArr = (L2Array)l1array[l1numUsed - 2];
				}else break;
			}
			//if the l1 array becomes full we create a new temp l1 array
			if(l1numUsed == l1array.length ) {//&& targetArr.numUsed < checkArr.numUsed) {
				Object[] templ1Arr = new Object[l1numUsed];
				int tempSize = size();
				//we pass references for the old l1 to the temp l1
				for(int i = 0; i < l1numUsed; i++) {
					templ1Arr[i] = l1array[i];
				}
				//clear out the l1
				clear();
				//assign the new capacity value to the l1
				l1array = new Object[templ1Arr.length*2];
				//pass back the references from temp
				for(int i = 0; i < templ1Arr.length; i++) {
					l1array[i] = templ1Arr[i];
					if(i > 0) {l1numUsed++;}
				}
				//update the grown array to its old size
				size = tempSize;
			}
			//limit the addition of a new L2 to only when the current is full
			if(targetArr.numUsed == targetArr.items.length) {
				l1array[l1numUsed] = new L2Array(L2_MINIMUM_SIZE);
				l1numUsed++;
				targetArr = (L2Array)l1array[l1numUsed - 1];
			}
		}else if(l1numUsed == 1 && targetArr.numUsed == 4) {
			l1array[l1numUsed] = new L2Array(L2_MINIMUM_SIZE);
			l1numUsed++;
			targetArr = (L2Array)l1array[l1numUsed - 1];
		}
		//find where the item is supposed to go.
		int targetIndex = findIndexAfter(item, targetArr);
		//add the item and move all other array items down
		//searching from the back eliminates using a temp var
		//to hold the next item. and probably some index oob stuff
		for(int i = targetArr.numUsed; i >= targetIndex; i--) {
			if(i == targetIndex) targetArr.items[i] = item;
			else targetArr.items[i] = targetArr.items[i - 1];
		}

		//finally increment all this stuff.
		//target array grew by 1
		//total size of the l1 grew by 1
		targetArr.numUsed++;
		size++;
		return true;
	}


	public void merge1Level() {
		L2Array firstArray = (L2Array)l1array[l1numUsed - 2];
		L2Array secondArray = (L2Array)l1array[l1numUsed - 1];
		L2Array finalArray = new L2Array(firstArray.numUsed + secondArray.numUsed);
		finalArray.numUsed = firstArray.numUsed + secondArray.numUsed;
		int firstIndex = 0, secondIndex = 0, finalIndex = 0;

		while(finalIndex < finalArray.numUsed){
			if (secondIndex == secondArray.numUsed) {
				finalArray.items[finalIndex] = firstArray.items[firstIndex];
				firstIndex++;
				finalIndex++;
			}else if(firstIndex == firstArray.numUsed) {
				finalArray.items[finalIndex] = secondArray.items[secondIndex];
				secondIndex++;
				finalIndex++;
			}else {
				if (comp.compare(firstArray.items[firstIndex], secondArray.items[secondIndex]) <= 0) {
					finalArray.items[finalIndex] = firstArray.items[firstIndex];
					finalIndex++;
					firstIndex++;
				}else {
					finalArray.items[finalIndex] = secondArray.items[secondIndex];
					finalIndex++;
					secondIndex++;
				}
			}
		}
		l1array[l1numUsed - 2] = finalArray;
		l1array[l1numUsed - 1] = null;
		l1numUsed--;
	}
	// Merge all levels
	// This is used: by iterator(), toArray() and subList()
	// This makes these easy to implement, and the full merge time would
	// likely be required for these operations anyway.
	// Note: the merging will likely cause the size of the array to no longer
	//       be a power of two.
	private void mergeAllLevels() {
		//while there is more than 1 l2 array, keep merging. 
		while(l1numUsed > 1) {
			merge1Level();
		}
	}

	/**
	 * copy the contents of the AddendumList into the specified array
	 * @param a - an array of the actual type and of the correct size
	 * @return the filled in array
	 */
	public E[] toArray(E[] a){
		//start by merging everything into one array
		mergeAllLevels();
		
		//assign the newly merged array to a variable (because i can't get it to work with straight casting)
		L2Array l2array = (L2Array)l1array[0];
		int copyLength = l2array.numUsed;
		
		//checking for correct size of "a" array, then chastising user
		if(l2array.numUsed > a.length) {
			System.out.println("The given array won't fit everything from the l2array! starting anyway...");
			copyLength = a.length; 
		}else if(l2array.numUsed < a.length) {
			System.out.println("The given array is too big! It's your fault there's nulls...");
		}
		
		//System.arraycopy makes a copy of src, starting at, dest, starting at, length specified
		//This also worked well with a for look that just goes through it piece by piece. but this looks nicer
		System.arraycopy(l2array.items, 0, a, 0, copyLength);
		
		//return the newly filled array 
		return a;
	}

	/**
	 * Returns a new independent AddendumList whose elements range 
	 * from fromElemnt(inclusive) to toElement(exclusive).
	 * The original list retains its original contents.
	 * @param fromElement
	 * @param toElement
	 * @return the sublist
	 */
	public AddendumList<E> subList(E fromElement, E toElement){
		// TO DO
		//merge current levels into one
		mergeAllLevels();
		
		//fields, indices and return AL
		L2Array searched = (L2Array)l1array[0];
		AddendumList<E> newSubList = new AddendumList<E>(comp);
		int fromIndex = Math.abs(findFirstInArray(fromElement, searched));
		int toIndex = Math.abs(findFirstInArray(toElement, searched));
		
		//add items from the main list to the sublist
		while(fromIndex < toIndex) {
			newSubList.add(searched.items[fromIndex]);
			fromIndex++;
		}
		
		//return the sublist
		return newSubList;
	}

	/**
	 * Returns an iterator for this list.
	 * This method just merges the items into a single array and creates an
	 * instance of the inner Itr() class
	 * (DONE)   
	 */
	public Iterator<E> iterator() {
		mergeAllLevels();
		return new Itr();
	}

	/**
	 * Iterator 
	 */
	private class Itr implements Iterator<E> {
		public int index;
		public L2Array l2array;

		/*
		 * initialize iterator to the start of list
		 */
		Itr(){
			// TO DO
			index = 0;
			l2array = (L2Array)l1array[0];
		}

		/**
		 * check if more items
		 */
		public boolean hasNext() {
			// TO DO
			
			return index != l2array.numUsed;
		}

		/**
		 * return item and move to next
		 * throws NoSuchElementException if off end of list
		 */
		public E next() {
			// TO DO
			try {
				index++;
				return l2array.items[index-1];
			}catch(NoSuchElementException ex){
				System.err.println(ex);
			}
			return null;
		}

		/**
		 * Remove is not implemented. Just use this code.
		 * (DONE)
		 */
		public void remove() {
			throw new UnsupportedOperationException();	
		}
	}
}