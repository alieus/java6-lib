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
public class ExposedList<E> extends AbstractList<E> {

    // Node is public
    public class Node {
        E datum;
        Node previous;
        Node next;
        boolean dettached;

        public Node(E datum) {
            this.datum = datum;
        }
        
        public Node(E datum, Node previous, Node next) {
            this.datum = datum;
            this.next = next;
            this.previous = previous;
        }
        
        public E getDatum() {
            return datum;
        }

        public Node previous() {
            checkNotDettached();
            return previous;
        }

        public Node next() {
            checkNotDettached();
            return next;
        }

        public ExposedList<E> getList() {
            return ExposedList.this;
        }
        
        public boolean isDetached() {
            return dettached;
        }
        
        public Iterable<E> untilEnd() {
            checkNotDettached();
            return new Iterable<E>() {
                public Iterator<E> iterator() {
                    return new Itr(next, -1); // we don't know index
                };
            };
        }
        
        public Iterable<E> fromStart() {
            checkNotDettached();
            return new Iterable<E>() {
                public Iterator<E> iterator() {
                    return new Itr(head, 0) {
                        @Override
                        public boolean hasNext() {
                            return super.hasNext() && nextNode != Node.this;
                        }
                    };
                };
            };
        }
        
        public void moveBack() {
            checkNotDettached();
            if (this == tail) {
                return;
            }
            unlink();
            previous = tail;
            tail.next = this;
            tail = this;
        }
        
        public void moveFront() {
            checkNotDettached();
            if (this == head) {
                return;
            }
            unlink();
            next = head;
            head.previous = this;
            head = this;
        }
        
        public void moveBefore(Node n) {
            checkNotDettached();
            n.checkNotDettached();
            if (n == this) {
                return;
            }
            checkSameList(n);
            unlink();
            next = n;
            if (n.previous != null) {
                previous = n.previous;
                previous.next = this;
                n.previous = this;
            } else {
                head = this;
                previous = null;
            }
        }
        
        public void moveAfter(Node n) {
            checkNotDettached();
            n.checkNotDettached();
            if (n == this) {
                return;
            }
            checkSameList(n);
            unlink();
            previous = n;
            if (n.next != null) {
                next = n.next;
                next.previous = this;
                n.next = this;
            } else {
                tail = this;
                next = null;
            }
        }
        
             
        final void unlink() {
            if (this == head) {
                head = this.next;
            }
            if (this == tail) {
                tail = this.previous;
            }
            if (this.previous != null) {
                this.previous.next = this.next;
            }
            if (this.next != null) {
                this.next.previous = this.previous;
            }
        }
        
        final void checkNotDettached() throws IllegalStateException {
            if (dettached) {
                throw new IllegalStateException("Node has been removed from the list.");
            }
        }
        
        final void checkSameList(Node other) throws IllegalArgumentException {
            if (ExposedList.this != other.getList()) {
                throw new IllegalArgumentException("Nodes are of diffrent lists");
            }
        }
    }

    private int size;
    private Node head;
    private Node tail;
    
    @Override
    public E get(int index) {
        return getNodeAt(index).datum;
    }

    @Override
    public int size() {
        return size;
    }
    
    
    
    public E getFirst() {
        if (head != null) {
            return head.datum;
        } else {
            return null;
        }
    }
    
    public E getLast() {
        if (tail != null) {
            return tail.datum;
        } else {
            return null;
        }
    }
    
    public E popFirst() {
        if (head == null) {
            return null;
        }
        E result = head.datum;
        removeNode(head);
        return result;
    }
    
    public E popLast() {
        if (tail == null) {
            return null;
        }
        E result = tail.datum;
        removeNode(tail);
        return result;
    }
    
    public void assertNotEmpty() {
        
    }
    
    public Node addFirst(E elem) {
        Node result = linkFirst(elem);
        incSize(1);
        return result;
    }
    
    public Node addLast(E elem) {
        Node result;
        if (tail == null) {
            result = linkFirst(elem);
        } else {
            result = linkAfter(tail, elem);
        }
        incSize(1);
        return result;
    }
    
    public Node getNodeAt(int index) {
        int i;
        Node it;
        if (index <= size /2) {
            for (i = 0, it = head; i < index; i++, it = it.next);
        } else {
            for (i = size-1, it = tail; i > index; i--, it = it.previous);
        }
        return it;
    }
    
    public void removeNode(Node n) {
        n.unlink();
        incSize(-1);
    }
    
    Node linkAfter(Node n, E datum) {
        n.next = new Node(datum, n, n.next);
        if (n.next.next != null) {
            n.next.next.previous = n.next;
        } else if (n == tail) {
            tail = n.next;
        }
        return n.next;
//        incSize(1);
    }
    
    Node linkFirst(E datum) {
        if (head == null) {
            head = new Node(datum, null, null);
            tail = head;
        } else {
            head = new Node(datum, null, head);
            head.next.previous = head;
        }
        return head;
//        incSize(1);
    }
    
    
    
    private void incSize(int count) {
        size += count;
        modCount++;
    }
    
// Iterator
    private class Itr implements ListIterator<E> {
        protected Node nextNode;
        protected Node last;
        protected int nextIndex;
        protected int itrModCount = modCount;

        public Itr(Node next, int nextIndex) {
            this.nextNode = next;
            this.nextIndex = nextIndex;
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
            last.datum = e;
        }

        @Override
        public void add(E e) {
            checkModCount();
            checkLast();
            linkAfter(last, e);
        }
        
        final void checkModCount() throws ConcurrentModificationException {
            if (itrModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
        
        final void checkLast() throws IllegalStateException {
            if (last == null) {
                throw new IllegalStateException("Operation requires a previouslly accessed node");
            }
        }
    }
}