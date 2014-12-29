/*
 * Copyright 2014 Stathis Aliprantis
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with This program. If not, see http://www.gnu.org/licenses/.
 */

package gr.alieus.lib6.collect;

import java.io.Serializable;

import java.util.*;


import static gr.alieus.lib6.collect.CollectUtil.reverseCopy;

/**
 * Implementation of {@code DequeMap} that uses a hash based map whose 
 * values are linked deques.
 * <p/> 
 * NOTE: this implementation is not thread safe.
 * 
 * @see DequeMap
 * @author Stathis Aliprantis - alieus@hotmail.gr
 */
public class LinkedDequeHashMap<K, V>
    implements DequeMap<K, V>, Serializable {
    
    /* 
     * The main principle of this implementation is that is will never contain
     * mappings to empty deques. A key with no values is considered not to exist
     * in the deque-map so, when a deque gets empty, it should be removed immediately.
     * Additionally, an effort is made to ensure that there is no way to modify
     * the deque of a key without triggering the mechanism that removes deques
     * when they get empty.   
     */
    
    /*
     * NOTE: some methods create deques (buckets) via ensureBucketAt and they remove
     * them immediately. A different approach would be not to create the bucket at all
     * if it is to be removed immediately.
     */
    
    private static final long serialVersionUID = 1L;

    private final Map<K, LinkedList<V>> data = new HashMap<K, LinkedList<V>>();
    private final Map<K, List<V>> mapView = new MapView();
    
    @Override
    public V get(K key) {
        LinkedList<V> bucket = data.get(key);
        return bucket == null || bucket.isEmpty()
                // a bucket is never supposed to be empty. but you never know.. 
                ? null
                : bucket.peek();
    }

    @Override
    public void set(K key, V value) {
        LinkedList<V> bucket = ensureBucketAt(key);
        bucket.clear();  // now it's empty...
        bucket.add(value);
    }

    @Override
    public V getLast(K key) {
        LinkedList<V> bucket = data.get(key);
        return bucket == null || bucket.isEmpty()
                ? null
                : bucket.peekLast();
    }

    @Override
    public V pop(K key) {
        LinkedList<V> bucket = ensureBucketAt(key);
        V result = bucket.poll();
        removeIfEmpty(key, bucket);
        return result;
    }

    @Override
    public V popLast(K key) {
        LinkedList<V> bucket = ensureBucketAt(key);
        V result = bucket.pollLast();
        removeIfEmpty(key, bucket);
        return result;
    }

    @Override
    public void add(K key, V value) {
        ensureBucketAt(key).addFirst(value);
        
    }

    @Override
    public void addLast(K key, V value) {
        ensureBucketAt(key).addLast(value);
        
    }

    /** 
     * {@inheritDoc}
     * <p/>
     * NOTE: This implementation currently does not support add and
     * remove operations via the iterators.
     */
    @Override
    public List<V> getAll(K key) {
        return new KeyDequeView(key);
    }

    @Override
    public void setAll(K key, Collection<? extends V> values) {
        LinkedList<V> bucket = ensureBucketAt(key);
        bucket.clear();  // now it's empty...
        bucket.addAll(values);
        removeIfEmpty(key, bucket);
    }

    @Override
    public List<V> popAll(K key) {
        return popN(key, countOf(key));
    }

    @Override
    public List<V> popAllLast(K key) {
        return popNLast(key, countOf(key));
    }

    @Override
    public List<V> popN(K key, int n) {
        LinkedList<V> bucket = ensureBucketAt(key);
        if (n >= bucket.size()) {
            // we send a copy instead of the actual bucket and then clear the bucket
            // so that if an iterator over the values of this key is open, it
            // will be invalidated
            List<V> result = new ArrayList<V>(bucket);
            bucket.clear();
            data.remove(key);
            return result;
        } else {
            List<V> bucketSubList = bucket.subList(0, n);
            List<V> result = new ArrayList<V>(bucketSubList);
            bucketSubList.clear();
            return result;
        }
    }

    @Override
    public List<V> popNLast(K key, int n) {
        LinkedList<V> bucket = ensureBucketAt(key);
        if (n >= bucket.size()) {
            // we send a copy instead of the actual bucket and then clear the bucket
            // so that if an iterator over the values of this key is open, it
            // will be invalidated
            List<V> result = reverseCopy(bucket);
            bucket.clear();
            data.remove(key);
            return result;
        } else {
            List<V> bucketSubList = bucket.subList(bucket.size()-n, bucket.size());
            List<V> result = reverseCopy(bucketSubList);
            bucketSubList.clear();
            return result;
        }
    }

    @Override
    public void addAll(K key, Collection<? extends V> values) {
        if (values.isEmpty()) return;
        ensureBucketAt(key).addAll(0, values);
    }

    @Override
    public void addAllLast(K key, Collection<? extends V> values) {
        if (values.isEmpty()) return;
        ensureBucketAt(key).addAll(values);
        
    }

    @Override
    public int countOf(K key) {
        LinkedList<V> bucket = data.get(key);
        return bucket == null
                ? 0
                : bucket.size();
    }

    @Override
    public void assertAtLeast(K key, int minCount) throws IllegalStateException {
        if (countOf(key) < minCount) {
            throw new IllegalStateException("Key: "+key+" contains less than "+minCount+" values.");
        }
        
    }

    @Override
    public boolean containsKey(K key) {
        return data.containsKey(key);
    }

    @Override
    public void assertContainsKey(K key) throws NoSuchElementException {
        if (!containsKey(key)) {
            throw new NoSuchElementException("Key: "+key+" does not exist.");
        }
        
    }

    @Override
    public int size() {
        return data.size();  // this should work... 
    }

    @Override
    public void addMap(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> en : map.entrySet()) {
            add(en.getKey(), en.getValue());
        }
    }

    @Override
    public void addMapLast(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> en : map.entrySet()) {
            add(en.getKey(), en.getValue());
        }
    }

    @Override
    public void addAllMap(Map<? extends K, ? extends Collection<? extends V>> map) {
        for (Map.Entry<? extends K, ? extends Collection<? extends V>> en : map.entrySet()) {
            addAll(en.getKey(), en.getValue());
        }
    }

    @Override
    public void addAllMapLast(Map<? extends K, ? extends Collection<? extends V>> map) {
        for (Map.Entry<? extends K, ? extends Collection<? extends V>> en : map.entrySet()) {
            addAllLast(en.getKey(), en.getValue());
        }
    }

    @Override
    public void setMap(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> en : map.entrySet()) {
            set(en.getKey(), en.getValue());
        }
    }

    @Override
    public void setAllMap(Map<? extends K, ? extends Collection<? extends V>> map) {
        for (Map.Entry<? extends K, ? extends Collection<? extends V>> en : map.entrySet()) {
            setAll(en.getKey(), en.getValue());
        }
        
    }
    
    @Override
    public void clearKey(K key) {
        data.remove(key);
    }

    @Override
    public void clear() {
        data.clear();
    }
    
    @Override
    public Map<K, List<V>> asMap() {
        return mapView;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DequeMap)) {
            return false;
        }
        
        return this.asMap().equals(((DequeMap<?, ?>) obj).asMap());
    }
    
    @Override
    public int hashCode() {
        return asMap().hashCode();
    }
    
    public String toString() {
        return data.toString();
    };
    
