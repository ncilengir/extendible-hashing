public interface Map<K,V> {


    int size();


    boolean isEmpty();


    AbstractMap.MapEntry<K, V> get(K k,V v);


    V put(K key, V value,int counter);


    int remove(K key,V value);


    Iterable<K> keySet();


    Iterable<V> values();


    Iterable<Entry<K,V>> entrySet();
}