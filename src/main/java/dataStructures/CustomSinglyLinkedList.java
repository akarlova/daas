package dataStructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CustomSinglyLinkedList<E> implements List<E> {

    private CustomNode<E> head;

    public CustomSinglyLinkedList() {
        this.head = null;
    }

    @Override
    public int size() {
        CustomNode<E> current = head;
        int size = 0;
        while (current != null) {
            size++;
            current = current.getNextNode();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(E e) {
        return false;
    }

    @Override
    public void addFirst(E e) {
        CustomNode<E> newNode = new CustomNode<>(e);
        newNode.setNextNode(head);
        head = newNode;
    }

    @Override
    public void addLast(E e) {
        if (head == null) {
            addFirst(e);
            return;
        }

        CustomNode<E> newNode = new CustomNode<>(e);
        CustomNode<E> current = head;
        while (current.getNextNode() != null) {
            current = current.getNextNode();
        }

        current.setNextNode(newNode);
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public E get(int index) {
        return null;
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {

    }

    @Override
    public E remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return List.of();
    }

    @Override
    public String toString() {
        String result = "";
        CustomNode<E> current = head;
        if (head == null) {
            return "";
        } else {
            while (current != null) {
                result += current + " ";
                current = current.getNextNode();
            }
            return result;
        }
    }
}
