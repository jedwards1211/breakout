package org.andork.bind;

import org.andork.func.Bimapper;

public class BimapperBinder<I, O> extends Binder<O> {
	Binder<I>		inBinder;
	Bimapper<I, O>	bimapper;

	O				out;

	public BimapperBinder(Bimapper<I, O> bimapper) {
		super();
		this.bimapper = bimapper;
	}

	public static <I, O> BimapperBinder<I, O> newInstance(Bimapper<I, O> bimapper) {
		return new BimapperBinder<I, O>(bimapper);
	}

	public static <I, O> BimapperBinder<I, O> bind(Bimapper<I, O> bimapper, Binder<I> inBinder) {
		return new BimapperBinder<I, O>(bimapper).bind(inBinder);
	}

	public BimapperBinder<I, O> bind(Binder<I> inBinder) {
		if (this.inBinder != inBinder) {
			if (this.inBinder != null) {
				unbind0(inBinder, this);
			}
			this.inBinder = inBinder;
			if (inBinder != null) {
				bind0(inBinder, this);
			}
			update(false);
		}
		return this;
	}

	public void unbind() {
		bind(null);
	}

	@Override
	public O get() {
		return out;
	}

	@Override
	public void set(O newValue) {
		if (inBinder != null && bimapper != null) {
			inBinder.set(bimapper.unmap(newValue));
		}
	}

	public void update(boolean force) {
		I in = inBinder == null ? null : inBinder.get();
		O newOut = in == null || bimapper == null ? null : bimapper.map(in);

		if (force || out != newOut) {
			out = newOut;
			updateDownstream(false);
		}
	}
}
