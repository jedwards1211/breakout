package org.andork.collect;

public class LinkedPriorityEntry<K extends Comparable<K>, V> extends PriorityEntry<K, V> {
	public final LinkedPriorityEntry<K, V> source;

	public LinkedPriorityEntry(K key, V value, LinkedPriorityEntry<K, V> source) {
		super(key, value);
		this.source = source;
	}
}
