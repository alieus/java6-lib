/*
 * Copyright 2014 Stathis Aliprantis
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License version 3 as published
 * by the Free Software Foundation.
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

import java.util.*;

/**
 * Data structure that maps a key to multiple values.
 * <p/>
 * The values of each key work as a deque (double ended queue).
 * Meaning, they can be operated upon as a stack or as a queue.
 * Additionally, the API of the {@code DequeMap} is designed in a way that it
 * can be treated as a regular, single-valued, map.
 * <p/>
 * A key is considered to exist in the map if at least one value is associated with it.
 * 
 * @author Stathis Aliprantis - alieus@hotmail.gr
 */
public interface DequeMap<K, V> {
    
    /**
     * Returns the first value associated with the given key.
     * Returns {@literal null} if key does not exist.
     * 
     * @param key the key to obtain it's first value
     * @return the first value associated with key or {@literal null}
     */
    V get(K key);
    
    /**
     * Sets the value of the given key.
     * Any pre-existing values will be removed.
     * After this operation the key will be associated with a single value
     * 
     * @param key the key to set it's value
     * @param value the value to set for key
     */
    void set(K key, V value);
    
    /**
     * Returns the last value associated with the given key.
     * Returns {@literal null} if key does not exist.
     * 
     * @param key the key to obtain it's last value
     * @return the last value associated with key or {@literal null}
     */
    V getLast(K key);
    
    /**
     * Returns and removes the first value associated with the given key.
     * If key does not exist, this method returns {@literal null}
     * 
     * @param key the key to pop it's first value
     * @return the first value associated with key or {@literal null}
     * @see #assertContainsKey
     */
    V pop(K key);
    
    /**
     * Returns and removes the last value associated with the given key.
     * If key does not exist, this method returns {@literal null}
     * 
     * @param key the key to pop it's last value
     * @return the last value associated with key or {@literal null}
     */
    V popLast(K key);
    
    /**
     * Adds the given value as the <strong>first</strong> value of {@code key}.
     * 
     * @param key the key to which the value will be added
     * @param value the value to add
     */
    void add(K key, V value);
    
    /**
     * Adds the given value as the last value of {@code key}.
     * 
     * @param key the key to which the value will be added
     * @param value the value to add
     */
    void addLast(K key, V value);
    
    /**
     * Returns a live view of the values of the given key.
     * Modifications on the returned list will modify the actual dequemap
     * and vice versa.
     * If the given key does not exist, an empty list is returned, which,
     * in turn, can be used to add elements to the key.
     * 
     * @param key the key to get it's values
     * @return a live view of the values of {@code key}
     */
    List<V> getAll(K key);
    
    /**
     * Sets the values of the given key.
     * 
     * @param key the key to set it's values
     * @param values the new values of {@code key}
     */
    void setAll(K key, Collection<? extends V> values);
    
    /**
     * Returns and removes the values of the given key.
     * If key does not exist, an empty list is returned.
     * 
     * @param key the key to pop it's values
     * @return the values of {@code key}
     */
    List<V> popAll(K key);
    
    /**
     * Returns and removes the values of the given key in reverse order.
     * If key does not exist, an empty list is returned.
     * 
     * @param key the key to pop it's values in reverse order
     * @return the values of {@code key}
     */
    List<V> popAllLast(K key);
    
    /**
     * Returns and removes the N first values of the given key.
     * If the key contains less than N values, all of them are returned
     * and removed.
     * 
     * @param key the key to pop it's N first values
     * @param n number of values to pop
     * @return at most N first values of {@code key}
     * @see #assertAtLeast
     */
    List<V> popN(K key, int n);
    
    /**
     * Returns and removes the N last values of the given key in reverse order.
     * If the key contains less than N values, all of them are returned
     * and removed in reverse order.
     * 
     * @param key the key to pop it's N last values
     * @param n number of values to pop
     * @return at most N last values of {@code key} in reverse order
     * @see #assertAtLeast
     */
    List<V> popNLast(K key, int n);
    
    /**
     * Adds all the values of the given collection
     * as the <strong>first</strong> values of the given key
     * in the order they are returned by the collections iterator.
     * <p/>
     * The effect of this call is <strong>not</strong> equivalent to that of
     * calling {@link #add} for each element of the given collection since
     * {@code add} would insert each value to the front causing the collection
     * to be added in reverse order.  
     * 
     * @param key the key to which the values will be added
     * @param values the values to add
     */
    void addAll(K key, Collection<? extends V> values);
    
