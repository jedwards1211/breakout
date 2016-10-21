package org.andork.swing.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.junit.Assert;
import org.junit.Test;

public class RealListModelTest {
	class Listener implements ListDataListener {
		@Override
		public void contentsChanged(ListDataEvent e) {
			events.add(e);
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			events.add(e);
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			events.add(e);
		}
	}

	RealListModel<String> model;
	Listener listener;
	ArrayList<ListDataEvent> events = new ArrayList<>();

	void assertAdded(int from, int to) {
		ListDataEvent event = events.remove(0);
		Assert.assertEquals(ListDataEvent.INTERVAL_ADDED, event.getType());
		Assert.assertEquals(from, event.getIndex0());
		Assert.assertEquals(to, event.getIndex1());
	}

	void assertChanged(int from, int to) {
		ListDataEvent event = events.remove(0);
		Assert.assertEquals(ListDataEvent.CONTENTS_CHANGED, event.getType());
		Assert.assertEquals(from, event.getIndex0());
		Assert.assertEquals(to, event.getIndex1());
	}

	void assertList(String... elements) {
		Assert.assertArrayEquals(elements, model.toArray(new String[model.size()]));
	}

	void assertRemoved(int from, int to) {
		ListDataEvent event = events.remove(0);
		Assert.assertEquals(ListDataEvent.INTERVAL_REMOVED, event.getType());
		Assert.assertEquals(from, event.getIndex0());
		Assert.assertEquals(to, event.getIndex1());
	}

	public void init(String... elements) {
		events.clear();
		model = new RealListModel<>(new ArrayList<>(Arrays.asList(elements)));
		listener = new Listener();
		model.addListDataListener(listener);
	}

	@Test
	public void testAdd() {
		init();
		model.add("this is a test");
		assertAdded(0, 0);
		model.add("test 2");
		assertAdded(1, 1);
		model.add(0, "test 3");
		assertAdded(0, 0);
		assertList("test 3", "this is a test", "test 2");
	}

	@Test
	public void testClear() {
		init("a", "b", "c", "d", "e");
		model.clear();
		assertRemoved(0, 4);
		assertList();
	}

	@Test
	public void testRemove() {
		init("a", "b", "c", "d", "e");
		model.remove("c");
		assertRemoved(2, 2);
		assertList("a", "b", "d", "e");
		model.remove(1);
		assertRemoved(1, 1);
		assertList("a", "d", "e");
	}

	@Test
	public void testSubListAdd() {
		init("a", "b", "c", "d", "e");
		List<String> sublist = model.subList(1, 4);
		sublist.add("j");
		sublist.add("k");
		assertAdded(4, 4);
		assertAdded(5, 5);
		assertList("a", "b", "c", "d", "j", "k", "e");
	}

	@Test
	public void testSubListAddAll() {
		init("a", "b", "c", "d", "e");
		List<String> sublist = model.subList(1, 4);
		sublist.addAll(Arrays.asList("j", "k"));
		assertAdded(4, 5);
		assertList("a", "b", "c", "d", "j", "k", "e");
	}

	@Test
	public void testSubListClear() {
		init("a", "b", "c", "d", "e");
		model.subList(1, 4).clear();
		assertRemoved(1, 3);
		assertList("a", "e");
	}
}
