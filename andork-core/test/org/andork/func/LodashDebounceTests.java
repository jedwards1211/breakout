package org.andork.func;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.andork.func.Lodash.DebounceOptions;
import org.junit.Assert;
import org.junit.Test;
import org.omg.CORBA.IntHolder;

public class LodashDebounceTests {
	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	@Test
	public void shouldDebounceAFunction() throws InterruptedException, ExecutionException {
		IntHolder callCount = new IntHolder(0);

		Function<Character, Character> debounced = Lodash.debounce(v -> {
			callCount.value++;
			return v;
		} , 32, new DebounceOptions<Character>().executor(executor));

		Character[] results = new Character[] { debounced.apply('a'), debounced.apply('b'), debounced.apply('c') };
		Assert.assertArrayEquals(new Character[] { null, null, null }, results);
		Assert.assertEquals(0, callCount.value);

		Future<?> f1 = executor.schedule(() -> {
			Assert.assertEquals(1, callCount.value);

			Character[] results2 = new Character[] { debounced.apply('a'), debounced.apply('b'), debounced.apply('c') };
			Assert.assertArrayEquals(new Character[] { 'c', 'c', 'c' }, results2);
			Assert.assertEquals(1, callCount.value);
		} , 128, TimeUnit.MILLISECONDS);

		Future<?> f2 = executor.schedule(() -> {
			Assert.assertEquals(2, callCount.value);
		} , 256, TimeUnit.MILLISECONDS);

		f1.get();
		f2.get();
	}

	@Test
	public void subsequentDebouncedCallsReturnTheLastResult() throws InterruptedException, ExecutionException {
		DebounceOptions<Character> o = new DebounceOptions<Character>().executor(executor);
		Function<Character, Character> debounced = Lodash.debounce(v -> v, 32, o);
		debounced.apply('a');

		Future<?> f1 = o.setTimeout().apply(() -> {
			Assert.assertNotEquals('b', (char) debounced.apply('b'));
		} , 64L);

		Future<?> f2 = o.setTimeout().apply(() -> {
			Assert.assertNotEquals('c', (char) debounced.apply('c'));
		} , 128L);

		f1.get();
		f2.get();
	}

	@Test
	public void shouldNotImmediatelyCallFuncWhenWaitIs0() throws InterruptedException, ExecutionException {
		IntHolder callCount = new IntHolder(0);
		DebounceOptions<Void> o = new DebounceOptions<Void>().executor(executor);
		Runnable debounced = Lodash.debounce(() -> {
			callCount.value++;
		} , 0, o);
		debounced.run();
		debounced.run();
		Assert.assertEquals(0, callCount.value);

		o.setTimeout().apply(() -> {
			Assert.assertEquals(1, callCount.value);
		} , 5L).get();
	}

	@Test
	public void shouldSupportALeadingOption() throws InterruptedException, ExecutionException {
		DebounceOptions<Void> o0 = new DebounceOptions<Void>().executor(executor).leading(true);
		DebounceOptions<Void> o1 = new DebounceOptions<Void>().executor(executor).leading(true).trailing(true);

		IntHolder callCount0 = new IntHolder(0);
		IntHolder callCount1 = new IntHolder(0);

		Runnable withLeading = Lodash.debounce(() -> {
			callCount0.value++;
		} , 32, o0);
		Runnable withLeadingAndTrailing = Lodash.debounce(() -> {
			callCount1.value++;
		} , 32, o1);

		withLeading.run();
		Assert.assertEquals(1, callCount0.value);

		withLeadingAndTrailing.run();
		withLeadingAndTrailing.run();
		Assert.assertEquals(1, callCount1.value);

		o1.setTimeout().apply(() -> {
			Assert.assertEquals(1, callCount0.value);
			Assert.assertEquals(2, callCount1.value);

			withLeading.run();
			Assert.assertEquals(2, callCount0.value);
		} , 64L).get();
	}

	@Test
	public void subsequentLeadingDebouncedCallsReturnTheLastResult() throws InterruptedException, ExecutionException {
		DebounceOptions<Character> o = new DebounceOptions<Character>().executor(executor).leading(true)
				.trailing(false);
		Function<Character, Character> debounced = Lodash.debounce(v -> v, 32, o);
		char[] results = new char[] { debounced.apply('a'), debounced.apply('b') };
		Assert.assertArrayEquals(new char[] { 'a', 'a' }, results);

		o.setTimeout().apply(() -> {
			char[] results2 = new char[] { debounced.apply('c'), debounced.apply('d') };
			Assert.assertArrayEquals(new char[] { 'c', 'c' }, results2);
		} , 64L).get();
	}

