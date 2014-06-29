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

import static org.junit.Assert.*;
import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for inner class MapView of LinkedDequeHashMap.
 * 
 * Each test method tests multiple methods together as follows:
 * 
 * size, equals, hashCode, isEmpty
 * clear, put, putAll, remove
 * containsKey, containsValue, getTest
 * entrySet
 * keySet
 * values
 * 
 * @author Stathis Aliprantis - alieus@hotmail.gr
 */
public class LinkedDequeHashMap_MapViewTest {

    private LinkedDequeHashMap<String, String> dequeMap;
    private Map<String, List<String>> dequeMapView;
    private Map<String, List<String>> expected;
    private final String keyA = "a";
    private final String valA1 = "a1";
    private final String valA2 = "a2";
    private final String keyB = "b";
    private final String valB1 = "b1";
    
    @Before
    public void setUp() throws Exception {
        dequeMap = new LinkedDequeHashMap<String, String>();
        expected = new HashMap<String, List<String>>();
        dequeMapView = dequeMap.asMap();
        expected.put(keyA, asList(valA1, valA2));
        expected.put(keyB, asList(valB1));
        dequeMap.addAll(keyA, asList(valA1, valA2));
        dequeMap.add(keyB, valB1);
    }
    
    @Test
    public void size_equals_hashCode_isEmptyTest() {
        
        assertEquals(expected, dequeMapView);
        assertEquals(expected.size(), dequeMapView.size());
        assertEquals(expected.hashCode(), dequeMapView.hashCode());
        
        // remove a value
        expected.put(keyA, asList(valA1));
        dequeMap.popLast(keyA);
        
        assertEquals(expected, dequeMapView);
        assertEquals(expected.size(), dequeMapView.size());
        assertEquals(expected.hashCode(), dequeMapView.hashCode());
        
        dequeMap.clear();
        assertTrue(dequeMapView.isEmpty());
    }

    @Test
    public void clear_put_putAll_removeTest() {
        int oldSize = dequeMapView.size();
        
        try {
            dequeMapView.clear();
            fail("Map view of DequeMap should be immutable");
        } catch (UnsupportedOperationException e) {
            assertEquals(dequeMapView.size(), oldSize);
        }
        try {
            dequeMapView.put(keyB, asList(valB1));
            fail("Map view of DequeMap should be immutable");
        } catch (UnsupportedOperationException e) {
            assertEquals(dequeMapView.size(), oldSize);
        }
        try {
            Map<String, List<String>> other = new HashMap<String, List<String>>();
            other.put(keyB, asList(valB1));
            dequeMapView.putAll(other);
            fail("Map view of DequeMap should be immutable");
        } catch (UnsupportedOperationException e) {
            assertEquals(dequeMapView.size(), oldSize);
        }
        try {
            dequeMapView.remove(keyA);
            fail("Map view of DequeMap should be immutable");
        } catch (UnsupportedOperationException e) {
            assertEquals(dequeMapView.size(), oldSize);
        }
    }
    
    @Test
    public void containsKey_containsValue_getTest() {
        assertTrue(dequeMapView.containsKey(keyA));
        assertTrue(dequeMapView.containsKey(keyB));
        assertFalse(dequeMapView.containsKey("lalala"));
        
        assertTrue(dequeMapView.containsValue(asList(valA1, valA2)));
        assertTrue(dequeMapView.containsValue(asList(valB1)));
        assertFalse(dequeMapView.containsValue(asList("lala")));
        
        assertEquals(asList(valB1), dequeMapView.get(keyB));
    }
    
    @Test
    public void entrySetTest() {
        Set<Entry<String, List<String>>> entrySet = dequeMapView.entrySet();
        assertEquals(dequeMapView.size(), entrySet.size());
        for (Map.Entry<String, List<String>> en : dequeMapView.entrySet()) {
            assertEquals(dequeMapView.get(en.getKey()), en.getValue());
        }
    }
    
    @Test
    public void keySetTest() {
        Set<String> keySet = dequeMapView.keySet();
        assertEquals(dequeMapView.size(), keySet.size());
        for (String key : keySet) {
            assertTrue(dequeMap.containsKey(key));
        }
    }
    
    @Test
    public void valuesTest() {
        Collection<List<String>> values = dequeMapView.values();
        assertEquals(dequeMapView.size(), values.size());
        for (Map.Entry<String, List<String>> en : dequeMapView.entrySet()) {
            assertTrue(values.contains(en.getValue()));
        }
        Iterator<List<String>> it = values.iterator();
        while (it.hasNext() ) {
            List<String> val = it.next();
            int oldSize = val.size();
            try {
                val.add("lala");
                fail("Key deques should be unmodifiable from map view");
            } catch (UnsupportedOperationException e) {
                assertEquals(oldSize, val.size());
            }
            try {
                val.clear();
                fail("Key deques should be unmodifiable from map view");
            } catch (UnsupportedOperationException e) {
                assertEquals(oldSize, val.size());
            }
            try {
                it.remove();
                fail("Key deques should be unmodifiable from map view");
            } catch (UnsupportedOperationException e) {
            }
        }
    }
}
