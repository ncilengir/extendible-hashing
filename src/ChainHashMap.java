import java.util.ArrayList;

public class ChainHashMap<K, V> extends AbstractHashMap<K, V> {
	// a fixed capacity array of UnsortedTableMap that serve as buckets
	private UnsortedTableMap<K, V>[] table; // initialized within createTable

	// provide same constructors as base class
	public ChainHashMap() {
		super();
	}

	public ChainHashMap(int cap) {
		super(cap);
	}

	public ChainHashMap(int cap, int p) {
		super(cap, p);
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	protected void createTable() {
		table = (UnsortedTableMap<K, V>[]) new UnsortedTableMap[capacity];
	}

	@Override
	protected V bucketGet(K k, V v) {
		String hc = codeHash(k), originkey = hc, temp;
		hc = to32bit(hc);
		int h = 0, counter = 0;
		UnsortedTableMap<K, V> bucket;
		V value = null;
		Boolean flag = false, writen = false;

		for (int i = 0; i < (int) Math.pow(2, globalDepth - 8); i++) {
			temp = Integer.toBinaryString(i) + hc.substring(hc.length() - 8);
			h = Integer.parseInt(temp, 2);
			bucket = table[h];
			if (table[h] != null && bucket.get(k, v) != null) {
				counter += bucket.get(k, v).getCounter();
				if (writen == false) {
					System.out.println("Search: " + bucket.get(k, v).getValue());
					System.out.println("Key: " + k);
					System.out.println("Index: " + originkey);
					System.out.println("Global depth: " + globalDepth);
					System.out.println("Local depth: " + bucket.get(k, v).getLocaldepth());
					writen = true;
				}
				flag = true;
			}
		}
		if (writen == true)
			System.out.println("Count: " + counter);

		if (flag == false)
			System.err.println("Not Found!");
		return value;
	}

	@Override
	protected V bucketPut(String hb, K k, V v, int counter) {

		hb = to32bit(hb);
		int h = Integer.parseInt(hb.substring(hb.length() - globalDepth), 2);

		UnsortedTableMap<K, V> bucket = table[h];
		if (bucket == null)
			bucket = table[h] = new UnsortedTableMap<>();

		int oldSize = bucket.size();

		if (bucket.size() >= 10) {
			K Removablekey = bucket.findRemovablekey();
			int ctemp = 0;

			while (bucket.size() >= 10 && Removablekey != null && ctemp < 10) {

				V Removablevalue = bucket.findRemovablevalue();
				int localtemp = bucket.get(Removablekey, Removablevalue).getLocaldepth();
				int count = 0;

				String hashcode = codeHash(Removablekey), temp = "";
				hashcode = to32bit(hashcode);

				for (int i = 0; i < (int) Math.pow(2, globalDepth - localtemp); i++) {
					temp = Integer.toBinaryString(i) + hashcode.substring(hashcode.length() - localtemp);
					int hr = Integer.parseInt(temp, 2);
					count += bucketRemove(hr, Removablekey, Removablevalue);
				}

				oldLocaldepth = 0;
				bucketPut(hashcode, Removablekey, Removablevalue, count);

				Removablekey = bucket.findRemovablekey();
				ctemp++;
			}

			if (bucket.size() >= 10) {
				globalDepth++;
				resize(((Double) Math.pow(2, globalDepth)).intValue());
			}
		}

		V answer;
		if (oldLocaldepth != 0) {
			int save = globalDepth;
			globalDepth = oldLocaldepth;
			answer = bucket.put(k, v, counter);
			globalDepth = save;
		} else
			answer = bucket.put(k, v, counter);

		n += (bucket.size() - oldSize); // size may have increased
		return answer;
	}

	@Override
	protected int bucketRemove(int h, K k, V v) {
		UnsortedTableMap<K, V> bucket = table[h];
		if (bucket == null)
			return 0;
		int oldSize = bucket.size();
		int answer = bucket.remove(k, v);
		n -= (oldSize - bucket.size()); // size may have decreased
		return answer;
	}

	@Override
	public Iterable<Entry<K, V>> entrySet() {
		ArrayList<Entry<K, V>> buffer = new ArrayList<>();
		for (int h = 0; h < capacity; h++)
			if (table[h] != null)
				for (Entry<K, V> entry : table[h].entrySet())
					buffer.add(entry);
		return buffer;
	}
}
