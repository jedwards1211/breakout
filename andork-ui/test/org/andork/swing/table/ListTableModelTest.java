package org.andork.swing.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.andork.swing.list.RealListModel;
import org.andork.swing.table.ListTableModel.ColumnBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ListTableModelTest {
	class Listener implements TableModelListener {
		@Override
		public void tableChanged(TableModelEvent e) {
			events.add(e);
		}
	}

	RealListModel<String> list;
	ListTableModel<String> model;
	Listener listener;
	ArrayList<TableModelEvent> events = new ArrayList<>();

	void assertAdded(int from, int to) {
		TableModelEvent event = events.remove(0);
		Assert.assertEquals(TableModelEvent.INSERT, event.getType());
		Assert.assertEquals(from, event.getFirstRow());
		Assert.assertEquals(to, event.getLastRow());
	}

	void assertChanged(int from, int to) {
		TableModelEvent event = events.remove(0);
		Assert.assertEquals(TableModelEvent.UPDATE, event.getType());
		Assert.assertEquals(from, event.getFirstRow());
		Assert.assertEquals(to, event.getLastRow());
	}

	void assertList(String... elements) {
		Assert.assertEquals(elements.length, model.getRowCount());
		for (int i = 0; i < elements.length; i++) {
			Assert.assertEquals(elements[i], model.getValueAt(i, 0));
		}
	}

	void assertRemoved(int from, int to) {
		TableModelEvent event = events.remove(0);
		Assert.assertEquals(TableModelEvent.DELETE, event.getType());
		Assert.assertEquals(from, event.getFirstRow());
		Assert.assertEquals(to, event.getLastRow());
	}

	public void init(String... elements) {
		events.clear();
		list = new RealListModel<>(new ArrayList<>(Arrays.asList(elements)));
		model = new ListTableModel<>(Arrays.asList(
				new ColumnBuilder<String, String>()
						.getter(s -> s).create()),
				(ListModel<String>) list);
		listener = new Listener();
		model.addTableModelListener(listener);
	}

	@Test
	public void testAdd() {
		init();
		list.add("this is a test");
		assertAdded(0, 0);
		list.add("test 2");
		assertAdded(1, 1);
		list.add(0, "test 3");
		assertAdded(0, 0);
		assertList("test 3", "this is a test", "test 2");
	}

	@Test
	public void testClear() {
		init("a", "b", "c", "d", "e");
		list.clear();
		assertRemoved(0, 4);
		assertList();
	}

	@Test
	public void testRemove() {
		init("a", "b", "c", "d", "e");
		list.remove("c");
		assertRemoved(2, 2);
		assertList("a", "b", "d", "e");
		list.remove(1);
		assertRemoved(1, 1);
		assertList("a", "d", "e");
	}

	@Test
	public void testSubListAdd() {
		init("a", "b", "c", "d", "e");
		List<String> sublist = list.subList(1, 4);
		sublist.add("j");
		sublist.add("k");
		assertAdded(4, 4);
		assertAdded(5, 5);
		assertList("a", "b", "c", "d", "j", "k", "e");
	}

	@Test
	public void testSubListAddAll() {
		init("a", "b", "c", "d", "e");
		List<String> sublist = list.subList(1, 4);
		sublist.addAll(Arrays.asList("j", "k"));
		assertAdded(4, 5);
		assertList("a", "b", "c", "d", "j", "k", "e");
	}

	@Test
	public void testSubListClear() {
		init("a", "b", "c", "d", "e");
		list.subList(1, 4).clear();
		assertRemoved(1, 3);
		assertList("a", "e");
	}
}
