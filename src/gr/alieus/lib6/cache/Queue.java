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

package gr.alieus.lib6.cache;
// TODO write tests!!

import java.util.NoSuchElementException;

/**
 * General purpose queue.
 * Supports inserting, extracting and moving a node to the end (making it tail)
 * in constant time.
 * 
 * @author Stathis Aliprantis - p3120005
 * @param <T> the type of the elements of this queue
 */
final class Queue<T> {
    
    /**
     * List node for storing the data of the queue.
     * An instance of this class is created and returned by the insert method.
     * The same instance may be used to move the node to end of the queue via the
     * moveBack method.
     * 
     * @param <E> the type of the element of this node
     */
    public final static class Node<E> {
        Node<E> previous;
        Node<E> next;
        E element;

        Node(E element) {
            this.element = element;
        }
        
        /**
         * Returns the element (the data) of this node.
         * 
         * @return the element (the data) of this node
         */
        public E getElement() {
            return element;
        }
    }
    
    private Node<T> head;
    private Node<T> tail;
    private int size;

    /**
     * Returns the size.
     * 
     * @return the number of elements of this queue
     */
    public int getSize() {
        return size;
    }
    
    /**
     * Inserts the specified element into the back of this queue.
     * 
     * @param elem the element to be inserted
     * @return the node created (may be used with the moveBack method)
     */
    public Node<T> insert(T elem) {
        Node<T> toInsert = new Node<T>(elem);
        
        if (head == null) {
            head = toInsert;
            tail = toInsert;
        } else {
            tail.next = toInsert;
            toInsert.previous = tail;
            tail = toInsert;
        }
        size++;
        
        return toInsert;
        
    }
    
    /**
     * Removes the head of this queue and returns its element
     * 
     * @return the element of the head of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    public T extract() {
        if (head == null) {
            throw new NoSuchElementException("Queue is Empty");
        }
        
        T result = head.element;
        head = head.next;
        if (head == null) {
            tail = null;
        } else {
            head.previous = null;
        }
        size--;
        
        return result;
    }
    
    /**
     * Moves the specified Node to the end of the queue.
     * The specified Node is assumed to belong to the queue.
     * 
     * @param node the node to be moved to the back
     */
    public void moveBack(Node<T> node) {
        if (node == tail) {
            return;
        }
        
        // remove the node
        Node<T> prev = node.previous,
                next = node.next;
        if (prev != null) {
            prev.next = node.next;
        }
        if (next != null) {
            next.previous = node.previous;
        }
        if (node == head) {
            head = next;
        }
        
        // append the node
        tail.next = node;
        node.previous = tail;
        node.next = null;
        tail = node;
    }
}