    /**
     * Adds all the values of the given collection
     * as the last values of the given key
     * in the order they are returned by the collections iterator.
     * 
     * @param key the key to which the values will be added
     * @param values the values to add
     */
    void addAllLast(K key, Collection<? extends V> values);
    
    /**
     * Returns the number of values associated with the given key.
     * 
     * @param key
     * @return the number of values associated with {@code key}
     * @see #assertAtLeast
     */
    int countOf(K key);
    
    /**
     * Throws an {@code IllegalStateException} if the given key does not
     * contain at least {@code minCount} values.
     * 
     * @param key the key to ensure it's minimum number of values
     * @param minCount the minimum number of values required
     * @throws IllegalStateException if the given key does not
     * contain at least {@code minCount} values
     */
    void assertAtLeast(K key, int minCount) throws IllegalStateException;
    
    /**
     * Returns {@literal true} if the given key exists in this map.
     * A key exists if it has at least one value associated with it.
     * 
     * @param key the key to check for existence 
     * @return {@literal true} if the given key exists in this map
     * @see #assertContainsKey
     */
    boolean containsKey(K key);
    
    /**
     * Throws a {@code NoSuchElementException} if the given key
     * does not exist.
     * 
     * @param key the key to ensure it's existence
     * @throws NoSuchElementException if the given key does not exist 
     */
    void assertContainsKey(K key) throws NoSuchElementException; 
    
    /**
     * Returns the number of keys of this map.
     * 
     * @return the number of keys of this map
     */
    int size();
    
    /**
     * Adds the values of the given map to the front of the respective
     * keys of this deque-map. The effect of this call is equivalent to 
     * that of calling {@link #add add} once for each entry of the given map.
     * 
     * @param map
     * @see #add
     */
    void addMap(Map<? extends K, ? extends V> map);
    
    /**
     * Adds the values of the given map to the back of the respective
     * keys of this deque-map. The effect of this call is equivalent to 
     * that of calling {@link #addLast addLast} once for each entry
     * of the given map.
     * 
     * @param map
     * @see #addLast
     */
    void addMapLast(Map<? extends K, ? extends V> map);
    
    /**
     * Adds the values of the collections of the given map to the
     * <strong>front</strong> of the respective keys of this deque-map.
     * The effect of this call is equivalent to 
     * that of calling {@link #addAll addAll} once for each entry
     * of the given map.
     * 
     * @param map
     * @see #addAll
     */
    void addAllMap(Map<? extends K, ? extends Collection<? extends V>> map);
    
    /**
     * Adds the values of the collections of the given map to the
     * back of the respective keys of this deque-map.
     * The effect of this call is equivalent to 
     * that of calling {@link #addAllLast addAllLast} once for each entry
     * of the given map.
     * 
     * @param map
     * @see #addAllLast
     */
    void addAllMapLast(Map<? extends K, ? extends Collection<? extends V>> map);
    
    /**
     * Sets the mapping of the keys of this deque-map to be the single value
     * mapped to the respective key of the given map.
     * The effect of this call is equivalent to 
     * that of calling {@link #set set} once for each entry
     * of the given map.
     * 
     * @param map
     * @see #set
     */
    void setMap(Map<? extends K, ? extends V> map);
    
    /**
     * Sets the mapping of the keys of this deque-map to be the collection of
     * values mapped to the respective key of the given map.
     * The effect of this call is equivalent to 
     * that of calling {@link #setAll setAll} once for each entry
     * of the given map.
     * 
     * @param map
     * @see #setAll
     */
    void setAllMap(Map<? extends K, ? extends Collection<? extends V>> map);
    
    /**
     * Removes all values associated with the given key.
     * 
     * @param key the key to remove it's values
     */
    void clearKey(K key);
    
    /**
     * Clears all mappings from this deque-map.
     */
    void clear();
    
    /**
     * Returns an unmodifiable {@code Map} view of this deque-map.
     * Changes to this deque-map are reflected to the returned map.
     * 
     * @return  an unmodifiable {@code Map} view of this deque-map
     */
    Map<K, List<V>> asMap();
    
    /**
     * Determines whether two {@code DequeMap}s contain the same keys with
     * the same values in the same order.
     * 
     * @param obj a {@code DequeMap} to check for equality
     * @return {@literal true} if this deque-map and the given one are equal
     */
    @Override
    boolean equals(Object obj);

    /**
     * Returns a hash value for this deque-map.
     * 
     * @return a hash value for this deque-map
     */
    @Override
    int hashCode();
}
