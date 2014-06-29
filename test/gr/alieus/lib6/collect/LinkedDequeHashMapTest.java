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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * Test for class LinkedDequeHashMapTest.
 * Methods asMap and getAll are tested in LinkedDequeHashMap_MapViewTest
 * and LinkedDequeHashMap_KeyDequeViewTest respectively.
 * 
 * Each test method tests multiple methods together as follows:
 * 
 * set, get
 * containsKey
 * size, clearKey, clear
 * add, pop, addLast, popLast, getLast_countOf
 * addAll, addAllLast, popAll, popAllLast, setAll
 * popN, popNLast
 * equals, hash
 * addMap, addMapLast, addAllMap, addAllMapLast, setMap, setAllMap
 * 
 * @author Stathis Aliprantis - alieus@hotmail.gr
 */
public class LinkedDequeHashMapTest {

    @Test
    public void set_getTest() {
        LinkedDequeHashMap<String, String> dequeMap = new LinkedDequeHashMap<String, String>();
        String keyA = "a";
        String valA1 = "a1";
        String valA2 = "a2";
        String keyB = "b";
        String valB1 = "b1";
        
        dequeMap.set(keyA, valA1);
        assertEquals(valA1, dequeMap.get(keyA));
        
        dequeMap.set(keyB, valB1);
        assertEquals(valB1, dequeMap.get(keyB));

        dequeMap.set(keyA, valA2);
        assertEquals(valA2, dequeMap.get(keyA));
    }
    
    @Test
    public void containsKeyTest() {
        LinkedDequeHashMap<String, String> dequeMap = new LinkedDequeHashMap<String, String>();
        String keyA = "a";
        String valA1 = "a1";
        String keyB = "b";
        
        assertFalse(dequeMap.containsKey(keyA));
        dequeMap.set(keyA, valA1);
        assertTrue(dequeMap.containsKey(keyA));
        assertFalse(dequeMap.containsKey(keyB));
    }
    
    @Test
    public void size_clearKey_clearTest() {
        LinkedDequeHashMap<String, String> dequeMap = new LinkedDequeHashMap<String, String>();
        String keyA = "a";
        String keyB = "b";
        
        int expectedSize = 0;
        assertEquals(expectedSize, dequeMap.size());
        
        dequeMap.set(keyA, "1");
        expectedSize++;
        assertEquals(expectedSize, dequeMap.size());
        
        dequeMap.set(keyB, "2");
        expectedSize++;
        assertEquals(expectedSize, dequeMap.size());
        
        dequeMap.set(keyA, "3"); // same key
        assertEquals(expectedSize, dequeMap.size());
        
        dequeMap.clearKey(keyA);
        expectedSize--;
        assertEquals(expectedSize, dequeMap.size());
        assertFalse(dequeMap.containsKey(keyA));
        
        dequeMap.set(keyA, "4");
        expectedSize++;
        dequeMap.clear();
        expectedSize = 0;
        assertEquals(expectedSize, dequeMap.size());
    }
    
