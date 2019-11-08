package com.abc.prime;

/**
 * Implementation of {@link LongFifo} which uses a circular array internally.
 * <p>
 * Look at the documentation in LongFifo to see how the methods are supposed to
 * work.
 * <p>
 * The data is stored in the slots array. count is the number of items currently
 * in the FIFO. When the FIFO is not empty, head is the index of the next item
 * to remove. When the FIFO is not full, tail is the index of the next available
 * slot to use for an added item. Both head and tail need to jump to index 0
 * when they "increment" past the last valid index of slots (this is what makes
 * it circular).
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/Circular_buffer">Circular Buffer
 * on Wikipedia</a> for more information.
 */
public class CircularArrayLongFifo implements LongFifo {
    // do not change any of these fields:
    private final long[] slots;
    private int head;
    private int tail;
    private int count;
    private final Object lockObject;

    // this constructor is correct as written - do not change
    public CircularArrayLongFifo(int fixedCapacity,
                                 Object proposedLockObject) {

        lockObject =
            proposedLockObject != null ? proposedLockObject : new Object();

        slots = new long[fixedCapacity];
        head = 0;
        tail = 0;
        count = 0;
    }

    // this constructor is correct as written - do not change
    public CircularArrayLongFifo(int fixedCapacity) {
        this(fixedCapacity, null);
    }

    // this method is correct as written - do not change
    @Override
    public int getCount() {
        synchronized ( lockObject ) {
            return count;
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized ( lockObject ) {
            return count == 0;
        }
    }

    @Override
    public boolean isFull() {
        synchronized ( lockObject ) {
            return count == slots.length;
        }
    }

    @Override
    public void clear() {
        synchronized ( lockObject ) {
            // No need - just keep the old junk (harmless):
            // Arrays.fill(slots, 0);
            head = 0;
            tail = 0;
            count = 0;
        }
    }

    @Override
    public int getCapacity() {
        return slots.length;
    }

    @Override
    public void add(long value) throws InterruptedException {
        synchronized (lockObject) {
            while(this.isFull()) {
                lockObject.wait();
            }
            slots[tail] = value;
            tail = (tail + 1) % slots.length;
            count++;
            lockObject.notifyAll();
        }
    }

    @Override
    public long remove() throws InterruptedException {
        synchronized ( lockObject ) {
            while(this.isEmpty()) {
                lockObject.wait();
            }
            long value = slots[head];
            head = (head + 1) % slots.length;
            count--;
            lockObject.notifyAll();
            return value;
        }
    }

    // this method is correct as written - do not change
    @Override
    public Object getLockObject() {
        return lockObject;
    }
}
