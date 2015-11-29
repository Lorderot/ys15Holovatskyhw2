package ua.yandex.shad.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Mykola Holovatsky
 */
public class DynamicList<T> implements Iterable<T> {
    private static final int memoryInitiation = 10;
    private T[] array;
    private int size = 1;

    public DynamicList() {
        array = (T[]) new Object[memoryInitiation];
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int pointer = 1;
            @Override
            public boolean hasNext() {
                return pointer < size;
            }

            @Override
            public T next() throws NoSuchElementException {
                if (pointer >= size) {
                    throw new NoSuchElementException();
                }
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

    public Object[] toArray() {
        return Arrays.copyOf((Object[]) array, size - 1);
    }

    public boolean isEmpty() {
        if (size == 1) {
            return true;
        }
        return false;
    }
}
