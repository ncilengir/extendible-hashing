public interface Entry<K,V> {

    K getKey();


    V getValue();
    
    public int getCounter();
    public int getLocaldepth();
    public void setLocaldepth(int newone);
    public void setCounter(int count);
    public void setKey(K key);
    
}