package ru.au.java2;

import java.util.*;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.function.Predicate;

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

    private static class PositionDescriptor<T> {
        private ListNode<T> first, second, third;
        private boolean hasMatchedPredicate;

        PositionDescriptor(ListNode<T> f, ListNode<T> s, ListNode<T> t) {
            first = f;
            second = s;
            third = t;
        }
    }

    // rootNode -> nextToRootNode -> everything else
    private ListNode<T> nextToRootNode = new ListNode<>(null);
    private ListNode<T> rootNode = new ListNode<>(null, nextToRootNode);

    public boolean isEmpty() {
        return nextToRootNode.next.getReference() == null;
    }

    private PositionDescriptor<T> walkUntil(Predicate<ListNode<T>> pred) {
        PositionDescriptor<T> result = new PositionDescriptor<>(rootNode,
                nextToRootNode, nextToRootNode.next.getReference());

        while (result.third != null && !pred.test(result.third)) {
            result.first = result.second;
            result.second = result.third;
            result.third = result.third.next.getReference();
        }

        result.hasMatchedPredicate = pred.test(result.third);

        return result;
    }

    /**
     * Given a position descriptor <first, second, third>, try to do
     *  second.next = newRef
     *
     * In order to do that, modify stamp between `first` and `second`. If that fails,
     * then somebody tried to modify this node and we should back off.
     *
     * If stamp is modified, try to replace current "second.next" with the new one.
     * If CAS fails, then back off.
     *
     * I thought it would be easier to implement this, rather than to try to understand
     * Harris list. But I can be wrong and this produces live-locks.
     *
     * @param positionDesc
     * @param newRef
     * @return: whether we succeeded.
     */
    private boolean tryAssignRef(PositionDescriptor<T> positionDesc, ListNode<T> newRef) {
        int curSecondStamp = positionDesc.second.next.getStamp();

        int[] curFirstStamp = new int[1];
        ListNode<T> firstNextRef = positionDesc.first.next.get(curFirstStamp);
        if (!positionDesc.first.next.compareAndSet(firstNextRef, firstNextRef, curFirstStamp[0],
                curFirstStamp[0] + 1)) {
            return false;
        }

        if (!positionDesc.second.next.compareAndSet(positionDesc.third, newRef,
                curSecondStamp, curSecondStamp)) {
            return false;
        }

        return true;
    }

    public void append(T value) {
        final ListNode<T> newNode = new ListNode<>(value);

        while (true) {
            PositionDescriptor<T> positionDesc = walkUntil(Objects::isNull);
            if (!positionDesc.hasMatchedPredicate) {
                throw new RuntimeException("Could not find list end");
            }

            if (tryAssignRef(positionDesc, newNode)) {
                break;
            }
        }
    }

    private Predicate<ListNode<T>> getFindPredicate(T value) {
        return x -> x != null && x.elem.equals(value);
    }

    // Returns "true" if contained
    public boolean remove(T value) {
        while (true) {
            PositionDescriptor<T> positionDesc = walkUntil(getFindPredicate(value));
            if (!positionDesc.hasMatchedPredicate) {
                return false;
            }

            if (tryAssignRef(positionDesc, positionDesc.third.next.getReference())) {
                return true;
            }
        }
    }

    public boolean contains(T value) {
        PositionDescriptor<T> positionDesc = walkUntil(getFindPredicate(value));
        return positionDesc.hasMatchedPredicate;
    }

    public List<T> debugSingleThreadSnapshot() {
        List<T> lst = new ArrayList<>();

        ListNode<T> cur = nextToRootNode.next.getReference();
        while (cur != null) {
            lst.add(cur.elem);
            cur = cur.next.getReference();
        }

        return lst;
    }

    public Set<T> debugSet() {
        Set<T> set = new HashSet<>();
        set.addAll(debugSingleThreadSnapshot());
        return set;
    }
}
