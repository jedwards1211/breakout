package org.andork.swing;

public abstract class DoSwingR2<R> implements Runnable {
	private R	result;

	public DoSwingR2() {
		DoSwing.doSwing(this);
	}

	@Override
	public void run() {
		result = doRun();
	}

	protected abstract R doRun();

	public R result() {
		return result;
	}
}
