package org.andork.redux;

import org.andork.redux.logger.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.IntHolder;

public class ReduxTest {
	@Test
	public void test1() {
		Store store = Redux.applyMiddleware(new Logger()).apply(
				Redux.createStore(
						new Reducer() {
							@Override
							public Object apply(Object state, Action action) {
								return action.type == "INCREMENT"
										? (Integer) state + 1
										: state;
							}
						},
						0));

		IntHolder eventCount = new IntHolder();

		Runnable unsub = store.subscribe(() -> eventCount.value++);

		Assert.assertEquals(0, store.getState());
		Assert.assertEquals(0, eventCount.value);
		store.dispatch(new Action("INCREMENT"));
		Assert.assertEquals(1, store.getState());
		Assert.assertEquals(1, eventCount.value);
		unsub.run();
		store.dispatch(new Action("INCREMENT"));
		Assert.assertEquals(2, store.getState());
		Assert.assertEquals(1, eventCount.value);
	}
}