	@Test
	public void shouldSupportATrailingOption() throws InterruptedException, ExecutionException {
		DebounceOptions<Void> o0 = new DebounceOptions<Void>().executor(executor).trailing(true);
		DebounceOptions<Void> o1 = new DebounceOptions<Void>().executor(executor).trailing(false);

		IntHolder withCount = new IntHolder(0);
		IntHolder withoutCount = new IntHolder(0);

		Runnable withTrailing = Lodash.debounce(() -> {
			withCount.value++;
		} , 32, o0);
		Runnable withoutTrailing = Lodash.debounce(() -> {
			withoutCount.value++;
		} , 32, o1);

		withTrailing.run();
		Assert.assertEquals(0, withCount.value);

		withoutTrailing.run();
		Assert.assertEquals(0, withoutCount.value);

		o1.setTimeout().apply(() -> {
			Assert.assertEquals(1, withCount.value);
			Assert.assertEquals(0, withoutCount.value);
		} , 64L).get();
	}

	@Test
	public void shouldSupportAMaxWaitOption() throws InterruptedException, ExecutionException {
		IntHolder callCount = new IntHolder(0);
		DebounceOptions<Void> o = new DebounceOptions<Void>().executor(executor).maxWait(64);
		Runnable debounced = Lodash.debounce(() -> {
			callCount.value++;
		} , 32, o);
		debounced.run();
		debounced.run();
		Assert.assertEquals(0, callCount.value);

		Future<?> f1 = o.setTimeout().apply(() -> {
			Assert.assertEquals(1, callCount.value);
			debounced.run();
			debounced.run();
			Assert.assertEquals(1, callCount.value);
		} , 128L);
		Future<?> f2 = o.setTimeout().apply(() -> {
			Assert.assertEquals(2, callCount.value);
		} , 256L);

		f1.get();
		f2.get();
	}

	@Test
	public void shouldSupportMaxWaitInATightLoop() throws InterruptedException, ExecutionException {
		int limit = 320;
		DebounceOptions<Void> o0 = new DebounceOptions<Void>().executor(executor).maxWait(128);
		DebounceOptions<Void> o1 = new DebounceOptions<Void>().executor(executor);

		IntHolder withCount = new IntHolder(0);
		IntHolder withoutCount = new IntHolder(0);

		Runnable withMaxWait = Lodash.debounce(() -> {
			withCount.value++;
		} , 64, o0);
		Runnable withoutMaxWait = Lodash.debounce(() -> {
			withoutCount.value++;
		} , 96, o1);

		long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < limit) {
			withMaxWait.run();
			withoutMaxWait.run();
		}

		o0.setTimeout().apply(() -> {
			Assert.assertTrue(withCount.value > 0);
			Assert.assertEquals(0, withoutCount.value);
		} , 64L).get();
	}

	@Test
	public void shouldQueueATrailingCallForSubsequentDebouncedCallsAfterMaxWait()
			throws InterruptedException, ExecutionException {
		IntHolder callCount = new IntHolder(0);
		DebounceOptions<Void> o = new DebounceOptions<Void>().executor(executor).maxWait(200);
		Runnable debounced = Lodash.debounce(() -> {
			callCount.value++;
		} , 200, o);

		debounced.run();

		o.setTimeout().apply(debounced, 190L);
		o.setTimeout().apply(debounced, 200L);
		o.setTimeout().apply(debounced, 210L);

		o.setTimeout().apply(() -> {
			Assert.assertEquals(2, callCount.value);
		} , 500L).get();
	}

	@Test
	public void shouldInvokeTheTrailingCallWithTheCorrectArguments() throws InterruptedException, ExecutionException {
		IntHolder callCount = new IntHolder(0);
		DebounceOptions<Boolean> o = new DebounceOptions<Boolean>().executor(executor).leading(true).maxWait(64);

		List<Integer> actualA = new ArrayList<>();
		List<String> actualB = new ArrayList<>();

		BiFunction<Integer, String, Boolean> debounced = Lodash.debounce((a, b) -> {
			actualA.add(a);
			actualB.add(b);
			return ++callCount.value != 2;
		} , 32, o);

		while (true) {
			if (!debounced.apply(1, "a")) {
				break;
			}
		}

		o.setTimeout().apply(() -> {
			Assert.assertEquals(2, callCount.value);
			Assert.assertEquals(Arrays.asList(1, 1), actualA);
			Assert.assertEquals(Arrays.asList("a", "a"), actualB);
		} , 64L).get();
	}
}
