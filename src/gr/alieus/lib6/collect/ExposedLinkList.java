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

        public Node(E datum, Node previous, Node next) {
            this.datum = datum;
            this.previous = previous;
            this.next = next;
        }
        
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
    
    void linkAfter(Node n, E datum) {
        n.next = new Node(datum, n, n.next);
        if (n.next.next != null) {
            n.next.next.previous = n.next;
        } else if (n == tail) {
            tail = n.next;
        }
        incSize(1);
    }
    
    void linkFirst(E datum) {
        if (head == null) {
            head = new Node(datum, null, null);
            tail = head;
        } else {
            head = new Node(datum, null, head);
            head.next.previous = head;
        }
        incSize(1);
    }
    
    private void incSize(int count) {
        size += count;
        modCount++;
    }
    
// Iterator
    private class Itr implements ListIterator<E> {
        private Node nextNode;
        private Node last;
        private int nextIndex;
        private int itrModCount = modCount;

        public Itr(Node next, int currentIndex) {
            this.nextNode = next;
            this.nextIndex = currentIndex;
        }
        
        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public E next() {
            checkModCount();
            if (!hasNext()) {
                throw new NoSuchElementException("No next element");
            }
            E result = nextNode.datum;
            last = nextNode; 
            nextNode = nextNode.next();
            nextIndex++;
            return result;
        }

        @Override
        public boolean hasPrevious() {
            return nextNode == null && tail != null
                    || nextNode.previous != null;
        }

        @Override
        public E previous() {
            checkModCount();
            if (!hasPrevious()) {
                throw new NoSuchElementException("No previous element");
            }
            if (nextNode == null) {
                nextNode = tail;
            } else {
                nextNode = nextNode.previous();
            }
            E result = nextNode.datum;
            last = nextNode;
            nextIndex--;
            return result;
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex-1;
        }

        @Override
        public void remove() {
            checkModCount();
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void set(E e) {
            nextNode.datum = e;
        }

        @Override
        public void add(E e) {
            checkModCount();
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        void checkModCount() throws ConcurrentModificationException {
            if (itrModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
        
    }
}