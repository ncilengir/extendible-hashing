import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class UnsortedTableMap<K, V> extends AbstractMap<K, V> {
	/** Underlying storage for the map of entries. */
	private ArrayList<MapEntry<K, V>> table = new ArrayList<>();

	/** Constructs an initially empty map. */
	public UnsortedTableMap() {
	}

	// private utility
	/** Returns the index of an entry with equal key, or -1 if none found. */
	private int findIndex(K key, V value) {
		int n = table.size();
		for (int j = 0; j < n; j++)
			if (table.get(j).getKey().equals(key) && table.get(j).getValue().equals(value))
				return j;
		return -1; // special value denotes that key was not found
	}

	// public methods

	@Override
	public int size() {
		return table.size();
	}

	@Override
	public AbstractMap.MapEntry<K, V> get(K key, V value) {
		int j = findIndex(key, value);
		if (j == -1)
			return null; // not found

		return table.get(j);
	}

	public void setLocaldepth(K key, V value) {
		int j = findIndex(key, value);
		if (j != -1)
			table.get(j).setLocaldepth(globalDepth);
		;
	}

	@Override
	public V put(K key, V value, int counter) {
		int j = findIndex(key, value);
		if (j == -1) {
			table.add(new MapEntry<>(key, value)); // add new entry
			j = findIndex(key, value);
			table.get(j).setCounter(counter);
			return null;
		} else // key already exists
			table.get(j).setCounter(counter);
		return table.get(j).setValue(value); // replaced value is returned
	}

	public K findRemovablekey() {
		int n = table.size();
		for (int j = 0; j < n; j++) {
			if (table.get(j).getLocaldepth() != globalDepth) {
				return table.get(j).getKey();
			}
		}
		return null;
	}

	public V findRemovablevalue() {
		int n = table.size();
		for (int j = 0; j < n; j++) {
			if (table.get(j).getLocaldepth() != globalDepth) {
				return table.get(j).getValue();
			}
		}
		return null;
	}

	@Override
	public int remove(K key, V value) {
		int j = findIndex(key, value);
		int n = size();
		if (j == -1)
			return 0; // not found
		int answer = table.get(j).getCounter();
		if (j != n - 1)
			table.set(j, table.get(n - 1)); // relocate last entry to 'hole'
											// created by removal
		table.remove(n - 1); // remove last entry of table
		return answer;
	}

	// ---------------- nested EntryIterator class ----------------
	private class EntryIterator implements Iterator<Entry<K, V>> {
		private int j = 0;

		public boolean hasNext() {
			return j < table.size();
		}

		public Entry<K, V> next() {
			if (j == table.size())
				throw new NoSuchElementException("No further entries");
			return table.get(j++);
		}

		public void remove() {
			throw new UnsupportedOperationException("remove not supported");
		}
	} // ----------- end of nested EntryIterator class -----------

	// ---------------- nested EntryIterable class ----------------
	private class EntryIterable implements Iterable<Entry<K, V>> {
		public Iterator<Entry<K, V>> iterator() {
			return new EntryIterator();
		}
	} // ----------- end of nested EntryIterable class -----------

	@Override
	public Iterable<Entry<K, V>> entrySet() {
		return new EntryIterable();
	}
}
