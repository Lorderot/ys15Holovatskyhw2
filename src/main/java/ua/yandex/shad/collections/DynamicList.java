package ua.yandex.shad.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Mykola Holovatsky
 */
public class DynamicList<T> implements Iterable<T> {
    private static final int MEMORY_ = 10;
    private T[] array;
    private int size;

    public DynamicList() {
        array = (T[]) new Object[MEMORY_];
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int pointer = 0;

            @Override
            public boolean hasNext() {
                return pointer < size;
            }

            @Override
            public T next() throws NoSuchElementException {
                checkIndex(pointer);
                return array[pointer++];
            }
        };
    }

    public void add(T element) {
        if (size < array.length) {
            array[size++] = element;
        } else {
            T[] extend = (T[]) new Object[array.length * 2];
            for (int i = 0; i < array.length; i++) {
                extend[i] = array[i];
            }
            extend[size++] = element;
            array = extend;
        }
    }

    public T set(int index, T element) throws NoSuchElementException {
        checkIndex(index);
        T replaced = array[index];
        array[index] = element;
        return replaced;
    }

    public T get(int index) throws NoSuchElementException {
        checkIndex(index);
        return array[index];
    }

    public Object[] toArray() {
        return Arrays.copyOf((Object[]) array, size);
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    public int size() {
        return this.size;
    }

    private void checkIndex(int index) throws NoSuchElementException {
        if (index < 0 || index >= size) {
            throw new NoSuchElementException();
        }
    }
}
