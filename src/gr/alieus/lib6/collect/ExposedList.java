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

import java.util.*;

// XXX Not complete!!

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
                    return new Itr(next); // we don't know index
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
        
        /**
         * Removes this node and it's data from the list.
         * 
         * @throws IllegalStateException if this node is not present on the list
         */
        public void remove() {
        	checkNotDettached();
            unlink();
            incSizeMod(-1);
        }
        
        public Node addAfter(E datum) {
        	checkNotDettached();
        	incSizeMod(1);
        	return linkAfter(this, datum);
        }
        
        public Node addBefore(E datum) {
        	if (this.previous != null) {
        		return this.previous.addBefore(datum);
        	} else {
        		incSizeMod(1);
        		return linkFirst(datum);
        	}
        }
        
        public void addAllAfter(Collection<E> data) {
        	
        }
        
        public void addAllBefore(Collection<E> data) {
        	
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
    
    /** 
     * Returns a list iterator over the elements in this list (in proper sequence),
     * starting at the specified position in the list. The specified index
     * indicates the first element that would be returned by an initial call to next.
     * An initial call to previous would return the element with the specified index minus one.
     * <p/>
     * The returned iterator throws {@code ConcurrentModificationException}s
     * as specified by {@code ListIterator}.
     * 
     * @throws IndexOutOfBoundsException if index < 0 or index > size
     */
    @Override
    public ListIterator<E> listIterator(int index) {
    	if (index == size) {
    		return new Itr(null, index);
    	}
    	return new Itr(getNodeAt(index), index);
    }

    /** 
     * Returns an iterator over the elements in this list in proper sequence.
     * This implementation returns {@code ListIterator(0)}.
     * @see #listIterator(int)
     */
    @Override
    public Iterator<E> iterator() {
    	return listIterator();
    }
    
    @Override
    public boolean contains(Object o) {
    	if (o == null) {
    		for (Node it = head; it != null; it = it.next) {
    			if (it.datum == null) return true;
    		}
    	} else {
    		for (Node it = head; it != null; it = it.next) {
    			if (o.equals(it.datum)) return true;
    		}
    	}
    	
    	return false;
    }
    
    @Override
    public void add(int index, E element) {
    	// TODO Auto-generated method stub
    	super.add(index, element);
    }
    
    /** 
     * Returns the first element of this list (at index 0) or {@literal null}
     * if the list is empty.
     * 
     * @return the first element of this list (at index 0) or {@literal null}
     * if the list is empty.
     */
    public E getFirst() {
        if (head != null) {
            return head.datum;
        } else {
            return null;
        }
    }
    
    /** 
     * Returns the last element of this list (at index 0) or {@literal null}
     * if the list is empty.
     * 
     * @return the last element of this list (at index 0) or {@literal null}
     * if the list is empty.
     */
    public E getLast() {
        if (tail != null) {
            return tail.datum;
        } else {
            return null;
        }
    }
    
    /**
     * Removes and returns the first element of this list.
     * If the list is empty, does nothing and returns null.
     * 
     * @return the first element of this list or {@code null} if this list is empty
     */
    public E popFirst() {
        if (head == null) {
            return null;
        }
        E result = head.datum;
        head.remove();
        return result;
    }

    /**
     * Removes and returns the last element of this list.
     * If the list is empty, does nothing and returns null.
     * 
     * @return the last element of this list or {@literal null} if this list is empty
     */
    public E popLast() {
        if (tail == null) {
            return null;
        }
        E result = tail.datum;
        tail.remove();
        return result;
    }
    
    /**
     * Throws {@code IllegalStateException} if this list is empty.
     * 
     * @throws IllegalStateException if this list is empty
     */
    public void assertNotEmpty() throws IllegalStateException {
        if (isEmpty()) {
        	throw new IllegalStateException("List is empty");
        }
    }
    
    /**
     * Throws {@code IllegalStateException} if this list contains less than the
     * specified elements.
     * 
     * @throws IllegalStateException if this list contains less than the
     * specified elements
     */
    public void assertContainsAtLeast(int count) {
    	if (size() < count) {
    		throw new IllegalStateException("List contains less than "+count+" elements");
    	}
    }
    
    /**
     * Adds the given element as the first element of this list.
     * Returns the node that holds the element.
     * 
     * @param elem the element to add
     * @return the node that holds the added element
     */
    public Node addFirst(E elem) {
        incSizeMod(1);
        return linkFirst(elem);
    }

    /**
     * Adds the given element as the last element of this list.
     * Returns the node that holds the element.
     * 
     * @param elem the element to add
     * @return the node that holds the added element
     */
    public Node addLast(E elem) {
        incSizeMod(1);
        if (tail == null) {
            return linkFirst(elem);
        } else {
            return linkAfter(tail, elem);
        }
    }
    
    /**
     * Returns the node that holds the element at the given position.
     * 
     * @param index the index of the node to return
     * @return the node at the given index
     * @throws IndexOutOfBoundsException if the given index is out of bounds
     */
    public Node getNodeAt(int index) {
    	checkIndex(index);
    	
        int i;
        Node it;
        if (index <= size /2) {
            for (i = 0, it = head; i < index; i++, it = it.next);
        } else {
            for (i = size-1, it = tail; i > index; i--, it = it.previous);
        }
        return it;
    }
    
    
    
    private Node linkAfter(Node n, E datum) {
        n.next = new Node(datum, n, n.next);
        if (n.next.next != null) {
            n.next.next.previous = n.next;
        } else if (n == tail) {
            tail = n.next;
        }
        
        return n.next;
    }
    
    private Node linkFirst(E datum) {
        if (head == null) {
            head = new Node(datum, null, null);
            tail = head;
        } else {
            head = new Node(datum, null, head);
            head.next.previous = head;
        }
        
        return head;
    }
    
    private Node createNodeChain(Collection<? extends E> c) {
        throw new UnsupportedOperationException("Not yet implemented.");
//    	Iterator<? extends E> iter = c.iterator();
//    	Node n = new Node(iter.next());
//    	while (iter.hasNext()) {
////    		n = 
//    	}
    }
    
    private void checkIndex(int index) {
    	if (index < 0 || index >= size) {
    		throw new IndexOutOfBoundsException("Index: "+index+" Size: "+size);
    	} 
    }
    
    private void incSizeMod(int count) {
        size += count;
        modCount++;
    }
    
    private void incMod() {
    	modCount++;
    }
    
// Iterator
    private class Itr implements ListIterator<E> {
        protected Node nextNode;
        protected Node last;
        protected int nextIndex;
        protected int itrModCount = modCount;
        protected final boolean noIndexAvailable;

        public Itr(Node next, int nextIndex) {
        	if (nextIndex < 0) {
        		throw new IllegalArgumentException("Next index < 0");
        	}
            this.nextNode = next;
            this.nextIndex = nextIndex;
            noIndexAvailable = false;
        }
        
        public Itr(Node next) {
            this.nextNode = next;
            this.nextIndex = -1;
            noIndexAvailable = true;
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
        	checkIndexAvailable();
            return nextIndex;
        }

        @Override
        public int previousIndex() {
        	checkIndexAvailable();
            return nextIndex-1;
        }

        @Override
        public void remove() {
            checkModCount();
            checkLast();
            last.unlink();
            incSizeMod(-1); itrModCount++;
            last = null;
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void set(E e) {
            checkLast();
            last.datum = e;
        }

        @Override
        public void add(E e) {
            checkModCount();
            // case A: add as first
            if (head == null || nextNode != null && nextNode.previous == null) {
            	linkFirst(e);
            // case B: add last
            } else if (nextNode == null) {
            	linkAfter(tail, e); // tail != null because list not empty because head!=null
            // case C: otherwise
            } else {
            	linkAfter(nextNode.previous, e);
            }
            nextIndex++;
            incSizeMod(1); itrModCount++;
            last = null;
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
        
        final void checkIndexAvailable() throws UnsupportedOperationException {
        	if (noIndexAvailable) {
        		throw new UnsupportedOperationException("Index is not available");
        	}
        }
    }
}