// private methods
    
    LinkedList<V> ensureBucketAt(K key) {
        LinkedList<V> bucket = data.get(key);
        
        if (bucket == null) {
            bucket = new LinkedList<V>();
            data.put(key, bucket);
        }
        
        return bucket;
    }
    
    boolean removeIfEmpty(K key, LinkedList<V> bucket) {
        if (bucket.isEmpty()) {
            data.remove(key);
            return true;
        }
        return false;
    }
    
    
    
// views
    
    class KeyDequeView extends AbstractList<V> implements List<V> {
        
        final K key;
        
        public KeyDequeView(K key) {
            this.key = key;
        }
        
        @Override
        public int size() {
            return countOf(key);
        }

        @Override
        public boolean isEmpty() {
            return !containsKey(key);
        }

        @Override
        public boolean contains(Object o) {
            LinkedList<V> bucket = data.get(key);
            return bucket != null && bucket.contains(o);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Iterator<V> iterator() {
            LinkedList<V> bucket = data.get(key);
            return bucket == null
                ? Collections.EMPTY_LIST.iterator()
                : Collections.unmodifiableList(bucket).iterator();
        }
        
        @Override
        public Object[] toArray() {
            LinkedList<V> bucket = data.get(key);
            return bucket == null ? new Object[0] : bucket.toArray();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            LinkedList<V> bucket = data.get(key);
            return bucket == null
                    ? (T[])java.lang.reflect.Array.newInstance(
                            a.getClass().getComponentType(), 0)
                    : bucket.toArray(a);
        }

        @Override
        public boolean add(V e) {
            LinkedDequeHashMap.this.addLast(key, e);
            return true;
        }

        @Override
        public boolean remove(Object o) {
            LinkedList<V> bucket = data.get(key);
            if (bucket == null) {
                return false;
            } else {
                boolean result = bucket.remove(o);
                removeIfEmpty(key, bucket);
                return result;
            }
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            LinkedList<V> bucket = data.get(key);
            if (bucket == null) {
                return c.isEmpty();
            } else {
                return bucket.containsAll(c);
            }
        }

        @Override
        public boolean addAll(Collection<? extends V> c) {
            LinkedDequeHashMap.this.addAll(key, c);
            return !c.isEmpty();
        }

        @Override
        public boolean addAll(int index, Collection<? extends V> c) {
            if (c.isEmpty()) {
                return false;
            }
            return ensureBucketAt(key).addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            LinkedList<V> bucket = data.get(key);
            if (bucket == null) {
                return false;
            } else {
                boolean result = bucket.removeAll(c);
                removeIfEmpty(key, bucket);
                return result;
            }
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            LinkedList<V> bucket = data.get(key);
            if (bucket == null) {
                return false;
            } else {
                boolean result = bucket.retainAll(c);
                removeIfEmpty(key, bucket);
                return result;
            }
        }

        @Override
        public void clear() {
            LinkedDequeHashMap.this.clearKey(key);
        }

        @Override
        public V get(int index) {
            return getBucketChecked().get(index);
        }

        @Override
        public V set(int index, V element) {
            return getBucketChecked().set(index, element);
        }

        @Override
        public void add(int index, V element) {
            if (index == 0) {
                LinkedDequeHashMap.this.add(key, element);
            } else {
                getBucketChecked().add(index, element);
            }
            
        }

        @Override
        public V remove(int index) {
            LinkedList<V> bucket = getBucketChecked();
            V result = bucket.remove(index);
            removeIfEmpty(key, bucket);
            return result;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public ListIterator<V> listIterator(final int index) {
            final LinkedList<V> bucket = data.get(key);
            
            return new ListIterator<V>() {
                
                {
                    if (bucket != null) {
                        origIt = bucket.listIterator(index);
//                        weHaveBucket = true;
                        currentBucket = bucket;
                    } else {
                        origIt = Collections.EMPTY_LIST.listIterator();
                    }
                }
                
                ListIterator<V> origIt;
                LinkedList<V> currentBucket;
//                boolean weHaveBucket;
                
                @Override public boolean hasNext() {
                    return origIt.hasNext();
                }

                @Override public V next() {
                    return origIt.next();
                }

                @Override public boolean hasPrevious() {
                    return origIt.hasPrevious();
                }

                @Override public V previous() {
                    return origIt.previous();
                }

                @Override public int nextIndex() {
                    return origIt.nextIndex();
                }

                @Override public int previousIndex() {
                    return origIt.previousIndex();
                }

                @Override public void remove() {
                    ensureWeAreStilAtBucket();
                    origIt.remove();
                    currentBucket = data.get(key);
                    if (currentBucket != null) {
                        removeIfEmpty(key, currentBucket);
                    }
                }

                @Override public void set(V e) {
                    origIt.set(e);
                    
                }

                @Override
                public void add(V e) {
                    if (currentBucket != null) {
                        ensureWeAreStilAtBucket();
                        origIt.add(e);
                    } else {
                        currentBucket = ensureBucketAt(key);
                        origIt = currentBucket.listIterator();
                        origIt.add(e);
                    }
                }
                
                // throws an exception
                void ensureWeAreStilAtBucket() throws IllegalStateException {
                    if (currentBucket != data.get(key)) {
                        throw new IllegalStateException(
                            "The iterated list is not the deque of key: "+key
                            +" anymore");
                    }
                }
            };
        };

        LinkedList<V> getBucketChecked() throws IndexOutOfBoundsException {
            LinkedList<V> bucket = data.get(key);
            if (bucket == null) {
                throw new IndexOutOfBoundsException("Key: "+key
                        +" does not curentlly exist in deque-map");
            }
            return bucket;
        }
        

    }
    
    
// Unmodifiable map view of this deque-map
    class MapView extends AbstractMap<K, List<V>> {

        @Override
        public boolean containsKey(Object key) {
            return data.containsKey(key);
        }
        
        @Override
        public List<V> get(Object key) {
            LinkedList<V> result = data.get(key);
            return result == null ? null : Collections.unmodifiableList(result);
        }
        
        @Override
        public int size() {
            return LinkedDequeHashMap.this.size();
        }
        
        @Override
        public boolean containsValue(Object value) {
            return data.containsValue(value);
        }
        
        @Override
        public boolean equals(Object o) {
            return data.equals(o);
        }
        
        @Override
        public int hashCode() {
            return data.hashCode();
        }
        
        @Override
        public Set<K> keySet() {
            return Collections.unmodifiableSet(data.keySet());
        }
        
        @Override
        public Collection<List<V>> values() {
            final Collection<LinkedList<V>> origValues = data.values();
            
            return new AbstractCollection<List<V>>() {

                @Override
                public Iterator<List<V>> iterator() {
                    final Iterator<LinkedList<V>> origIt = origValues.iterator();
                    return new Iterator<List<V>>() {

                        @Override public boolean hasNext() {
                            return origIt.hasNext();
                        }

                        @Override public List<V> next() {
                            return Collections.unmodifiableList(origIt.next());
                        }

                        @Override public void remove() {
                            throw new UnsupportedOperationException("Removal not allowed.");
                            
                        }
                    };
                }

                @Override public int size() {
                    return origValues.size();
                }
            };
        }
        
        @Override
        public Set<java.util.Map.Entry<K, List<V>>> entrySet() {
            return new AbstractSet<Map.Entry<K,List<V>>>() {
                
                @Override public Iterator<java.util.Map.Entry<K, List<V>>> iterator() {
                    final Iterator<java.util.Map.Entry<K, LinkedList<V>>> origIt
                      = data.entrySet().iterator();
                    
                    return new Iterator<Map.Entry<K,List<V>>>() {

                        @Override public boolean hasNext() {
                            return origIt.hasNext();
                        }

                        @Override public java.util.Map.Entry<K, List<V>> next() {
                            final java.util.Map.Entry<K, LinkedList<V>> origEntry
                                 = origIt.next();
                            return new Entry<K, List<V>>() {

                                @Override public K getKey() {
                                    return origEntry.getKey();
                                }

                                @Override public List<V> getValue() {
                                    return Collections.unmodifiableList(origEntry.getValue());
                                }

                                @Override public List<V> setValue(List<V> value) {
                                    throw new UnsupportedOperationException("Muttation not allowed.");
                                }

                            };
                        }
                        
                        @Override public void remove() {
                            throw new UnsupportedOperationException("Removal not allowed.");
                        }
                    };
                }

                @Override public int size() {
                    return LinkedDequeHashMap.this.size();
                }
            };
        }
        
    }
    
}
