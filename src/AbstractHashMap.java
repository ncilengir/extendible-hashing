import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class AbstractHashMap<K, V> extends AbstractMap<K, V> {
	protected int n = 0; // number of entries in the dictionary
	protected int capacity; // length of the table
	protected int oldLocaldepth = 0;

	/** Creates a hash table with the given capacity and prime factor. */
	public AbstractHashMap(int cap, int p) {
		capacity = cap;
		globalDepth = 8;
		createTable();
	}

	/** Creates a hash table with given capacity and prime factor 109345121. */
	public AbstractHashMap(int cap) {
		this(cap, 109345121);
	} // default prime

	/** Creates a hash table with capacity 17 and prime factor 109345121. */
	public AbstractHashMap() {
		this(256);
	} // default capacity

	// public methods

	@Override
	public int size() {
		return n;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractMap.MapEntry<K, V> get(K key, V value) {
		return (AbstractMap.MapEntry<K, V>) bucketGet(key, value);
	}

	@Override
	public int remove(K key, V value) {
		return bucketRemove(Integer.parseInt(codeHash(key), 2), key, value);
	}

	@Override
	public V put(K key, V value, int counter) {

		V answer = bucketPut(codeHash(key), key, value, 1);

		return answer;
	}

	// private utilities
	/** Hash function applying MAD method to default hash code. */
	/** Updates the size of the hash table and rehashes all entries. */
	protected void resize(int newCap) {
		ArrayList<Entry<K, V>> buffer = new ArrayList<>(n);
		for (Entry<K, V> e : entrySet())
			buffer.add(e);
		capacity = newCap;
		createTable(); // based on updated capacity
		n = 0; // will be recomputed while reinserting entries
		for (Entry<K, V> e : buffer) {
			oldLocaldepth = 0;

			if (e.getLocaldepth() != globalDepth) {
				String br = codeHash(e.getKey()), or = "";
				br = to32bit(br);
				oldLocaldepth = e.getLocaldepth();

				br = br.substring(br.length() - globalDepth + 1);

				or = "1" + br;
				bucketPut(or, e.getKey(), e.getValue(), e.getCounter());

				br = "0" + br;
				bucketPut(br, e.getKey(), e.getValue(), 0);
			}
		}
	}

	// protected abstract methods to be implemented by subclasses
	/** Creates an empty table having length equal to current capacity. */

	public void readFile() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader("cartoon.txt"));
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(in);

			while (scanner.hasNext()) {
				@SuppressWarnings("unchecked")
				V[] words = (V[]) scanner.nextLine().split(" ");
				for (int i = 0; i < words.length; i++) {
					put(functionHash(words[i]), words[i], 1);
				}

			}

		} catch (Exception e) {
			System.out.println("There was a problem");
		}
	}

	@SuppressWarnings("unchecked")
	protected K functionHash(V values) {

		char[] toKey = ((String) values).toCharArray();
		int aKey = 0;

		for (char ch : toKey) {
			aKey = (aKey * 31) + ch;
		}
		aKey = Math.abs(aKey);
		return (K) String.valueOf(aKey);

	}

	protected String codeHash(K key) {

		int aKey = Integer.parseInt((String) key);
		String binary = "";

		while (aKey > 0) {

			binary = ((aKey % 2) == 0 ? "0" : "1") + binary;
			aKey = aKey / 2;
		}
		return binary;
	}

	protected String to32bit(String str) {
		str = "00000000000000000000000000000000".substring(str.length()) + str;
		return str;
	}

	protected abstract void createTable();

	protected abstract V bucketGet(K k, V v);

	protected abstract V bucketPut(String h, K k, V v, int C);

	protected abstract int bucketRemove(int h, K k, V v);
}
