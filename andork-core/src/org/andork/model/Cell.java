package org.andork.model;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.andork.func.Bimapper;

public interface Cell<T> extends Supplier<T>, Consumer<T> {

	public T get();

	public void set(T newValue);

	public default void accept(T newValue) {
		set(newValue);
	}

	public static <T> Cell<T> from(Supplier<T> getter, Consumer<T> setter) {
		return new Cell<T>() {
			@Override
			public T get() {
				return getter.get();
			}

			@Override
			public void set(T newValue) {
				if (setter != null) {
					setter.accept(newValue);
				}
			}
		};
	}

	public static <T> Cell<T> from(Supplier<Cell<T>> getCell) {
		return new Cell<T>() {
			@Override
			public T get() {
				Cell<T> cell = getCell.get();
				return cell == null ? null : cell.get();
			}

			@Override
			public void set(T newValue) {
				Cell<T> cell = getCell.get();
				if (cell != null)
					cell.set(newValue);
			}
		};
	}

	public static <T, U> Cell<U> compose(Cell<T> cell, Bimapper<T, U> bimapper) {
		return new Cell<U>() {
			@Override
			public U get() {
				return bimapper.map(cell.get());
			}

			@Override
			public void set(U newValue) {
				cell.set(bimapper.unmap(newValue));
			}
		};
	}
}
