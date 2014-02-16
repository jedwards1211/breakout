package org.andork.swing;


public abstract class DoSwingR<R> implements Runnable {
	public R	result;

	public DoSwingR() {
		DoSwing.doSwing(this);
	}
}
