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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.io.Serializable;

/**
 * Common operations over collections.
 * 
 * @author Stathis Aliprantis - alieus@hotmail.gr
 */
public class CollectUtil {
    private CollectUtil() {}
    
    /**
     * Returns an <em>unmodifiable</em> reversed version of the given list.
     * Changes to the original list are reflected to the returned list but
     * the opposite is not possible because the returned list is unmodifiable.
     * The result is {@link Serializable}  if the original list is {@code Serializable}. 
     * 
     * @param original the list to revert
     * @return the reverse of the given list
     */
    public static <E> List<E> reverseUnmodifiable(final List<E> original) {
        return new ReversedList<E>(original);
    }
    
    private static class ReversedList<E> extends AbstractList<E> implements Serializable {
        private static final long serialVersionUID = 0L;
        
        private final List<E> original;
        
        public ReversedList(List<E> original) {
            this.original = original;
        }
        
        int reverseIndex(int index) {
            return (original.size() - 1) - index;
        }

        @Override
        public E get(int index) {
            return original.get(reverseIndex(index));
        }

        @Override
        public int size() {
            return original.size();
        }
    }
    
    
    /**
     * Returns a reversed shallow copy of the given list.
     * The result is {@link Serializable} and {@link RandomAccess}.
     * 
     * @param original the list to revert
     * @return a reversed shallow copy of the given list
     */
    public static <E> List<E> reverseCopy(List<E> original) {
        List<E> result = new ArrayList<E>(original.size());
        
        if (original instanceof RandomAccess) {
            ListIterator<E> it = original.listIterator(original.size());
            while (it.hasPrevious()) {
                result.add(it.previous());
            }
        } else {
            for (int i = original.size()-1; i >= 0; i--) {
                result.add(original.get(i));
            }
        }
        
        return result;
    }
    
}
