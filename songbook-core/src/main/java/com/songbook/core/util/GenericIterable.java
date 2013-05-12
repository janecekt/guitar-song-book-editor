package com.songbook.core.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class GenericIterable<T> implements Iterable<T> {
    private GenericIterator iterator;

    protected abstract T readNextEntry();


    @Override
    public Iterator<T> iterator() {
        if (iterator == null) {
            iterator = new GenericIterator();
            return iterator;
        } else {
            throw new UnsupportedOperationException("Iterator can be returned only once !");
        }
    }


    private class GenericIterator implements Iterator<T> {
        private T nextEntry;

        public GenericIterator() {
            this.nextEntry = readNextEntry();
        }


        @Override
        public boolean hasNext() {
            return (nextEntry != null);
        }


        @Override
        public T next() {
            if (hasNext()) {
                // Read next line
                T result = nextEntry;
                nextEntry = readNextEntry();
                return result;
            } else {
                // No next elements - throw exception as per spec !
                throw new NoSuchElementException("There are no more entries !");
            }

        }


        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported !");
        }
    }
}
