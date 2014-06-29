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

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

/**
 * Test of CollectUtil.
 * 
 * @author Stathis Aliprantis - alieus@hotmail.gr
 */
public class CollectUtilTest {

    @Test
    public void testReverseUnmodifiable() {
        List<Integer> original = new ArrayList<Integer>(asList(1, 2, 3));
        List<Integer> expected = asList(3, 2, 1);
        List<Integer> result = CollectUtil.reverseUnmodifiable(original);
        assertEquals(expected, result);
        original.add(4);
        expected = asList(4, 3, 2, 1);
        assertEquals(expected, result);
    }

    @Test
    public void testReverseCopy() {
     // Random access
        List<Integer> original = new ArrayList<Integer>(asList(1, 2, 3));
        List<Integer> expected = asList(3, 2, 1);
        List<Integer> result = CollectUtil.reverseCopy(original);
        assertEquals("RandomAccess. Excpect: "+expected+" != Result: "+result, expected, result);
        
        // Non random access
        original = new LinkedList<Integer>(asList(1, 2, 3));
        expected = asList(3, 2, 1);
        result = CollectUtil.reverseCopy(original);
        assertEquals("Non-RandomAccess. Excpect: "+expected+" != Result: "+result, expected, result);
    }

}
