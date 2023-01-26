//Estelle Brady
//CS 351 - 401
//Collaborated with Miguel Garcia, Marwan Salama, Jaihui, Christian Oropeza, Julian Moreno
package edu.uwm.cs351;
import java.util.function.Consumer;
import edu.uwm.cs.util.Primes;

/**
 * A class to manage string instances.
 * All equal string instances that are interned will be identical.
 */


public class StringCache extends Primes {
	// even with a Spy, we still use "private":
	private String[] table;
	private int numEntries;


	// TODO: hash helper function used by wellFormed and intern

	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}


	private int nextIndex(int i) {
		//if it is at the "first" index, we go to the last index (like a cycle)
		if (i == 0) return table.length -1;
		//otherwise, we go backwards again.
		else return i-1;
	}

	//from textbook
	// The return value is a valid index of the table’s arrays. The index is
	// calculated as the remainder when the absolute value of the key’s
	// hash code is divided by the size of the table’s arrays.
	private int hash(String key) {
		return Math.abs(key.hashCode() % table.length);
	}

	private int findIndexHelper(String key) {
		//we make an "index" for key
		int i = hash(key);

		while((table[i]!= null) && (!(table[i].equals(key)))) {
			//if collision occurs, we check for the next open index
			i = nextIndex(i);
		}
		//we return that index
		return i;
	}

	private boolean wellFormed() {
		// 1. table is non-null and prime length
		if((table == null) || (!isPrime(table.length)))
			return report("table is non-null or does not have a prime length");
		// 2. number of entries is never more half the table size
		if(numEntries > table.length/2)
			return report("number of entries shoudl not be more than half the table size");
		// 3. number of non-null entries in the table is numEntries
		int counter = 0;
		for(int i = 0; i<table.length; i++) {
			if(table[i]!= null) {
				//if it is not found
				if (i != findIndexHelper(table[i])) return report("there is a string array that isn't hashed in the corrext place");
				counter++;
			}
		}
		if(numEntries != counter)
			return report("number of non-null entries in table is not equal to numEntries");
		// 4. every string in the array is hashed into the correct place
		//    using backward linear probing
		// TODO
		return true;
	}
	private StringCache(boolean ignored) {} // do not change
	/**
	 * Create an empty string cache.
	 */
	public StringCache() {
		numEntries = 0;
		//makes it 2
		table = new String[nextPrime(numEntries)];
		assert wellFormed() : "invariant broken in constructor"; 
	}

	private void rehash() {
		//make a new string array
		String[] temp = table;
		//set c to our capacity
		int c = nextPrime(4*numEntries);
		//set the table to the capacity
		table = new String[c];
		//loop through and check if it is null
		for(int i = 0; i<temp.length; i++) {
			//if it is not null
			if(temp[i]!= null) {
				table[findIndexHelper(temp[i])] = temp[i];
			}
		}
	}
	// TODO: declare rehash helper method

	/**
	 * Return a string equal to the argument.  
	 * For equal strings, the same (identical) result is always returned.
	 * As a special case, if null is passed, it is returned.
	 * @param value string, may be null
	 * @return a string equal to the argument (or null if the argument is null).
	 */
	public String intern(String value) {
		assert wellFormed() : "invariant broken before intern";

		//if value is null return it
		if(value == null)
			return null;

		//if that specific index is null then we give it value
		if(table[findIndexHelper(value)] == null) {
			table[findIndexHelper(value)] = value;
			//increment numEntries
			++numEntries;

			//for the invariant and rehashes
			if(numEntries > table.length/2) {
				rehash();
			}
			return table[findIndexHelper(value)];
		}else {
			assert wellFormed() : "invariant broken after intern";
			return table[findIndexHelper(value)];
		}
	}

	public static class Spy { // do not modify (or use!) this class
		/**
		 * Create a String Cache with the given data structure,
		 * that has not been checked.
		 * @return new debugging version of a StringCache
		 */
		public StringCache create(String[] t, int c) {
			StringCache sc = new StringCache(false);
			sc.table = t;
			sc.numEntries = c;
			return sc;
		}

		/**
		 * Return the number of entries in the string cache
		 * @param sc string cache, must not be null
		 * @return number of entries in the cache.
		 */
		public int getSize(StringCache sc) {
			return sc.numEntries;
		}

		/**
		 * Return capacity of the table in the cache
		 * @param sc cache to examine, must not be null
		 * @return capacity
		 */
		public int getCapacity(StringCache sc) {
			return sc.table.length;
		}

		public boolean wellFormed(StringCache sc) {
			return sc.wellFormed();
		}

		public Consumer<String> getReporter() {
			return reporter;
		}

		public void setReporter(Consumer<String> c) {
			reporter = c;
		}

	}
}
