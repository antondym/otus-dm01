package com.github.antondym.otus.l3.containers;

import java.lang.reflect.Array;
import java.util.*;

public class MyArrayList<T> implements List<T> {
    private Object[] _array;
    private int _size;

    public MyArrayList() {
        this(10);
    }

    public MyArrayList(int capacity) {
        assert capacity >= 0;
        _array = new Object[capacity];
    }

    public int size() {
        return _size;
    }

    public boolean isEmpty() {
        return _size == 0;
    }

    private int _indexOf(Object o) {
        for (int i = 0; i < _size; i++)
            if (Objects.equals(o, _array[i]))
                return i;
        return -1;
    }

    private int _lastIndexOf(Object o) {
        for (int i = _size - 1; i >= 0; i--)
            if (Objects.equals(o, _array[i]))
                return i;
        return -1;
    }

    public boolean contains(Object o) {
        return _indexOf(o) >= 0;
    }

    public Object[] toArray() {
        Object[] result = new Object[_size];
        System.arraycopy(_array, 0, result, 0, _size);
        return result;
    }

    public <T1> T1[] toArray(T1[] a) {
        assert a != null;

        T1[] dest;
        if (a.length >= _size)
            dest = a;
        else
            dest = (T1[]) Array.newInstance(a.getClass().getComponentType());
        System.arraycopy(_array, 0, a, 0, _size);

        return dest;
    }

    private int _capacity(int requiredCapacity) {
        // TODO: Improve capacity prediction
        return (int) (requiredCapacity * 1.1);
    }

    private void _ensureCapacity(int newElements) {
        // TODO: Check if thread-safety is required
        if (_array.length >= _size + newElements)
            return;

        Object[] newArray = new Object[_capacity(_size + newElements)];
        System.arraycopy(_array, 0, newArray, 0, _size);

        _array = newArray;
    }

    public boolean add(T t) {
        _ensureCapacity(1);
        _array[_size] = t;
        _size++;
        return true;
    }

    private void _remove(int idx) {
        assert idx >= 0;
        assert idx < _size;

        if (idx < _size - 1)
            System.arraycopy(_array, idx + 1, _array, idx, _size - idx - 1);

        _size--;
    }

    public boolean remove(Object o) {
        int idx = _indexOf(o);
        if (idx < 0) return false;

        return false;
    }

    public boolean containsAll(Collection<?> c) {
        for (Object o : c)
            if (!contains(o))
                return false;
        return true;
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        int newElements = c.size();
        _ensureCapacity(newElements);
        System.arraycopy(_array, index, _array, index + newElements, _size - index);
        Object[] cArray = c.toArray();
        System.arraycopy(cArray, 0, _array, index, newElements);
        return true;
    }

    public boolean addAll(Collection<? extends T> c) {
        return addAll(_size, c);
    }

    public boolean removeAll(Collection<?> c) {
        boolean result = false;
        for (Object o : c)
            result |= remove(o);
        return result;
    }

    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        for (int i = 0; i < _size; )
            if (!c.contains(_array[i])) {
                _remove(i);
                changed = true;
            } else
                i++;
        return changed;
    }

    public void clear() {
        _size = 0;
    }

    public T get(int index) {
        assert index >= 0;
        assert index < _size;

        return (T)_array[index];
    }

    public T set(int index, T element) {
        assert index >= 0;
        assert index < _size;

        T previous = (T)_array[index];
        _array[index] = element;
        return previous;
    }

    public void add(int index, T element) {
        _ensureCapacity(1);
        System.arraycopy(_array, index, _array, index + 1, _size - index);
        _array[index] = element;
        _size++;
    }

    public T remove(int index) {
        assert index >= 0;
        assert index < _size;

        Object removed = _array[index];
        _remove(index);
        return (T)removed;
    }

    public int indexOf(Object o) {
        return _indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return _lastIndexOf(o);
    }

    public ListIterator<T> listIterator(int index) {
        return new MyIterator(index);
    }

    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    public Iterator<T> iterator() {
        return listIterator();
    }

    public List<T> subList(int fromIndex, int toIndex) {
        // Thank God it's not needed
        return null;
    }

    private class MyIterator implements ListIterator<T> {
        private int _nextIndex;
        private int _lastIndex;
        private boolean _allowModifications = true;

        public MyIterator(int nextIndex) {
            assert nextIndex >= 0;
            assert nextIndex <= _size;
            _nextIndex = nextIndex;
        }

        @Override
        public boolean hasNext() {
            return _nextIndex < _size;
        }

        @Override
        public T next() {
            assert hasNext();
            _lastIndex = _nextIndex;
            T result = (T)_array[_lastIndex];
            _nextIndex = _nextIndex + 1;
            _allowModifications = true;
            return result;
        }

        @Override
        public boolean hasPrevious() {
            return _nextIndex > 0;
        }

        @Override
        public T previous() {
            assert hasPrevious();
            _lastIndex = _nextIndex - 1;
            T result =  (T)_array[_lastIndex];
            _nextIndex = _nextIndex - 1;
            _allowModifications = true;
            return result;
        }

        @Override
        public int nextIndex() {
            return _nextIndex;
        }

        @Override
        public int previousIndex() {
            return _nextIndex - 1;
        }

        @Override
        public void remove() {
            assert _allowModifications;
            _remove(_lastIndex);
            _allowModifications = false;
        }

        @Override
        public void set(T t) {
            assert _allowModifications;
            MyArrayList.this.set(_lastIndex, t);
            _allowModifications = false;
        }

        @Override
        public void add(T t) {
            assert _allowModifications;
            MyArrayList.this.add(_nextIndex, t);
            _nextIndex++;
            _allowModifications = false;
        }
    }
}
