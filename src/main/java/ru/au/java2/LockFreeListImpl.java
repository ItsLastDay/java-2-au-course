package ru.au.java2;

import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeListImpl<T> implements LockFreeList<T> {
    private static class ListNode<T> {
        private volatile T elem;
        private volatile AtomicStampedReference<ListNode<T>> next;

        ListNode(T elem) {
            this.elem = elem;
            next = new AtomicStampedReference<>(null, 0);
        }

        ListNode(T elem, ListNode<T> ref) {
            this.elem = elem;
            next = new AtomicStampedReference<>(ref, 0);
        }
    }

    private ListNode<T> tail = new ListNode<>(null);
    private ListNode<T> head = new ListNode<>(null, tail);

    private class SearchResult {
        private ListNode<T> left, right;
    }

    private SearchResult search(T key) {
        SearchResult res = new SearchResult();

        while (true) {
            ListNode<T> t = head;
            int tNextStamp[] = new int[1];
            ListNode<T> tNext = head.next.get(tNextStamp);

            ListNode<T> leftNodeNext = null;

            do {
                if (tNextStamp[0] == 0) {
                    res.left = t;
                    leftNodeNext = tNext;
                }

                t = tNext;
                if (t == tail)
                    break;
                tNext = t.next.get(tNextStamp);
            } while (tNextStamp[0] == 1 || !t.elem.equals(key));
            res.right = t;

            if (leftNodeNext == res.right) {
                if (res.right != tail && res.right.next.getStamp() == 1)
                    continue;
                else
                    return res;
            }

            if (res.left.next.compareAndSet(leftNodeNext, res.right, 0, 0)) {
                if (res.right != tail && res.right.next.getStamp() == 1)
                    continue;
                else
                    return res;
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return head.next.getReference() == tail;
    }

    @Override
    public void append(T value) {
        if (value == null) {
            throw new RuntimeException("Cannot insert null to list");
        }

        while (true) {
            SearchResult res = search(null);
            if (res.right != tail) {
                throw new RuntimeException("Expected to arrive at tail");
            }

            ListNode<T> newNode = new ListNode<>(value, res.right);

            if (res.left.next.compareAndSet(res.right, newNode, 0, 0))
                break;
        }
    }

    @Override
    public boolean remove(T value) {
        if (value == null) {
            return false;
        }

        SearchResult res;
        ListNode<T> rightNext;

        while (true) {
            res = search(value);

            if (res.right == tail || !res.right.elem.equals(value))
                return false;

            int rightStamp[] = new int[1];
            rightNext = res.right.next.get(rightStamp);
            if (rightStamp[0] == 0) {
                if (res.right.next.compareAndSet(rightNext, rightNext, 0, 1)) {
                    break;
                }
            }
        }

        if (!res.left.next.compareAndSet(res.right, rightNext, 0, 0)) {
            search(res.right.elem);
        }

        return true;
    }

    @Override
    public boolean contains(T value) {
        if (value == null) {
            return false;
        }
        SearchResult res = search(value);
        return value.equals(res.right.elem);
    }

}
