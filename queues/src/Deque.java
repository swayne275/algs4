import java.util.Iterator;

public class Deque<Item> implements Iterable<Item> {
    private Node first;
    private Node last;
    private int size;

    private class Node {
        Item item;
        Node next;
        Node previous;
    }

    /// Construct an empty deque
    public Deque() {
        first = null;
        last = null;
        size = 0;
    }

    /// Is the deque empty?
    public boolean isEmpty() {
        return size == 0;
    }

    /// return the number of items on the deque
    public int size() {
        return size;
    }

    /// Add the item to the front
    public void addFirst(Item item) {
        enforceNotNull(item);

        Node newFirst = new Node();
        newFirst.item = item;
        if (isEmpty()) {
            first = newFirst;
            last = first;
        } else {
            first.previous = newFirst;
            newFirst.next = first;
            first = newFirst;
        }

        size++;
    }

    /// Add the item to the end
    public void addLast(Item item) {
        enforceNotNull(item);

        Node newLast = new Node();
        newLast.item = item;
        if (isEmpty()) {
            last = newLast;
            first = last;
        } else {
            newLast.previous = last;
            last.next = newLast;
            last = newLast;
        }

        size++;
    }

    /**
     * Remove and return the first element from the deque
     *
     * @return First item in the deque
     * @note This will properly set first = last if size is 1
     */
    public Item removeFirst() {
        enforceNotEmpty();

        Item firstItem = first.item;
        if (first == last) {
            // Deque is now empty
            first = null;
            last = null;
        } else {
            first = first.next;
            first.previous = null;
        }
        size--;

        return firstItem;
    }

    /**
     * Remove and return the last element from the deque
     *
     * @return Last item in the deque
     * @note This will properly set first = last if the size is 1
     */
    public Item removeLast() {
        enforceNotEmpty();

        Item lastItem = last.item;
        if (first == last) {
            // Deque is now empty
            first = null;
            last = null;
        } else {
            last = last.previous;
            last.next = null;
        }
        size--;

        return lastItem;
    }

    /// return an iterator over items in order from front to end
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new java.lang.UnsupportedOperationException("Remove unsupported");
        }

        public Item next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException("No next element");
            }
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    private void enforceNotEmpty() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException("Cannot remove from empty deque");
        }
    }

    private void enforceNotNull(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException("Can't add null element");
        }
    }

    public static void main(String[] args) {
        // Intentionally empty
    }
}
