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
 * A linked list which exposes it's nodes so there is no need to traverse the
 * list and find them again.
 * 
 * @author Stathis Aliprantis - alieus@hotmail.gr
 * @param <E> the type of the elements of the list
 */
public class ExposedLinkList<E> extends AbstractList<E> {

    // Node is public
    public class Node {
        E datum;
        Node previous;
        Node next;
        boolean detached;

        public E getDatum() {
            return datum;
        }

        public Node previous() {
            checkNotDetached();
            return previous;
        }

        public Node next() {
            checkNotDetached();
            return next;
        }

        public boolean isDetached() {
            return detached;
        }
        
        public Iterable<E> untilEnd() {
            checkNotDetached();
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public Iterable<E> fromStart() {
            checkNotDetached();
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public void moveBack() {
            checkNotDetached();
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public void moveFront() {
            checkNotDetached();
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        void checkNotDetached() throws IllegalStateException {
            if (detached) {
                throw new IllegalStateException("Node has been removed from the list.");
            }
        }
    }

    private int size;
    private Node head;
    private Node tail;
    
    @Override
    public E get(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    private class Itr implements ListIterator<E> {
        private Node current;
        private int currentIndex;
        private int itrModCount = modCount;

        public Itr(Node current, int currentIndex) {
            this.current = current;
            this.currentIndex = currentIndex;
        }
        
        @Override
        public boolean hasNext() {
            return current.next != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No next element");
            }
            current = current.next();
            currentIndex++;
            return current.datum;
        }

        @Override
        public boolean hasPrevious() {
            return current.previous != null;
        }

        @Override
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException("No previous element");
            }
            current = current.previous();
            currentIndex--;
            return current.datum;
        }

        @Override
        public int nextIndex() {
            return currentIndex+1;
        }

        @Override
        public int previousIndex() {
            return currentIndex-1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void set(E e) {
            current.datum = e;
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}