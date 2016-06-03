package org.andork.tracker;

import javax.swing.SwingUtilities;

import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.IntHolder;

public class TrackerTest {
	@Test
	public void test1() throws Exception {
		Dependency dep1 = new Dependency();
		Dependency dep2 = new Dependency();

		IntHolder callCount = new IntHolder(0);

		SwingUtilities.invokeAndWait(() -> {
			Tracker.EDT.autorun(() -> {
				dep1.depend();
				callCount.value++;
				if (callCount.value > 1) {
					dep2.depend();
				}
			});
			Assert.assertEquals(1, callCount.value);
			dep1.changed();
			dep2.changed();
		});

		SwingUtilities.invokeAndWait(() -> {
			Assert.assertEquals(2, callCount.value);
			dep2.changed();
		});
		SwingUtilities.invokeAndWait(() -> {
			Assert.assertEquals(3, callCount.value);
		});
	}
}
