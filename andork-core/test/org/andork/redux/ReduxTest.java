package org.andork.redux;

import java.util.Arrays;

import org.andork.redux.logger.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.IntHolder;

public class ReduxTest {
	@Test
	public void test1() {
		Store<Number> store = Redux.applyMiddleware(
				Arrays.asList(new Logger()),
				Redux.createStore(
						new Reducer<Number>() {
							@Override
							public Number apply(Number state, Action action) {
								return action.type == "INCREMENT"
										? state.intValue() + 1
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
