package org.andork.q;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class QAutorunTest {
	static class TestModel extends QSpec<TestModel> {
		public static final Attribute<Integer> foo = newAttribute(Integer.class, "foo");
		public static final Attribute<Integer> bar = newAttribute(Integer.class, "bar");
		public static final Attribute<Integer> baz = newAttribute(Integer.class, "baz");
		public static final Attribute<QArrayList<Integer>> list = newAttribute(QArrayList.class, "list");
		public static final Attribute<QHashMap<String, Integer>> map = newAttribute(QHashMap.class, "map");

		public static final TestModel instance = new TestModel();

		public static QObject<TestModel> newInstance() {
			return QObject.newInstance(instance);
		}
	}

	@Test
	public void testQObject001() {
		List<Object> autorunValues = new ArrayList<>();

		QObject<TestModel> model = TestModel.newInstance();
		model.set(TestModel.foo, 1);
		model.set(TestModel.bar, 2);

		new QAutorun(() -> {
			autorunValues.add(model.get(TestModel.foo) * 2);
		});

		Assert.assertArrayEquals(new Object[] { 2 }, autorunValues.toArray());

		autorunValues.clear();

		model.set(TestModel.foo, 5);
		Assert.assertArrayEquals(new Object[] { 10 }, autorunValues.toArray());

		autorunValues.clear();
		model.set(TestModel.bar, 3);
		Assert.assertArrayEquals(new Object[] {}, autorunValues.toArray());
	}

	@Test
	public void testQObject002() {
		List<Object> autorunValues = new ArrayList<>();

		QObject<TestModel> model = TestModel.newInstance();
		model.set(TestModel.foo, 1);
		model.set(TestModel.bar, 2);

		new QAutorun(() -> {
			autorunValues.add(model.get(TestModel.foo) + model.get(TestModel.bar));
		});

		Assert.assertArrayEquals(new Object[] { 3 }, autorunValues.toArray());

		autorunValues.clear();

		model.set(TestModel.foo, 5);
		Assert.assertArrayEquals(new Object[] { 7 }, autorunValues.toArray());

		autorunValues.clear();
		model.set(TestModel.bar, 3);
		Assert.assertArrayEquals(new Object[] { 8 }, autorunValues.toArray());

		autorunValues.clear();
		model.set(TestModel.baz, 8);
		Assert.assertArrayEquals(new Object[] {}, autorunValues.toArray());
	}

	@Test
	public void testQObjectAndList() {
		List<Object> autorunValues = new ArrayList<>();

		QObject<TestModel> model = TestModel.newInstance();
		model.set(TestModel.foo, 1);
		model.set(TestModel.bar, 2);
		model.set(TestModel.list, QArrayList.newInstance(Arrays.asList(1, 2, 3)));

		new QAutorun(() -> {
			autorunValues.add(model.get(TestModel.foo) + model.get(TestModel.list).get(0));
		});

		Assert.assertArrayEquals(new Object[] { 2 }, autorunValues.toArray());

		autorunValues.clear();
		model.set(TestModel.foo, 5);
		Assert.assertArrayEquals(new Object[] { 6 }, autorunValues.toArray());

		autorunValues.clear();
		model.get(TestModel.list).set(0, 8);
		Assert.assertArrayEquals(new Object[] { 13 }, autorunValues.toArray());
	}

	@Test
	public void testQObjectAndMap() {
		List<Object> autorunValues = new ArrayList<>();

		QObject<TestModel> model = TestModel.newInstance();
		model.set(TestModel.foo, 1);
		model.set(TestModel.bar, 2);
		model.set(TestModel.map, QHashMap.newInstance());
		model.get(TestModel.map).put("a", 5);

		new QAutorun(() -> {
			Integer mapValue = model.get(TestModel.map).get("a");
			autorunValues.add(model.get(TestModel.foo) + (mapValue == null ? 0 : mapValue));
		});

		Assert.assertArrayEquals(new Object[] { 6 }, autorunValues.toArray());

		autorunValues.clear();
		model.set(TestModel.foo, 5);
		Assert.assertArrayEquals(new Object[] { 10 }, autorunValues.toArray());

		autorunValues.clear();
		model.get(TestModel.map).set("a", 8);
		Assert.assertArrayEquals(new Object[] { 13 }, autorunValues.toArray());

		autorunValues.clear();
		model.get(TestModel.map).set("b", 4);
		Assert.assertArrayEquals(new Object[] { 13 }, autorunValues.toArray());

		autorunValues.clear();
		QHashMap<String, Integer> newMap = QHashMap.newInstance();
		newMap.set("a", -6);
		model.set(TestModel.map, newMap);
		Assert.assertArrayEquals(new Object[] { -1 }, autorunValues.toArray());

		autorunValues.clear();
		model.get(TestModel.map).clear();
		Assert.assertArrayEquals(new Object[] { 5 }, autorunValues.toArray());
	}
}
