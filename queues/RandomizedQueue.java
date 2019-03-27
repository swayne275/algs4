/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description: Constant amortized time, so use an array (not linked list)
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] items;
    private int numItems;

    public RandomizedQueue() {
        numItems = 0;
        items = (Item[]) new Object[2]; // required ugly cast
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return numItems;
    }

    public void enqueue(Item item) {
        enforceNotNull(item);
        checkResize();

        items[numItems++] = item;
    }

    public Item dequeue() {
        enforceNotEmpty();

        // uniform as [a, b), using as 0-indexed position
        int randomIdx = StdRandom.uniform(numItems);
        Item item = items[randomIdx];
        if (randomIdx != (numItems - 1)) {
            // If not the last one, swap the last one in here
            items[randomIdx] = items[numItems - 1];
        }

        numItems--;
        checkResize(); // might need to shrink the array
        return item;
    }

    /**
     * return a random item but do not remove it
     *
     * @return A random item in the queue
     */
    public Item sample() {
        enforceNotEmpty();
        return items[StdRandom.uniform(numItems)];
    }

    public Iterator<Item> iterator() {
        return new RandomQueueIterator();
    }

    private class RandomQueueIterator implements Iterator<Item> {
        private final Item[] shuffledItems;
        private final int numShuffledItems; // don't use size() since it can change if stuff was added after this call
        private int shuffledIdx;

        private RandomQueueIterator() {
            numShuffledItems = size();
            shuffledItems = (Item[]) new Object[numShuffledItems]; // Required ugly cast
            for (int i = 0; i < numShuffledItems; i++) {
                // Initial population
                shuffledItems[i] = items[i];
            }
            StdRandom.shuffle(shuffledItems);
            shuffledIdx = 0;
        }

        public boolean hasNext() {
            return shuffledIdx < numShuffledItems;
        }

        public void remove() {
            throw new java.lang.UnsupportedOperationException("Remove unsupported");
        }

        public Item next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException("No next element");
            }

            return shuffledItems[shuffledIdx++];
        }
    }

    private void enforceNotEmpty() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException("Queue is empty");
        }
    }

    private void enforceNotNull(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException("Can't enque null element");
        }
    }

    /**
     * Resize the array
     *
     * @param size Capacity of the array after resizing
     */
    private void resizeArray(final int size) {
        if (size > 0) {
            Item[] tempStore = items;
            items = (Item[]) new Object[size]; // required ugly cast
            for (int i = 0; i < numItems; i++) {
                items[i] = tempStore[i];
            }
        }
    }

    /**
     * Double array size when capacity reached, half when using 1/4 or less
     */
    private void checkResize() {
        if (numItems == items.length) {
            resizeArray(2 * items.length);
        } else if (numItems <= items.length / 4) {
            resizeArray(items.length / 2);
        }
    }

    public static void main(String[] args) {
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();
        rq.enqueue(17);
        System.out.println(rq.dequeue());
        rq.enqueue(69);
    }
}
