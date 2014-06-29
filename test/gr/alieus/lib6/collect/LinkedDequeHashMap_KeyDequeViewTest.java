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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for inner class KeyDequeViewTest of LinkedDequeHashMap.
 * 
 * @author Stathis Aliprantis - alieus@hotmail.gr
 */
public class LinkedDequeHashMap_KeyDequeViewTest {

    private LinkedDequeHashMap<String, String> dequeMap;
    
    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String KEY3 = "key3";
    private static final String KEY_EMPTY = "keyempty";
    
    @Before
    public void setUp() throws Exception {
        dequeMap = new LinkedDequeHashMap<String, String>();
        dequeMap.set(KEY1, "value1a");
        dequeMap.addLast(KEY1, "value1b");
        dequeMap.addLast(KEY2, "value2a");
        dequeMap.addAll(KEY3, asList("lala", "tata", "nana"));
    }

    @Test
    public void testSize() {
        assertEquals(dequeMap.countOf(KEY1), dequeMap.getAll(KEY1).size());
        assertEquals(dequeMap.countOf(KEY3), dequeMap.getAll(KEY3).size());
        assertEquals(dequeMap.countOf(KEY_EMPTY), dequeMap.getAll(KEY_EMPTY).size());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(dequeMap.getAll(KEY_EMPTY).isEmpty());
        assertFalse(dequeMap.getAll(KEY3).isEmpty());
    }

    @Test
    public void testClear() {
        dequeMap.getAll(KEY3).clear();;
        assertFalse(dequeMap.containsKey(KEY3));
    }

    @Test
    public void testContainsObject() {
        String val = "lala23234";
        dequeMap.add(KEY2, val);
        assertTrue(dequeMap.getAll(KEY2).contains(val));
        assertFalse(dequeMap.getAll(KEY2).contains("lola578/7"));
    }

    @Test
    public void testAddV() {
        String val = "fooooo";
        dequeMap.getAll(KEY2).add(val);
        System.out.println("vals of key2 are "+dequeMap.getAll(KEY2));
        assertEquals(val, dequeMap.getLast(KEY2));
        
        dequeMap.getAll(KEY_EMPTY).add(val);
        assertEquals(val, dequeMap.getLast(KEY_EMPTY));
    }

    @Test
    public void testRemoveObject() {
        String val = "lala23234";
        dequeMap.add(KEY2, val);
        dequeMap.getAll(KEY2).remove(val);
        assertFalse(dequeMap.getAll(KEY2).contains(val));
        
        int oldSize = dequeMap.size();
        dequeMap.add(KEY_EMPTY, val);
        dequeMap.getAll(KEY_EMPTY).remove(val);
        assertEquals(oldSize, dequeMap.size());
    }

    
    
    @Test
    public void testGetInt() {
        List<String> keyVals = dequeMap.getAll(KEY_EMPTY);
        for (int i = 0; i < 5; i++) {
            dequeMap.addLast(KEY_EMPTY, ""+i);
        }
        for (int i = 0; i < 5; i++) {
            assertEquals(""+i, keyVals.get(i));
        }
    }

    @Test
    public void testSetIntV() {
        String val = "oleee";
        dequeMap.getAll(KEY2).set(0, val);
        assertEquals(val, dequeMap.get(KEY2));
    }

    @Test
    public void testAddIntV() {
        String val = "oleee";
        dequeMap.getAll(KEY2).add(1, val);
        assertEquals(val, dequeMap.getAll(KEY2).get(1));
    }

    @Test
    public void testRemoveInt() {
        dequeMap.addAll(KEY_EMPTY, asList("0", "1", "2", "3"));
        dequeMap.getAll(KEY_EMPTY).remove(2);
        assertEquals(asList("0", "1", "3"), dequeMap.getAll(KEY_EMPTY));
    }

    @Test
    public void testListIteratorInt() {
        String val = "a val";
        
        dequeMap.setAll(KEY1, asList("0", "1", "2", "3"));
        
        int iterCount = 0;
        Iterator<String> it = dequeMap.getAll(KEY1).listIterator(0);
        while (it.hasNext()) {
            assertTrue(dequeMap.getAll(KEY1).contains(it.next()));
            iterCount++;
        }
        assertEquals(dequeMap.countOf(KEY1), iterCount);
        
        ListIterator<String> empIt = dequeMap.getAll(KEY_EMPTY).listIterator(0);
        empIt.add(val);
        assertFalse(empIt.hasNext());
        assertTrue(empIt.hasPrevious());
        assertEquals(dequeMap.get(KEY_EMPTY), val);
        assertEquals(val, empIt.previous());
        
        empIt.remove();
        assertFalse(dequeMap.containsKey(KEY_EMPTY));
        assertFalse(empIt.hasNext());
        assertFalse(empIt.hasPrevious());
        
        // TODO test for concurrent modifications
    }

}