    @Test
    public void add_pop_addLast_popLast_getLast_countOfTest() {
        LinkedDequeHashMap<String, String> dequeMap = new LinkedDequeHashMap<String, String>();
        String keyA = "a";
        String valA1 = "a1";
        String valA2 = "a2";
        String valA3 = "a3";
//        String keyB = "b";
        
        int expectedCountOfA = 0;
        
        dequeMap.add(keyA, valA1);
        expectedCountOfA++;
        assertTrue(dequeMap.containsKey(keyA));
        assertEquals(expectedCountOfA, dequeMap.countOf(keyA));
        
        dequeMap.add(keyA, valA2);
        expectedCountOfA++;
        assertEquals(valA2, dequeMap.get(keyA));
        assertEquals(valA2, dequeMap.pop(keyA));
        expectedCountOfA--;
        assertEquals(expectedCountOfA, dequeMap.countOf(keyA));
        assertEquals(valA1, dequeMap.pop(keyA));
        expectedCountOfA--;
        assertFalse(dequeMap.containsKey(keyA));
        
        dequeMap.addLast(keyA, valA2);
        expectedCountOfA++;
        dequeMap.add(keyA, valA1);
        expectedCountOfA++;
        dequeMap.addLast(keyA, valA3);
        expectedCountOfA++;
        assertEquals(valA3, dequeMap.getLast(keyA));
        assertEquals(valA3, dequeMap.popLast(keyA));
        expectedCountOfA--;
        assertEquals(expectedCountOfA, dequeMap.countOf(keyA));
        assertEquals(valA2, dequeMap.popLast(keyA));
        expectedCountOfA--;
        assertEquals(valA1, dequeMap.popLast(keyA));
        expectedCountOfA--;
        assertFalse(dequeMap.containsKey(keyA));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void addAll_addAllLast_popAll_popAllLast_setAllTest() {
        LinkedDequeHashMap<String, String> dequeMap = new LinkedDequeHashMap<String, String>();
        String keyA = "a";
        String valA1 = "a1";
        String valA2 = "a2";
        String valA3 = "a3";
        String valA4 = "a4";
        String valA5 = "a5";
        
        dequeMap.addAll(keyA, asList(valA1, valA2, valA3));
        dequeMap.addAllLast(keyA, asList(valA4, valA5));
        
        assertEquals(5, dequeMap.countOf(keyA));
        assertEquals(valA1, dequeMap.pop(keyA));
        assertEquals(valA2, dequeMap.pop(keyA));
        assertEquals(valA3, dequeMap.pop(keyA));
        assertEquals(valA4, dequeMap.pop(keyA));
        assertEquals(valA5, dequeMap.pop(keyA));
        assertFalse(dequeMap.containsKey(keyA));
        
        List<String> valsA = asList(valA1, valA2, valA3);
        dequeMap.addAll(keyA, valsA);
        assertEquals(valsA, dequeMap.popAll(keyA));
        assertFalse(dequeMap.containsKey(keyA));
        
        dequeMap.addAllLast(keyA, valsA);
        assertEquals(valA3, dequeMap.popLast(keyA));
        assertEquals(valA2, dequeMap.popLast(keyA));
        assertEquals(valA1, dequeMap.popLast(keyA));
        assertFalse(dequeMap.containsKey(keyA));
        
        dequeMap.addAll(keyA, valsA);
        assertEquals(CollectUtil.reverseCopy(valsA), dequeMap.popAllLast(keyA));
        
        dequeMap.setAll(keyA, asList(valA5, valA3));
        assertEquals(asList(valA5, valA3), dequeMap.popAll(keyA));
        dequeMap.add(keyA, valA4);
        dequeMap.setAll(keyA, Collections.EMPTY_LIST);
        assertFalse(dequeMap.containsKey(keyA));
    }
    
    @Test
    public void popN_popNLastTest() {
        LinkedDequeHashMap<String, String> dequeMap = new LinkedDequeHashMap<String, String>();
        String keyA = "a";
        String valA1 = "a1";
        String valA2 = "a2";
        String valA3 = "a3";
        String valA4 = "a4";
        String valA5 = "a5";
        
        dequeMap.addAll(keyA, asList(valA1, valA2, valA3, valA4, valA5));
        
        assertEquals(asList(valA1, valA2), dequeMap.popN(keyA, 2));
        assertEquals(asList(valA5, valA4), dequeMap.popNLast(keyA, 2));
        
        // case that we request to more elements than we have
        assertEquals(asList(valA3), dequeMap.popN(keyA, 3));
        dequeMap.addAll(keyA, asList(valA1, valA2));
        assertEquals(asList(valA2, valA1), dequeMap.popNLast(keyA, 5));
    }
    
    @Test
    public void equals_hashTest() {
        // implicitly checks asMap
        LinkedDequeHashMap<String, String> dequeMap1 = new LinkedDequeHashMap<String, String>();
        LinkedDequeHashMap<String, String> dequeMap2 = new LinkedDequeHashMap<String, String>();
        String keyA = "a";
        String valA1 = "a1";
        String valA2 = "a2";
        String keyB = "b";
        String valB1 = "b1";
        @SuppressWarnings("unused")
        String valB2 = "b2";
        
        dequeMap1.addLast(keyA, valA1);
        dequeMap1.addLast(keyA, valA2);
        dequeMap2.addAll(keyA, asList(valA1, valA2));
        
        dequeMap1.setAll(keyB, asList(valB1, valB1));
        dequeMap2.set(keyB, valB1);
        dequeMap2.addLast(keyB, valB1);
        
        assertEquals(dequeMap1, dequeMap2);
        assertEquals(dequeMap1.hashCode(), dequeMap2.hashCode());
    }
    
    @Test
    public void addMap_addMapLast_addAllMap_addAllMapLast_setMap_setAllMapTest() {
        LinkedDequeHashMap<String, String> expected = new LinkedDequeHashMap<String, String>();
        LinkedDequeHashMap<String, String> dequeMap = new LinkedDequeHashMap<String, String>();
        String keyA = "a";
        String valA1 = "a1";
        String valA2 = "a2";
        String keyB = "b";
        String valB1 = "b1";
        String valB2 = "b2";
        String keyC = "c";
        String valC1 = "c1";
        String valC2 = "c2";
        Map<String, String> map = new HashMap<String, String>();
        Map<String, List<String>> mapList = new HashMap<String, List<String>>();
        
        expected.add(keyA, valA1);
        expected.add(keyB, valB1);
        map.put(keyA, valA1);
        map.put(keyB, valB1);
        dequeMap.addMap(map);
        assertEquals(expected, dequeMap);
        
        map.clear();
        expected.addLast(keyA, valA1);
        map.put(keyA, valA1);
        dequeMap.addMapLast(map);
        assertEquals(expected, dequeMap);
        
        expected.set(keyA, valA2);
        expected.set(keyB, valB2);
        expected.set(keyC, valC2);
        map.put(keyA, valA2);
        map.put(keyB, valB2);
        map.put(keyC, valC2);
        dequeMap.setMap(map);
        assertEquals(expected, dequeMap);
        
        expected.addAll(keyA, asList(valA1));        
        expected.addAll(keyC, asList(valC1, valC1));
        mapList.put(keyA, asList(valA1));
        mapList.put(keyC, asList(valC1, valC1));
        dequeMap.addAllMap(mapList);
        assertEquals(expected, dequeMap);
        
        mapList.clear();
        expected.addAllLast(keyB, asList(valB1));
        mapList.put(keyB, asList(valB1));
        dequeMap.addAllMapLast(mapList);
        assertEquals(expected, dequeMap);
        
        for (Map.Entry<String, List<String>> en : mapList.entrySet()) {
            expected.setAll(en.getKey(), en.getValue());
        }
        dequeMap.setAllMap(mapList);
        assertEquals(expected, dequeMap);
    }
    
   
}
