package com.abc.ds.list;

import java.util.*;

import com.abc.ds.*;
import com.abc.ds.action.*;
import com.abc.ds.filter.*;
import com.abc.ds.iterator.*;
import com.abc.ds.sack.*;
import com.programix.util.*;

public class ArrayDSList<T> implements DSList<T> {
    private static final int NOT_FOUND_INDEX = -1;

    private T[] slots;
    private int count;
    private final Class<T> itemType;
    private final T[] itemTypeZeroLenArray;
    private final int percentToGrowCapacity;

    public ArrayDSList(Class<T> itemType,
                       int initialCapacity,
                       int percentToGrowCapacity) {

        ObjectTools.paramNullCheck(itemType, "itemType");
        if ( percentToGrowCapacity < 1 ) {
            throw new IllegalArgumentException("percentToGrowCapacity=" +
                percentToGrowCapacity + ", but must be at least 1");
        }
        this.itemType = itemType;
        this.percentToGrowCapacity = percentToGrowCapacity;

        itemTypeZeroLenArray = DSTools.createArrayFromType(itemType, 0);
        slots = DSTools.createArrayFromType(itemType, initialCapacity);
        count = 0;
    }

    public ArrayDSList(Class<T> itemType) {
        this(itemType, 100, 20);
    }

    private void growSlotsIfNeeded(int additionalCount) {
        if ( count + additionalCount > slots.length ) {
            int newCapacity = Math.max(
                count + additionalCount,
                (slots.length * (100 + percentToGrowCapacity)) / 100);
            T[] newSlots = DSTools.createArrayFromType(itemType, newCapacity);
            System.arraycopy(slots, 0, newSlots, 0, count);
            slots = newSlots;
        }
    }

    private void checkIndex(int index) throws IndexOutOfBoundsException {
        if ( isEmpty() ) {
            throw new IndexOutOfBoundsException(
                "index=" + index + ", list is empty, no index values are valid");
        } else if ( index < 0 || index >= count ) {
            throw new IndexOutOfBoundsException(
                "index=" + index + ", must be in the range [0.." +
                (count - 1) + "]");
        }
    }

    private void checkEmptyNoSuchElementException() throws NoSuchElementException {
        if ( isEmpty() ) {
            throw new NoSuchElementException("list is empty");
        }
    }


