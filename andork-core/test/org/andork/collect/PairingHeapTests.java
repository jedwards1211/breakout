package org.andork.collect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.IntHolder;

public class PairingHeapTests {
	static class Dumbheap<K extends Comparable<K>, V> {
		static class Entry<K extends Comparable<K>, V> {
			K key;
			V value;

			public Entry(K key, V value) {
				super();
				this.key = key;
				this.value = value;
			}
		}

		List<Entry<K, V>> entries = new ArrayList<>();

		int size() {
			return entries.size();
		}

		boolean isEmpty() {
			return entries.isEmpty();
		}

		void clear() {
			entries.clear();
		}

		void insert(K key, V value) {
			entries.add(new Entry<>(key, value));
		}

		void merge(Dumbheap<K, V> other) {
			entries.addAll(other.entries);
			other.entries.clear();
		}

		Entry<K, V> findMin() {
			Entry<K, V> minEntry = null;
			for (Entry<K, V> entry : entries) {
				if (minEntry == null || entry.key.compareTo(minEntry.key) < 0) {
					minEntry = entry;
				}
			}
			return minEntry;
		}

		Entry<K, V> removeMin() {
			Entry<K, V> minEntry = findMin();
			if (minEntry != null) {
				entries.remove(minEntry);
			}
			return minEntry;
		}

		void decreaseKey(Entry<K, V> entry, K newKey) {
			if (newKey.compareTo(entry.key) > 0) {
				throw new IllegalArgumentException("newKey must be <= entry's key");
			}
			entry.key = newKey;
		}
	}

	static <K extends Comparable<K>, V> void sanityCheck(PairingHeap.Entry<K, V> Entry, IntHolder size) {
		size.value++;
		if (Entry.left != null) {
			Assert.assertSame(Entry.left.parent, Entry);
			Assert.assertTrue(Entry.getKey().compareTo(Entry.left.getKey()) <= 0);
			sanityCheck(Entry.left, size);
		}
		if (Entry.right != null) {
			Assert.assertSame(Entry.right.parent, Entry);
			sanityCheck(Entry.right, size);
		}
	}

	static <K extends Comparable<K>, V> void sanityCheck(PairingHeap<K, V> heap) {
		IntHolder size = new IntHolder(0);
		Assert.assertEquals(heap.findMin() == null, heap.isEmpty());
		if (heap.findMin() != null) {
			sanityCheck(heap.findMin(), size);
		}
		Assert.assertEquals(size.value, heap.size());
		Assert.assertEquals(size.value == 0, heap.isEmpty());
	}

	public void singleFuzzTest() {
		Random rand = new Random();
		PairingHeap<Integer, Integer> p = new PairingHeap<>();
		Dumbheap<Integer, Integer> d = new Dumbheap<>();
		PairingHeap.Entry<Integer, Integer> pe;
		Dumbheap.Entry<Integer, Integer> de;
		int i, j, key, value;
		for (i = 0; i < 1000; i++) {
			double r = Math.random();
			if (r < 0.6) {
				key = rand.nextInt(1000);
				value = rand.nextInt(1000);
				p.insert(key, value);
				d.insert(key, value);
			} else if (r < 0.9) {
				if (!d.isEmpty()) {
					pe = p.removeMin();
					de = d.removeMin();
					Assert.assertEquals(pe.getKey(), de.key);
				}
			} else {
				PairingHeap<Integer, Integer> op = new PairingHeap<>();
				Dumbheap<Integer, Integer> od = new Dumbheap<>();
				for (j = 0; j < rand.nextInt(100); j++) {
					key = rand.nextInt(1000);
					value = rand.nextInt(1000);
					op.insert(key, value);
					od.insert(key, value);
				}
				p.merge(op);
				d.merge(od);
			}
			sanityCheck(p);
			Assert.assertEquals(d.size(), p.size());
			if (!p.isEmpty()) {
				Assert.assertEquals(d.findMin().key, p.findMin().getKey());
			} else {
				Assert.assertNull(p.findMin());
			}
		}

		while (!p.isEmpty() && !d.isEmpty()) {
			pe = p.removeMin();
			de = d.removeMin();
			Assert.assertEquals(de.key, pe.getKey());
			Assert.assertEquals(d.size(), p.size());
		}
		Assert.assertEquals(d.size(), p.size());
	}

	@Test
	public void fuzzTest() {
		for (int i = 0; i < 10; i++) {
			singleFuzzTest();
		}
	}
}
