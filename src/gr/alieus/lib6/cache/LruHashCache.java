package gr.alieus.lib6.cache;

import java.math.BigInteger;

/**
 * Cache implementation using a hashtable with chaining for resolving collisions.
 * The size of this cache is specified at creation and cannot change.
 * This implementation uses the "Least Recently Used" replacement policy.
 * If an attempt to store a new key occurs while the cache is full then the
 * last entry accessed (stored or retrieved) is evicted to free space.
 * Storing a value with a key that already exists causes the old value to be replaced.
 * 
 * @author Stathis Aliprantis - p3120005
 * @param <K> the type of the keys
 * @param <V> the type of the values
 * @see Cache
 * @see java.util.HashMap
 */
public class LruHashCache<K, V> {
    
    /**
     * Key value pair.
     * 
     * @param <K> the type of the key
     * @param <V> the type of the value
     */
    static final class Entry<K, V> {
        final K key;
        V value;

        /**
         * The node of the queue used for the lru strategy.
         * When this entry is accessed, this node is moved to the end of lru queue.
         */
        Queue.Node<LruEntry> queueEntry;
        
        /**
         * The hash value of the key
         * so there is no need to recalculate.
         */
        int hash;

        /**
         * The next entry of the chain
         * if multiple entries are sent to the same bucket.
         */
        Entry<K, V> next;

        Entry(K key) {
            this.key = key;
        }

        Entry(K key, V value, int hash, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }

        @Override
        public String toString() {
            return "["+key+" = "+value+"]";
        }
    }

    
    /**
     * Entry of the lru queue.
     * Holds the information needed to track an entry immediately when it is
     * time to evict it.
     */
    static final class LruEntry {

        /** The index of the actual entry on the hashtable. */
        final int bucket;

        /** The actual entry. */
        final Entry entry;

        LruEntry(int bucket, Entry entry) {
            this.bucket = bucket;
            this.entry = entry;
        }
    }
    
    static final float DEFAULT_LOAD_FACTOR = 0.78F;
    
    /** Hashtable for holding the data. */
    Entry<K, V>[] table;

    /**
     * The head of this tail is always the least recently accessed entry.
     * Every time an entry is accessed (stored or retrieved), it is moved to the
     * back of this queue.
     * When en entry needs to be evicted in order to free space, the head of this
     * queue is extracted and the entry pointed by it is removed from the the cache.
     */
    Queue<LruEntry> lru;
    
    private long hitCount;
    private long lookupCount;

    /** The ratio: (number of entries) / (hashtable length). */
    private float loadFactor = DEFAULT_LOAD_FACTOR;

    /** The maximum number of entries this cache can store. */
    private int size;

    /**
     * Creates a new CacheImpl with the given size and load factor.
     * 
     * @param size the size of the cache
     * @param loadFactor the load factor. 0.75 should produce near-constant time
     * operations.
     */
    @SuppressWarnings("unchecked")
    public LruHashCache(int size, float loadFactor) {
        this.size = size;
        this.loadFactor = loadFactor;
        int requestedTableSize = (int) (size / loadFactor);
        // the actual size of the table should be a prime number
        BigInteger actualTableSize =  BigInteger.valueOf(requestedTableSize);
        actualTableSize = actualTableSize.nextProbablePrime();
        while (! actualTableSize.isProbablePrime(1000)) {
            actualTableSize = actualTableSize.nextProbablePrime();
        }
        table = new Entry[actualTableSize.intValue()];
        lru = new Queue<LruEntry>();
    }
    
    /**
     * Creates a new CacheImpl with the given size and the default load factor (0.78).
     * @param size the size of the cache
     */
    public LruHashCache(int size) {
        this(size, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Returns the size.
     * 
     * @return the maximum number of entries
     */
    public int getSize() {
        return size;
    }
    
    public V lookUp(K key) {
        lookupCount++;
        
        int hash = key.hashCode();
        Entry<K, V> entry = table[indexOf(key, hash)];
        
        while (entry != null) {
            if (isKey(key, hash, entry)) {
                lru.moveBack(entry.queueEntry); // this entry was just accessed
                hitCount++;                     // so it should be moved to end of the queue
                return entry.value;
            }
            entry = entry.next;
        }
        
        return null;
    }

    public void store(K key, V value) {
        int hash = key.hashCode();
        int index = indexOf(key, hash);
        
        if (table[index] == null) {
            // bucket is empty -> the key does not exist -> create a new Entry
            Entry<K, V> newEntry = new Entry<K, V>(key, value, hash, null);
            table[index] = newEntry;
            Queue.Node<LruEntry> lruElem = lru.insert(new LruEntry(index, newEntry));
            newEntry.queueEntry = lruElem;
        } else {
            Entry<K, V> entry = table[index];
            // first, search for the key in the bucket. If found change the value
            boolean found = false;
            while (entry != null && !found) {
                if (isKey(key, hash, entry)) {
                    entry.value = value;
                    lru.moveBack(entry.queueEntry); // we just accessed the entry
                    found = true;
                }
                entry = entry.next;
            }
            
            // key not found -> create a new entry
            if (!found) {
                Entry<K, V> newEntry = new Entry<K, V>(key, value, hash, table[index]);
                table[index] = newEntry;
                Queue.Node<LruEntry> lruElem = lru.insert(new LruEntry(index, newEntry));
                newEntry.queueEntry = lruElem;
                
            }
        }
        
        // evict a value to free space if we exceed the size
        if (lru.getSize() > size) {
            LruEntry toClear = lru.extract(); // extract the least recently used
            clear(toClear.bucket, toClear.entry);
        }
    }

    public double getHitRatio() {
        return hitCount / (double) lookupCount;
    }

    public long getHits() {
        return hitCount;
    }

    public long getMisses() {
        return lookupCount - hitCount;
    }

    public long getNumberOfLookUps() {
        return lookupCount;
    }
    
    /**
     * Returns the index of the table where the given key with the given
     * hash value should be placed
     * 
     * @param key the key
     * @param hash the hash value of the key
     * @return the proposed index
     */
    private int indexOf(K key, int hash) {
        return Math.abs(hash) % table.length;
    }
    
    /**
     * Compares the given key with the key of the given entry.
     * 
     * @param key the key to compare with the key of the given entry
     * @param hash the hash value of the given key
     * @param entry the entry
     * @return {@literal true} if the given key and the key of the given entry are equal.
     */
    private static boolean isKey(Object key, int hash, Entry entry) {
        return hash == entry.hash && (
                entry.key == key || entry.key.equals(key));
    }
    
    /**
     * Removes the given entry from the specified bucket.
     * 
     * @param bucket the index of the bucket witch contains the entry to be cleared.
     * @param toClear the entry to remove
     */
    private void clear(int bucket, Entry toClear) {
        Entry<K, V> currEntry = table[bucket];
        if (toClear == currEntry) {
            table[bucket] = currEntry.next;
            currEntry.next = null;
            currEntry.queueEntry = null;
        } else {
            while (currEntry.next != null) {
                if (toClear == currEntry.next) {
                    Entry<K, V> nextOfCleared = currEntry.next.next;
                    currEntry.next.next = null;
                    //toClear.next.queueEntry = null;
                    currEntry.next = nextOfCleared;
                    break;
                }
                currEntry = currEntry.next;
            }
        }
    }
    
}