    @Override
    public Class<T> getItemType() {
        return itemType;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public void clear() {
        Arrays.fill(slots, null);
        count = 0;
    }

    @Override
    public boolean add(T item) {
        growSlotsIfNeeded(1);
        slots[count] = item;
        count++;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int addAll(T... items) {
        if ( ObjectTools.isEmpty(items) ) {
            return 0;
        }
        growSlotsIfNeeded(items.length);
        for ( T item : items ) {
            slots[count] = item;
            count++;
        }
        return items.length;
    }

    @Override
    public int addAll(DSSack<T> otherSack) {
        if ( DSTools.isEmpty(otherSack) ) {
            return 0;
        }
        return addAll(otherSack.peekAll());
    }

    @Override
    public void insertBefore(int index, T item)
            throws IndexOutOfBoundsException {

        if ( index == count ) {
            add(item);
        } else {
            checkIndex(index);
            growSlotsIfNeeded(1);
            for ( int i = count; i > index ; i-- ) {
                slots[i] = slots[i - 1];
            }
            slots[index] = item;
            count++;
        }
    }

    @Override
    public void insertFirst(T item) {
        insertBefore(0, item);
    }

    private int findFirstMatchIndex(DSFilter<T> filter) {
        for ( int i = 0; i < count; i++ ) {
            if ( filter.matches(slots[i]) ) {
                return i;
            }
        }
        return NOT_FOUND_INDEX;
    }

    @Override
    public int firstIndexOf(T item, int fromIndex) {
        throw new RuntimeException("not implemented yet"); // FIXME
    }

    @Override
    public int firstIndexOf(T item) {
        throw new RuntimeException("not implemented yet"); // FIXME
    }

    @Override
    public int lastIndexOf(T item, int fromIndex) {
        throw new RuntimeException("not implemented yet"); // FIXME
    }

    @Override
    public int lastIndexOf(T item) {
        throw new RuntimeException("not implemented yet"); // FIXME
    }

    private int findFirstIndexOf(T item) {
        return findFirstMatchIndex(new EqualToDSFilter<T>(item));
    }

    @Override
    public boolean remove(T item) {
        int idx = findFirstIndexOf(item);
        if ( idx == NOT_FOUND_INDEX ) {
            return false;
        }
        for ( int i = idx; i < count - 1; i++ ) {
            slots[i] = slots[i + 1];
        }
        slots[count - 1] = null; // don't inhibit garbage collection
        count--;
        return true;
    }

    @Override
    public T[] removeAndReturnMatches(DSFilter<T> filter) {
        if ( isEmpty() ) {
            return itemTypeZeroLenArray;
        }
        return removeMatchesCommon(filter,
            DSMatchHelper.createMatchAndCount(itemType, count / 4, 50))
            .getMatches();
    }

    @Override
    public int removeAndCountMatches(DSFilter<T> filter) {
        if ( isEmpty() ) {
            return 0;
        }
        return removeMatchesCommon(
            filter, DSMatchHelper.createCountOnly(itemType)).getMatchCount();
    }

    private DSMatchHelper<T> removeMatchesCommon(DSFilter<T> filter,
                                                 DSMatchHelper<T> matchHelper) {
        int dstIdx = 0;
        for ( int srcIdx = 0; srcIdx < count; srcIdx++ ) {
            T item = slots[srcIdx];
            if ( filter.matches(item) ) {
                matchHelper.append(item);
            } else {
                if ( dstIdx < srcIdx ) {
                    slots[dstIdx] = item;
                }
                dstIdx++;
            }
        }

        for ( ; dstIdx < count; dstIdx++ ) {
            slots[dstIdx] = null; // don't inhibit garbage collection
        }

        count -= matchHelper.getMatchCount();
        return matchHelper;
    }

    @Override
    public T[] removeAll() {
        T[] results = peekAll();
        clear();
        return results;
    }

    @Override
    public T removeAtIndex(int index) throws IndexOutOfBoundsException {
        throw new RuntimeException("not implemented yet"); // FIXME
    }

    @Override
    public T removeFirst() throws NoSuchElementException {
        throw new RuntimeException("not implemented yet"); // FIXME
    }

    @Override
    public T removeLast() throws NoSuchElementException {
        throw new RuntimeException("not implemented yet"); // FIXME
    }

    @Override
    public boolean contains(T item) {
        return findFirstIndexOf(item) != NOT_FOUND_INDEX;
    }

    @Override
    public T[] peekMatches(DSFilter<T> filter) {
        if ( isEmpty() ) {
            return itemTypeZeroLenArray;
        }
        return DSMatchHelper.createMatchAndCount(itemType, count / 4, 50)
            .append(this, filter)
            .getMatches();
    }

    @Override
    public int countMatches(DSFilter<T> filter) {
        if ( isEmpty() ) {
            return 0;
        }
        return DSMatchHelper.createMatchAndCount(itemType, count / 4, 20)
            .append(this, filter)
            .getMatchCount();
    }

    @Override
    public T[] peekAll() {
        if ( isEmpty() ) {
            return itemTypeZeroLenArray;
        }
        T[] results = DSTools.createArrayFromType(itemType, count);
        System.arraycopy(slots, 0, results, 0, count);
        return results;
    }

    @Override
    public T peekAtIndex(int index) throws IndexOutOfBoundsException {
        throw new RuntimeException("not implemented yet"); // FIXME
    }

    @Override
    public T peekFirst() throws NoSuchElementException {
        throw new RuntimeException("not implemented yet"); // FIXME
    }

    @Override
    public T peekLast() throws NoSuchElementException {
        throw new RuntimeException("not implemented yet"); // FIXME
    }

    @Override
    public void replaceAtLindex(int index, T replacementItem)
            throws IndexOutOfBoundsException {

        throw new RuntimeException("not implemented yet"); // FIXME
    }

    @Override
    public void performOnAll(DSAction<T> action) {
        // do this via peekAll() since we don't know if the action will
        // add or remove anything from this sack.
        for ( T item : peekAll() ) {
            action.perform(item);
        }
    }

    @Override
    public int performOnMatches(DSFilter<T> filter, DSAction<T> action) {
        // do this via peekMatches() since we don't know if the action will
        // add or remove anything from this sack.
        T[] matches = peekMatches(filter);
        for ( T item : matches ) {
            action.perform(item);
        }
        return matches.length;
    }

    @Override
    public DSIterator<T> createIterator() {
        return new InternalIterator();
    }

    @Override
    public DSIterator<T> createReverseIterator() {
        throw new RuntimeException("not implemented yet"); // FIXMEt
    }

    private class InternalIterator implements DSIterator<T> {
        private int currentIndex;

        public InternalIterator() {
            currentIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return (currentIndex + 1) < count;
        }

        @Override
        public T next() throws NoSuchElementException {
            currentIndex++;
            return slots[currentIndex];
        }
    } // type InternalIterator
}
